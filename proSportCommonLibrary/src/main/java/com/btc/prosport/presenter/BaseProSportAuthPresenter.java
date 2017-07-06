package com.btc.prosport.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.message.MessageManager;
import com.btc.common.control.manager.rx.RxManager;
import com.btc.common.mvp.presenter.BasePresenter;
import com.btc.prosport.api.response.ProSportErrorResponse;
import com.btc.prosport.common.R;
import com.btc.prosport.core.manager.auth.AuthManager;
import com.btc.prosport.core.manager.auth.AuthResult;
import com.btc.prosport.core.manager.auth.exception.AuthExceptionType;
import com.btc.prosport.core.manager.firebaseMessaging.FirebaseMessagingManager;
import com.btc.prosport.screen.BaseProSportAuthScreen;

import java.net.HttpURLConnection;
import java.util.List;

import retrofit2.adapter.rxjava.HttpException;

@Accessors(prefix = "_")
public class BaseProSportAuthPresenter<TScreen extends BaseProSportAuthScreen>
    extends BasePresenter<TScreen> {
    protected BaseProSportAuthPresenter(
        @NonNull final RxManager rxManager,
        @NonNull final MessageManager messageManager,
        @NonNull final AuthManager authManager,
        @NonNull final FirebaseMessagingManager firebaseMessagingManager) {
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(authManager, "authManager == null");
        Contracts.requireNonNull(firebaseMessagingManager, "firebaseMessagingManager == null");

        _rxManager = rxManager;
        _messageManager = messageManager;
        _authManager = authManager;
        _firebaseMessagingManager = firebaseMessagingManager;
    }

    protected void displayContent() {
        val screen = getScreen();
        if (screen != null) {
            screen.displayContent();
        }
    }

    protected void displayLoading() {
        val screen = getScreen();
        if (screen != null) {
            screen.displayLoading();
        }
    }

    protected void displayPasswordError(@StringRes final int errorMessage) {
        val screen = getScreen();
        if (screen != null) {
            screen.displayPasswordError(errorMessage);
        }
    }

    protected void displayPasswordError(final String errorMessage) {
        val screen = getScreen();
        if (screen != null) {
            screen.displayPasswordError(errorMessage);
        }
    }

    protected void displaySuccessfulAuth(@NonNull final AuthResult authResult) {
        Contracts.requireNonNull(authResult, "authResult == null");

        val screen = getScreen();
        if (screen != null) {
            screen.displaySuccessfulAuth(authResult);
        }

        val rxManager = getRxManager();
        getFirebaseMessagingManager()
            .userSubscribe(true)
            .observeOn(rxManager.getUIScheduler())
            .subscribeOn(rxManager.getIOScheduler())
            .subscribe();
    }

    protected void handleIOException() {
        getMessageManager().showErrorMessage(R.string.message_error_no_internet_connection);
    }

    protected void handleProSportApiException(
        @NonNull final ProSportErrorResponse errorResponse) {
        Contracts.requireNonNull(errorResponse, "errorResponse == null");

        val detail = errorResponse.getDetail();
        if (detail != null) {
            getMessageManager().showErrorMessage(detail);
        }

        val nonFieldErrors = errorResponse.getNonFieldErrors();
        if (nonFieldErrors != null) {
            getMessageManager().showErrorMessage(joinErrors(nonFieldErrors));
        }
    }

    protected void handleProSportAuthException(@NonNull final List<AuthExceptionType> types) {
        Contracts.requireNonNull(types, "types == null");

        if (types.contains(AuthExceptionType.REQUIRED_PASSWORD)) {
            displayPasswordError(R.string.message_error_field_required);
        }
    }

    protected void handleServerException(final HttpException error) {
        final val messageManager = getMessageManager();
        if (error.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            messageManager.showErrorMessage(R.string.message_error_auth_unauthorized);
        } else {
            messageManager.showErrorMessage(R.string.message_error_server_side);
        }
    }

    protected String joinErrors(@NonNull final List<String> errors) {
        Contracts.requireNonNull(errors, "errors == null");

        final String result;
        if (errors.size() == 1) {
            result = errors.get(0);
        } else {
            result = TextUtils.join(".\n", errors);
        }
        return result;
    }

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final AuthManager _authManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final FirebaseMessagingManager _firebaseMessagingManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final MessageManager _messageManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final RxManager _rxManager;
}
