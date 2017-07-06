package com.btc.prosport.player.screen;

import android.support.annotation.NonNull;

import com.btc.common.event.generic.Event;
import com.btc.common.event.notice.NoticeEvent;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.prosport.player.core.eventArgs.SelectSearchTimeEventArgs;

public interface SportComplexesViewerScreen extends PlayerNavigationScreen {
    void displaySelectedSportComplexesSearchDateParam(
        final int year, final int month, final int dayOfMonth);

    void displaySelectedSportComplexesEndTimeSearchParam(int hourOfDay, int minute);

    void displaySelectedSportComplexesStartTimeSearchParam(int hourOfDay, int minute);

    @NonNull
    NoticeEvent getSelectSportComplexesSearchDateEvent();

    @NonNull
    Event<SelectSearchTimeEventArgs> getSelectSportComplexesSearchTimeEvent();

    @NonNull
    Event<IdEventArgs> getViewSportComplexDetailsEvent();
}
