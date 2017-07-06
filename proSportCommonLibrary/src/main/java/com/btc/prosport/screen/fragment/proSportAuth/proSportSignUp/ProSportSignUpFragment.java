package com.btc.prosport.screen.fragment.proSportAuth.proSportSignUp;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
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
import com.btc.common.event.notice.ManagedNoticeEvent;
import com.btc.common.event.notice.NoticeEvent;
import com.btc.common.extension.delegate.loading.LoadingViewDelegate;
import com.btc.common.extension.delegate.loading.ProgressVisibilityHandler;
import com.btc.common.extension.view.ContentLoaderProgressBar;
import com.btc.common.mvp.presenter.Presenter;
import com.btc.common.utility.ImeUtils;
import com.btc.common.utility.listener.SimpleTextWatcher;
import com.btc.prosport.common.R;
import com.btc.prosport.core.manager.auth.AuthResult;
import com.btc.prosport.di.qualifier.PresenterNames;
import com.btc.prosport.screen.ProSportSignUpScreen;
import com.btc.prosport.screen.fragment.proSportAuth.ProSportAuthFragment;

import javax.inject.Inject;
import javax.inject.Named;

// TODO: 16.02.2017 Need to fix showing keyboard after request focus.
@Accessors(prefix = "_")
public class ProSportSignUpFragment extends ProSportAuthFragment implements ProSportSignUpScreen {
    private static final String _KEY_PHONE_NUMBER = "phoneNumber";

    public static void startView(
        @NonNull final FragmentManager fragmentManager,
        final int containerId,
        @Nullable final String phoneNumber) {
        Contracts.requireNonNull(fragmentManager, "fragmentManager == null");

        val fragment = new ProSportSignUpFragment();
        if (phoneNumber != null) {
            val bundle = new Bundle(1);
            bundle.putString(_KEY_PHONE_NUMBER, phoneNumber);
            fragment.setArguments(bundle);
        }

        val fragmentTransaction = fragmentManager.beginTransaction();
        // FIXME: 15.05.2017 Remove containerId from findFragmentById.
        if (fragmentManager.findFragmentById(containerId) != null) {
            fragmentTransaction
                .addToBackStack(null)
                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        }
        fragmentTransaction.replace(containerId, fragment).commit();
    }

    @CallSuper
    @Override
    public void bindViews(@NonNull final View source) {
        super.bindViews(source);

        _phoneInputView = (TextInputEditText) source.findViewById(R.id.prosport_phone_input);
        _passwordInputView = (TextInputEditText) source.findViewById(R.id.prosport_password_input);
        _retryPasswordInputView =
            (TextInputEditText) source.findViewById(R.id.prosport_retry_password_input);

        _phoneLayout = (TextInputLayout) source.findViewById(R.id.prosport_phone_input_layout);
        _passwordLayout =
            (TextInputLayout) source.findViewById(R.id.prosport_password_input_layout);
        _retryPasswordLayout =
            (TextInputLayout) source.findViewById(R.id.prosport_retry_password_input_layout);

        _signUpButton = (Button) source.findViewById(R.id.prosport_sign_up_button);
        _signUpContent = (ConstraintLayout) source.findViewById(R.id.prosport_sign_up_content);
        _logInProgressBar =
            (ContentLoaderProgressBar) source.findViewById(R.id.prosport_sign_up_progressbar);

        if (_signUpButton != null) {
            _signUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    performSignUp();
                }
            });
        }

        if (_retryPasswordInputView != null) {
            _retryPasswordInputView.setOnEditorActionListener(_retryPasswordActionListener);
            _retryPasswordInputView.addTextChangedListener(_retryPasswordTextWatcher);
        }

        if (_passwordInputView != null) {
            _passwordInputView.addTextChangedListener(_passwordTextWatcher);
        }

        if (_phoneInputView != null) {
            _phoneInputView.addTextChangedListener(_phoneTextWatcher);
        }

        _loadingDelegate = LoadingViewDelegate
            .builder()
            .setContentView(_signUpContent)
            .setLoadingView(_logInProgressBar)
            .setLoadingVisibilityHandler(new ProgressVisibilityHandler())
            .build();

        _loadingDelegate.showContent();

        if (_phoneInputView != null) {
            if (!TextUtils.isEmpty(_phoneNumber) && _passwordInputView != null) {
                _phoneInputView.setText(_phoneNumber);
                _passwordInputView.requestFocus();
            } else {
                _phoneInputView.requestFocus();
            }
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
        if (_retryPasswordInputView != null) {
            _retryPasswordInputView.removeTextChangedListener(_retryPasswordTextWatcher);
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

        val inflate = inflater.inflate(R.layout.fragment_sign_up, container, false);

        val arguments = getArguments();
        if (arguments != null) {
            _phoneNumber = arguments.getString(_KEY_PHONE_NUMBER);
        }

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
        Contracts.requireNonNull(authResult, "signInResult == null");

        setAccountAuthenticatorResult(authResult);

        _additionalInfoEvent.rise();
    }

    @Override
    public void displayPhoneError(@StringRes final int errorMessage) {
        displayPhoneError(getString(errorMessage));
    }

    @Override
    public void displayPhoneError(@Nullable final String errorMessage) {
        if (_phoneLayout != null) {
            _phoneLayout.setError(errorMessage);
            _phoneLayout.requestFocus();
        }
    }

    @Override
    public void displayRetryPasswordError(@StringRes final int errorMessage) {
        displayRetryPasswordError(getString(errorMessage));
    }

    @Override
    public void displayRetryPasswordError(@Nullable final String errorMessage) {
        if (_retryPasswordLayout != null) {
            _retryPasswordLayout.setError(errorMessage);
            _retryPasswordLayout.requestFocus();
        }
    }

    @NonNull
    @Override
    public NoticeEvent getAdditionalInfoEvent() {
        return _additionalInfoEvent;
    }

    @NonNull
    @Override
    public Event<SignUpEventArgs> getSignUpEvent() {
        return _signUpEvent;
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
    protected void performSignUp() {
        if (_phoneInputView != null && _passwordInputView != null &&
            _retryPasswordInputView != null) {
            ImeUtils.hideIme(_phoneInputView);
            _signUpEvent.rise(new SignUpEventArgs(_phoneInputView.getText().toString(),
                                                  _passwordInputView.getText().toString(),
                                                  _retryPasswordInputView.getText().toString()));
        }
    }

    @Named(PresenterNames.SIGN_UP)
    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Nullable
    /*package-private*/ Presenter<ProSportSignUpScreen> _presenter;

    private final ManagedNoticeEvent _additionalInfoEvent = Events.createNoticeEvent();

    private final ManagedEvent<SignUpEventArgs> _signUpEvent = Events.createEvent();

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private LoadingViewDelegate _loadingDelegate;

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
    private TextInputEditText _retryPasswordInputView;

    @NonNull
    private final TextView.OnEditorActionListener _retryPasswordActionListener =
        new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(
                final TextView textView, final int id, final KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE) {
                    performSignUp();
                    return true;
                }
                return false;
            }
        };

    @Nullable
    private TextInputLayout _retryPasswordLayout;

    @NonNull
    private final SimpleTextWatcher _retryPasswordTextWatcher = new SimpleTextWatcher() {
        @Override
        public void onTextChanged(
            final CharSequence s, final int start, final int before, final int count) {
            super.onTextChanged(s, start, before, count);

            if (_retryPasswordLayout != null) {
                _retryPasswordLayout.setError(null);
            }
        }
    };

    @Nullable
    private Button _signUpButton;

    @Nullable
    private ConstraintLayout _signUpContent;
}
