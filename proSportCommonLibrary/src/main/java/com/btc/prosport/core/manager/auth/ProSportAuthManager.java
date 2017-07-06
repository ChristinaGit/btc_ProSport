package com.btc.prosport.core.manager.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import rx.Observable;
import rx.functions.Func1;

import com.btc.common.contract.Contracts;
import com.btc.prosport.api.model.entity.UserEntity;
import com.btc.prosport.api.request.UserRegisterParams;
import com.btc.prosport.api.response.AuthorizationResponse;
import com.btc.prosport.core.manager.account.AccountContracts;
import com.btc.prosport.core.manager.auth.exception.AuthException;
import com.btc.prosport.core.manager.auth.exception.AuthExceptionType;
import com.btc.prosport.core.manager.credential.CredentialManager;
import com.btc.prosport.core.manager.proSportApi.ProSportApiManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Accessors(prefix = "_")
public class ProSportAuthManager implements AuthManager {
    public ProSportAuthManager(
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final CredentialManager credentialManager,
        @NonNull final AccountManager accountManager,
        @NonNull final String accountType,
        @NonNull final String authTokenType) {

        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(credentialManager, "credentialManager == null");
        Contracts.requireNonNull(accountManager, "accountManager == null");
        Contracts.requireNonNull(accountType, "accountType == null");
        Contracts.requireNonNull(authTokenType, "authTokenType == null");

        _proSportApiManager = proSportApiManager;
        _credentialManager = credentialManager;
        _accountManager = accountManager;
        _accountType = accountType;
        _authTokenType = authTokenType;
    }

    @NonNull
    @Override
    public Observable<AuthResult> logIn(@NonNull final LogInArgs logInArgs) {
        Contracts.requireNonNull(logInArgs, "logInArgs == null");

        val exceptions = new ArrayList<AuthExceptionType>();
        checkLogInData(exceptions, logInArgs);
        if (!exceptions.isEmpty()) {
            return Observable.error(new AuthException(exceptions));
        } else {
            return attemptLogIn(logInArgs);
        }
    }

    @NonNull
    @Override
    public Observable<AuthResult> reLogIn(@NonNull final LogInArgs logInArgs) {
        Contracts.requireNonNull(logInArgs, "logInArgs == null");

        val exceptions = new ArrayList<AuthExceptionType>();
        checkReLogInData(exceptions, logInArgs);
        if (!exceptions.isEmpty()) {
            return Observable.error(new AuthException(exceptions));
        } else {
            return attemptLogIn(logInArgs);
        }
    }

    @NonNull
    @Override
    public Observable<AuthResult> signUp(@NonNull final SignUpArgs signUpArgs) {
        Contracts.requireNonNull(signUpArgs, "signUpArgs == null");

        val exceptionTypes = new ArrayList<AuthExceptionType>();
        checkSignUpData(exceptionTypes, signUpArgs);

        if (!exceptionTypes.isEmpty()) {
            return Observable.error(new AuthException(exceptionTypes));
        } else {
            return attemptRegister(signUpArgs);
        }
    }

    @NonNull
    @Getter(AccessLevel.PROTECTED)
    private final AccountManager _accountManager;

    @NonNull
    @Getter(AccessLevel.PROTECTED)
    private final String _accountType;

    @NonNull
    @Getter(AccessLevel.PROTECTED)
    private final String _authTokenType;

    @NonNull
    @Getter(AccessLevel.PROTECTED)
    private final CredentialManager _credentialManager;

    @NonNull
    @Getter(AccessLevel.PROTECTED)
    private final ProSportApiManager _proSportApiManager;

    @Nullable
    private Account addAccount(@NonNull final String phoneNumber) {
        Contracts.requireNonNull(phoneNumber, "phoneNumber == null");

        val account = new Account(phoneNumber, getAccountType());

        final boolean added = getAccountManager().addAccountExplicitly(account, null, null);

        if (added) {
            return account;
        } else {
            return null;
        }
    }

    @NonNull
    private Observable<AuthResult> attemptLogIn(@NonNull final LogInArgs logInArgs) {
        Contracts.requireNonNull(logInArgs, "logInArgs == null");

        val credentialManager = getCredentialManager();
        final val phone = logInArgs.getPhone();
        val password = logInArgs.getPassword();
        if (phone != null && password != null) {
            return getProSportApiManager()
                .getProSportApi()
                .logInUser(credentialManager.getClientId(),
                           credentialManager.getClientSecret(),
                           phone,
                           password)
                .map(new Func1<AuthorizationResponse, AuthResult>() {

                    @Override
                    public AuthResult call(final AuthorizationResponse authorizationResponse) {
                        val authToken = authorizationResponse.getAccessToken();
                        val refreshToken = authorizationResponse.getRefreshToken();

                        Account account = null;
                        for (final val accountByType : getAccountManager().getAccountsByType(
                            getAccountType())) {
                            if (accountByType.name.equals(phone)) {
                                account = accountByType;
                            }
                        }

                        if (account == null) {
                            account = addAccount(phone);
                        }

                        if (account == null) {
                            throw new AuthException(Collections.singletonList(AuthExceptionType
                                                                                  .ACCOUNT_NOT_ADDED));
                        } else {
                            val accountManager = getAccountManager();

                            accountManager.setAuthToken(account,
                                                        AccountContracts.TOKEN_TYPE_FULL_ACCESS,
                                                        authToken);

                            accountManager.setUserData(account,
                                                       AccountContracts.KEY_REFRESH_TOKEN,
                                                       refreshToken);

                            return new AuthResult(phone, getAccountType());
                        }
                    }
                });
        } else {
            throw new RuntimeException("Phone or password == null");
        }
    }

    private Observable<AuthResult> attemptRegister(@NonNull final SignUpArgs signUpArgs) {
        Contracts.requireNonNull(signUpArgs, "signUpArgs == null");

        val password = signUpArgs.getPassword();
        val phone = signUpArgs.getPhone();
        final UserRegisterParams userRegisterBody;
        if (password != null && phone != null) {
            userRegisterBody = new UserRegisterParams(password, phone);
        } else {
            throw new RuntimeException("User register params not created");
        }

        return getProSportApiManager()
            .getProSportApi()
            .registerPlayer(userRegisterBody)
            .flatMap(new Func1<UserEntity, Observable<AuthResult>>() {
                @Override
                public Observable<AuthResult> call(final UserEntity userResponse) {
                    return logIn(new LogInArgs(userResponse.getPhoneNumber(), password));
                }
            });
    }

    private void checkLogInData(
        @NonNull final List<AuthExceptionType> authExceptionTypes,
        @NonNull final LogInArgs logInArgs) {
        Contracts.requireNonNull(authExceptionTypes, "authExceptionTypes == null");
        Contracts.requireNonNull(logInArgs, "logInArgs == null");

        checkPhone(authExceptionTypes, logInArgs.getPhone());

        checkPassword(authExceptionTypes, logInArgs.getPassword());
    }

    private void checkPassword(
        @NonNull final List<AuthExceptionType> authExceptionTypes,
        @Nullable final String password) {
        Contracts.requireNonNull(authExceptionTypes, "authExceptionTypes == null");

        if (TextUtils.isEmpty(password)) {
            authExceptionTypes.add(AuthExceptionType.REQUIRED_PASSWORD);
        }
    }

    private void checkPhone(
        @NonNull final List<AuthExceptionType> authExceptionTypes, @Nullable final String phone) {
        Contracts.requireNonNull(authExceptionTypes, "authExceptionTypes == null");

        if (TextUtils.isEmpty(phone)) {
            authExceptionTypes.add(AuthExceptionType.REQUIRED_PHONE);
        } else {
            for (final val account : getAccountManager().getAccountsByType(getAccountType())) {
                if (account.name.equals(phone)) {
                    authExceptionTypes.add(AuthExceptionType.ACCOUNT_ALREADY_EXISTS);
                    break;
                }
            }
        }
    }

    private void checkReLogInData(
        @NonNull final List<AuthExceptionType> authExceptionTypes,
        @NonNull final LogInArgs logInArgs) {
        Contracts.requireNonNull(authExceptionTypes, "authExceptionTypes == null");
        Contracts.requireNonNull(logInArgs, "logInArgs == null");

        checkPassword(authExceptionTypes, logInArgs.getPassword());
    }

    private void checkSignUpData(
        @NonNull final List<AuthExceptionType> authExceptionTypes,
        @NonNull final SignUpArgs signUpArgs) {
        Contracts.requireNonNull(authExceptionTypes, "authExceptionTypes == null");
        Contracts.requireNonNull(signUpArgs, "signUpArgs == null");

        val password = signUpArgs.getPassword();
        val phone = signUpArgs.getPhone();
        val retryPassword = signUpArgs.getRetryPassword();

        checkPhone(authExceptionTypes, phone);

        checkPassword(authExceptionTypes, password);

        if (TextUtils.isEmpty(retryPassword)) {
            authExceptionTypes.add(AuthExceptionType.REQUIRED_RETRY_PASSWORD);
        } else if (!Objects.equals(password, retryPassword)) {
            authExceptionTypes.add(AuthExceptionType.RETRY_PASSWORD_MISMATCH);
        }
    }
}
