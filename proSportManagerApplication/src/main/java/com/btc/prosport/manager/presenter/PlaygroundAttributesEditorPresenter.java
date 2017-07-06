package com.btc.prosport.manager.presenter;

import android.support.annotation.NonNull;
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
import com.btc.prosport.api.model.Attribute;
import com.btc.prosport.api.model.entity.AttributeEntity;
import com.btc.prosport.api.model.entity.PlaygroundPreviewEntity;
import com.btc.prosport.api.request.ChangePlaygroundAttributesParams;
import com.btc.prosport.core.manager.proSportApi.ProSportApiManager;
import com.btc.prosport.core.manager.proSportServiceHelper.ProSportAccountHelper;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.eventArgs.ChangePlaygroundAttributesEventArgs;
import com.btc.prosport.manager.screen.PlaygroundAttributesEditorScreen;

import java.util.List;

import okhttp3.ResponseBody;

@Accessors(prefix = "_")
public final class PlaygroundAttributesEditorPresenter
    extends BasePresenter<PlaygroundAttributesEditorScreen> {

    private static final String _LOG_TAG =
        ConstantBuilder.logTag(PlaygroundAttributesEditorPresenter.class);

    public PlaygroundAttributesEditorPresenter(
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
        @NonNull final PlaygroundAttributesEditorScreen screen) {
        super.onScreenAppear(screen);

        screen.getViewPlaygroundAttributesEvent().addHandler(_viewAttributesHandler);
        screen.getChangePlaygroundAttributesEvent().addHandler(_changeAttributesHandler);
    }

    @Override
    protected void onScreenDisappear(
        @NonNull final PlaygroundAttributesEditorScreen screen) {
        super.onScreenDisappear(screen);

        screen.getViewPlaygroundAttributesEvent().removeHandler(_viewAttributesHandler);
        screen.getChangePlaygroundAttributesEvent().removeHandler(_changeAttributesHandler);
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
    private final EventHandler<ChangePlaygroundAttributesEventArgs> _changeAttributesHandler =
        new EventHandler<ChangePlaygroundAttributesEventArgs>() {
            @Override
            public void onEvent(@NonNull final ChangePlaygroundAttributesEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                final long playgroundId = eventArgs.getId();
                final val attributes = eventArgs.getNewAttributes();

                performChangeAttributes(playgroundId, attributes);
            }
        };

    @NonNull
    private final EventHandler<IdEventArgs> _viewAttributesHandler =
        new EventHandler<IdEventArgs>() {
            @Override
            public void onEvent(@NonNull final IdEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");
                final val playgroundId = eventArgs.getId();

                displayLoading();

                performLoadAttributes(playgroundId);
            }
        };

    private void displayPlaygroundAttributes(
        @NonNull final List<Attribute> playgroundAttributes,
        @NonNull final List<Attribute> allAttributes) {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayPlaygroundAttributes(playgroundAttributes, allAttributes);
        }
    }

    @NonNull
    private Observable<List<AttributeEntity>> getAttributesObservable() {
        final val proSportApi = getProSportApiManager().getProSportApi();

        return getProSportAccountHelper().withAccessToken(new Func1<String,
            Observable<List<AttributeEntity>>>() {
            @Override
            public Observable<List<AttributeEntity>> call(final String token) {
                Contracts.requireWorkerThread();

                return proSportApi.getAttributes(token);
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

    private void performChangeAttributes(
        final long playgroundId, final List<Long> attributeIds) {

        final val proSportApi = getProSportApiManager().getProSportApi();
        final val rxManager = getRxManager();
        final val messageManager = getMessageManager();

        messageManager.showProgressMessage(
            R.string.playground_editor_progress_title,
            R.string.playground_editor_attributes_progress_message);

        getProSportAccountHelper()
            .withAccessToken(new Func1<String, Observable<ResponseBody>>() {
                @Override
                public Observable<ResponseBody> call(final String token) {
                    Contracts.requireWorkerThread();

                    final val changePlaygroundAttributesParams =
                        new ChangePlaygroundAttributesParams(attributeIds);

                    return proSportApi.changePlaygroundAttributes(playgroundId,
                                                                  token,
                                                                  changePlaygroundAttributesParams);
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

                    revertChangedAttributes();
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

    private void performLoadAttributes(final long playgroundId) {
        final val rxManager = getRxManager();

        rxManager
            .autoManage(Observable.zip(getAttributesObservable(),
                                       getPlaygroundObservable(playgroundId),
                                       new Func2<List<AttributeEntity>, PlaygroundPreviewEntity,
                                           Tuple2<List<AttributeEntity>, List<AttributeEntity>>>() {
                                           @Override
                                           public Tuple2<List<AttributeEntity>,
                                               List<AttributeEntity>> call(
                                               final List<AttributeEntity> attributes,
                                               final PlaygroundPreviewEntity
                                                   playgroundPreviewEntity) {

                                               return Tuples.from(
                                                   playgroundPreviewEntity.getAttributes(),
                                                   attributes);
                                           }
                                       }))
            .subscribeOn(rxManager.getIOScheduler())
            .observeOn(rxManager.getUIScheduler())
            .subscribe(new Action1<Tuple2<List<AttributeEntity>, List<AttributeEntity>>>() {
                @Override
                public void call(
                    final Tuple2<List<AttributeEntity>, List<AttributeEntity>> arg) {
                    Contracts.requireMainThread();

                    displayPlaygroundAttributes(
                        (List<Attribute>) (List<? extends Attribute>) arg.get1(),
                        (List<Attribute>) (List<? extends Attribute>) arg.get2());
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable throwable) {
                    Contracts.requireMainThread();

                    Log.w(
                        _LOG_TAG,
                        "Failed to load playground. Playground Id: " + playgroundId +
                        " and attributes",
                        throwable);
                    getMessageManager().showInfoMessage(R.string.loading_fail);
                    displayLoadingError();
                }
            });
    }

    private void revertChangedAttributes() {
        final val screen = getScreen();
        if (screen != null) {
            screen.revertChangedAttributes();
        }
    }
}
