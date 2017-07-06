package com.btc.prosport.screen.fragment.feedback;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.event.Events;
import com.btc.common.event.generic.Event;
import com.btc.common.event.generic.ManagedEvent;
import com.btc.common.event.notice.ManagedNoticeEvent;
import com.btc.common.event.notice.NoticeEvent;
import com.btc.common.extension.delegate.loading.LoadingViewDelegate;
import com.btc.common.extension.delegate.loading.ProgressVisibilityHandler;
import com.btc.common.mvp.presenter.Presenter;
import com.btc.common.utility.listener.SimpleTextWatcher;
import com.btc.prosport.api.model.User;
import com.btc.prosport.common.R;
import com.btc.prosport.core.eventArgs.SendMessageEventArgs;
import com.btc.prosport.core.utility.UserUtils;
import com.btc.prosport.di.qualifier.PresenterNames;
import com.btc.prosport.screen.FeedbackViewerScreen;
import com.btc.prosport.screen.fragment.BaseProSportFragment;

import javax.inject.Inject;
import javax.inject.Named;

@Accessors(prefix = "_")
public class ProSportFeedbackFragment extends BaseProSportFragment implements FeedbackViewerScreen {

    public static final int REQUEST_CODE = 994;

    @NonNull
    public final NoticeEvent getNavigateToHomeEvent() {
        return _navigateToHomeEvent;
    }

    @Override
    public void bindViews(@NonNull final View source) {
        super.bindViews(source);
        _sendMessageButton = (Button) source.findViewById(R.id.send_message_button);
        _messageView = (TextInputEditText) source.findViewById(R.id.message);
        _emailUserView = (TextInputEditText) source.findViewById(R.id.email_manager);
        _nameUserView = (TextInputEditText) source.findViewById(R.id.name_manager);
        _loadingView = (ProgressBar) source.findViewById(R.id.progress_bar);
        _messageSentSuccessView =
            (ConstraintLayout) source.findViewById(R.id.message_success_sent_view);
        _feedbackContentView = (ConstraintLayout) source.findViewById(R.id.feedback_view);
        _navigateToHomeButton = (Button) source.findViewById(R.id.navigate_to_home_button);
    }

    @Nullable
    @Override
    public View onCreateView(
        final LayoutInflater inflater,
        @Nullable final ViewGroup container,
        @Nullable final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final val view = inflater.inflate(R.layout.fragment_feedback, container, false);

        bindViews(view);

        if (_messageView != null) {
            _messageView.addTextChangedListener(_messageTextWatcher);
        }
        if (_sendMessageButton != null) {
            _sendMessageButton.setOnClickListener(_sendMessageButtonOnClick);
        }
        if (_navigateToHomeButton != null) {
            _navigateToHomeButton.setOnClickListener(_navigateToHomeButtonOnClick);
        }

        _loadingSendMessageDelegate = LoadingViewDelegate
            .builder()
            .setContentView(_feedbackContentView)
            .setLoadingView(_loadingView)
            .setLoadingVisibilityHandler(new ProgressVisibilityHandler())
            .build();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (_messageView != null) {
            _messageView.removeTextChangedListener(_messageTextWatcher);
        }
    }

    @Override
    public void disableLoading() {
        final val loadingDelegate = getLoadingSendMessageDelegate();
        if (loadingDelegate != null) {
            loadingDelegate.showContent();
        }
        if (_messageSentSuccessView != null) {
            _messageSentSuccessView.setVisibility(View.GONE);
        }
        if (_feedbackContentView != null) {
            _feedbackContentView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void displayLoading() {
        final val loadingDelegate = getLoadingSendMessageDelegate();
        if (loadingDelegate != null) {
            loadingDelegate.showLoading();
        }
    }

    @Override
    public void displaySuccessMessageSentView() {
        final val loadingDelegate = getLoadingSendMessageDelegate();
        if (loadingDelegate != null) {
            loadingDelegate.showContent();
        }
        if (_messageSentSuccessView != null) {
            _messageSentSuccessView.setVisibility(View.VISIBLE);
        }
        if (_feedbackContentView != null) {
            _feedbackContentView.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public final Event<SendMessageEventArgs> getSendMessageEvent() {
        return _sendMessageEvent;
    }

    public void setUser(@Nullable final User user) {
        _user = user;
        setupUserInfo();
    }

    @CallSuper
    @Override
    protected void onInjectMembers() {
        super.onInjectMembers();

        getProSportSubscreenComponent().inject(this);
        val presenter = getPresenter();
        if (presenter != null) {
            presenter.bindScreen(this);
        }
    }

    @Named(PresenterNames.FEEDBACK)
    @Getter(AccessLevel.PROTECTED)
    @Nullable
    @Inject
    /*package-private*/ Presenter<FeedbackViewerScreen> _presenter;

    @NonNull
    private final ManagedNoticeEvent _navigateToHomeEvent = Events.createNoticeEvent();

    @NonNull
    private final View.OnClickListener _navigateToHomeButtonOnClick = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            _navigateToHomeEvent.rise();
        }
    };

    @NonNull
    private final ManagedEvent<SendMessageEventArgs> _sendMessageEvent = Events.createEvent();

    @Nullable
    private TextInputEditText _emailUserView;

    @Nullable
    private ConstraintLayout _feedbackContentView;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private LoadingViewDelegate _loadingSendMessageDelegate;

    @Nullable
    private ProgressBar _loadingView;

    @Nullable
    private ConstraintLayout _messageSentSuccessView;

    @Nullable
    private TextInputEditText _messageView;

    @Nullable
    private TextInputEditText _nameUserView;

    @NonNull
    private final View.OnClickListener _sendMessageButtonOnClick = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            performSendMessage();
        }
    };

    @Nullable
    private Button _navigateToHomeButton;

    @Nullable
    private Button _sendMessageButton;

    @NonNull
    private final SimpleTextWatcher _messageTextWatcher = new SimpleTextWatcher() {
        @Override
        public void onTextChanged(
            final CharSequence s, final int start, final int before, final int count) {
            super.onTextChanged(s, start, before, count);
            if (_sendMessageButton != null) {
                if (s != null && s.length() > 0) {
                    if (!_sendMessageButton.isEnabled()) {
                        _sendMessageButton.setEnabled(true);
                    }
                } else {
                    if (_sendMessageButton.isEnabled()) {
                        _sendMessageButton.setEnabled(false);
                    }
                }
            }
        }
    };

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private User _user;

    private void performSendMessage() {
        if (_messageView != null) {
            final String name;
            final String email;
            final String message;

            message = _messageView.getText().toString();

            if (_nameUserView != null) {
                name = _nameUserView.getText().toString();
            } else {
                name = null;
            }
            if (_emailUserView != null) {
                email = _emailUserView.getText().toString();
            } else {
                email = null;
            }
            _sendMessageEvent.rise(new SendMessageEventArgs(email, name, message));
        }
    }

    private void setupUserInfo() {
        final val user = getUser();

        if (_nameUserView != null) {
            _nameUserView.setText(user != null ? UserUtils.getDisplayUserNameOrPhone(user) : null);
        }
        if (_emailUserView != null) {
            _emailUserView.setText(user != null ? user.getEmail() : null);
        }

        final val loadingViewDelegate = getLoadingSendMessageDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showContent();
        }
    }
}
