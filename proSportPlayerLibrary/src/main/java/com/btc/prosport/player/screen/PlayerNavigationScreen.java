package com.btc.prosport.player.screen;

import android.support.annotation.NonNull;

import com.btc.common.event.notice.NoticeEvent;

public interface PlayerNavigationScreen extends PlayerAccountScreen {

    @NonNull
    NoticeEvent getChangePlayerAvatarEvent();

    @NonNull
    NoticeEvent getViewHomeScreenEvent();

    @NonNull
    NoticeEvent getViewPlayerOrdersEvent();

    @NonNull
    NoticeEvent getViewPlayerSettingsEvent();

    @NonNull
    NoticeEvent getViewFeedbackEvent();
}
