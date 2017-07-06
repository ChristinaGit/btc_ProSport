package com.btc.prosport.api.model.utility;

import android.support.annotation.NonNull;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.contract.Contracts;

import java.util.Calendar;
import java.util.Objects;

@Accessors(prefix = "_")
public enum WeekDay {
    SUNDAY("7", Calendar.SUNDAY),
    MONDAY("1", Calendar.MONDAY),
    TUESDAY("2", Calendar.TUESDAY),
    WEDNESDAY("3", Calendar.WEDNESDAY),
    THURSDAY("4", Calendar.THURSDAY),
    FRIDAY("5", Calendar.FRIDAY),
    SATURDAY("6", Calendar.SATURDAY);

    @NonNull
    public static WeekDay byCode(final int code) {
        WeekDay result = null;

        for (final val playgroundWorkTimeDay : values()) {
            if (playgroundWorkTimeDay.getCode() == code) {
                result = playgroundWorkTimeDay;
                break;
            }
        }

        if (result == null) {
            throw new IllegalArgumentException("Unknown code: " + code);
        }

        return result;
    }

    @NonNull
    public static WeekDay byId(@NonNull final String id) {
        Contracts.requireNonNull(id, "id == null");

        WeekDay result = null;

        for (final val playgroundWorkTimeDay : values()) {
            if (Objects.equals(playgroundWorkTimeDay.getId(), id)) {
                result = playgroundWorkTimeDay;
                break;
            }
        }

        if (result == null) {
            throw new IllegalArgumentException("Unknown id: " + id);
        }

        return result;
    }

    @Getter
    private final int _code;

    @Getter
    private final String _id;

    WeekDay(@NonNull final String id, final int code) {
        Contracts.requireNonNull(id, "id == null");

        _id = id;
        _code = code;
    }
}
