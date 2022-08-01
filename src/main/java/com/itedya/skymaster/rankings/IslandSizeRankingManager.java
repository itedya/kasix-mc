package com.itedya.skymaster.rankings;

import com.itedya.skymaster.dtos.IslandSizeRankingDto;
import com.itedya.skymaster.runnables.rankings.RefreshIslandRankingRunnable;
import com.itedya.skymaster.utils.ThreadUtil;

public class IslandSizeRankingManager extends RankingManager<IslandSizeRankingDto> {
    private IslandSizeRankingManager() {
        // 30 sec
        ThreadUtil.asyncRepeat(this::refresh, 600);
    }

    private static IslandSizeRankingManager instance = null;

    public static IslandSizeRankingManager getInstance() {
        if (instance == null) instance = new IslandSizeRankingManager();

        return instance;
    }

    @Override
    public void refresh() {
        ThreadUtil.async(new RefreshIslandRankingRunnable());
    }
}
