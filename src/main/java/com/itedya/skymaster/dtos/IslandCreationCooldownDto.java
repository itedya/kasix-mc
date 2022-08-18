package com.itedya.skymaster.dtos;


public class IslandCreationCooldownDto {
    public String playerUuid;
    public int expiresIn;

    public String getStringHours() {
        int hours = expiresIn / 3600;
        return String.format("%02d", hours);
    }

    public String getStringMinutes() {
        int minutes = (expiresIn % 3600) / 60;
        return String.format("%02d", minutes);
    }

    public String getStringSeconds() {
        int seconds = expiresIn % 60;
        return String.format("%02d", seconds);
    }
}
