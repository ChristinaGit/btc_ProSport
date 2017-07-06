package com.btc.prosport.api.request;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.Getter;

import com.btc.common.contract.Contracts;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public final class ChangeOrderForUnknownUserParams extends BaseChangeOrderParams {
    public ChangeOrderForUnknownUserParams(
        @NonNull final String playerPhoneNumber,
        @Nullable final String playerFirstName,
        @Nullable final String playerLastName,
        final long playgroundId,
        @NonNull final List<Interval> intervals) {
        super(playgroundId, Contracts.requireNonNull(intervals, "intervals == null"));
        Contracts.requireNonNull(playerPhoneNumber, "playerPhoneNumber == null");

        _playerPhoneNumber = playerPhoneNumber;
        _playerFirstName = StringUtils.defaultString(playerFirstName);
        _playerLastName = StringUtils.defaultString(playerLastName);
    }

    @Getter
    @SerializedName("player_first_name")
    @Nullable
    private final String _playerFirstName;

    @Getter
    @SerializedName("player_last_name")
    @Nullable
    private final String _playerLastName;

    @Getter
    @SerializedName("player_phone")
    @NonNull
    private final String _playerPhoneNumber;
}
