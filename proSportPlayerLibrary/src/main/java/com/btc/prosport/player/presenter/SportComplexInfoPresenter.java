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
import com.btc.common.mvp.presenter.BasePresenter;
import com.btc.prosport.api.model.SportComplexInfo;
import com.btc.prosport.api.model.entity.SportComplexInfoEntity;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.prosport.core.manager.proSportApi.ProSportApiManager;
import com.btc.prosport.player.R;
import com.btc.prosport.player.core.manager.navigation.PlayerNavigationManager;
import com.btc.prosport.player.screen.SportComplexInfoScreen;

@Accessors(prefix = "_")
public final class SportComplexInfoPresenter extends BasePresenter<SportComplexInfoScreen> {
    private static final String _LOG_TAG = ConstantBuilder.logTag(SportComplexInfoPresenter.class);

    public SportComplexInfoPresenter(
        @NonNull final RxManager rxManager,
        @NonNull final MessageManager messageManager,
        @NonNull final PlayerNavigationManager playerNavigationManager,
        @NonNull final ProSportApiManager proSportApiManager) {
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(playerNavigationManager, "playerNavigationManager == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");

        _rxManager = rxManager;
        _messageManager = messageManager;
        _playerNavigationManager = playerNavigationManager;
        _proSportApiManager = proSportApiManager;
    }

    public void displaySportComplex(@Nullable final SportComplexInfo sportComplex) {
        final val screen = getScreen();
        if (screen != null) {
            screen.displaySportComplex(sportComplex);
        }
    }

    public void displaySportComplexLoading() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displaySportComplexLoading();
        }
    }

    public void displaySportComplexLoadingError() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displaySportComplexLoadingError();
        }
    }

    @Override
    protected void onScreenAppear(@NonNull final SportComplexInfoScreen screen) {
        super.onScreenAppear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getViewSportComplexEvent().addHandler(_viewSportComplexHandler);
        screen.getViewSportComplexDetailsEvent().addHandler(_viewSportComplexDetailsHandler);
    }

    @Override
    protected void onScreenDisappear(@NonNull final SportComplexInfoScreen screen) {
        super.onScreenDisappear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getViewSportComplexEvent().removeHandler(_viewSportComplexHandler);
        screen.getViewSportComplexDetailsEvent().removeHandler(_viewSportComplexDetailsHandler);
    }

    protected void performLoadSportComplex(final long sportComplexId) {
        final val proSportApiManager = getProSportApiManager();
        final val proSportApi = proSportApiManager.getProSportApi();

        final val rxManager = getRxManager();
        rxManager
            .autoManage(proSportApi.getSportComplexInfo(sportComplexId, null))
            .observeOn(rxManager.getUIScheduler())
            .subscribeOn(rxManager.getIOScheduler())
            .subscribe(new Action1<SportComplexInfoEntity>() {
                @Override
                public void call(final SportComplexInfoEntity sportComplex) {
                    Contracts.requireMainThread();

                    displaySportComplex(sportComplex);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Contracts.requireMainThread();

                    Log.w(_LOG_TAG, "Failed to load sport complex. Id: " + sportComplexId, error);

                    getMessageManager().showInfoMessage(R.string.sport_complexes_list_load_fail);
                    displaySportComplexLoadingError();
                }
            });
    }

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final MessageManager _messageManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final PlayerNavigationManager _playerNavigationManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ProSportApiManager _proSportApiManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final RxManager _rxManager;

    @NonNull
    private final EventHandler<IdEventArgs> _viewSportComplexDetailsHandler =
        new EventHandler<IdEventArgs>() {
            @Override
            public void onEvent(@NonNull final IdEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                getPlayerNavigationManager().navigateToSportComplexDetails(eventArgs.getId());
            }
        };

    @NonNull
    private final EventHandler<IdEventArgs> _viewSportComplexHandler =
        new EventHandler<IdEventArgs>() {
            @Override
            public void onEvent(@NonNull final IdEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                displaySportComplexLoading();

                performLoadSportComplex(eventArgs.getId());
            }
        };
}
