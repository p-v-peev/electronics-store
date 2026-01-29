package com.example.pvpeev.electronics_store;

public enum NumericConstants {

    MAX_PAGE_REQUEST(50);

    private final int value;

    NumericConstants(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
