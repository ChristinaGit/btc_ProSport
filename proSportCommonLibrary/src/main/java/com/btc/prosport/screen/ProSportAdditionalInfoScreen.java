package com.btc.prosport.screen;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.btc.common.event.generic.Event;
import com.btc.common.mvp.screen.Screen;
import com.btc.prosport.screen.fragment.proSportAuth.proSportAdditionalInfo.AdditionalInfoArgs;

public interface ProSportAdditionalInfoScreen extends Screen {
    void displayContent();

    void displayEmailAddressError(@Nullable String errorMessage);

    void displayFirstNameError(@Nullable String errorMessage);

    void displayLastNameError(@Nullable String errorMessage);

    void displayLoading();

    void displaySuccessfulSendData();

    @NonNull
    Event<AdditionalInfoArgs> getSendAdditionInfoEvent();
}
