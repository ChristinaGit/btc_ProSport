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
import com.btc.prosport.api.exception.ProSportApiException;
import com.btc.prosport.api.response.ProSportErrorResponse;
import com.btc.prosport.api.response.error.LogInErrors;
import com.btc.prosport.common.R;
import com.btc.prosport.core.manager.auth.AuthManager;
import com.btc.prosport.core.manager.auth.AuthResult;
import com.btc.prosport.core.manager.auth.LogInArgs;
import com.btc.prosport.core.manager.auth.exception.AuthException;
import com.btc.prosport.core.manager.auth.exception.AuthExceptionType;
import com.btc.prosport.core.manager.firebaseMessaging.FirebaseMessagingManager;
import com.btc.prosport.core.manager.navigation.ProSportNavigationManager;
import com.btc.prosport.screen.ProSportLogInScreen;
import com.btc.prosport.screen.fragment.proSportAuth.proSportLogIn.LogInEventArgs;
import com.btc.prosport.screen.fragment.proSportAuth.proSportLogIn.SignUpEventArgs;

import java.io.IOException;
import java.util.List;

import retrofit2.adapter.rxjava.HttpException;

@Accessors(prefix = "_")
public class ProSportLogInPresenter extends BaseProSportAuthPresenter<ProSportLogInScreen> {
    private static final String _LOG_TAG = ConstantBuilder.logTag(ProSportLogInPresenter.class);

    public ProSportLogInPresenter(
        @NonNull final RxManager rxManager,
        @NonNull final MessageManager messageManager,
        @NonNull final AuthManager authManager,
        @NonNull final ProSportNavigationManager proSportNavigationManager,
        @NonNull final FirebaseMessagingManager firebaseMessagingManager) {
        super(
            Contracts.requireNonNull(rxManager, "rxManager == null"),
            Contracts.requireNonNull(messageManager, "messageManager == null"),
            Contracts.requireNonNull(authManager, "authManager == null"),
            Contracts.requireNonNull(firebaseMessagingManager, "firebaseMessagingManager == null"));

        Contracts.requireNonNull(proSportNavigationManager, "proSportNavigationManager == null");

        _proSportNavigationManager = proSportNavigationManager;
    }

    protected void attemptLogIn(@NonNull final LogInEventArgs logInEventArgs) {
        Contracts.requireNonNull(logInEventArgs, "logInEventArgs == null");

        getRxManager()
            .autoManage(getAuthManager().logIn(new LogInArgs(logInEventArgs.getPhone(),
                                                             logInEventArgs.getPassword())))
            .subscribeOn(getRxManager().getIOScheduler())
            .observeOn(getRxManager().getUIScheduler())
            .subscribe(new Action1<AuthResult>() {
                @Override
                public void call(final AuthResult authResult) {
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
                        val errorResponse =
                            apiException.getErrorBodyAs(ProSportErrorResponse.class);
                        if (errorResponse != null) {
                            handleProSportApiException(errorResponse);
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

    @Override
    protected void handleProSportApiException(
        @NonNull final ProSportErrorResponse errorResponse) {
        super.handleProSportApiException(errorResponse);

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
    protected void handleProSportAuthException(
        @NonNull final List<AuthExceptionType> types) {
        super.handleProSportAuthException(types);

        if (types.contains(AuthExceptionType.REQUIRED_PHONE)) {
            displayPhoneError(R.string.message_error_field_required);
        } else if (types.contains(AuthExceptionType.ACCOUNT_ALREADY_EXISTS)) {
            displayPhoneError(R.string.message_error_account_already_exists);
        } else if (types.contains(AuthExceptionType.ACCOUNT_NOT_ADDED)) {
            getMessageManager().showErrorMessage(R.string.message_error_fail_add_account);
        }
    }

    @CallSuper
    @Override
    protected void onScreenAppear(@NonNull final ProSportLogInScreen screen) {
        super.onScreenAppear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getLogInEvent().addHandler(_logInHandler);
        screen.getSignUpEvent().addHandler(_signUpHandler);
    }

    @Override
    protected void onScreenCreate(
        @NonNull final ProSportLogInScreen screen) {
        super.onScreenCreate(screen);
    }

    @CallSuper
    @Override
    protected void onScreenDisappear(
        @NonNull final ProSportLogInScreen screen) {
        super.onScreenDisappear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getLogInEvent().removeHandler(_logInHandler);
        screen.getSignUpEvent().removeHandler(_signUpHandler);
    }

    @NonNull
    private final EventHandler<LogInEventArgs> _logInHandler = new EventHandler<LogInEventArgs>() {
        @Override
        public void onEvent(@NonNull final LogInEventArgs logInEventArgs) {
            Contracts.requireNonNull(logInEventArgs, "logInEventArgs == null");

            displayLoading();
            attemptLogIn(logInEventArgs);
        }
    };

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ProSportNavigationManager _proSportNavigationManager;

    @NonNull
    private final EventHandler<SignUpEventArgs> _signUpHandler =
        new EventHandler<SignUpEventArgs>() {
            @Override
            public void onEvent(
                @NonNull final SignUpEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                getProSportNavigationManager().navigateToSignUp(eventArgs.getPhoneNumber());
            }
        };
}
