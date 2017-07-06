package com.btc.prosport.presenter;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.util.Log;

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
import com.btc.prosport.core.manager.auth.AuthManager;
import com.btc.prosport.core.manager.auth.AuthResult;
import com.btc.prosport.core.manager.auth.LogInArgs;
import com.btc.prosport.core.manager.auth.exception.AuthException;
import com.btc.prosport.core.manager.firebaseMessaging.FirebaseMessagingManager;
import com.btc.prosport.screen.ProSportReLogInScreen;
import com.btc.prosport.screen.fragment.proSportAuth.proSportReLogIn.ReLogInEventArgs;

import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;

@Accessors(prefix = "_")
public class ProSportReLogInPresenter extends BaseProSportAuthPresenter<ProSportReLogInScreen> {
    private static final String _LOG_TAG = ConstantBuilder.logTag(ProSportReLogInPresenter.class);

    public ProSportReLogInPresenter(
        @NonNull final RxManager rxManager,
        @NonNull final MessageManager messageManager,
        @NonNull final AuthManager authManager,
        @NonNull final FirebaseMessagingManager firebaseMessagingManager) {
        super(
            Contracts.requireNonNull(rxManager, "rxManager == null"),
            Contracts.requireNonNull(messageManager, "messageManager == null"),
            Contracts.requireNonNull(authManager, "authManager == null"),
            Contracts.requireNonNull(firebaseMessagingManager, "firebaseMessagingManager == null"));
    }

    protected void attemptReLogIn(@NonNull final ReLogInEventArgs reLogInEventArgs) {
        Contracts.requireNonNull(reLogInEventArgs, "reLogInEventArgs == null");

        getRxManager()
            .autoManage(getAuthManager().reLogIn(new LogInArgs(reLogInEventArgs.getPhone(),
                                                               reLogInEventArgs.getPassword())))
            .subscribeOn(getRxManager().getIOScheduler())
            .observeOn(getRxManager().getUIScheduler())
            .subscribe(new Action1<AuthResult>() {
                @Override
                public void call(final AuthResult bundle) {
                    displaySuccessfulAuth(bundle);
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
                        val logInErrorResponse =
                            ((ProSportApiException) error).getErrorBodyAs(ProSportErrorResponse
                                                                              .class);
                        if (logInErrorResponse != null) {
                            handleProSportApiException(logInErrorResponse);
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

    @Override
    protected void handleProSportApiException(
        @NonNull final ProSportErrorResponse errorResponse) {
        super.handleProSportApiException(errorResponse);

        val passwordErrors = LogInErrors.getPasswordErrors(errorResponse);
        if (passwordErrors != null) {
            displayPasswordError(joinErrors(passwordErrors));
        }
    }

    @CallSuper
    @Override
    protected void onScreenAppear(@NonNull final ProSportReLogInScreen screen) {
        super.onScreenAppear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getReLogInEvent().addHandler(_logInHandler);
    }

    @CallSuper
    @Override
    protected void onScreenDisappear(
        @NonNull final ProSportReLogInScreen screen) {
        super.onScreenDisappear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getReLogInEvent().removeHandler(_logInHandler);
    }

    @NonNull
    private final EventHandler<ReLogInEventArgs> _logInHandler =
        new EventHandler<ReLogInEventArgs>() {
            @Override
            public void onEvent(@NonNull final ReLogInEventArgs reLogInEventArgs) {
                Contracts.requireNonNull(reLogInEventArgs, "reLogInEventArgs == null");

                displayLoading();
                attemptReLogIn(reLogInEventArgs);
            }
        };
}
