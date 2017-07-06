package com.btc.prosport.player.screen.activity.feedback;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.contract.Contracts;
import com.btc.common.event.Events;
import com.btc.common.event.generic.EventHandler;
import com.btc.common.event.notice.ManagedNoticeEvent;
import com.btc.common.event.notice.NoticeEvent;
import com.btc.common.event.notice.NoticeEventHandler;
import com.btc.common.mvp.presenter.Presenter;
import com.btc.prosport.api.model.User;
import com.btc.prosport.core.eventArgs.SendMessageEventArgs;
import com.btc.prosport.di.qualifier.PresenterNames;
import com.btc.prosport.player.BuildConfig;
import com.btc.prosport.player.R;
import com.btc.prosport.player.screen.FeedbackScreen;
import com.btc.prosport.player.screen.activity.BasePlayerNavigationActivity;
import com.btc.prosport.screen.fragment.feedback.ProSportFeedbackFragment;

import javax.inject.Inject;
import javax.inject.Named;

@Accessors(prefix = "_")
public class FeedbackActivity extends BasePlayerNavigationActivity implements FeedbackScreen {
    @NonNull
    public static Intent getIntent(@NonNull final Context context) {
        Contracts.requireNonNull(context, "context == null");

        return new Intent(context, FeedbackActivity.class);
    }

    @CallSuper
    @Override
    public void bindViews() {
        super.bindViews();

        _toolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    @Override
    protected void onActivityResult(
        final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ProSportFeedbackFragment.REQUEST_CODE) {
            finish();
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

    @Override
    public void displayPlayer(@Nullable final User player) {
        super.displayPlayer(player);

        final val feedbackFragment = getFeedbackFragment();
        if (feedbackFragment != null) {
            feedbackFragment.setUser(player);
        }
    }

    @NonNull
    @Override
    public NoticeEvent getNavigateToHomeEvent() {
        return _navigateToHomeEvent;
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_feedback);

        bindViews();

        if (_toolbar != null) {
            setSupportActionBar(_toolbar);
        }

        final val actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
        }

        final val fragment = getFeedbackFragment();
        if (fragment != null) {
            fragment.getNavigateToHomeEvent().addHandler(_navigateToHomeHandler);
            fragment.getSendMessageEvent().addHandler(_sendMessageHandler);
        }
    }

    @CallSuper
    @Override
    protected void onDestroy() {
        super.onDestroy();

        final val fragment = getFeedbackFragment();
        if (fragment != null) {
            fragment.getNavigateToHomeEvent().removeHandler(_navigateToHomeHandler);
        }
    }

    @CallSuper
    @Override
    protected void onInjectMembers() {
        super.onInjectMembers();

        getPlayerScreenComponent().inject(this);

        final val presenter = getPresenter();
        if (presenter != null) {
            presenter.bindScreen(this);
        }
    }

    @Named(PresenterNames.FEEDBACK)
    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Nullable
    /*package-private*/ Presenter<FeedbackScreen> _presenter;

    @NonNull
    private final ManagedNoticeEvent _navigateToHomeEvent = Events.createNoticeEvent();

    @NonNull
    private final NoticeEventHandler _navigateToHomeHandler = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            _navigateToHomeEvent.rise();
        }
    };

    @NonNull
    private final EventHandler<SendMessageEventArgs> _sendMessageHandler =
        new EventHandler<SendMessageEventArgs>() {
            @Override
            public void onEvent(@NonNull final SendMessageEventArgs eventArgs) {
                final val fragment = getFeedbackFragment();
                if (fragment != null) {
                    fragment.displayLoading();
                }
                performSendMessage(eventArgs.getMessage());
            }
        };

    @Nullable
    private Toolbar _toolbar;

    @Nullable
    private ProSportFeedbackFragment getFeedbackFragment() {
        return (ProSportFeedbackFragment) getSupportFragmentManager().findFragmentById(R.id.feedback);
    }

    private void performSendMessage(final String message) {
        final val intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL,
                        new String[]{getString(R.string.feedback_receiver_email)});
        intent.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.application_player_label) + " " +
                        BuildConfig.VERSION_NAME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        startActivityForResult(
            Intent.createChooser(intent, getString(R.string.feedback_email_sender_chooser_label)),
            ProSportFeedbackFragment.REQUEST_CODE);
    }
}
