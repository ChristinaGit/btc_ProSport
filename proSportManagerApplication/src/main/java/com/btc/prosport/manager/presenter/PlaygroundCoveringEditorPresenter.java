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
import rx.functions.Func2;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.message.MessageManager;
import com.btc.common.control.manager.rx.RxManager;
import com.btc.common.event.generic.EventHandler;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.mvp.presenter.BasePresenter;
import com.btc.common.utility.tuple.Tuple2;
import com.btc.common.utility.tuple.Tuples;
import com.btc.prosport.api.model.Covering;
import com.btc.prosport.api.model.entity.CoveringEntity;
import com.btc.prosport.api.model.entity.PlaygroundPreviewEntity;
import com.btc.prosport.api.request.ChangePlaygroundCoveringParams;
import com.btc.prosport.core.manager.proSportApi.ProSportApiManager;
import com.btc.prosport.core.manager.proSportServiceHelper.ProSportAccountHelper;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.eventArgs.ChangePlaygroundCoveringEventArgs;
import com.btc.prosport.manager.screen.PlaygroundCoveringEditorScreen;

import java.util.List;

import okhttp3.ResponseBody;

@Accessors(prefix = "_")
public final class PlaygroundCoveringEditorPresenter
    extends BasePresenter<PlaygroundCoveringEditorScreen> {

    private static final String _LOG_TAG =
        ConstantBuilder.logTag(PlaygroundCoveringEditorPresenter.class);

    public PlaygroundCoveringEditorPresenter(
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

    public void displayLoading() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayLoading();
        }
    }

    public void displayLoadingError() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayLoadingError();
        }
    }

    @Override
    protected void onScreenAppear(
        @NonNull final PlaygroundCoveringEditorScreen screen) {
        super.onScreenAppear(screen);

        screen.getViewPlaygroundCoveringEvent().addHandler(_viewCoveringHandler);
        screen.getChangePlaygroundCoveringEvent().addHandler(_changePlaygroundCoveringHandler);
    }

    @Override
    protected void onScreenDisappear(
        @NonNull final PlaygroundCoveringEditorScreen screen) {
        super.onScreenDisappear(screen);

        screen.getViewPlaygroundCoveringEvent().removeHandler(_viewCoveringHandler);
        screen.getChangePlaygroundCoveringEvent().removeHandler(_changePlaygroundCoveringHandler);
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
    private final EventHandler<ChangePlaygroundCoveringEventArgs> _changePlaygroundCoveringHandler =
        new EventHandler<ChangePlaygroundCoveringEventArgs>() {
            @Override
            public void onEvent(@NonNull final ChangePlaygroundCoveringEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                final long playgroundId = eventArgs.getId();
                final long coveringId = eventArgs.getCoveringId();

                performChangeCovering(playgroundId, coveringId);
            }
        };

    @NonNull
    private final EventHandler<IdEventArgs> _viewCoveringHandler = new EventHandler<IdEventArgs>() {
        @Override
        public void onEvent(@NonNull final IdEventArgs eventArgs) {
            Contracts.requireNonNull(eventArgs, "eventArgs == null");
            final val playgroundId = eventArgs.getId();

            displayLoading();

            performLoadCovering(playgroundId);
        }
    };

    private void displayPlaygroundCovering(
        @Nullable final Covering covering, @Nullable final List<Covering> coverings) {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayPlaygroundCovering(covering, coverings);
        }
    }

    @NonNull
    private Observable<List<CoveringEntity>> getCoveringsObservable() {
        final val proSportApi = getProSportApiManager().getProSportApi();

        return getProSportAccountHelper().withAccessToken(new Func1<String,
            Observable<List<CoveringEntity>>>() {
            @Override
            public Observable<List<CoveringEntity>> call(final String token) {
                Contracts.requireWorkerThread();

                return proSportApi.getCoverings(token);
            }
        }, true);
    }

    @NonNull
    private Observable<PlaygroundPreviewEntity> getPlaygroundObservable(final long playgroundId) {
        final val proSportApi = getProSportApiManager().getProSportApi();

        return getProSportAccountHelper().withAccessToken(new Func1<String,
            Observable<PlaygroundPreviewEntity>>() {
            @Override
            public Observable<PlaygroundPreviewEntity> call(final String token) {
                Contracts.requireWorkerThread();

                return proSportApi.getPlaygroundPreview(playgroundId, token);
            }
        }, true);
    }

    private void performChangeCovering(
        final long playgroundId, final long coveringId) {
        final val proSportApi = getProSportApiManager().getProSportApi();
        final val rxManager = getRxManager();
        final val messageManager = getMessageManager();

        messageManager.showProgressMessage(
            R.string.playground_editor_progress_title,
            R.string.playground_editor_covering_progress_message);

        getProSportAccountHelper()
            .withAccessToken(new Func1<String, Observable<ResponseBody>>() {
                @Override
                public Observable<ResponseBody> call(final String token) {
                    Contracts.requireWorkerThread();

                    final val changePlaygroundCoveringParams =
                        new ChangePlaygroundCoveringParams(coveringId);

                    return proSportApi.changePlaygroundCovering(playgroundId,
                                                                token,
                                                                changePlaygroundCoveringParams);
                }
            }, true)
            .subscribeOn(rxManager.getIOScheduler())
            .observeOn(rxManager.getUIScheduler())
            .subscribe(new Action1<ResponseBody>() {
                @Override
                public void call(final ResponseBody playgroundPreview) {
                    Contracts.requireMainThread();
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Contracts.requireMainThread();

                    Log.w(
                        _LOG_TAG,
                        "Failed to load playground. Playground Id: " + playgroundId,
                        error);
                    revertPlaygroundCovering();
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

    private void performLoadCovering(final long playgroundId) {
        final val rxManager = getRxManager();

        rxManager
            .autoManage(Observable.zip(getCoveringsObservable(),
                                       getPlaygroundObservable(playgroundId),
                                       new Func2<List<CoveringEntity>, PlaygroundPreviewEntity,
                                           Tuple2<List<CoveringEntity>, Covering>>() {
                                           @Override
                                           public Tuple2<List<CoveringEntity>, Covering> call(
                                               final List<CoveringEntity> coverings,
                                               final PlaygroundPreviewEntity playgroundPreviewEntity) {

                                               return Tuples.from(
                                                   coverings,
                                                   (Covering) playgroundPreviewEntity.getCovering());
                                           }
                                       }))
            .subscribeOn(rxManager.getIOScheduler())
            .observeOn(rxManager.getUIScheduler())
            .subscribe(new Action1<Tuple2<List<CoveringEntity>, Covering>>() {
                @Override
                public void call(final Tuple2<List<CoveringEntity>, Covering> arg) {
                    Contracts.requireMainThread();

                    displayPlaygroundCovering(
                        arg.get2(),
                        (List<Covering>) (List<? extends Covering>) arg.get1());
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable throwable) {
                    Contracts.requireMainThread();

                    Log.w(
                        _LOG_TAG,
                        "Failed to load playground. Playground Id: " + playgroundId +
                        " and coverings",
                        throwable);
                    getMessageManager().showInfoMessage(R.string.loading_fail);
                    displayLoadingError();
                }
            });
    }

    private void revertPlaygroundCovering() {
        final val screen = getScreen();
        if (screen != null) {
            screen.revertPlaygroundCovering();
        }
    }
}
