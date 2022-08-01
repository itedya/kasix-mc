package com.itedya.skymaster.utils;

public class IslandUtil {
    public static double getExpandCost(int radius) {
        return Math.pow(radius * 2, 2);
    }
}
