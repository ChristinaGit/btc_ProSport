package com.btc.prosport.player.screen;

import android.support.annotation.NonNull;

import com.btc.common.event.notice.NoticeEvent;

public interface FeedbackScreen extends PlayerNavigationScreen {

    @NonNull
    NoticeEvent getNavigateToHomeEvent();
}
