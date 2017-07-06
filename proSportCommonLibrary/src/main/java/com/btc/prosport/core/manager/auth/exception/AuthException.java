package com.btc.prosport.core.manager.auth.exception;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;

@Accessors(prefix = "_")
public class AuthException extends RuntimeException {
    private static final long serialVersionUID = -7046198014553095304L;

    public AuthException() {
        super((String) null);

        _authExceptionTypes = null;
    }

    public AuthException(@Nullable final List<AuthExceptionType> authExceptionTypes) {
        super((String) null);

        _authExceptionTypes = authExceptionTypes;
    }

    public AuthException(
        final String message, @Nullable final List<AuthExceptionType> authExceptionTypes) {
        super(message);

        _authExceptionTypes = authExceptionTypes;
    }

    public AuthException(
        final String message,
        final Throwable cause,
        @Nullable final List<AuthExceptionType> authExceptionTypes) {
        super(message, cause);

        _authExceptionTypes = authExceptionTypes;
    }

    public AuthException(
        final Throwable cause, @Nullable final List<AuthExceptionType> authExceptionTypes) {
        super(cause);

        _authExceptionTypes = authExceptionTypes;
    }

    @TargetApi(Build.VERSION_CODES.N)
    protected AuthException(
        final String message,
        final Throwable cause,
        final boolean enableSuppression,
        final boolean writableStackTrace,
        @Nullable final List<AuthExceptionType> authExceptionTypes) {
        super(message, cause, enableSuppression, writableStackTrace);

        _authExceptionTypes = authExceptionTypes;
    }

    @Getter
    @Nullable
    private final List<AuthExceptionType> _authExceptionTypes;
}
