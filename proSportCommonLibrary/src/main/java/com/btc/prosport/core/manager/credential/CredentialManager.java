package com.btc.prosport.core.manager.credential;

import android.support.annotation.NonNull;

public interface CredentialManager {
    @NonNull
    String getClientId();

    @NonNull
    String getClientSecret();
}
