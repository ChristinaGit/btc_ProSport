package com.btc.prosport.player.screen;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.btc.common.event.notice.NoticeEvent;
import com.btc.common.mvp.screen.Screen;
import com.btc.prosport.api.model.User;

public interface PlayerAccountScreen extends Screen {
    void displayNoAccountsError();

    void displayPlayer(@Nullable User player);

    void displayPlayerLoading();

    void displayPlayerLoadingError();

    @NonNull
    NoticeEvent getPlayerLoginEvent();

    @NonNull
    NoticeEvent getViewPlayerEvent();
}
