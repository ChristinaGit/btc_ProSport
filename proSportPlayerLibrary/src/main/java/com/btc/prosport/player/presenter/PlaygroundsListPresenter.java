package com.btc.prosport.player.presenter;

import android.support.annotation.CallSuper;
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
import com.btc.prosport.api.model.PlaygroundPreview;
import com.btc.prosport.api.model.entity.PlaygroundPreviewEntity;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.prosport.core.manager.proSportApi.ProSportApiManager;
import com.btc.prosport.player.R;
import com.btc.prosport.player.screen.PlaygroundsListScreen;

import java.util.List;

@Accessors(prefix = "_")
public final class PlaygroundsListPresenter extends BasePresenter<PlaygroundsListScreen> {
    private static final String _LOG_TAG = ConstantBuilder.logTag(PlaygroundsListPresenter.class);

    public PlaygroundsListPresenter(
        @NonNull final MessageManager messageManager,
        @NonNull final RxManager rxManager,
        @NonNull final ProSportApiManager proSportApiManager) {
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");

        _messageManager = messageManager;
        _rxManager = rxManager;
        _proSportApiManager = proSportApiManager;
    }

    protected final void displayPlaygrounds(@Nullable final List<PlaygroundPreview> playgrounds) {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayPlaygrounds(playgrounds);
        }
    }

    protected final void displayPlaygroundsLoading() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayPlaygroundsLoading();
        }
    }

    protected final void displayPlaygroundsLoadingError() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayPlaygroundsLoadingError();
        }
    }

    @CallSuper
    @Override
    protected void onScreenAppear(@NonNull final PlaygroundsListScreen screen) {
        super.onScreenAppear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getRefreshPlaygroundsEvent().addHandler(_refreshPlaygroundsHandler);
        screen.getReservePlaygroundEvent().addHandler(_reservePlaygroundHandler);
        screen.getViewPlaygroundsEvent().addHandler(_viewPlaygroundsHandler);
    }

    @CallSuper
    @Override
    protected void onScreenDisappear(@NonNull final PlaygroundsListScreen screen) {
        super.onScreenDisappear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getRefreshPlaygroundsEvent().removeHandler(_refreshPlaygroundsHandler);
        screen.getReservePlaygroundEvent().removeHandler(_reservePlaygroundHandler);
        screen.getViewPlaygroundsEvent().removeHandler(_viewPlaygroundsHandler);
    }

    protected void performLoadPlaygrounds(
        final long sportComplexId) {
        final val rxManager = getRxManager();
        final val proSportApi = getProSportApiManager().getProSportApi();

        proSportApi
            .getPlaygroundsPreviews(sportComplexId, null, null, null)
            .observeOn(rxManager.getUIScheduler())
            .subscribeOn(rxManager.getIOScheduler())
            .subscribe(new Action1<List<PlaygroundPreviewEntity>>() {
                @Override
                public void call(final List<PlaygroundPreviewEntity> response) {
                    Contracts.requireMainThread();

                    final val playgrounds =
                        (List<PlaygroundPreview>) (List<? extends PlaygroundPreview>) response;

                    displayPlaygrounds(playgrounds);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Contracts.requireMainThread();

                    Log.w(_LOG_TAG, "Failed to load playgrounds.", error);

                    getMessageManager().showInfoMessage(R.string.playgrounds_list_load_fail);
                    displayPlaygroundsLoadingError();
                }
            });
    }

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final MessageManager _messageManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ProSportApiManager _proSportApiManager;

    @NonNull
    private final EventHandler<IdEventArgs> _reservePlaygroundHandler =
        new EventHandler<IdEventArgs>() {
            @Override
            public void onEvent(@NonNull final IdEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");
            }
        };

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final RxManager _rxManager;

    @NonNull
    private final EventHandler<IdEventArgs> _refreshPlaygroundsHandler =
        new EventHandler<IdEventArgs>() {
            @Override
            public void onEvent(@NonNull final IdEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                performLoadPlaygrounds(eventArgs.getId());
            }
        };

    @NonNull
    private final EventHandler<IdEventArgs> _viewPlaygroundsHandler =
        new EventHandler<IdEventArgs>() {
            @Override
            public void onEvent(@NonNull final IdEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                displayPlaygroundsLoading();

                performLoadPlaygrounds(eventArgs.getId());
            }
        };
}
