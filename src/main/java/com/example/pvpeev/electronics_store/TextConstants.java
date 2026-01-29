package com.example.pvpeev.electronics_store;

public enum TextConstants {
    PRODUCT_THUMBNAILS_STORAGE("product-thumbnails"),
    PRODUCT_IMAGES_STORAGE("product-images");

    private final String value;

    TextConstants(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
