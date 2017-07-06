package com.btc.prosport.api.response.error;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.experimental.Accessors;

import com.btc.common.contract.Contracts;
import com.btc.prosport.api.response.ProSportErrorResponse;

import java.util.List;

@Accessors(prefix = "_")
public final class AdditionalInfoErrors {
    public static final String FIELD_EMAIL_ERRORS = "email";

    public static final String FIELD_FIRST_NAME_ERRORS = "first_name";

    public static final String FIELD_LAST_NAME_ERRORS = "last_name";

    @Nullable
    public static List<String> getEmailErrors(@NonNull final ProSportErrorResponse response) {
        Contracts.requireNonNull(response, "response == null");

        return (List<String>) response.getFiledError(FIELD_EMAIL_ERRORS);
    }

    @Nullable
    public static List<String> getLastNameErrors(@NonNull final ProSportErrorResponse response) {
        Contracts.requireNonNull(response, "response == null");

        return (List<String>) response.getFiledError(FIELD_LAST_NAME_ERRORS);
    }

    @Nullable
    public static List<String> getFirstNameErrors(@NonNull final ProSportErrorResponse response) {
        Contracts.requireNonNull(response, "response == null");

        return (List<String>) response.getFiledError(FIELD_FIRST_NAME_ERRORS);
    }

    private AdditionalInfoErrors() {
        Contracts.unreachable();
    }
}
