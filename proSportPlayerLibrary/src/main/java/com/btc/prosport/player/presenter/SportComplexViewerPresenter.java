package com.btc.prosport.player.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

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
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.prosport.api.model.SportComplexTitle;
import com.btc.prosport.api.model.entity.SportComplexTitleEntity;
import com.btc.prosport.core.manager.account.AccountManager;
import com.btc.prosport.core.manager.account.ProSportAccount;
import com.btc.prosport.core.manager.navigation.ProSportNavigationManager;
import com.btc.prosport.core.manager.photo.PhotoManager;
import com.btc.prosport.core.manager.proSportApi.ProSportApiManager;
import com.btc.prosport.core.manager.proSportServiceHelper.ProSportAccountHelper;
import com.btc.prosport.player.R;
import com.btc.prosport.player.core.manager.navigation.PlayerNavigationManager;
import com.btc.prosport.player.screen.SportComplexViewerScreen;

import okhttp3.ResponseBody;
import retrofit2.Response;

@Accessors(prefix = "_")
public final class SportComplexViewerPresenter
    extends BasePlayerNavigationPresenter<SportComplexViewerScreen> {
    private static final String _LOG_TAG =
        ConstantBuilder.logTag(SportComplexViewerPresenter.class);

    public SportComplexViewerPresenter(
        @NonNull final AccountManager<ProSportAccount> proSportAccountManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull final PlayerNavigationManager playerNavigationManager,
        @NonNull final RxManager rxManager,
        @NonNull final MessageManager messageManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final PhotoManager photoManager,
        @NonNull final ProSportNavigationManager proSportNavigationManager) {
        super(
            Contracts.requireNonNull(proSportAccountManager, "proSportAccountManager == null"),
            Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null"),
            Contracts.requireNonNull(rxManager, "rxManager == null"),
            Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null"),
            Contracts.requireNonNull(playerNavigationManager,
                                     "playerNavigationManager == " + "null"),
            Contracts.requireNonNull(messageManager, "messageManager == null"),
            Contracts.requireNonNull(photoManager, "photoManager == null"),
            Contracts.requireNonNull(proSportNavigationManager,
                                     "proSportNavigationManager == null"));
    }

    protected final void displayChangeSportComplexFavoriteStateError() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayChangeSportComplexFavoriteStateError();
        }
    }

    protected final void displaySportComplex(@Nullable final SportComplexTitle sportComplex) {
        final val screen = getScreen();
        if (screen != null) {
            screen.displaySportComplex(sportComplex);
        }
    }

    protected final void displaySportComplexLoading() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displaySportComplexLoading();
        }
    }

    protected final void displaySportComplexLoadingError() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displaySportComplexLoadingError();
        }
    }

    @Override
    protected void onScreenCreate(@NonNull final SportComplexViewerScreen screen) {
        super.onScreenCreate(Contracts.requireNonNull(screen, "screen == null"));

        screen.getViewSportComplexEvent().addHandler(_viewSportComplexHandler);
        screen.getAddSportComplexToFavoriteEvent().addHandler(_addSportComplexToFavoriteHandler);
        screen
            .getRemoveSportComplexFromFavoriteEvent()
            .addHandler(_removeSportComplexFromFavoritesHandler);
    }

    @Override
    protected void onScreenDestroy(@NonNull final SportComplexViewerScreen screen) {
        super.onScreenDestroy(Contracts.requireNonNull(screen, "screen == null"));

        screen.getViewSportComplexEvent().removeHandler(_viewSportComplexHandler);
        screen.getAddSportComplexToFavoriteEvent().removeHandler(_addSportComplexToFavoriteHandler);
        screen
            .getRemoveSportComplexFromFavoriteEvent()
            .removeHandler(_removeSportComplexFromFavoritesHandler);
    }

    protected void performAddSportComplexToFavorites(final long sportComplexId) {
        final val rxManager = getRxManager();
        final val proSportApi = getProSportApiManager().getProSportApi();

        getProSportAccountHelper()
            .withAccessToken(new Func1<String, Observable<Response<ResponseBody>>>() {
                @Override
                public Observable<Response<ResponseBody>> call(final String token) {
                    final val favorite = proSportApi.favoriteSportComplex(sportComplexId, token);
                    return rxManager.autoManage(favorite);
                }
            }, false)
            .subscribeOn(rxManager.getIOScheduler())
            .observeOn(rxManager.getUIScheduler())
            .subscribe(new Action1<Response<ResponseBody>>() {
                @Override
                public void call(final Response<ResponseBody> response) {
                    Contracts.requireMainThread();

                    performLoadSportComplex(sportComplexId);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Contracts.requireMainThread();

                    Log.w(_LOG_TAG, "Failed to add to favorites", error);

                    getMessageManager().showErrorMessage(R.string
                                                             .sport_complex_viewer_failed_add_to_favorites);
                    displayChangeSportComplexFavoriteStateError();
                }
            });
    }

    protected void performRemoveSportComplexFromFavorites(final long sportComplexId) {
        final val rxManager = getRxManager();
        final val proSportApi = getProSportApiManager().getProSportApi();

        getProSportAccountHelper()
            .withRequiredAccessToken(new Func1<String, Observable<Response<ResponseBody>>>() {
                @Override
                public Observable<Response<ResponseBody>> call(final String token) {
                    final val unfavorite =
                        proSportApi.unfavoriteSportComplex(sportComplexId, token);
                    return rxManager.autoManage(unfavorite);
                }
            }, true)

            .subscribeOn(rxManager.getIOScheduler())
            .observeOn(rxManager.getUIScheduler())
            .subscribe(new Action1<Response<ResponseBody>>() {
                @Override
                public void call(final Response<ResponseBody> response) {
                    Contracts.requireMainThread();

                    performLoadSportComplex(sportComplexId);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Contracts.requireMainThread();

                    Log.w(_LOG_TAG, "Failed to remove from favorites", error);

                    getMessageManager().showErrorMessage(R.string
                                                             .sport_complex_viewer_failed_remove_from_favorites);
                    displayChangeSportComplexFavoriteStateError();
                }
            });
    }

    @NonNull
    private final EventHandler<IdEventArgs> _addSportComplexToFavoriteHandler =
        new EventHandler<IdEventArgs>() {
            @Override
            public void onEvent(@NonNull final IdEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                displaySportComplexLoading();

                performAddSportComplexToFavorites(eventArgs.getId());
            }
        };

    @NonNull
    private final EventHandler<IdEventArgs> _removeSportComplexFromFavoritesHandler =
        new EventHandler<IdEventArgs>() {
            @Override
            public void onEvent(@NonNull final IdEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                displaySportComplexLoading();

                performRemoveSportComplexFromFavorites(eventArgs.getId());
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

    private void performLoadSportComplex(final long sportComplexId) {
        final val rxManager = getRxManager();

        getProSportAccountHelper()
            .withAccessToken(new Func1<String, Observable<SportComplexTitleEntity>>() {
                @Override
                public Observable<SportComplexTitleEntity> call(final String token) {
                    return rxManager.autoManage(getProSportApiManager()
                                                    .getProSportApi()
                                                    .getSportComplexTitle(sportComplexId, token));
                }
            }, false)
            .subscribeOn(rxManager.getIOScheduler())
            .observeOn(rxManager.getUIScheduler())
            .subscribe(new Action1<SportComplexTitleEntity>() {
                @Override
                public void call(final SportComplexTitleEntity sportComplex) {
                    Contracts.requireMainThread();

                    displaySportComplex(sportComplex);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Contracts.requireMainThread();

                    Log.w(_LOG_TAG, "Failed to load sport complex", error);

                    displaySportComplexLoadingError();
                }
            });
    }
}
