package com.btc.prosport.manager.screen;

import android.support.annotation.NonNull;

import com.btc.common.event.generic.Event;
import com.btc.common.event.notice.NoticeEvent;
import com.btc.common.extension.eventArgs.IdEventArgs;

public interface WorkspaceScreen extends ManagerNavigationScreen {
    @NonNull
    Event<IdEventArgs> getCreateOrderEvent();

    @NonNull
    NoticeEvent getCreateSaleEvent();
}
