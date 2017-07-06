package com.btc.prosport.api.model.utility;

import android.support.annotation.NonNull;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.contract.Contracts;

@Accessors(prefix = "_")
public enum SaleType {
    ABSOLUTE(1), RELATIVE(2), NEW_PRICE(3);

    @NonNull
    public static SaleType byCode(final int code) {
        Contracts.requireNonNull(code, "code == null");

        SaleType result = null;

        for (final val orderState : values()) {
            if (orderState.getCode() == code) {
                result = orderState;
                break;
            }
        }

        if (result == null) {
            throw new IllegalArgumentException("Unknown code: " + code);
        }

        return result;
    }

    @Getter
    private final int _code;

    SaleType(final int code) {
        _code = code;
    }
}
