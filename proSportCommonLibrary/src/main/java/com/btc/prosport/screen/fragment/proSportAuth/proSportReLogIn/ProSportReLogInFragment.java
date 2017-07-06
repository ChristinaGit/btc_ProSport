package com.btc.prosport.screen.fragment.proSportAuth.proSportReLogIn;

import android.accounts.AccountManager;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.contract.Contracts;
import com.btc.common.event.Events;
import com.btc.common.event.generic.Event;
import com.btc.common.event.generic.ManagedEvent;
import com.btc.common.extension.delegate.loading.LoadingViewDelegate;
import com.btc.common.extension.delegate.loading.ProgressVisibilityHandler;
import com.btc.common.extension.view.ContentLoaderProgressBar;
import com.btc.common.mvp.presenter.Presenter;
import com.btc.common.utility.ImeUtils;
import com.btc.common.utility.listener.SimpleTextWatcher;
import com.btc.prosport.common.R;
import com.btc.prosport.core.manager.auth.AuthResult;
import com.btc.prosport.di.qualifier.PresenterNames;
import com.btc.prosport.screen.ProSportReLogInScreen;
import com.btc.prosport.screen.fragment.proSportAuth.ProSportAuthFragment;

import javax.inject.Inject;
import javax.inject.Named;

// TODO: 16.02.2017 Need to fix showing keyboard after request focus.
@Accessors(prefix = "_")
public class ProSportReLogInFragment extends ProSportAuthFragment implements ProSportReLogInScreen {
    private static final String _KEY_PHONE_NUMBER = "phoneNumber";

    public static void startView(
        @NonNull final FragmentManager fragmentManager,
        final int containerId,
        @Nullable final String phoneNumber) {
        Contracts.requireNonNull(fragmentManager, "fragmentManager == null");

        val fragment = new ProSportReLogInFragment();
        if (phoneNumber != null) {
            val bundle = new Bundle(1);
            bundle.putString(_KEY_PHONE_NUMBER, phoneNumber);
            fragment.setArguments(bundle);
        }
        fragmentManager.beginTransaction().replace(containerId, fragment).commit();
    }

    @CallSuper
    @Override
    public void bindViews(@NonNull final View source) {
        super.bindViews(source);

        _passwordInputView = (TextInputEditText) source.findViewById(R.id.prosport_password_input);

        _passwordLayout =
            (TextInputLayout) source.findViewById(R.id.prosport_password_input_layout);

        _reLogInButton = (Button) source.findViewById(R.id.prosport_re_log_in_button);
        _reLogInContent = (ConstraintLayout) source.findViewById(R.id.prosport_re_log_in_content);
        _reLogInProgressBar =
            (ContentLoaderProgressBar) source.findViewById(R.id.prosport_re_log_in_progressbar);

        if (_passwordInputView != null) {
            _passwordInputView.setOnEditorActionListener(_passwordActionListener);
            _passwordInputView.addTextChangedListener(_passwordTextWatcher);
        }

        if (_reLogInButton != null) {
            _reLogInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    performReLogIn();
                }
            });
        }

        _loadingDelegate = LoadingViewDelegate
            .builder()
            .setContentView(_reLogInContent)
            .setLoadingView(_reLogInProgressBar)
            .setLoadingVisibilityHandler(new ProgressVisibilityHandler())
            .build();

        _loadingDelegate.showContent();
    }

    @Override
    public void unbindViews() {
        super.unbindViews();

        if (_passwordInputView != null) {
            _passwordInputView.removeTextChangedListener(_passwordTextWatcher);
        }
    }

    @CallSuper
    @Nullable
    @Override
    public View onCreateView(
        final LayoutInflater inflater,
        @Nullable final ViewGroup container,
        @Nullable final Bundle savedInstanceState) {
        super.onCreateView(Contracts.requireNonNull(inflater, "inflater == null"),
                           container,
                           savedInstanceState);

        val arguments = getArguments();
        if (arguments != null) {
            _phoneNumber = arguments.getString(_KEY_PHONE_NUMBER);
        }

        setTitle(_phoneNumber);

        val view = inflater.inflate(R.layout.fragment_re_log_in, container, false);

        bindViews(view);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unbindViews();
    }

    @CallSuper
    @Override
    protected void onReleaseInjectedMembers() {
        super.onReleaseInjectedMembers();

        val presenter = getPresenter();
        if (presenter != null) {
            presenter.unbindScreen();
        }
    }

    @Override
    public void displayContent() {
        val loadingDelegate = getLoadingDelegate();
        if (loadingDelegate != null) {
            loadingDelegate.showContent();
        }
    }

    @Override
    public void displayLoading() {
        val loadingDelegate = getLoadingDelegate();
        if (loadingDelegate != null) {
            loadingDelegate.showLoading();
        }
    }

    @Override
    public void displayPasswordError(@StringRes final int errorMessage) {
        displayPasswordError(getString(errorMessage));
    }

    @Override
    public void displayPasswordError(@Nullable final String errorMessage) {
        if (_passwordLayout != null) {
            _passwordLayout.setError(errorMessage);
            _passwordLayout.requestFocus();
        }
    }

    @CallSuper
    @Override
    public void displaySuccessfulAuth(@NonNull final AuthResult authResult) {
        Contracts.requireNonNull(authResult, "authResult == null");

        setAccountAuthenticatorResult(authResult);
        finishAuth();
    }

    @NonNull
    @Override
    public Event<ReLogInEventArgs> getReLogInEvent() {
        return _reLogInEvent;
    }

    @CallSuper
    @Override
    public void onResume() {
        super.onResume();
        if (_passwordInputView != null) {
            _passwordInputView.requestFocus();
        }
    }

    @NonNull
    @Override
    protected Bundle createResult(
        @NonNull final AuthResult authResult) {

        val result = new Bundle(1);
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, true);

        return result;
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

    @CallSuper
    protected void performReLogIn() {
        if (_phoneNumber != null && _passwordInputView != null) {
            val password = _passwordInputView.getText().toString();
            ImeUtils.hideIme(_passwordInputView);
            _reLogInEvent.rise(new ReLogInEventArgs(_phoneNumber, password));
        }
    }

    @Named(PresenterNames.RE_LOG_IN)
    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Nullable
    /*package-private*/ Presenter<ProSportReLogInScreen> _presenter;

    @NonNull
    private final ManagedEvent<ReLogInEventArgs> _reLogInEvent = Events.createEvent();

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private LoadingViewDelegate _loadingDelegate;

    @Nullable
    private TextInputEditText _passwordInputView;

    @Nullable
    private TextInputLayout _passwordLayout;

    @NonNull
    private final SimpleTextWatcher _passwordTextWatcher = new SimpleTextWatcher() {
        @Override
        public void onTextChanged(
            final CharSequence s, final int start, final int before, final int count) {
            super.onTextChanged(s, start, before, count);

            if (_passwordLayout != null) {
                _passwordLayout.setError(null);
            }
        }
    };

    @Nullable
    private String _phoneNumber;

    @NonNull
    private final TextView.OnEditorActionListener _passwordActionListener =
        new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(
                final TextView textView, final int id, final KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE) {
                    performReLogIn();
                    return true;
                }
                return false;
            }
        };

    @Nullable
    private Button _reLogInButton;

    @Nullable
    private ConstraintLayout _reLogInContent;

    @Nullable
    private ContentLoaderProgressBar _reLogInProgressBar;
}
