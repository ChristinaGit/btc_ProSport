package com.btc.prosport.api.exception;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.prosport.api.response.ProSportErrorResponse;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

@Accessors(prefix = "_")
public class ProSportApiException extends RuntimeException {
    private static final long serialVersionUID = 5753097876479360873L;

    private static final String _LOG_TAG = ConstantBuilder.logTag(ProSportApiException.class);

    public ProSportApiException(
        @NonNull final Retrofit retrofit, @NonNull final Response<?> response) {
        Contracts.requireNonNull(retrofit, "retrofit == null");
        Contracts.requireNonNull(response, "response == null");

        _response = response;
        _retrofit = retrofit;
    }

    public ProSportApiException(
        @NonNull final Retrofit retrofit,
        @NonNull final Response<?> response,
        final String message) {
        super(message);
        Contracts.requireNonNull(retrofit, "retrofit == null");
        Contracts.requireNonNull(response, "response == null");

        _response = response;
        _retrofit = retrofit;
    }

    public ProSportApiException(
        @NonNull final Retrofit retrofit,
        @NonNull final Response<?> response,
        final Throwable cause) {
        super(cause);
        Contracts.requireNonNull(retrofit, "retrofit == null");
        Contracts.requireNonNull(response, "response == null");

        _response = response;
        _retrofit = retrofit;
    }

    @Nullable
    public <T> T getErrorBodyAs(@NonNull final Class<T> type) {
        Contracts.requireNonNull(type, "type == null");

        T convertedBody = null;
        if (_response.errorBody() != null) {
            final Converter<ResponseBody, T> converter =
                _retrofit.responseBodyConverter(type, new Annotation[0]);
            try {
                convertedBody = converter.convert(_response.errorBody());
            } catch (final IOException e) {
                Log.w(_LOG_TAG, e);
            }
        }
        return convertedBody;
    }

    @Nullable
    public ProSportErrorResponse getErrorResponse() {
        return getErrorBodyAs(ProSportErrorResponse.class);
    }

    @TargetApi(Build.VERSION_CODES.N)
    protected ProSportApiException(
        @NonNull final Retrofit retrofit,
        @NonNull final Response<?> response,
        final String message,
        final Throwable cause,
        final boolean enableSuppression,
        final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        Contracts.requireNonNull(retrofit, "retrofit == null");
        Contracts.requireNonNull(response, "response == null");

        _response = response;
        _retrofit = retrofit;
    }

    ProSportApiException(
        @NonNull final Retrofit retrofit,
        @NonNull final Response<?> response,
        final String message,
        final Throwable exception) {
        super(message, exception);
        Contracts.requireNonNull(retrofit, "retrofit == null");
        Contracts.requireNonNull(response, "response == null");

        _response = response;
        _retrofit = retrofit;
    }

    @Getter
    @NonNull
    private final Response<?> _response;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final Retrofit _retrofit;
}