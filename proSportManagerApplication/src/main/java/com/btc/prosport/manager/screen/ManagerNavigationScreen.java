package com.btc.prosport.manager.screen;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.btc.common.event.generic.Event;
import com.btc.common.event.notice.NoticeEvent;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.prosport.api.model.PlaygroundReport;
import com.btc.prosport.api.model.SportComplexReport;

import java.util.List;

public interface ManagerNavigationScreen extends ManagerAccountScreen {
    void displayChangedPlayground(@NonNull PlaygroundReport playground);

    void displaySportComplexes(@Nullable List<SportComplexReport> sportComplexes);

    void displaySportComplexesLoading();

    void displaySportComplexesLoadingError();

    @NonNull
    NoticeEvent getChangeManagerAvatarEvent();

    @NonNull
    NoticeEvent getCreatePlaygroundEvent();

    @NonNull
    Event<IdEventArgs> getEditPlaygroundPricesEvent();

    @NonNull
    NoticeEvent getViewFeedbackEvent();

    @NonNull
    NoticeEvent getViewHomeScreenEvent();

    @NonNull
    NoticeEvent getViewManagerOrdersEvent();

    @NonNull
    Event<IdEventArgs> getViewManagerPlaygroundEvent();

    @NonNull
    NoticeEvent getViewManagerPricesEvent();

    @NonNull
    NoticeEvent getViewManagerSettingsEvent();

    @NonNull
    NoticeEvent getViewManagerSportComplexesEvent();
}
