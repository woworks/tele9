package com.woworks.scheduling;

import com.woworks.client9.model.Advert;
import com.woworks.client9.model.AdvertHistory;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public interface AdvertWatcher {

    List<AdvertHistory> watchAdvert(long userId, long advertId, long chatId);

    void unwatchAdvert(long userId, long advertId) throws AdvertWatcherException;

    Set<Long> getUserAdvertIds(long userId);

    List<AdvertHistory> getUserAdvertsHistory(long userId);

    void stopWatch(long userId);

    Advert getAdvert(long advertId) throws ExecutionException;
}
