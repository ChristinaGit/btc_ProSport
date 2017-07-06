package com.btc.prosport.presenter;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.message.MessageManager;
import com.btc.common.control.manager.rx.RxManager;
import com.btc.common.event.generic.EventHandler;
import com.btc.common.mvp.presenter.BasePresenter;
import com.btc.prosport.api.ProSportApi;
import com.btc.prosport.api.exception.ProSportApiException;
import com.btc.prosport.api.model.entity.UserEntity;
import com.btc.prosport.api.model.utility.UserRole;
import com.btc.prosport.api.request.UpdateUserAdditionalInfoParams;
import com.btc.prosport.api.response.ProSportErrorResponse;
import com.btc.prosport.api.response.error.AdditionalInfoErrors;
import com.btc.prosport.common.R;
import com.btc.prosport.core.manager.proSportServiceHelper.ProSportAccountHelper;
import com.btc.prosport.screen.ProSportAdditionalInfoScreen;
import com.btc.prosport.screen.fragment.proSportAuth.proSportAdditionalInfo.AdditionalInfoArgs;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

import retrofit2.adapter.rxjava.HttpException;

@Accessors(prefix = "_")
public class ProSportAdditionalInfoPresenter extends BasePresenter<ProSportAdditionalInfoScreen> {
    private static final String _LOG_TAG =
        ConstantBuilder.logTag(ProSportAdditionalInfoPresenter.class);

    public ProSportAdditionalInfoPresenter(
        @NonNull final RxManager rxManager,
        @NonNull final MessageManager messageManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull final ProSportApi proSportApi,
        @NonNull final UserRole userRole) {
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");
        Contracts.requireNonNull(proSportApi, "proSportApi == null");
        Contracts.requireNonNull(userRole, "userRole == null");

        _rxManager = rxManager;
        _messageManager = messageManager;
        _proSportAccountHelper = proSportAccountHelper;
        _proSportApi = proSportApi;
        _userRole = userRole;
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

        val screen = getScreen();
        if (screen != null) {
            val emailErrors = AdditionalInfoErrors.getEmailErrors(errorResponse);
            if (emailErrors != null) {
                screen.displayEmailAddressError(joinErrors(emailErrors));
            }
            val lastNameErrors = AdditionalInfoErrors.getLastNameErrors(errorResponse);
            if (lastNameErrors != null) {
                screen.displayLastNameError(joinErrors(lastNameErrors));
            }
            val firstNameErrors = AdditionalInfoErrors.getFirstNameErrors(errorResponse);
            if (firstNameErrors != null) {
                screen.displayFirstNameError(joinErrors(firstNameErrors));
            }
        }
    }

    protected void handleServerException(final HttpException error) {
        if (error.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            getMessageManager().showErrorMessage(R.string.message_error_auth_unauthorized);
        } else {
            getMessageManager().showErrorMessage(R.string.message_error_server_side);
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

    @Override
    protected void onScreenAppear(@NonNull final ProSportAdditionalInfoScreen screen) {
        super.onScreenAppear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getSendAdditionInfoEvent().addHandler(_sendAdditionalInfoHandler);
    }

    @Override
    protected void onScreenDisappear(@NonNull final ProSportAdditionalInfoScreen screen) {
        super.onScreenDisappear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getSendAdditionInfoEvent().removeHandler(_sendAdditionalInfoHandler);
    }

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final MessageManager _messageManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ProSportAccountHelper _proSportAccountHelper;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ProSportApi _proSportApi;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final RxManager _rxManager;

    private final EventHandler<AdditionalInfoArgs> _sendAdditionalInfoHandler =
        new EventHandler<AdditionalInfoArgs>() {
            @Override
            public void onEvent(@NonNull final AdditionalInfoArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                performSendUserData(eventArgs);
            }
        };

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final UserRole _userRole;

    private void performSendUserData(@NonNull final AdditionalInfoArgs eventArgs) {
        Contracts.requireNonNull(eventArgs, "eventArgs == null");

        displayLoading();

        val rxManager = getRxManager();
        rxManager
            .autoManage(getProSportAccountHelper()
                            .withAccessToken(new Func1<String, Observable<UserEntity>>() {
                                @Override
                                public Observable<UserEntity> call(final String token) {
                                    val params = new UpdateUserAdditionalInfoParams(
                                        eventArgs.getFirstName(),
                                        eventArgs.getLastName(),
                                        eventArgs.getEmail());
                                    return getProSportApi().updateUserAdditionalInfo(token, params);
                                }
                            }, true)
                            .subscribeOn(rxManager.getIOScheduler())
                            .observeOn(rxManager.getUIScheduler()))
            .subscribe(new Action1<UserEntity>() {
                @Override
                public void call(final UserEntity userEntity) {
                    val screen = getScreen();
                    if (screen != null) {
                        screen.displaySuccessfulSendData();
                    }
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    if (error instanceof ProSportApiException) {
                        val infoErrorResponse =
                            ((ProSportApiException) error).getErrorBodyAs(ProSportErrorResponse
                                                                              .class);
                        if (infoErrorResponse != null) {
                            handleProSportApiException(infoErrorResponse);
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
}
