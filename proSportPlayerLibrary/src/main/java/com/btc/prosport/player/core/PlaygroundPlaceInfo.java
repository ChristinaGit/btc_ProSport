package com.btc.prosport.player.core;

import android.net.Uri;
import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.Locale;

@ToString(doNotUseGetters = true)
@Accessors(prefix = "_")
public final class PlaygroundPlaceInfo {
    @Getter
    @Setter
    @Nullable
    private CharSequence _address;

    @Getter
    @Setter
    @Nullable
    private CharSequence _attributions;

    @Getter
    @Setter
    @Nullable
    private LatLng _coordinates;

    @Getter
    @Setter
    @Nullable
    private Locale _locale;

    @Getter
    @Setter
    @Nullable
    private CharSequence _name;

    @Getter
    @Setter
    @Nullable
    private CharSequence _phoneNumber;

    @Getter
    @Setter
    @Nullable
    private String _placeId;

    @Getter
    @Setter
    private int _priceLevel;

    @Getter
    @Setter
    private float _rating;

    @Getter
    @Setter
    @Nullable
    private LatLngBounds _viewport;

    @Getter
    @Setter
    @Nullable
    private Uri _websiteUr;
}
