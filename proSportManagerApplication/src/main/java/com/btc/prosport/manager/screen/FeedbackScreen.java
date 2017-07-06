package com.btc.prosport.manager.screen;

import android.support.annotation.NonNull;

import com.btc.common.event.notice.NoticeEvent;

public interface FeedbackScreen extends ManagerNavigationScreen {

    @NonNull
    NoticeEvent getNavigateToHomeEvent();
}
