package com.btc.prosport.core.manager.navigation;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.Observable;

import com.btc.prosport.core.result.PhotoResult;

public interface ProSportNavigationManager {
    void navigateToAdditionalInfo();

    boolean navigateToApplicationSettings();

    @NonNull
    Observable<PhotoResult> navigateToImageCapture(@NonNull final Uri path);

    @NonNull
    Observable<PhotoResult> navigateToImagePicker();

    void navigateToLogIn(@Nullable String phoneNumber, final boolean allowRegistration);

    void navigateToReLogIn(@Nullable String phoneNumber);

    void navigateToSignUp(@Nullable String phoneNumber);
}
