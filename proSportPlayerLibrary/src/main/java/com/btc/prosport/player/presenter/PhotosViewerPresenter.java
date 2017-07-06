package com.btc.prosport.player.presenter;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import rx.functions.Action1;

import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.rx.RxManager;
import com.btc.common.event.generic.EventHandler;
import com.btc.common.mvp.presenter.BasePresenter;
import com.btc.prosport.api.model.Photo;
import com.btc.prosport.api.model.PlaygroundTitle;
import com.btc.prosport.api.model.SportComplexTitle;
import com.btc.prosport.api.model.entity.PhotoEntity;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.prosport.core.manager.proSportApi.ProSportApiManager;
import com.btc.prosport.player.screen.PhotosViewerScreen;

import java.util.List;

@Accessors(prefix = "_")
public final class PhotosViewerPresenter extends BasePresenter<PhotosViewerScreen> {
    public PhotosViewerPresenter(
        @NonNull final RxManager rxManager, @NonNull final ProSportApiManager proSportApiManager) {
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");

        _rxManager = rxManager;
        _proSportApiManager = proSportApiManager;
    }

    public final void displayPlaygroundPhotos(
        @Nullable final PlaygroundTitle playground, @Nullable final List<Photo> photos) {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayPlaygroundPhotos(playground, photos);
        }
    }

    public final void displaySportComplexPhotos(
        @Nullable final SportComplexTitle sportComplex, @Nullable final List<Photo> photos) {
        final val screen = getScreen();
        if (screen != null) {
            screen.displaySportComplexPhotos(sportComplex, photos);
        }
    }

    protected final void displayLoading() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayPhotosLoading();
        }
    }

    protected final void displayLoadingError() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayPhotosLoadingError();
        }
    }

    protected final void performLoadPlaygroundPhotos(final long playgroundId) {
        final val proSportApiManager = getProSportApiManager();
        final val proSportApi = proSportApiManager.getProSportApi();

        final val rxManager = getRxManager();
        rxManager
            .autoManage(proSportApi.getPlaygroundPhotos(playgroundId))
            .observeOn(rxManager.getUIScheduler())
            .subscribeOn(rxManager.getIOScheduler())
            .subscribe(new Action1<List<PhotoEntity>>() {
                @Override
                public void call(@NonNull final List<PhotoEntity> photos) {
                    Contracts.requireMainThread();

                    displayPlaygroundPhotos(null, (List<Photo>) (List<? extends Photo>) photos);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Contracts.requireMainThread();

                    displayLoadingError();
                }
            });
    }

    protected final void performLoadSportComplexPhotos(final long sportComplexId) {
        final val proSportApiManager = getProSportApiManager();
        final val proSportApi = proSportApiManager.getProSportApi();

        final val rxManager = getRxManager();
        rxManager
            .autoManage(proSportApi.getSportComplexPhotos(sportComplexId))
            .observeOn(rxManager.getUIScheduler())
            .subscribeOn(rxManager.getIOScheduler())
            .subscribe(new Action1<List<PhotoEntity>>() {
                @Override
                public void call(@NonNull final List<PhotoEntity> photos) {
                    Contracts.requireMainThread();

                    displayPlaygroundPhotos(null, (List<Photo>) (List<? extends Photo>) photos);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Contracts.requireMainThread();

                    displayLoadingError();
                }
            });
    }

    @CallSuper
    @Override
    protected void onScreenAppear(@NonNull final PhotosViewerScreen screen) {
        super.onScreenAppear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getViewSportComplexPhotosEvent().addHandler(_viewSportComplexPhotosHandler);
        screen.getViewPlaygroundPhotosEvent().addHandler(_viewPlaygroundPhotosHandler);
    }

    @CallSuper
    @Override
    protected void onScreenDisappear(@NonNull final PhotosViewerScreen screen) {
        super.onScreenDisappear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getViewSportComplexPhotosEvent().removeHandler(_viewSportComplexPhotosHandler);
        screen.getViewPlaygroundPhotosEvent().removeHandler(_viewPlaygroundPhotosHandler);
    }

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ProSportApiManager _proSportApiManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final RxManager _rxManager;

    @NonNull
    private final EventHandler<IdEventArgs> _viewPlaygroundPhotosHandler =
        new EventHandler<IdEventArgs>() {
            @Override
            public void onEvent(@NonNull final IdEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                displayLoading();

                performLoadPlaygroundPhotos(eventArgs.getId());
            }
        };

    @NonNull
    private final EventHandler<IdEventArgs> _viewSportComplexPhotosHandler =
        new EventHandler<IdEventArgs>() {
            @Override
            public void onEvent(@NonNull final IdEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                displayLoading();

                performLoadSportComplexPhotos(eventArgs.getId());
            }
        };
}
