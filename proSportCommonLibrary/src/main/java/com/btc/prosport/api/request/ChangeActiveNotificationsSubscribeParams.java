package com.btc.prosport.api.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import com.google.gson.annotations.SerializedName;

@Accessors(prefix = "_")
public final class ChangeActiveNotificationsSubscribeParams {
    @Getter
    @Setter
    @SerializedName("active")
    private boolean _active;

    @Getter
    @Setter
    @SerializedName("id")
    private long _id;

    @Getter
    @Setter
    @SerializedName("registration_id")
    private String _registrationId;
}
