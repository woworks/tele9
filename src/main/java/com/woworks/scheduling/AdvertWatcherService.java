package com.woworks.scheduling;

import com.google.common.collect.Sets;
import io.quarkus.scheduler.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class AdvertWatcherService {
    private static final Logger LOG = LoggerFactory.getLogger("AdvertWatcherService");

    private final Map<Long, Set<Long>> watchList = new HashMap<>();

    @Scheduled(every="30s")
    void checkAdvertPrices() {
        LOG.info("Checking advert prices...");
        LOG.debug("Checking advert prices debug...");
        LOG.info("Watch list: {}", watchList);
    }

    public void watchAdvert(Long userId, Long advertId) {
        if (watchList.containsKey(userId)) {
            watchList.get(userId).add(advertId);
        } else {
            watchList.put(userId, Sets.newHashSet(advertId));
        }
        LOG.debug("Started watching advert id '{}' for user id: '{}'", advertId, userId);
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
}
