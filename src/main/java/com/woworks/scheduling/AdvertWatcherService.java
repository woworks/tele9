package com.woworks.scheduling;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Sets;
import com.woworks.client9.model.Advert;
import com.woworks.client9.model.AdvertHistory;
import com.woworks.client9.scrape.ScrapperService;
import io.quarkus.scheduler.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@ApplicationScoped
public class AdvertWatcherService {
    private static final Logger LOG = LoggerFactory.getLogger("AdvertWatcherService");
    public static final int ADVERT_EXPIRE_MINS = 10;
    public static final String CHECK_PRICE_INTERVAL = "10s";

    private final ScrapperService scrapperService;
    private final Map<Long, Set<Long>> watchList = new HashMap<>();
    private final Map<Long, AdvertHistory> userHistoryMap = new HashMap<>();

    LoadingCache<Long, Advert> advertsCache = CacheBuilder.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(ADVERT_EXPIRE_MINS))
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

    @Scheduled(every=CHECK_PRICE_INTERVAL)
    void checkAdvertPrices() {
        LOG.info("Checking advert prices...");
        LOG.info("Watch list: {}", watchList);
        watchList.forEach((id, adList) -> {
            LOG.debug("id: '{}' -> '{}'", id, adList);
            adList.forEach( adId -> {
                try {
                    LOG.debug("advert: {}", advertsCache.get(adId));
                } catch (ExecutionException e) {
                    LOG.error("Could not get advert from cache!", e);
                }
            });
        });
    }

    public void watchAdvert(Long userId, Long advertId) {
        boolean newAd;
        if (watchList.containsKey(userId)) {
            newAd = watchList.get(userId).add(advertId);
        } else {
            watchList.put(userId, Sets.newHashSet(advertId));
            newAd = true;
        }
        LOG.debug((newAd ? "Started" : "Continue") + " watching advert id '{}' for user id: '{}'", advertId, userId);
    }

    public void unwatchAdvert(Long userId, Long advertId) {
        if (watchList.containsKey(userId)) {
            watchList.get(userId).remove(advertId);
        } else {
            LOG.warn("User: '{}' has no advert with id = '{}'", userId, advertId);
        }
        LOG.debug("Removed advert id '{}' for user id: '{}'", advertId, userId);
    }

    public Set<Long> getUserAdvertIds(Long userId) {
        Set<Long> userWatchList = watchList.get(userId);
        LOG.debug("User id: '{}' has list: '{}'", userId, userWatchList);
        return userWatchList;
    }

    public AdvertHistory getUserAdvertsHistory(Long userId) {
        AdvertHistory userAdvertHistory = userHistoryMap.get(userId);
        LOG.debug("User id: '{}' has history: '{}'", userId, userAdvertHistory);
        return userAdvertHistory;
    }
}
