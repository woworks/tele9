package com.woworks.scheduling;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Sets;
import com.woworks.client9.model.Advert;
import com.woworks.client9.model.AdvertHistory;
import com.woworks.client9.model.PriceChange;
import com.woworks.client9.scrape.ScrapperService;
import io.quarkus.scheduler.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;

@ApplicationScoped
public class AdvertWatcherService implements AdvertWatcher {
    static final int ADVERT_EXPIRE_MINS = 10;
    static final String CHECK_PRICE_INTERVAL = "20s";
    private static final Logger LOG = LoggerFactory.getLogger("AdvertWatcherService");
    private final ScrapperService scrapperService;
    private final Map<Long, Set<Long>> watchList = new HashMap<>();
    private final Map<Long, Map<Long, List<PriceChange>>> userHistoryMap = new HashMap<>();

    LoadingCache<Long, Advert> advertsCache = CacheBuilder.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(ADVERT_EXPIRE_MINS))
            .build(new CacheLoader<Long, Advert>() {
                @Override
                public Advert load(final Long advertId) throws Exception {
                    return scrapperService.getAdvert(advertId);
                }
            });

    @Inject
    AdvertWatcherService(ScrapperService scrapperService) {
        this.scrapperService = scrapperService;
    }

    @Scheduled(every = CHECK_PRICE_INTERVAL)
    void checkAdvertPrices() {
        LOG.info("Checking advert prices...");
        LOG.info("Watch list: {}", watchList);
        watchList.forEach((userId, userAdList) -> {
            LOG.debug("userId: '{}' -> '{}'", userId, userAdList);
            Map<Long, List<PriceChange>> userAdvertHist = userHistoryMap.get(userId);
            Map<Long, List<PriceChange>> userAdvertHistoryMap = userAdvertHist != null ? userAdvertHist : new HashMap<>();

            userAdList.forEach(adId -> {

                    //Advert advert = advertsCache.get(adId);
                    Advert advert = scrapperService.getAdvert(adId);
                    advertsCache.put(adId, advert);
                    //LOG.debug("advert: {}", advert);
                    PriceChange priceChange = new PriceChange(advert.getPrice(), LocalDateTime.now());
                    List<PriceChange> advertHistory = new ArrayList<>();
                    if (!userAdvertHistoryMap.isEmpty()) {
                        LOG.debug("userAdvertHistoryMap NOT NEW  = {}", userAdvertHistoryMap);
                        List<PriceChange> advertHist = userAdvertHistoryMap.get(adId);
                        advertHistory = advertHist != null ? advertHist : new ArrayList<>();
                        if (advertHistory.isEmpty() || priceChanged(advertHistory, advert)) {
                            LOG.debug("ADV HIST = {}; price change = {}", advertHistory, priceChange);
                            advertHistory.add(priceChange);
                            userAdvertHistoryMap.put(adId, advertHistory);
                        }

                    } else {
                        LOG.debug("NEW ADV HIST = {}; price change = {}", advertHistory, priceChange);
                        LOG.debug("userAdvertHistoryMap NEW  = {}", userAdvertHistoryMap.keySet());
                        userAdvertHistoryMap.put(adId, new ArrayList<>(Arrays.asList(priceChange)));
                    }

            });

            userHistoryMap.put(userId, userAdvertHistoryMap);
        });
    }

    private boolean priceChanged(List<PriceChange> advertHistory, Advert advert) {
        return !advertHistory.get(advertHistory.size() - 1).getPrice().equals(advert.getPrice());
    }

    @Override
    public List<AdvertHistory> watchAdvert(Long userId, Long advertId) {
        checkAdvertPrices();
        boolean newAd;
        if (watchList.containsKey(userId)) {
            newAd = watchList.get(userId).add(advertId);
        } else {
            watchList.put(userId, Sets.newHashSet(advertId));
            newAd = true;
        }
        LOG.debug("{} watching advert id '{}' for user id: '{}'", (newAd ? "Started" : "Continue"), advertId, userId);
        return getUserAdvertsHistory(userId);
    }

    @Override
    public void unwatchAdvert(Long userId, Long advertId) {
        if (watchList.containsKey(userId)) {
            watchList.get(userId).remove(advertId);
        } else {
            LOG.warn("User: '{}' has no advert with id = '{}'", userId, advertId);
        }
        LOG.debug("Removed advert id '{}' for user id: '{}'", advertId, userId);
    }

    @Override
    public Set<Long> getUserAdvertIds(Long userId) {
        Set<Long> userWatchList = watchList.get(userId);
        LOG.debug("User id: '{}' has list: '{}'", userId, userWatchList);
        return userWatchList;
    }

    @Override
    public List<AdvertHistory> getUserAdvertsHistory(Long userId) {
        LOG.debug("userHistoryMap: '{}'", userHistoryMap);
        Map<Long, List<PriceChange>> userAdvertHistory = userHistoryMap.get(userId);
        if (userAdvertHistory == null) {
            return new ArrayList<>();
        }

        List<AdvertHistory> advertHistoryList  = new ArrayList<>();
        LOG.debug("User id: '{}' has history: '{}'", userId, userAdvertHistory);

        userAdvertHistory.forEach( (adId, changesList) -> {
            try {
                advertHistoryList.add(new AdvertHistory(advertsCache.get(adId), userAdvertHistory.get(adId)));
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });

        return advertHistoryList;
    }
}
