package com.woworks.scheduling;

import com.woworks.client9.model.Advert;
import com.woworks.client9.model.AdvertHistory;
import com.woworks.client9.model.UserHistory;
import com.woworks.client9.scrape.ScrapperService;
import io.quarkus.scheduler.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.ExecutionException;

@ApplicationScoped
public class AdvertWatcherService implements AdvertWatcher {

    static final String CHECK_PRICE_INTERVAL = "20s";

    private static final Logger LOG = LoggerFactory.getLogger("AdvertWatcherService");
    private final WatchHistory watchHistory;

    private final Map<Long, Long> userChatMap = new HashMap<>();

    public WatchHistory getWatchHistory() {
        return watchHistory;
    }

    @Inject
    AdvertWatcherService(ScrapperService scrapperService) {
        watchHistory = new WatchHistory(scrapperService);
    }

    @Scheduled(every = CHECK_PRICE_INTERVAL)
    void checkAdvertPrices() {
        LOG.debug("Checking advert prices...");
        watchHistory.checkAdvertPrices(userChatMap);
    }

    @Override
    public void stopWatch(long userId) {
        watchHistory.removeUserFromWatch(userId);
    }

    @Override
    public List<AdvertHistory> watchAdvert(long userId, long advertId, long chatId) {
        userChatMap.put(userId, chatId);
        boolean newAd = false;
        try {
            newAd = watchHistory.watch(userId, advertId);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        checkAdvertPrices();
        LOG.debug("{} watching advert id '{}' for user id: '{}'", (newAd ? "Started" : "Continue"), advertId, userId);
        return getUserAdvertsHistory(userId);
    }

    @Override
    public void unwatchAdvert(long userId, long advertId) throws AdvertWatcherException {
        watchHistory.unwatch(userId, advertId);
        LOG.debug("Removed advert id '{}' for user id: '{}'", advertId, userId);
    }

    @Override
    public Set<Long> getUserAdvertIds(long userId) {
        Set<Long> userWatchList = watchHistory.getUserAdverts(userId);
        LOG.debug("User id: '{}' has list: '{}'", userId, userWatchList);
        return userWatchList;
    }

    @Override
    public List<AdvertHistory> getUserAdvertsHistory(long userId) {
        UserHistory userHistory = watchHistory.userHistory(userId);
        return userHistory != null ? userHistory.getAdvertHistoryList() : Collections.emptyList();
    }

    @Override
    public Advert getAdvert(long advertId) {
        return  watchHistory.getAdvert(advertId);
    }


}
