package com.woworks.scheduling;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Sets;
import com.woworks.bot9.BotCommandProcessor;
import com.woworks.bot9.Watch999Bot;
import com.woworks.client9.model.Advert;
import com.woworks.client9.model.AdvertHistory;
import com.woworks.client9.model.PriceChange;
import com.woworks.client9.scrape.ScrapperException;
import com.woworks.client9.scrape.ScrapperService;
import io.quarkus.scheduler.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@ApplicationScoped
public class AdvertWatcherService implements AdvertWatcher {
    static final int ADVERT_EXPIRE_MINS = 10;
    static final String CHECK_PRICE_INTERVAL = "20s";
    private static Watch999Bot bot;
    private static final Logger LOG = LoggerFactory.getLogger("AdvertWatcherService");
    private final ScrapperService scrapperService;
    private final Map<Long, Long> userChatMap = new HashMap<>();
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
        LOG.debug("Checking advert prices...");
        LOG.debug("Watch list: {}", watchList);
        watchList.forEach((userId, userAdList) -> {
            LOG.debug("userId: '{}' -> '{}'", userId, userAdList);
            Map<Long, List<PriceChange>> userAdvertHist = userHistoryMap.get(userId);
            Map<Long, List<PriceChange>> userAdvertHistoryMap = userAdvertHist != null ? userAdvertHist : new HashMap<>();

            userAdList.forEach(adId -> {

                Advert advert;
                try {
                    advert = scrapperService.getAdvert(adId);
                } catch (ScrapperException e) {
                    LOG.warn("Could not get the advert", e);
                    return;
                }

                advertsCache.put(adId, advert);
                PriceChange priceChange = new PriceChange(advert.getPrice(), LocalDateTime.now());
                List<PriceChange> advertHistory = new ArrayList<>();
                if (!userAdvertHistoryMap.isEmpty()) {
                    List<PriceChange> advertHist = userAdvertHistoryMap.get(adId);
                    advertHistory = advertHist != null ? advertHist : new ArrayList<>();
                    if (advertHistory.isEmpty() || priceChanged(advertHistory, advert)) {
                        advertHistory.add(priceChange);
                        userAdvertHistoryMap.put(adId, advertHistory);
                        sendPriceChangeMessage(userId, adId);
                    }
                } else {
                    userAdvertHistoryMap.put(adId, new ArrayList<>(Arrays.asList(priceChange)));
                }

            });

            userHistoryMap.put(userId, userAdvertHistoryMap);
        });
    }

    private void sendPriceChangeMessage(Long userId, Long adId) {
        String messageText = BotCommandProcessor.getWatchHistoryListFormatted(
                getUserAdvertsHistory(userId).stream().filter(advertHistory -> advertHistory.getAdvert().getId().equals(adId)).collect(Collectors.toList())
        );
        SendMessage message = new SendMessage()
                .setChatId(userChatMap.get(userId))
                .setParseMode(ParseMode.HTML)
                .setText(messageText);
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private boolean priceChanged(List<PriceChange> advertHistory, Advert advert) {
        return !advertHistory.get(advertHistory.size() - 1).getPrice().equals(advert.getPrice());
    }

    @Override
    public void stopWatch(long userId) {
        watchList.remove(userId);
        userHistoryMap.remove(userId);
    }

    @Override
    public List<AdvertHistory> watchAdvert(long userId, Long advertId, Long chatId) {
        boolean newAd;
        userChatMap.put(userId, chatId);
        if (watchList.containsKey(userId)) {
            newAd = watchList.get(userId).add(advertId);
        } else {
            watchList.put(userId, Sets.newHashSet(advertId));
            newAd = true;
        }
        checkAdvertPrices();
        LOG.debug("{} watching advert id '{}' for user id: '{}'", (newAd ? "Started" : "Continue"), advertId, userId);
        return getUserAdvertsHistory(userId);
    }

    @Override
    public void unwatchAdvert(long userId, Long advertId) throws AdvertWatcherException {
        if (watchList.containsKey(userId)) {
            watchList.get(userId).remove(advertId);
            userHistoryMap.get(userId).remove(advertId);
        } else {
            String error = String.format("User: '%s' has no advert with id = '%s'", userId, advertId);
            LOG.warn(error);
            throw new AdvertWatcherException(error);
        }
        LOG.debug("Removed advert id '{}' for user id: '{}'", advertId, userId);
    }

    @Override
    public Set<Long> getUserAdvertIds(long userId) {
        Set<Long> userWatchList = watchList.get(userId);
        LOG.debug("User id: '{}' has list: '{}'", userId, userWatchList);
        return userWatchList;
    }

    @Override
    public List<AdvertHistory> getUserAdvertsHistory(long userId) {
        LOG.debug("userHistoryMap: '{}'", userHistoryMap);
        Map<Long, List<PriceChange>> userAdvertHistory = userHistoryMap.get(userId);
        if (userAdvertHistory == null) {
            return new ArrayList<>();
        }

        List<AdvertHistory> advertHistoryList = new ArrayList<>();
        LOG.debug("User id: '{}' has history: '{}'", userId, userAdvertHistory);

        userAdvertHistory.forEach((adId, changesList) -> {
            try {
                advertHistoryList.add(new AdvertHistory(advertsCache.get(adId), userAdvertHistory.get(adId)));
            } catch (ExecutionException e) {
                LOG.error("Could not get advert from cache", e);
            }
        });

        return advertHistoryList;
    }

    @Override
    public Advert getAdvert(long advertId) throws ExecutionException {
        return  advertsCache.get((Long) advertId);
    }

    public void setBot(Watch999Bot bot) {
        this.bot = bot;
    }
}
