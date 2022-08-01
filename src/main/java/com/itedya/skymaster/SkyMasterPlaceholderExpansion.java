package com.itedya.skymaster;

import com.itedya.skymaster.dtos.IslandSizeRankingDto;
import com.itedya.skymaster.rankings.IslandSizeRankingManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

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
                int place = Integer.parseInt(params.replaceAll("island_size_player_", ""));
                var rankingManager = IslandSizeRankingManager.getInstance();

                IslandSizeRankingDto dto = rankingManager.getDataForPlace(place);
                if (dto == null) return "";

                return dto.islandOwnerName;
            } else if (islandSizePattern.matcher(params).find()) {
                int place = Integer.parseInt(params.replaceAll("island_size_", ""));
                var rankingManager = IslandSizeRankingManager.getInstance();

                IslandSizeRankingDto dto = rankingManager.getDataForPlace(place);
                if (dto == null) return "";

                return String.valueOf(dto.islandDto.radius * 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
