package com.woworks.scheduling;

import com.woworks.client9.model.AdvertHistory;

import java.util.List;
import java.util.Set;

public interface AdvertWatcher {

    List<AdvertHistory> watchAdvert(long userId, Long advertId);

    void unwatchAdvert(Long userId, Long advertId);

    Set<Long> getUserAdvertIds(Long userId);

    List<AdvertHistory> getUserAdvertsHistory(Long userId);

    void stopWatch(long userId);
}
