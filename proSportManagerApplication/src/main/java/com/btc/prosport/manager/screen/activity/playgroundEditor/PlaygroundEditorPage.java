package com.btc.prosport.manager.screen.activity.playgroundEditor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.btc.common.event.notice.NoticeEvent;

public interface PlaygroundEditorPage {
    void commitChanges();

    @NonNull
    NoticeEvent getChangesStateChangedEvent();

    @NonNull
    NoticeEvent getEditingStateChangedEvent();

    @StringRes
    int getLabelId();

    @Nullable
    Long getPlaygroundId();

    void setPlaygroundId(@Nullable final Long playgroundId);

    boolean hasUncommittedChanges();

    boolean isInEditingState();
}
