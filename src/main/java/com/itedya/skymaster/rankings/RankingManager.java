package com.itedya.skymaster.rankings;

import java.util.List;

public abstract class RankingManager<T> {
    private List<T> data;

    public abstract void refresh();

    public void saveData(List<T> data) {
        this.data = data;
    }

    public T getDataForPlace(int place) {
        try {
            return data.get(place - 1);
        } catch (Exception e) {
            return null;
        }
    }
}
