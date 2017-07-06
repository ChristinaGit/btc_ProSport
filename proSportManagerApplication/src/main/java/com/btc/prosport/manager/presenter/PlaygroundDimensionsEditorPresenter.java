package com.btc.prosport.manager.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.message.MessageManager;
import com.btc.common.control.manager.rx.RxManager;
import com.btc.common.event.generic.EventHandler;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.mvp.presenter.BasePresenter;
import com.btc.prosport.api.model.PlaygroundPreview;
import com.btc.prosport.api.model.entity.PlaygroundPreviewEntity;
import com.btc.prosport.api.request.ChangePlaygroundDimensionsParams;
import com.btc.prosport.core.manager.proSportApi.ProSportApiManager;
import com.btc.prosport.core.manager.proSportServiceHelper.ProSportAccountHelper;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.Dimension;
import com.btc.prosport.manager.core.eventArgs.ChangePlaygroundDimensionsEventArgs;
import com.btc.prosport.manager.screen.PlaygroundDimensionsEditorScreen;

import okhttp3.ResponseBody;

@Accessors(prefix = "_")
public final class PlaygroundDimensionsEditorPresenter
    extends BasePresenter<PlaygroundDimensionsEditorScreen> {

    private static final String _LOG_TAG =
        ConstantBuilder.logTag(PlaygroundDimensionsEditorPresenter.class);

    public PlaygroundDimensionsEditorPresenter(
        @NonNull final MessageManager messageManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final RxManager rxManager) {
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(rxManager, "rxManager == null");

        _messageManager = messageManager;
        _proSportAccountHelper = proSportAccountHelper;
        _proSportApiManager = proSportApiManager;
        _rxManager = rxManager;
    }

    public void displayDimensionsLoading() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayDimensionsLoading();
        }
    }

    public void displayDimensionsLoadingError() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayDimensionsLoadingError();
        }
    }

    @Override
    protected void onScreenAppear(
        @NonNull final PlaygroundDimensionsEditorScreen screen) {
        super.onScreenAppear(screen);

        screen.getViewPlaygroundDimensionsEvent().addHandler(_viewDimensionsHandler);
        screen.getChangePlaygroundDimensionsEvent().addHandler(_changePlaygroundDimensionsHandler);
    }

    @Override
    protected void onScreenDisappear(
        @NonNull final PlaygroundDimensionsEditorScreen screen) {
        super.onScreenDisappear(screen);

        screen.getViewPlaygroundDimensionsEvent().removeHandler(_viewDimensionsHandler);
        screen
            .getChangePlaygroundDimensionsEvent()
            .removeHandler(_changePlaygroundDimensionsHandler);
    }

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final MessageManager _messageManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ProSportAccountHelper _proSportAccountHelper;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ProSportApiManager _proSportApiManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final RxManager _rxManager;

    @NonNull
    private final EventHandler<ChangePlaygroundDimensionsEventArgs>
        _changePlaygroundDimensionsHandler =
        new EventHandler<ChangePlaygroundDimensionsEventArgs>() {
            @Override
            public void onEvent(@NonNull final ChangePlaygroundDimensionsEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                final long playgroundId = eventArgs.getId();
                final Integer width = eventArgs.getWidth();
                final Integer length = eventArgs.getLength();
                final Integer height = eventArgs.getHeight();

                performChangeDimensions(playgroundId, width, length, height);
            }
        };

    @NonNull
    private final EventHandler<IdEventArgs> _viewDimensionsHandler =
        new EventHandler<IdEventArgs>() {
            @Override
            public void onEvent(@NonNull final IdEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");
                final val playgroundId = eventArgs.getId();

                displayDimensionsLoading();

                performLoadDimensions(playgroundId);
            }
        };

    private void displayPlaygroundDimensions(@Nullable final PlaygroundPreview playgroundPreview) {
        final val screen = getScreen();
        if (screen != null) {
            if (playgroundPreview != null) {
                final val dimension = new Dimension(
                    playgroundPreview.getWidth(),
                    playgroundPreview.getLength(),
                    playgroundPreview.getHeight());
                screen.displayPlaygroundDimensions(dimension);
            }
        }
    }

    private void performChangeDimensions(
        final long playgroundId,
        @Nullable final Integer width,
        @Nullable final Integer length,
        @Nullable final Integer height) {
        final val proSportApi = getProSportApiManager().getProSportApi();
        final val rxManager = getRxManager();
        final val messageManager = getMessageManager();

        messageManager.showProgressMessage(
            R.string.playground_editor_progress_title,
            R.string.playground_editor_dimensions_progress_message);

        getProSportAccountHelper()
            .withAccessToken(new Func1<String, Observable<ResponseBody>>() {
                @Override
                public Observable<ResponseBody> call(final String token) {
                    Contracts.requireWorkerThread();

                    final val changePlaygroundDimensionsParams =
                        new ChangePlaygroundDimensionsParams(width, length, height);

                    return proSportApi.changePlaygroundDimensions(playgroundId,
                                                                  token,
                                                                  changePlaygroundDimensionsParams);
                }
            }, true)
            .subscribeOn(rxManager.getIOScheduler())
            .observeOn(rxManager.getUIScheduler())
            .subscribe(new Action1<ResponseBody>() {
                @Override
                public void call(final ResponseBody responseBody) {
                    Contracts.requireMainThread();
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Contracts.requireMainThread();

                    revertChangedDimensions();
                    messageManager.dismissProgressMessage();
                    getMessageManager().showInfoMessage(R.string.request_fail);
                }
            }, new Action0() {
                @Override
                public void call() {
                    messageManager.dismissProgressMessage();
                }
            });
    }

    private void performLoadDimensions(final long playgroundId) {
        final val proSportApi = getProSportApiManager().getProSportApi();
        final val rxManager = getRxManager();

        getProSportAccountHelper()
            .withAccessToken(new Func1<String, Observable<PlaygroundPreviewEntity>>() {
                @Override
                public Observable<PlaygroundPreviewEntity> call(final String token) {
                    Contracts.requireWorkerThread();

                    return proSportApi.getPlaygroundPreview(playgroundId, token);
                }
            }, true)
            .subscribeOn(rxManager.getIOScheduler())
            .observeOn(rxManager.getUIScheduler())
            .subscribe(new Action1<PlaygroundPreviewEntity>() {
                @Override
                public void call(final PlaygroundPreviewEntity playgroundPreview) {
                    Contracts.requireMainThread();

                    displayPlaygroundDimensions(playgroundPreview);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Contracts.requireMainThread();

                    Log.w(
                        _LOG_TAG,
                        "Failed to load playground. Playground Id: " + playgroundId,
                        error);

                    getMessageManager().showInfoMessage(R.string.loading_fail);
                    displayDimensionsLoadingError();
                }
            });
    }

    private void revertChangedDimensions() {
        final val screen = getScreen();
        if (screen != null) {
            screen.revertChangedDimensions();
        }
    }
}
