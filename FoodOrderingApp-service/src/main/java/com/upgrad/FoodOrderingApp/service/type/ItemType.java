package com.upgrad.FoodOrderingApp.service.type;

public enum ItemType {
    VEG("VEG"),
    NON_VEG("NON_VEG"),
    VEGAN("VEGAN");

    private String value;

    ItemType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getValue();
    }
}
