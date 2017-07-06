package com.btc.prosport.core.manager.account;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Binder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.var;
import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.utility.HandlerUtils;
import com.btc.prosport.api.ProSportApi;
import com.btc.prosport.api.model.utility.UserRole;
import com.btc.prosport.common.R;
import com.btc.prosport.core.manager.credential.CredentialManager;
import com.btc.prosport.core.manager.firebaseMessaging.FirebaseMessagingManager;
import com.btc.prosport.core.manager.notification.NotificationType;
import com.btc.prosport.core.manager.notification.ProSportNotificationManager;
import com.btc.prosport.screen.activity.proSportVerification.ProSportVerificationActivity;
import com.btc.prosport.screen.activity.proSportVerification.ProSportVerificationState;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.HttpURLConnection;

import retrofit2.adapter.rxjava.HttpException;

@Accessors(prefix = "_")
/*package-private*/ class ProSportAccountAuthenticator extends AbstractAccountAuthenticator {
    private static final String _LOG_TAG =
        ConstantBuilder.logTag(ProSportAccountAuthenticator.class);

    @Override
    public Bundle editProperties(
        final AccountAuthenticatorResponse response, final String accountType) {
        return null;
    }

    @Override
    public Bundle addAccount(
        final AccountAuthenticatorResponse response,
        final String accountType,
        final String authTokenType,
        final String[] requiredFeatures,
        final Bundle options)
        throws NetworkErrorException {

        final Bundle result;
        val context = getContext();
        val accountManager = AccountManager.get(context);
        if (accountManager.getAccountsByType(accountType).length == 0) {
            val accountName = options.getString(AccountManager.KEY_ACCOUNT_NAME);
            final boolean allowRegistration = isAllowRegistration(accountType);
            val intent = ProSportVerificationActivity.getViewIntent(context,
                                                                    null,
                                                                    accountName,
                                                                    allowRegistration);
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

            result = new Bundle(1);
            result.putParcelable(AccountManager.KEY_INTENT, intent);
        } else {
            result = new Bundle(2);
            result.putInt(AccountManager.KEY_ERROR_CODE, AccountManager.ERROR_CODE_CANCELED);
            result.putString(AccountManager.KEY_ERROR_MESSAGE,
                             context.getString(R.string.message_error_account_already_exists));

            if (Binder.getCallingUid() != context.getApplicationInfo().uid) {
                HandlerUtils.getMainThreadHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context,
                                       R.string.message_error_account_already_exists,
                                       Toast.LENGTH_LONG).show();
                    }
                });
            }
        }

        return result;
    }

    @Override
    public Bundle confirmCredentials(
        final AccountAuthenticatorResponse response, final Account account, final Bundle options)
        throws NetworkErrorException {

        val result = new Bundle(1);
        val intent = ProSportVerificationActivity.getViewIntent(_context,
                                                                ProSportVerificationState.RE_LOG_IN,
                                                                account.name,
                                                                false);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        result.putParcelable(AccountManager.KEY_INTENT, intent);

        return result;
    }

    // TODO: Research return exception.
    @Override
    public Bundle getAuthToken(
        final AccountAuthenticatorResponse response,
        final Account account,
        final String authTokenType,
        final Bundle options)
        throws NetworkErrorException {

        val accountManager = AccountManager.get(getContext());

        var authToken = accountManager.peekAuthToken(account, authTokenType);
        boolean hasServerException = false;

        if (StringUtils.isEmpty(authToken)) {
            val refreshToken =
                accountManager.getUserData(account, AccountContracts.KEY_REFRESH_TOKEN);
            if (!StringUtils.isEmpty(refreshToken)) {
                try {
                    final var credentialManager = getCredentialManager();
                    val authorizationResponse =
                        getProSportApi().refreshToken(credentialManager.getClientId(),
                                                      credentialManager.getClientSecret(),
                                                      refreshToken).toBlocking().first();

                    val accessToken = authorizationResponse.getAccessToken();
                    if (!TextUtils.isEmpty(accessToken)) {
                        accountManager.setUserData(account,
                                                   AccountContracts.KEY_REFRESH_TOKEN,
                                                   authorizationResponse.getRefreshToken());
                        accountManager.setAuthToken(account, authTokenType, accessToken);
                        authToken = accessToken;
                    }
                } catch (final RuntimeException throwable) {
                    val cause = throwable.getCause();
                    if (cause instanceof IOException) {
                        throw new NetworkErrorException(cause);
                    } else if (cause instanceof HttpException) {
                        if (((HttpException) cause).code() != HttpURLConnection.HTTP_UNAUTHORIZED) {
                            hasServerException = true;
                        }
                    } else {
                        hasServerException = true;
                    }
                }
            }
        }

        final Bundle result;
        if (!hasServerException) {
            result = new Bundle(3);
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
        } else {
            result = new Bundle(2);

            result.putInt(AccountManager.KEY_ERROR_CODE,
                          AccountManager.ERROR_CODE_REMOTE_EXCEPTION);
            result.putString(AccountManager.KEY_ERROR_MESSAGE,
                             _context.getString(R.string.message_error_remote_exception));
        }

        return result;
    }

    @Override
    public String getAuthTokenLabel(final String authTokenType) {
        return null;
    }

    @Override
    public Bundle updateCredentials(
        final AccountAuthenticatorResponse response,
        final Account account,
        final String authTokenType,
        final Bundle options)
        throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(
        final AccountAuthenticatorResponse response, final Account account, final String[] features)
        throws NetworkErrorException {

        val result = new Bundle(1);
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
        return result;
    }

    @Override
    public Bundle getAccountRemovalAllowed(
        final AccountAuthenticatorResponse response, final Account account)
        throws NetworkErrorException {

        val accountRemovalAllowed = super.getAccountRemovalAllowed(response, account);
        final boolean remove = accountRemovalAllowed.getBoolean(AccountManager.KEY_BOOLEAN_RESULT);
        if (remove) {
            getFirebaseMessagingManager().generalSubscribe(true).subscribe();
            getNotificationManager().cancelAllUserNotifications();
        }

        return accountRemovalAllowed;
    }

    /*package-private*/ ProSportAccountAuthenticator(
        @NonNull final Context context,
        @NonNull final ProSportApi proSportApi,
        @NonNull final CredentialManager credentialManager,
        @NonNull final FirebaseMessagingManager firebaseMessagingManager,
        @NonNull final ProSportNotificationManager<NotificationType> notificationManager) {
        super(Contracts.requireNonNull(context, "context == null"));
        Contracts.requireNonNull(proSportApi, "proSportApi == null");
        Contracts.requireNonNull(credentialManager, "credentialManager == null");
        Contracts.requireNonNull(firebaseMessagingManager, "firebaseMessagingManager == null");
        Contracts.requireNonNull(notificationManager, "notificationManager == null");

        _context = context;
        _proSportApi = proSportApi;
        _credentialManager = credentialManager;
        _firebaseMessagingManager = firebaseMessagingManager;
        _notificationManager = notificationManager;
    }

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final Context _context;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final CredentialManager _credentialManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final FirebaseMessagingManager _firebaseMessagingManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ProSportNotificationManager<NotificationType> _notificationManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ProSportApi _proSportApi;

    private boolean isAllowRegistration(@NonNull final String accountType) {
        Contracts.requireNonNull(accountType, "accountType == null");

        return UserRole.byAccountType(accountType).isAllowRegistration();
    }
}
