package com.btc.prosport.api.model.utility;

import android.support.annotation.NonNull;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.contract.Contracts;

@Accessors(prefix = "_")
public enum OrderState {
    NOT_CONFIRMED(1), CONFIRMED(2), CANCELED(3);

    @NonNull
    public static OrderState byCode(final int code) {
        Contracts.requireNonNull(code, "code == null");

        OrderState result = null;

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

    OrderState(final int code) {
        _code = code;
    }
}
