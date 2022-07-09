package com.itedya.skymaster.rankings;

import java.util.HashMap;
import java.util.Map;

public abstract class RankingManager {
    protected Map data = new HashMap();

    public void refresh() {
    }

    public Map getDataForPlace(int place) {
        return null;
    }
}
