package com.btc.prosport.player.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import rx.functions.Action1;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.message.MessageManager;
import com.btc.common.control.manager.rx.RxManager;
import com.btc.common.event.generic.EventHandler;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.extension.eventArgs.UriEventArgs;
import com.btc.common.mvp.presenter.BasePresenter;
import com.btc.prosport.api.model.SportComplexDetail;
import com.btc.prosport.api.model.entity.SportComplexDetailEntity;
import com.btc.prosport.core.manager.proSportApi.ProSportApiManager;
import com.btc.prosport.player.R;
import com.btc.prosport.player.core.eventArgs.ViewPhotoEventArgs;
import com.btc.prosport.player.core.manager.navigation.PlayerNavigationManager;
import com.btc.prosport.player.core.utility.PresenterHelper;
import com.btc.prosport.player.screen.SportComplexDetailScreen;

@Accessors(prefix = "_")
public final class SportComplexDetailPresenter extends BasePresenter<SportComplexDetailScreen> {
    private static final String _LOG_TAG =
        ConstantBuilder.logTag(SportComplexDetailPresenter.class);

    public SportComplexDetailPresenter(
        @NonNull final RxManager rxManager,
        @NonNull final MessageManager messageManager,
        @NonNull final PlayerNavigationManager playerNavigationManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final PresenterHelper presenterHelper) {
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(playerNavigationManager, "playerNavigationManager == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(presenterHelper, "presenterHelper == null");

        _rxManager = rxManager;
        _messageManager = messageManager;
        _playerNavigationManager = playerNavigationManager;
        _proSportApiManager = proSportApiManager;
        _presenterHelper = presenterHelper;
    }

    protected final void displayLoading() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displaySportComplexLoading();
        }
    }

    protected final void displayLoadingError() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displaySportComplexLoadingError();
        }
    }

    protected final void displayPlayground(@Nullable final SportComplexDetail sportComplex) {
        final val screen = getScreen();
        if (screen != null) {
            screen.displaySportComplex(sportComplex);
        }
    }

    protected final void performLoadSportComplex(final long sportComplexId) {
        final val proSportApiManager = getProSportApiManager();
        final val proSportApi = proSportApiManager.getProSportApi();

        final val rxManager = getRxManager();
        rxManager
            .autoManage(proSportApi.getSportComplexDetail(sportComplexId, null))
            .observeOn(rxManager.getUIScheduler())
            .subscribeOn(rxManager.getIOScheduler())
            .subscribe(new Action1<SportComplexDetailEntity>() {
                @Override
                public void call(final SportComplexDetailEntity arg) {
                    Contracts.requireMainThread();

                    displayPlayground(arg);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Contracts.requireMainThread();

                    Log.w(_LOG_TAG, "Failed to load playground. Id: " + sportComplexId, error);

                    getMessageManager().showInfoMessage(R.string.sport_complexes_list_load_fail);
                    displayLoadingError();
                }
            });
    }

    @Override
    protected void onScreenAppear(@NonNull final SportComplexDetailScreen screen) {
        super.onScreenAppear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getViewSportComplexEvent().addHandler(_viewPlaygroundHandler);
        screen.getRefreshSportComplexEvent().addHandler(_refreshPlaygroundHandler);
        screen.getViewSportComplexPhotosEvent().addHandler(_viewPlaygroundPhotosHandler);
        screen.getSportComplexPhoneCallEvent().addHandler(_sportComplexPhoneCallHandler);
    }

    @Override
    protected void onScreenDisappear(@NonNull final SportComplexDetailScreen screen) {
        super.onScreenDisappear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getViewSportComplexEvent().removeHandler(_viewPlaygroundHandler);
        screen.getRefreshSportComplexEvent().removeHandler(_refreshPlaygroundHandler);
        screen.getViewSportComplexPhotosEvent().removeHandler(_viewPlaygroundPhotosHandler);
        screen.getSportComplexPhoneCallEvent().removeHandler(_sportComplexPhoneCallHandler);
    }

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final MessageManager _messageManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final PlayerNavigationManager _playerNavigationManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final PresenterHelper _presenterHelper;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ProSportApiManager _proSportApiManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final RxManager _rxManager;

    @NonNull
    private final EventHandler<IdEventArgs> _refreshPlaygroundHandler =
        new EventHandler<IdEventArgs>() {
            @Override
            public void onEvent(@NonNull final IdEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                performLoadSportComplex(eventArgs.getId());
            }
        };

    @NonNull
    private final EventHandler<UriEventArgs> _sportComplexPhoneCallHandler =
        new EventHandler<UriEventArgs>() {
            @Override
            public void onEvent(@NonNull final UriEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                getPresenterHelper().performPhoneCall(eventArgs.getUri());
            }
        };

    @NonNull
    private final EventHandler<IdEventArgs> _viewPlaygroundHandler =
        new EventHandler<IdEventArgs>() {
            @Override
            public void onEvent(@NonNull final IdEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                displayLoading();

                performLoadSportComplex(eventArgs.getId());
            }
        };

    @NonNull
    private final EventHandler<ViewPhotoEventArgs> _viewPlaygroundPhotosHandler =
        new EventHandler<ViewPhotoEventArgs>() {
            @Override
            public void onEvent(@NonNull final ViewPhotoEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                getPlayerNavigationManager().navigateToSportComplexPhotos(eventArgs.getId(),
                                                                          eventArgs.getPosition());
            }
        };
}
