package com.itedya.skymaster.enums;

public enum IslandRate {
    UP_VOTE(1), DOWN_VOTE(-1);
    private final int value;
    private IslandRate(int value){
        this.value = value;
    }
    public int getValue(){
        return value;
    }
}
