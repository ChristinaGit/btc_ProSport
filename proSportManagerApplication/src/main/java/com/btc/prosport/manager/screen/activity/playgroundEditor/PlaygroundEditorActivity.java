package com.btc.prosport.manager.screen.activity.playgroundEditor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;

import butterknife.BindView;

import rx.functions.Action1;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.message.MessageManager;
import com.btc.common.control.manager.message.UserActionReaction;
import com.btc.common.event.notice.NoticeEventHandler;
import com.btc.common.mvp.presenter.Presenter;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.di.qualifier.PresenterNames;
import com.btc.prosport.manager.screen.PlaygroundEditorScreen;
import com.btc.prosport.manager.screen.activity.BaseManagerActivity;
import com.btc.prosport.manager.screen.fragment.priceEditor.PriceEditorFragment;

import org.parceler.Parcels;

import javax.inject.Inject;
import javax.inject.Named;

@Accessors(prefix = "_")
public class PlaygroundEditorActivity extends BaseManagerActivity
    implements PlaygroundEditorScreen {
    private static final String _LOG_TAG = ConstantBuilder.logTag(PlaygroundEditorActivity.class);

    private static final String _KEY_SAVED_STATE =
        ConstantBuilder.savedStateKey(PlaygroundEditorActivity.class, "saved_state");

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.playground_editor, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        super.onPrepareOptionsMenu(menu);

        final val activeEditorPage = getActiveEditorPage();
        final val canCommitChanges =
            activeEditorPage != null && !activeEditorPage.isInEditingState() &&
            activeEditorPage.hasUncommittedChanges();

        menu.findItem(R.id.action_commit_changes).setVisible(canCommitChanges);

        return true;
    }

    @CallSuper
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final boolean consumed;

        final val id = item.getItemId();
        switch (id) {
            case R.id.action_commit_changes: {
                final val activeEditorPage = getActiveEditorPage();
                if (activeEditorPage != null) {
                    activeEditorPage.commitChanges();
                }

                consumed = true;
                break;
            }
            case android.R.id.home: {
                final val activeEditorPage = getActiveEditorPage();
                if (activeEditorPage != null) {
                    if (activeEditorPage.hasUncommittedChanges()) {
                        final val messageManager = getMessageManager();
                        if (messageManager != null) {
                            messageManager
                                .showModalActionMessage(R.string
                                                            .playground_editor_message_has_uncommitted_changes)
                                .subscribe(new Action1<UserActionReaction>() {
                                    @Override
                                    public void call(final UserActionReaction reaction) {
                                        Contracts.requireMainThread();

                                        if (reaction == UserActionReaction.PERFORM) {
                                            activeEditorPage.commitChanges();
                                        }
                                    }
                                }, new Action1<Throwable>() {
                                    @Override
                                    public void call(final Throwable error) {
                                        Contracts.requireMainThread();

                                        Log.w(_LOG_TAG, error);
                                    }
                                });
                        } else {
                            onBackPressed();
                        }
                    } else {
                        onBackPressed();
                    }
                } else {
                    onBackPressed();
                }

                consumed = true;
                break;
            }
            default: {
                consumed = super.onOptionsItemSelected(item);

                break;
            }
        }

        return consumed;
    }

    @Nullable
    @CallSuper
    public PlaygroundEditorSavedState onHandleEditIntent(@NonNull final Intent intent) {
        Contracts.requireNonNull(intent, "intent == null");

        final PlaygroundEditorSavedState state;

        final val playgroundId = PlaygroundEditorIntent.getPlaygroundId(intent);
        if (playgroundId != null) {
            state = new PlaygroundEditorSavedState();

            state.setPlaygroundId(playgroundId);
        } else {
            state = null;
        }

        return state;
    }

    @Nullable
    protected PlaygroundEditorPage getActiveEditorPage() {
        return _activeEditorFragment instanceof PlaygroundEditorPage
               ? (PlaygroundEditorPage) _activeEditorFragment
               : null;
    }

    protected void invalidateActionBar() {
        final val activeEditorPage = getActiveEditorPage();
        if (activeEditorPage != null) {
            setTitle(activeEditorPage.getLabelId());
        }

        supportInvalidateOptionsMenu();
    }

    @CallSuper
    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            _state = onHandleSavedState(savedInstanceState);
        }

        if (_state == null) {
            _state = onHandleIntent(getIntent());
        }

        if (_state != null) {
            setContentView(R.layout.activity_playground_editor);
            bindViews();

            if (_toolbar != null) {
                setSupportActionBar(_toolbar);
            }

            final val actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayShowTitleEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }

            setPlaygroundId(_state.getPlaygroundId());

            showEditorFragment(new PriceEditorFragment());
        } else {
            finish();
        }
    }

    @Nullable
    @CallSuper
    protected PlaygroundEditorSavedState onHandleInsertIntent(@Nullable final Intent intent) {
        Contracts.requireNonNull(intent, "intent == null");

        return new PlaygroundEditorSavedState();
    }

    @Nullable
    @CallSuper
    protected PlaygroundEditorSavedState onHandleIntent(@NonNull final Intent intent) {
        Contracts.requireNonNull(intent, "intent == null");

        final PlaygroundEditorSavedState state;

        final val action = getIntent().getAction();
        if (Intent.ACTION_EDIT.equals(action)) {
            state = onHandleEditIntent(intent);
        } else if (Intent.ACTION_INSERT.equals(action)) {
            state = onHandleInsertIntent(intent);
        } else {
            state = null;
        }

        return state;
    }

    @Nullable
    @CallSuper
    protected PlaygroundEditorSavedState onHandleSavedState(
        @NonNull final Bundle savedInstanceState) {
        PlaygroundEditorSavedState state = null;

        if (savedInstanceState.containsKey(_KEY_SAVED_STATE)) {
            state = Parcels.unwrap(savedInstanceState.getParcelable(_KEY_SAVED_STATE));
        }

        return state;
    }

    @Override
    protected void onInjectMembers() {
        super.onInjectMembers();

        getManagerScreenComponent().inject(this);
        final val presenter = getPresenter();
        if (presenter != null) {
            presenter.bindScreen(this);
        }
    }

    @CallSuper
    @Override
    protected void onReleaseInjectedMembers() {
        super.onReleaseInjectedMembers();

        final val presenter = getPresenter();
        if (presenter != null) {
            presenter.unbindScreen();
        }
    }

    @Getter(AccessLevel.PROTECTED)
    @Inject
    @Nullable
    /*package-private*/ MessageManager _messageManager;

    @Named(PresenterNames.PLAYGROUND_EDITOR)
    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Nullable
    /*package-private*/ Presenter<PlaygroundEditorScreen> _presenter;

    @Nullable
    @BindView(R.id.toolbar)
    /*package-private*/ Toolbar _toolbar;

    @Nullable
    private Fragment _activeEditorFragment;

    @NonNull
    private final NoticeEventHandler _changesStateChangedHandler = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            invalidateActionBar();
        }
    };

    @NonNull
    private final NoticeEventHandler _editingStateChangedHandler = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            invalidateActionBar();
        }
    };

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private Long _playgroundId;

    @Nullable
    private PlaygroundEditorSavedState _state;

    private void closeEditorFragment() {
        if (_activeEditorFragment != null) {
            if (_activeEditorFragment instanceof PlaygroundEditorPage) {
                final val editorPage = (PlaygroundEditorPage) _activeEditorFragment;

                editorPage.getEditingStateChangedEvent().removeHandler(_editingStateChangedHandler);
                editorPage.getChangesStateChangedEvent().removeHandler(_changesStateChangedHandler);
            }

            getSupportFragmentManager().beginTransaction().remove(_activeEditorFragment).commit();

            invalidateActionBar();
        }
    }

    private void showEditorFragment(@NonNull final Fragment editorFragment) {
        Contracts.requireNonNull(editorFragment, "editorFragment == null");

        closeEditorFragment();

        if (editorFragment instanceof PlaygroundEditorPage) {
            final val editorPage = (PlaygroundEditorPage) editorFragment;
            editorPage.setPlaygroundId(getPlaygroundId());

            editorPage.getEditingStateChangedEvent().addHandler(_editingStateChangedHandler);
            editorPage.getChangesStateChangedEvent().addHandler(_changesStateChangedHandler);
        }
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.content_container, editorFragment)
            .commit();

        _activeEditorFragment = editorFragment;

        invalidateActionBar();
    }
}
