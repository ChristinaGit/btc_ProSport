package com.btc.prosport.screen;

import android.support.annotation.NonNull;

import com.btc.common.event.generic.Event;
import com.btc.common.mvp.screen.Screen;
import com.btc.prosport.core.eventArgs.SendMessageEventArgs;

public interface FeedbackViewerScreen extends Screen {

    void disableLoading();

    void displayLoading();

    void displaySuccessMessageSentView();

    @NonNull
    Event<SendMessageEventArgs> getSendMessageEvent();
}
