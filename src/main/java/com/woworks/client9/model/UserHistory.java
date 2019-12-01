package com.woworks.client9.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserHistory {

    private Long userId;
    private List<AdvertHistory> advertHistory = new ArrayList<>();

    public UserHistory(long userId, ArrayList<AdvertHistory> advertHistory) {
        this.userId = userId;
        this.advertHistory = advertHistory;

    }

    public UserHistory() {
        // is it ok??
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<AdvertHistory> getAdvertHistoryList() {
        return advertHistory;
    }

    public void setAdvertHistory(List<AdvertHistory> advertHistory) {
        this.advertHistory = advertHistory;
    }

    public AdvertHistory getAdvertHistory(long advertId) {
        return advertHistory.stream()
                .filter(adHistory -> (adHistory != null && advertId == adHistory.getAdvert().getId()))
                .findFirst()
                .or(() -> {
                            AdvertHistory newAdHist = new AdvertHistory();
                            advertHistory.add(newAdHist);
                            return Optional.of(newAdHist);
                        }
                ).get();
    }

    public boolean isNotEmpty() {
        return advertHistory != null;
    }

    public void removeAdvertHistory(long advertId) {
        advertHistory
                .removeIf(adHistory -> advertId == adHistory.getAdvert().getId());
    }
}
