package com.btc.prosport.di.application.module;

import android.support.annotation.NonNull;

import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.task.TaskManager;
import com.btc.common.utility.LocaleUtils;
import com.btc.prosport.api.ProSportApi;
import com.btc.prosport.api.ProSportContract;
import com.btc.prosport.api.debug.DebugProSportApi;
import com.btc.prosport.api.model.utility.UserRole;
import com.btc.prosport.api.response.ProSportErrorResponse;
import com.btc.prosport.core.RxErrorHandlingCallAdapterFactory;
import com.btc.prosport.core.manager.notificationEvent.ProSportInternalNotificationEventManger;
import com.btc.prosport.core.manager.notificationEvent.ProSportMangerNotificationEventManager;
import com.btc.prosport.core.manager.notificationEvent.ProSportPlayerNotificationEventManager;
import com.btc.prosport.di.application.ApplicationScope;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Map;

import dagger.Module;
import dagger.Provides;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@ApplicationScope
public class ProSportApiApplicationModule {
    private static final String _LOG_TAG =
        ConstantBuilder.logTag(ProSportApiApplicationModule.class);

    public ProSportApiApplicationModule(
        @NonNull final UserRole userRole, final boolean debug) {
        Contracts.requireNonNull(userRole, "userRole == null");

        _userRole = userRole;
        _debug = debug;
    }

    @Provides
    @ApplicationScope
    @NonNull
    public final Gson provideProSportApiParser() {
        final val errorAdapter = new JsonDeserializer<ProSportErrorResponse>() {
            @Override
            public ProSportErrorResponse deserialize(
                final JsonElement json,
                final Type typeOfT,
                final JsonDeserializationContext context)
                throws JsonParseException {

                final val type = new TypeToken<Map<String, Object>>() {
                }.getType();
                final Map<String, Object> fieldsMap = context.deserialize(json, type);

                return new ProSportErrorResponse(fieldsMap);
            }
        };
        return new GsonBuilder()
            .registerTypeAdapter(ProSportErrorResponse.class, errorAdapter)
            .create();
    }

    @Provides
    @ApplicationScope
    @NonNull
    public final ProSportMangerNotificationEventManager
    provideProSportMangerNotificationEventManager(
        @NonNull final ProSportInternalNotificationEventManger internalManger) {
        Contracts.requireNonNull(internalManger, "internalManger == null");

        return internalManger;
    }

    @Provides
    @ApplicationScope
    @NonNull
    public final ProSportInternalNotificationEventManger
    provideProSportNotificationEventInternalManger() {
        return new ProSportInternalNotificationEventManger();
    }

    @Provides
    @ApplicationScope
    @NonNull
    public final ProSportPlayerNotificationEventManager
    provideProSportPLayerNotificationEventManager(
        @NonNull final ProSportInternalNotificationEventManger internalManger) {
        Contracts.requireNonNull(internalManger, "internalManger == null");

        return internalManger;
    }

    @Provides
    @ApplicationScope
    @NonNull
    public final Retrofit provideProSportRetrofit(@NonNull final Gson proSportApiParser) {
        Contracts.requireNonNull(proSportApiParser, "proSportApiParser == null");

        final val retrofitBuilder = new Retrofit.Builder();

        final val httpClientBuild = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(final Chain chain)
                throws IOException {
                final val original = chain.request();

                final val languageTag = LocaleUtils.getLanguageTag(Locale.getDefault());

                final val requestBuilder = original.newBuilder();
                if (languageTag != null) {
                    requestBuilder.header("Accept-Language", languageTag);
                }
                requestBuilder.method(original.method(), original.body());

                return chain.proceed(requestBuilder.build());
            }
        });
        if (_debug) {
            httpClientBuild.addInterceptor(new HttpLoggingInterceptor().setLevel(Level.BODY));
        }

        retrofitBuilder.client(httpClientBuild.build());
        return retrofitBuilder
            .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(proSportApiParser))
            .baseUrl(new HttpUrl.Builder()
                         .scheme(ProSportContract.SCHEME)
                         .host(ProSportContract.HOST)
                         .port(ProSportContract.PORT)
                         .addEncodedPathSegments(ProSportContract.PATH_SEGMENT_API_V1 + "/")
                         .build())
            .build();
    }

    @Provides
    @ApplicationScope
    @NonNull
    public final ProSportApi provideProSportService(
        @NonNull final Retrofit retrofit, @NonNull final TaskManager taskManager) {
        Contracts.requireNonNull(retrofit, "retrofit == null");
        Contracts.requireNonNull(taskManager, "taskManager == null");

        return _debug ? new DebugProSportApi() : retrofit.create(ProSportApi.class);
    }

    @Provides
    @ApplicationScope
    @NonNull
    public final UserRole provideUserRole() {
        return _userRole;
    }

    private final boolean _debug;

    @NonNull
    private final UserRole _userRole;
}
