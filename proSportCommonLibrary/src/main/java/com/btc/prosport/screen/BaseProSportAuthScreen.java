package com.btc.prosport.screen;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.btc.common.mvp.screen.Screen;
import com.btc.prosport.core.manager.auth.AuthResult;

public interface BaseProSportAuthScreen extends Screen {
    void displayContent();

    void displayLoading();

    void displayPasswordError(@StringRes int errorMessage);

    void displayPasswordError(@Nullable String errorMessage);

    void displaySuccessfulAuth(@NonNull final AuthResult authResult);
}
