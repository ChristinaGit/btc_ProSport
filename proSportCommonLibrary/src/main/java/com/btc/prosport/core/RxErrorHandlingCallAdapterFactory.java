package com.btc.prosport.core;

import android.support.annotation.NonNull;

import lombok.experimental.Accessors;
import lombok.val;

import rx.Observable;
import rx.functions.Func1;

import com.btc.common.contract.Contracts;
import com.btc.prosport.api.exception.ProSportApiException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

@Accessors(prefix = "_")
public final class RxErrorHandlingCallAdapterFactory extends CallAdapter.Factory {
    @NonNull
    public static CallAdapter.Factory create() {
        return new RxErrorHandlingCallAdapterFactory();
    }

    @Override
    @NonNull
    public CallAdapter<?> get(
        final Type returnType, final Annotation[] annotations, final Retrofit retrofit) {
        return new RxCallAdapterWrapper(retrofit,
                                        _rxJavaCallAdapterFactory.get(returnType,
                                                                      annotations,
                                                                      retrofit));
    }

    @NonNull
    private final RxJavaCallAdapterFactory _rxJavaCallAdapterFactory;

    private RxErrorHandlingCallAdapterFactory() {
        _rxJavaCallAdapterFactory = RxJavaCallAdapterFactory.create();
    }

    private static class RxCallAdapterWrapper implements CallAdapter<Observable<?>> {
        public RxCallAdapterWrapper(
            @NonNull final Retrofit retrofit, @NonNull final CallAdapter<?> wrapped) {
            Contracts.requireNonNull(retrofit, "retrofit == null");
            Contracts.requireNonNull(wrapped, "wrapped == null");

            _retrofit = retrofit;
            _wrapped = wrapped;
        }

        @Override
        public Type responseType() {
            return _wrapped.responseType();
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public <T> Observable<?> adapt(final Call<T> call) {
            return ((Observable) _wrapped.adapt(call)).onErrorResumeNext(new Func1<Throwable,
                Observable>() {
                @Override
                public Observable call(final Throwable throwable) {
                    return Observable.error(asRetrofitException(throwable));
                }
            });
        }

        @NonNull
        private final Retrofit _retrofit;

        @NonNull
        private final CallAdapter<?> _wrapped;

        private Throwable asRetrofitException(final Throwable throwable) {
            if (throwable instanceof HttpException) {
                final val httpException = (HttpException) throwable;
                if (httpException.code() == HttpURLConnection.HTTP_BAD_REQUEST) {
                    final val response = httpException.response();
                    return new ProSportApiException(_retrofit, response);
                }
            }

            return throwable;
        }
    }
}