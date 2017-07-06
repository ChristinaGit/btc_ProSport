package com.btc.prosport.api.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import com.google.gson.annotations.SerializedName;

@Accessors(prefix = "_")
public final class NotificationsSubscribeParams {
    public static final String FIELD_NAME = "name";

    public static final String FIELD_REGISTRATION_ID = "registration_id";

    public static final String FIELD_DEVICE_ID = "device_id";

    public static final String FIELD_ACTIVE = "active";

    public static final String FIELD_DATE_CREATED = "date_created";

    public static final String FIELD_TYPE = "type";

    @Getter
    @Setter
    @SerializedName(FIELD_ACTIVE)
    private boolean _active;

    @Getter
    @Setter
    @SerializedName(FIELD_DATE_CREATED)
    private String _dateCreated;

    @Getter
    @Setter
    @SerializedName(FIELD_DEVICE_ID)
    private String _deviceId;

    @Getter
    @Setter
    @SerializedName(FIELD_NAME)
    private String _name;

    @Getter
    @Setter
    @SerializedName(FIELD_REGISTRATION_ID)
    private String _registrationId;

    @Getter
    @Setter
    @SerializedName(FIELD_TYPE)
    private String _type;
}
