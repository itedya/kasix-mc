package com.itedya.skymaster;

import com.itedya.skymaster.dtos.database.IslandDto;
import com.itedya.skymaster.rankings.IslandSizeRankingManager;
import com.itedya.skymaster.utils.SkyMasterStringUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.regex.Pattern;

public class SkyMasterPlaceholderExpansion extends PlaceholderExpansion {
    private final Pattern islandSizeNicknamePattern = Pattern.compile("island_size_player_\\d", Pattern.CASE_INSENSITIVE);
    private final Pattern islandSizePattern = Pattern.compile("island_size_\\d", Pattern.CASE_INSENSITIVE);

    public SkyMasterPlaceholderExpansion() {

    }

    @Override
    public @NotNull String getIdentifier() {
        return "skymaster";
    }

    @Override
    public @NotNull String getAuthor() {
        return "itedya";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    public String onRequest(OfflinePlayer player, @NotNull String params) {
        try {
            if (islandSizeNicknamePattern.matcher(params).find()) {
                int place = SkyMasterStringUtil.getIntFromEnd(params);
                var rankingManager = IslandSizeRankingManager.getInstance();

                Map placeData = rankingManager.getDataForPlace(place);
                if (placeData == null) return "";

                return (String) placeData.get("nickname");
            } else if (islandSizePattern.matcher(params).find()) {
                int place = SkyMasterStringUtil.getIntFromEnd(params);
                var rankingManager = IslandSizeRankingManager.getInstance();

                Map placeData = rankingManager.getDataForPlace(place);
                if (placeData == null) return "";

                IslandDto islandDto = (IslandDto) placeData.get("dto");

                return String.valueOf(islandDto.radius * 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
