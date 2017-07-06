package com.btc.prosport.screen.fragment.proSportAuth.proSportLogIn;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
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
import com.btc.prosport.screen.ProSportLogInScreen;
import com.btc.prosport.screen.fragment.proSportAuth.ProSportAuthFragment;

import javax.inject.Inject;
import javax.inject.Named;

// TODO: 16.02.2017 Need to fix showing keyboard after request focus.
@Accessors(prefix = "_")
public class ProSportLogInFragment extends ProSportAuthFragment implements ProSportLogInScreen {
    private static final String _KEY_PHONE_NUMBER = "phoneNumber";

    private static final String _KEY_ALLOW_REGISTRATION = "allowRegistration";

    public static void startView(
        @NonNull final FragmentManager fragmentManager,
        final int containerId,
        @Nullable final String phoneNumber,
        final boolean allowRegistration) {
        Contracts.requireNonNull(fragmentManager, "fragmentManager == null");

        val fragment = new ProSportLogInFragment();
        val bundle = new Bundle();

        bundle.putBoolean(_KEY_ALLOW_REGISTRATION, allowRegistration);
        if (phoneNumber != null) {
            bundle.putString(_KEY_PHONE_NUMBER, phoneNumber);
        }
        fragment.setArguments(bundle);

        fragmentManager.beginTransaction().replace(containerId, fragment).commit();
    }

    @CallSuper
    @Override
    public void bindViews(@NonNull final View source) {
        super.bindViews(source);

        _phoneInputView = (TextInputEditText) source.findViewById(R.id.prosport_phone_input);
        _passwordInputView = (TextInputEditText) source.findViewById(R.id.prosport_password_input);

        _phoneLayout = (TextInputLayout) source.findViewById(R.id.prosport_phone_input_layout);
        _passwordLayout =
            (TextInputLayout) source.findViewById(R.id.prosport_password_input_layout);

        _logInButton = (Button) source.findViewById(R.id.prosport_log_in_button);
        _signUpButton = (Button) source.findViewById(R.id.prosport_sign_up_button);
        _logInContent = source.findViewById(R.id.prosport_log_in_content);
        _logInProgressBar =
            (ContentLoaderProgressBar) source.findViewById(R.id.prosport_log_in_progressbar);

        if (_phoneInputView != null) {
            _phoneInputView.addTextChangedListener(_phoneTextWatcher);
        }

        if (_passwordInputView != null) {
            _passwordInputView.setOnEditorActionListener(_passwordActionListener);
            _passwordInputView.addTextChangedListener(_passwordTextWatcher);
        }

        if (_logInButton != null) {
            _logInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    performLogIn();
                }
            });
        }

        if (_signUpButton != null) {
            if (_allowRegistration) {
                _signUpButton.setVisibility(View.VISIBLE);
                _signUpButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        performSignUp();
                    }
                });
            } else {
                _signUpButton.setVisibility(View.GONE);
            }
        }

        _loadingDelegate = LoadingViewDelegate
            .builder()
            .setContentView(_logInContent)
            .setLoadingView(_logInProgressBar)
            .setLoadingVisibilityHandler(new ProgressVisibilityHandler())
            .build();

        _loadingDelegate.showContent();

        if (!TextUtils.isEmpty(_phoneNumber) && _phoneInputView != null) {
            _phoneInputView.setText(_phoneNumber);
        }
        if (_phoneLayout != null) {
            _phoneLayout.setHintAnimationEnabled(true);
        }
    }

    @Override
    public void unbindViews() {
        super.unbindViews();

        if (_phoneInputView != null) {
            _phoneInputView.removeTextChangedListener(_phoneTextWatcher);
        }
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
            _allowRegistration = arguments.getBoolean(_KEY_ALLOW_REGISTRATION, true);
        }

        val inflate = inflater.inflate(R.layout.fragment_log_in, container, false);

        bindViews(inflate);

        return inflate;
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
    public void displayPasswordError(@StringRes final int errorMessageId) {
        displayPasswordError(getString(errorMessageId));
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

    @Override
    public void displayPhoneError(@StringRes final int errorMessageId) {
        if (_phoneLayout != null) {
            _phoneLayout.setError(getString(errorMessageId));
            _phoneLayout.requestFocus();
        }
    }

    @Override
    public void displayPhoneError(@Nullable final String errorMessage) {

    }

    @NonNull
    @Override
    public Event<LogInEventArgs> getLogInEvent() {
        return _logInEvent;
    }

    @NonNull
    @Override
    public Event<SignUpEventArgs> getSignUpEvent() {
        return _signUpEvent;
    }

    @CallSuper
    @Override
    public void onResume() {
        super.onResume();

        if (_phoneInputView != null) {
            if (_phoneInputView.getText().length() != 0 && _passwordInputView != null) {
                _passwordInputView.requestFocus();
            } else {
                _phoneInputView.requestFocus();
            }
        }
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
    protected void performLogIn() {
        if (_phoneInputView != null && _passwordInputView != null) {
            val phone = _phoneInputView.getText().toString();
            val password = _passwordInputView.getText().toString();
            ImeUtils.hideIme(_phoneInputView);
            _logInEvent.rise(new LogInEventArgs(phone, password));
        }
    }

    @CallSuper
    protected void performSignUp() {
        if (_phoneInputView != null) {
            _signUpEvent.rise(new SignUpEventArgs(_phoneInputView.getText().toString()));
        }
    }

    @Named(PresenterNames.LOG_IN)
    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Nullable
    /*package-private*/ Presenter<ProSportLogInScreen> _presenter;

    @NonNull
    private final ManagedEvent<LogInEventArgs> _logInEvent = Events.createEvent();

    @NonNull
    private final ManagedEvent<SignUpEventArgs> _signUpEvent = Events.createEvent();

    private boolean _allowRegistration = true;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private LoadingViewDelegate _loadingDelegate;

    @Nullable
    private Button _logInButton;

    @Nullable
    private View _logInContent;

    @Nullable
    private ContentLoaderProgressBar _logInProgressBar;

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
    private TextInputEditText _phoneInputView;

    @NonNull
    private final TextView.OnEditorActionListener _passwordActionListener =
        new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(
                final TextView textView, final int id, final KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE) {
                    performLogIn();
                    return true;
                }
                return false;
            }
        };

    @Nullable
    private TextInputLayout _phoneLayout;

    @NonNull
    private final SimpleTextWatcher _phoneTextWatcher = new SimpleTextWatcher() {
        @Override
        public void onTextChanged(
            final CharSequence s, final int start, final int before, final int count) {
            super.onTextChanged(s, start, before, count);

            if (_phoneLayout != null) {
                _phoneLayout.setError(null);
            }
        }
    };

    @Nullable
    private String _phoneNumber;

    @Nullable
    private Button _signUpButton;
}
