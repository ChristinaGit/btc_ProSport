package com.btc.prosport.api.model.entity;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.btc.prosport.api.model.User;
import com.google.gson.annotations.SerializedName;

@ToString(doNotUseGetters = true)
@Accessors(prefix = "_")
public final class UserEntity implements User {
    private static final String FIELD_ID = "id";

    private static final String FIELD_AVATAR_URI = "avatar";

    private static final String FIELD_CITY = "city";

    private static final String FIELD_FIRST_NAME = "first_name";

    private static final String FIELD_LAST_NAME = "last_name";

    private static final String FIELD_PHONE_NUMBER = "phone";

    private static final String FIELD_EMAIL = "email";

    @Getter(onMethod = @__(@Override))
    @SerializedName(FIELD_AVATAR_URI)
    @Nullable
    private String _avatarUri;

    @Getter(onMethod = @__(@Override))
    @SerializedName(FIELD_CITY)
    @Nullable
    private CityEntity _city;

    @Getter(onMethod = @__(@Override))
    @SerializedName(FIELD_FIRST_NAME)
    @Nullable
    private String _firstName;

    @Getter(onMethod = @__(@Override))
    @SerializedName(FIELD_ID)
    private long _id;

    @Getter(onMethod = @__(@Override))
    @SerializedName(FIELD_LAST_NAME)
    @Nullable
    private String _lastName;

    @Getter(onMethod = @__(@Override))
    @SerializedName(FIELD_PHONE_NUMBER)
    @Nullable
    private String _phoneNumber;

    @Getter(onMethod = @__(@Override))
    @SerializedName(FIELD_EMAIL)
    @Nullable
    private String _email;


}
