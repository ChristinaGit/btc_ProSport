package com.btc.prosport.manager.screen.fragment.playgroundEditor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.btc.common.event.notice.NoticeEvent;
import com.btc.prosport.manager.screen.activity.playgroundEditor.PlaygroundEditorPage;
import com.btc.prosport.manager.screen.fragment.BaseManagerFragment;

public class BasePlaygroundEditor extends BaseManagerFragment implements PlaygroundEditorPage {
    @Override
    public void commitChanges() {

    }

    @NonNull
    @Override
    public NoticeEvent getChangesStateChangedEvent() {
        return null;
    }

    @NonNull
    @Override
    public NoticeEvent getEditingStateChangedEvent() {
        return null;
    }

    @Override
    public int getLabelId() {
        return 0;
    }

    @Nullable
    @Override
    public Long getPlaygroundId() {
        return null;
    }

    @Override
    public void setPlaygroundId(@Nullable final Long playgroundId) {

    }

    @Override
    public boolean hasUncommittedChanges() {
        return false;
    }

    @Override
    public boolean isInEditingState() {
        return false;
    }
}
