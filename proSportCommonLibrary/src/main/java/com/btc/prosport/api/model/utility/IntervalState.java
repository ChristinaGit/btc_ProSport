package com.btc.prosport.api.model.utility;

import android.support.annotation.NonNull;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.contract.Contracts;

import java.util.Objects;

@Accessors(prefix = "_")
public enum IntervalState {
    FREE("free"), NOT_SPECIFIED("not_specified"), BUSY("busy"), PAST("past");

    @NonNull
    public static IntervalState byCode(@NonNull final String code) {
        Contracts.requireNonNull(code, "code == null");

        IntervalState result = null;

        for (final val intervalState : values()) {
            if (Objects.equals(intervalState.getCode(), code)) {
                result = intervalState;
                break;
            }
        }

        if (result == null) {
            throw new IllegalArgumentException("Unknown code: " + code);
        }

        return result;
    }

    @Getter
    @NonNull
    private final String _code;

    IntervalState(@NonNull final String code) {
        Contracts.requireNonNull(code, "code == null");

        _code = code;
    }
}
