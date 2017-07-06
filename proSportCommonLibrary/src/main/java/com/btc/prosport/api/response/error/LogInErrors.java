package com.btc.prosport.api.response.error;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.experimental.Accessors;

import com.btc.common.contract.Contracts;
import com.btc.prosport.api.response.ProSportErrorResponse;

import java.util.List;

@Accessors(prefix = "_")
public final class LogInErrors {
    public static final String FIELD_PASSWORD_ERRORS = "password";

    public static final String FIELD_PHONE_ERRORS = "phone";

    @Nullable
    public static List<String> getPasswordErrors(@NonNull final ProSportErrorResponse response) {
        Contracts.requireNonNull(response, "response == null");

        return (List<String>) response.getFiledError(FIELD_PASSWORD_ERRORS);
    }

    @Nullable
    public static List<String> getPhoneErrors(@NonNull final ProSportErrorResponse response) {
        Contracts.requireNonNull(response, "response == null");

        return (List<String>) response.getFiledError(FIELD_PHONE_ERRORS);
    }

    private LogInErrors() {
        Contracts.unreachable();
    }
}
