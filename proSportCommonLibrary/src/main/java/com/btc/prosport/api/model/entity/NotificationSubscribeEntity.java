package com.btc.prosport.api.model.entity;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import com.btc.prosport.api.model.NotificationSubscribe;
import com.google.gson.annotations.SerializedName;

@Accessors(prefix = "_")
public final class NotificationSubscribeEntity implements NotificationSubscribe {
    public static final String FIELD_ID = "id";

    public static final String FIELD_NAME = "name";

    public static final String FIELD_REGISTRATION_ID = "registration_id";

    public static final String FIELD_DEVICE_ID = "device_id";

    public static final String FIELD_ACTIVE = "active";

    public static final String FIELD_DATE_CREATED = "date_created";

    public static final String FIELD_TYPE = "type";

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_ACTIVE)
    private boolean _active;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_DATE_CREATED)
    @Nullable
    private String _dateCreated;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_DEVICE_ID)
    @Nullable
    private String _deviceId;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_ID)
    private long _id;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_NAME)
    @Nullable
    private String _name;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_REGISTRATION_ID)
    @Nullable
    private String _registrationId;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_TYPE)
    @Nullable
    private String _type;
}
