package com.woworks.scheduling;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.woworks.bot9.BotCommandProcessor;
import com.woworks.bot9.Watch999Bot;
import com.woworks.client9.model.Advert;
import com.woworks.client9.model.AdvertHistory;
import com.woworks.client9.model.PriceChange;
import com.woworks.client9.model.UserHistory;
import com.woworks.client9.scrape.ScrapperException;
import com.woworks.client9.scrape.ScrapperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class WatchHistory {

    private static final int ADVERT_EXPIRE_MINS = 10;
    private static final Logger LOG = LoggerFactory.getLogger("WatchHistory");
    private static Watch999Bot bot;
    private final ScrapperService scrapperService;


    private LoadingCache<Long, Advert> advertsCache = CacheBuilder.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(ADVERT_EXPIRE_MINS))
            .build(new CacheLoader<Long, Advert>() {
                @Override
                public Advert load(final Long advertId) throws ScrapperException {
                    return scrapperService.getAdvert(advertId);
                }
            });

    //                userId    set of Ids
    private final Map<Long, Set<Long>> watchList = new HashMap<>();
    //                userId    UserHistory
    private final Map<Long, UserHistory> userHistory = new HashMap<>();

    WatchHistory(ScrapperService scrapperService) {
        this.scrapperService = scrapperService;
    }


    public Advert getAdvert(Long advertId) {
        try {
            return advertsCache.get(advertId);
        } catch (ExecutionException e) {
            LOG.error("Could not get advert from cache for advertId: {}", advertId);
            return null;
        }
    }

    public Set<Long> getUserAdverts(long userId) {
        return watchList.get(userId);
    }

    public void removeUserFromWatch(long userId) {
        watchList.remove(userId);
        userHistory.remove(userId);
    }

    public UserHistory userHistory(long userId) {
        System.out.println("userHistoryMap = " + userHistory);
        return userHistory.get(userId);
    }

    public boolean watch(long userId, long advertId) throws ExecutionException {
        boolean newAd;
        if (watchList.containsKey(userId)) {
            newAd = watchList.get(userId).add(advertId);
            userHistory.get(userId).getAdvertHistory(advertId).setAdvert(advertsCache.get(advertId));
        } else {
            Advert ad = advertsCache.get(advertId);
            if (ad == null) {
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>> NULL" + advertId);
            }
            watchList.put(userId, Sets.newHashSet(advertId));

            userHistory.put(userId,
                    new UserHistory(userId,

                            Lists.newArrayList(new AdvertHistory(
                                    ad, Lists.newArrayList(new PriceChange(ad.getPrice(), LocalDateTime.now()))
                                    )
                            )
                    )
            );
            newAd = true;
        }
        return newAd;
    }

    public void unwatch(long userId, long advertId) throws AdvertWatcherException {
        if (watchList.containsKey(userId)) {
            watchList.get(userId).remove(advertId);
            userHistory.get(userId).removeAdvertHistory(advertId);
        } else {
            String error = String.format("User: '%s' has no advert with id = '%s'", userId, advertId);
            LOG.warn(error);
            throw new AdvertWatcherException(error);
        }
    }

    void checkAdvertPrices(Map<Long, Long> userChatMap) {
        LOG.debug("Watch list: {}", watchList);
        watchList.forEach((userId, userAdList) -> {
            LOG.debug("userId: '{}' -> '{}'", userId, userAdList);
            UserHistory userAdvertHist = userHistory.get(userId) != null ? userHistory.get(userId) : new UserHistory();
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
                List<PriceChange> advertPriceChangeList = new ArrayList<>();
                if (userAdvertHist.isNotEmpty()) {

                    List<PriceChange> advertHist = userAdvertHist.getAdvertHistory(adId).getPriceHistory();

                    advertPriceChangeList = advertHist != null ? advertHist : new ArrayList<>();
                    if (advertPriceChangeList.isEmpty() || priceChanged(advertPriceChangeList, advert)) {
                        advertPriceChangeList.add(priceChange);
                        System.out.println("priceChange = " + priceChange);
                        System.out.println("advertPriceChangeList = " + advertPriceChangeList);


                        userAdvertHist.getAdvertHistory(adId).setPriceHistory(advertPriceChangeList);

                        System.out.println("userAdvertHist.getAdvertHistory(adId) = " + userAdvertHist.getAdvertHistory(adId));
                        String messageText = BotCommandProcessor.getWatchHistoryListFormatted(
                                //this.userHistory(userId).getAdvertHistoryList()
                                userAdvertHist.getAdvertHistoryList()
                        );
                        SendMessage message = new SendMessage()
                                .setChatId(userChatMap.get(userId))
                                .setParseMode(ParseMode.HTML)
                                .setText(messageText);

                        sendPriceChangeMessage(userId, adId, message);
                    }
                } else {
                    userAdvertHist.setAdvertHistory(Collections.singletonList(
                            new AdvertHistory(advert, new ArrayList<>(Arrays.asList(priceChange)))
                    ));
                }

            });

            userHistory.put(userId, userAdvertHist);
        });
    }

    private boolean priceChanged(List<PriceChange> advertHistory, Advert advert) {
        return !advertHistory.get(advertHistory.size() - 1).getPrice().equals(advert.getPrice());
    }

    private void sendPriceChangeMessage(Long userId, Long adId, SendMessage message) {

        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            LOG.error("Bot Could Not Execute a message", e);
        }
    }

    public void setBot(Watch999Bot bot9) {
        bot = bot9;
    }
}
