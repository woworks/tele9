package com.woworks.scheduling;

import com.woworks.client9.model.AdvertHistory;

import java.util.List;
import java.util.Set;

/**
 * @author Artiom Slastin
 */
public interface AdvertWatcher {
    void watchAdvert(Long userId, Long advertId);

    void unwatchAdvert(Long userId, Long advertId);

    Set<Long> getUserAdvertIds(Long userId);

    List<AdvertHistory> getUserAdvertsHistory(Long userId);
}
