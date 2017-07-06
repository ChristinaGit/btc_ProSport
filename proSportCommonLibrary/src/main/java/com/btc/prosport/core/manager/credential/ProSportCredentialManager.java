package com.btc.prosport.core.manager.credential;

import android.content.pm.ApplicationInfo;
import android.support.annotation.NonNull;

import lombok.val;

import com.btc.common.contract.Contracts;

public class ProSportCredentialManager implements CredentialManager {
    private static final String _METADATA_NAME_CLIENT_ID = "com.btc.prosport.CLIENT_ID";

    private static final String _METADATA_NAME_CLIENT_SECRET = "com.btc.prosport.CLIENT_SECRET";

    public ProSportCredentialManager(@NonNull final ApplicationInfo applicationInfo) {
        Contracts.requireNonNull(applicationInfo, "applicationInfo == null");

        _applicationInfo = applicationInfo;
    }

    @NonNull
    @Override
    public String getClientId() {
        return getMetadata(_METADATA_NAME_CLIENT_ID);
    }

    @NonNull
    @Override
    public String getClientSecret() {
        return getMetadata(_METADATA_NAME_CLIENT_SECRET);
    }

    @NonNull
    private final ApplicationInfo _applicationInfo;

    @NonNull
    private String getMetadata(final String metadataName) {
        final String value;
        val metaData = _applicationInfo.metaData;
        if (metaData != null && metaData.containsKey(metadataName)) {
            value = metaData.getString(metadataName);

            Contracts.requireNonNull(value, metadataName + " metadata value == null");
        } else {
            throw new RuntimeException("Need to add " + metadataName + " in manifest");
        }
        return value;
    }
}
