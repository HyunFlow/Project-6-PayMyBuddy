package com.openclassrooms.pay_my_buddy.domain.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CurrencyType {
    EUR, USD;

    @JsonCreator
    public static CurrencyType from(String currency) {
        if(currency == null) return null;
        return CurrencyType.valueOf(currency.trim().toUpperCase());
    }

    @JsonValue
    public String toJson() {
        return name();
    }
}
