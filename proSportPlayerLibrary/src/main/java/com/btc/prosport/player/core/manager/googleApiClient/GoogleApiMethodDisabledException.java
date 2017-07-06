package com.btc.prosport.player.core.manager.googleApiClient;

import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

public class GoogleApiMethodDisabledException extends RuntimeException {
    private static final long serialVersionUID = -6208995139819628590L;

    public GoogleApiMethodDisabledException() {
    }

    public GoogleApiMethodDisabledException(@Nullable final String message) {
        super(message);
    }

    public GoogleApiMethodDisabledException(
        @Nullable final String message, @Nullable final Throwable cause) {
        super(message, cause);
    }

    public GoogleApiMethodDisabledException(@Nullable final Throwable cause) {
        super(cause);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected GoogleApiMethodDisabledException(
        @Nullable final String message,
        @Nullable final Throwable cause,
        final boolean enableSuppression,
        final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
