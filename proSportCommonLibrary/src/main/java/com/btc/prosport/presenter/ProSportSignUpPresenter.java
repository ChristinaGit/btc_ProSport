package com.btc.prosport.presenter;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Log;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import rx.functions.Action1;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.message.MessageManager;
import com.btc.common.control.manager.rx.RxManager;
import com.btc.common.event.generic.EventHandler;
import com.btc.common.event.notice.NoticeEventHandler;
import com.btc.prosport.api.exception.ProSportApiException;
import com.btc.prosport.api.response.ProSportErrorResponse;
import com.btc.prosport.api.response.error.LogInErrors;
import com.btc.prosport.common.R;
import com.btc.prosport.core.manager.auth.AuthManager;
import com.btc.prosport.core.manager.auth.AuthResult;
import com.btc.prosport.core.manager.auth.SignUpArgs;
import com.btc.prosport.core.manager.auth.exception.AuthException;
import com.btc.prosport.core.manager.auth.exception.AuthExceptionType;
import com.btc.prosport.core.manager.firebaseMessaging.FirebaseMessagingManager;
import com.btc.prosport.core.manager.navigation.ProSportNavigationManager;
import com.btc.prosport.screen.ProSportSignUpScreen;
import com.btc.prosport.screen.fragment.proSportAuth.proSportSignUp.SignUpEventArgs;

import java.io.IOException;
import java.util.List;

import retrofit2.adapter.rxjava.HttpException;

@Accessors(prefix = "_")
public class ProSportSignUpPresenter extends BaseProSportAuthPresenter<ProSportSignUpScreen> {
    private static final String _LOG_TAG = ConstantBuilder.logTag(ProSportSignUpPresenter.class);

    public ProSportSignUpPresenter(
        @NonNull final RxManager rxManager,
        @NonNull final MessageManager messageManager,
        @NonNull final AuthManager authManager,
        @NonNull final FirebaseMessagingManager firebaseMessagingManager,
        @NonNull final ProSportNavigationManager proSportNavigationManager) {
        super(Contracts.requireNonNull(rxManager, "rxManager == null"),
              Contracts.requireNonNull(messageManager, "messageManager == null"),
              Contracts.requireNonNull(authManager, "authManager == null"),
              Contracts.requireNonNull(firebaseMessagingManager,
                                       "firebaseMessagingManager == null"));
        Contracts.requireNonNull(proSportNavigationManager, "proSportNavigationManager == null");

        _proSportNavigationManager = proSportNavigationManager;
    }

    protected void attemptSignUp(@NonNull final SignUpEventArgs signUpEventArgs) {
        Contracts.requireNonNull(signUpEventArgs, "signUpEventArgs == null");

        getRxManager()
            .autoManage(getAuthManager().signUp(new SignUpArgs(signUpEventArgs.getPhone(),
                                                               signUpEventArgs.getPassword(),
                                                               signUpEventArgs.getRetryPassword())))
            .subscribeOn(getRxManager().getIOScheduler())
            .observeOn(getRxManager().getUIScheduler())
            .subscribe(new Action1<AuthResult>() {
                @Override
                public void call(final AuthResult authResult) {
                    displayContent();
                    displaySuccessfulAuth(authResult);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    if (error instanceof AuthException) {
                        val exceptionTypes = ((AuthException) error).getAuthExceptionTypes();
                        if (exceptionTypes != null) {
                            handleProSportAuthException(exceptionTypes);
                        }
                    } else if (error instanceof ProSportApiException) {
                        final val apiException = (ProSportApiException) error;
                        val signUpErrorResponse =
                            apiException.getErrorBodyAs(ProSportErrorResponse.class);
                        if (signUpErrorResponse != null) {
                            handleProSportApiException(signUpErrorResponse);
                        }
                    } else if (error instanceof HttpException) {
                        handleServerException((HttpException) error);
                    } else if (error instanceof IOException) {
                        handleIOException();
                    } else {
                        Log.w(_LOG_TAG, error);
                    }

                    displayContent();
                }
            });
    }

    protected void displayPhoneError(@StringRes final int errorMessage) {
        val screen = getScreen();
        if (screen != null) {
            screen.displayPhoneError(errorMessage);
        }
    }

    protected void displayPhoneError(@Nullable final String errorMessage) {
        val screen = getScreen();
        if (screen != null) {
            screen.displayPhoneError(errorMessage);
        }
    }

    protected void displayRetryPasswordError(@StringRes final int errorMessage) {
        val screen = getScreen();
        if (screen != null) {
            screen.displayRetryPasswordError(errorMessage);
        }
    }

    @Override
    protected void handleProSportApiException(
        @NonNull final ProSportErrorResponse errorResponse) {
        super.handleProSportApiException(Contracts.requireNonNull(errorResponse,
                                                                  "errorResponse == null"));

        val passwordErrors = LogInErrors.getPasswordErrors(errorResponse);
        if (passwordErrors != null) {
            displayPasswordError(joinErrors(passwordErrors));
        }

        val phoneErrors = LogInErrors.getPhoneErrors(errorResponse);
        if (phoneErrors != null) {
            displayPhoneError(joinErrors(phoneErrors));
        }
    }

    @Override
    protected void handleProSportAuthException(@NonNull final List<AuthExceptionType> types) {
        Contracts.requireNonNull(types, "types == null");

        if (types.contains(AuthExceptionType.REQUIRED_RETRY_PASSWORD)) {
            displayRetryPasswordError(R.string.message_error_field_required);
        } else if (types.contains(AuthExceptionType.RETRY_PASSWORD_MISMATCH)) {
            displayRetryPasswordError(R.string.message_error_retry_password_mismatch);
        }

        super.handleProSportAuthException(types);

        if (types.contains(AuthExceptionType.REQUIRED_PHONE)) {
            displayPhoneError(R.string.message_error_field_required);
        } else if (types.contains(AuthExceptionType.ACCOUNT_ALREADY_EXISTS)) {
            displayPhoneError(R.string.message_error_account_already_exists);
        }
    }

    @CallSuper
    @Override
    protected void onScreenAppear(@NonNull final ProSportSignUpScreen screen) {
        super.onScreenAppear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getSignUpEvent().addHandler(_signUpHandler);
        screen.getAdditionalInfoEvent().addHandler(_additionalInfoHandler);
    }

    @CallSuper
    @Override
    protected void onScreenDisappear(@NonNull final ProSportSignUpScreen screen) {
        super.onScreenDisappear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getSignUpEvent().removeHandler(_signUpHandler);
        screen.getAdditionalInfoEvent().removeHandler(_additionalInfoHandler);
    }

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ProSportNavigationManager _proSportNavigationManager;

    private final NoticeEventHandler _additionalInfoHandler = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            _proSportNavigationManager.navigateToAdditionalInfo();
        }
    };

    @NonNull
    private final EventHandler<SignUpEventArgs> _signUpHandler =
        new EventHandler<SignUpEventArgs>() {
            @Override
            public void onEvent(
                @NonNull final SignUpEventArgs eventArgs) {
                displayLoading();
                attemptSignUp(eventArgs);
            }
        };
}
