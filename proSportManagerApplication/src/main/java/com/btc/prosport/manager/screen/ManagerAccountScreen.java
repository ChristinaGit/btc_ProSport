package com.btc.prosport.manager.screen;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.btc.common.event.notice.NoticeEvent;
import com.btc.common.mvp.screen.Screen;
import com.btc.prosport.api.model.User;

public interface ManagerAccountScreen extends Screen {
    void displayManager(@Nullable User manager);

    void displayManagerLoading();

    void displayManagerLoadingError();

    void displayNoAccountsError();

    @NonNull
    NoticeEvent getViewManagerEvent();
}
