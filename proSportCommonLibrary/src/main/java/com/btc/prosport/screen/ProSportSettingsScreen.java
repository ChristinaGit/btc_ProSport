package com.btc.prosport.screen;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.btc.common.event.generic.Event;
import com.btc.common.event.notice.NoticeEvent;
import com.btc.common.mvp.screen.Screen;
import com.btc.prosport.api.model.City;
import com.btc.prosport.api.model.User;
import com.btc.prosport.core.eventArgs.ChangeUserCityEventArgs;
import com.btc.prosport.core.eventArgs.ChangeUserEmailEventArgs;
import com.btc.prosport.core.eventArgs.ChangeUserFirstNameEventArgs;
import com.btc.prosport.core.eventArgs.ChangeUserLastNameEventArgs;
import com.btc.prosport.core.eventArgs.ChangeUserPhoneNumberEventArgs;
import com.btc.prosport.core.eventArgs.LogoutEventArgs;

import java.util.List;

public interface ProSportSettingsScreen extends Screen {
    void disableLoading();

    void displayCities(@Nullable List<? extends City> cities);

    void displayLoading();

    void displayPlayer(@Nullable User user);

    void displayRemoveAccountResult(boolean success);

    void displayRequireAuthorizationError();

    @NonNull
    Event<ChangeUserCityEventArgs> getChangeCityEvent();

    @NonNull
    Event<ChangeUserEmailEventArgs> getChangeEmailEvent();

    @NonNull
    Event<ChangeUserFirstNameEventArgs> getChangeFirstNameEvent();

    @NonNull
    Event<ChangeUserLastNameEventArgs> getChangeLastNameEvent();

    @NonNull
    Event<ChangeUserPhoneNumberEventArgs> getChangePhoneNumberEvent();

    @NonNull
    Event<LogoutEventArgs> getLogoutEvent();

    @NonNull
    NoticeEvent getViewCitiesEvent();

    @NonNull
    NoticeEvent getViewUserEvent();
}
