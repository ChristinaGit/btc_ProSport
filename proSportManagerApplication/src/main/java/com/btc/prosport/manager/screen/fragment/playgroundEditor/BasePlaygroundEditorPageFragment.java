package com.btc.prosport.manager.screen.fragment.playgroundEditor;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.event.Events;
import com.btc.common.event.notice.ManagedNoticeEvent;
import com.btc.common.event.notice.NoticeEvent;
import com.btc.prosport.manager.screen.activity.playgroundEditor.PlaygroundEditorPage;
import com.btc.prosport.manager.screen.fragment.BaseManagerFragment;

import java.util.Objects;

@Accessors(prefix = "_")
public abstract class BasePlaygroundEditorPageFragment extends BaseManagerFragment
    implements PlaygroundEditorPage {
    @NonNull
    @Override
    public final NoticeEvent getChangesStateChangedEvent() {
        return _changesStateChangedEvent;
    }

    @NonNull
    @Override
    public final NoticeEvent getEditingStateChangedEvent() {
        return _editingStateChangedEvent;
    }

    @Override
    public void setPlaygroundId(@Nullable final Long playgroundId) {
        if (!Objects.equals(_playgroundId, playgroundId)) {
            _playgroundId = playgroundId;

            onPlaygroundIdChanged();
        }
    }

    @Override
    public final boolean hasUncommittedChanges() {
        return _hasUncommittedChanges;
    }

    public final void setInEditingState(final boolean inEditingState) {
        if (_inEditingState != inEditingState) {
            _inEditingState = inEditingState;

            _editingStateChangedEvent.rise();
        }
    }

    @CallSuper
    protected void onPlaygroundIdChanged() {
    }

    protected void setHasUncommittedChanges(final boolean hasUncommittedChanges) {
        if (_hasUncommittedChanges != hasUncommittedChanges) {
            _hasUncommittedChanges = hasUncommittedChanges;

            _changesStateChangedEvent.rise();
        }
    }

    @NonNull
    private final ManagedNoticeEvent _changesStateChangedEvent = Events.createNoticeEvent();

    @NonNull
    private final ManagedNoticeEvent _editingStateChangedEvent = Events.createNoticeEvent();

    private boolean _hasUncommittedChanges;

    @Getter(onMethod = @__(@Override))
    private boolean _inEditingState;

    @Getter(onMethod = @__(@Override))
    @Nullable
    private Long _playgroundId;
}
