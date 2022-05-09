package com.itedya.skymaster.dtos;

import org.bukkit.entity.Player;

public class IslandInviteDto {
    private IslandDto islandDto;
    private Player fromPlayer;
    private Player toPlayer;

    public IslandDto getIslandDto() {
        return islandDto;
    }

    public void setIslandDto(IslandDto islandDto) {
        this.islandDto = islandDto;
    }

    public Player getFromPlayer() {
        return fromPlayer;
    }

    public void setFromPlayer(Player fromPlayer) {
        this.fromPlayer = fromPlayer;
    }

    public Player getToPlayer() {
        return toPlayer;
    }

    public void setToPlayer(Player toPlayer) {
        this.toPlayer = toPlayer;
    }
}
