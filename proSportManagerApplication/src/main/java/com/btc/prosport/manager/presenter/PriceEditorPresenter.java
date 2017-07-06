package com.btc.prosport.manager.presenter;

import android.support.annotation.CallSuper;
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
import com.btc.prosport.api.model.Price;
import com.btc.prosport.api.model.entity.PriceEntity;
import com.btc.prosport.api.request.ChangePriceParams;
import com.btc.prosport.core.manager.proSportApi.ProSportApiManager;
import com.btc.prosport.core.manager.proSportServiceHelper.ProSportAccountHelper;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.eventArgs.ChangePlaygroundPricesEventArgs;
import com.btc.prosport.manager.screen.PriceEditorScreen;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Response;

@Accessors(prefix = "_")
public final class PriceEditorPresenter extends BasePresenter<PriceEditorScreen> {
    private static final String _LOG_TAG = ConstantBuilder.logTag(PriceEditorPresenter.class);

    public PriceEditorPresenter(
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

    public void displayPrices(@Nullable final List<Price> prices) {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayPrices(prices);
        }
    }

    public void displayPricesLoading() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayPricesLoading();
        }
    }

    public void displayPricesLoadingError() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayPricesLoadingError();
        }
    }

    protected final void displayPricesChangedResult() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayPricesChangedResult();
        }
    }

    @CallSuper
    @Override
    protected void onScreenAppear(@NonNull final PriceEditorScreen screen) {
        super.onScreenAppear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getViewPricesEvent().addHandler(_viewPricesHandler);
        screen.getChangePlaygroundPricesEvent().addHandler(_changePlaygroundPricesHandler);
    }

    @CallSuper
    @Override
    protected void onScreenDisappear(@NonNull final PriceEditorScreen screen) {
        super.onScreenDisappear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getViewPricesEvent().removeHandler(_viewPricesHandler);
        screen.getChangePlaygroundPricesEvent().removeHandler(_changePlaygroundPricesHandler);
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
    private final EventHandler<ChangePlaygroundPricesEventArgs> _changePlaygroundPricesHandler =
        new EventHandler<ChangePlaygroundPricesEventArgs>() {
            @Override
            public void onEvent(@NonNull final ChangePlaygroundPricesEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                final val playgroundId = eventArgs.getId();
                final val newPriceByPriceId = eventArgs.getNewPrices();

                performChangePrices(playgroundId, newPriceByPriceId);
            }
        };

    @NonNull
    private final EventHandler<IdEventArgs> _viewPricesHandler = new EventHandler<IdEventArgs>() {
        @Override
        public void onEvent(@NonNull final IdEventArgs eventArgs) {
            Contracts.requireNonNull(eventArgs, "eventArgs == null");
            final val playgroundId = eventArgs.getId();

            displayPricesLoading();

            performLoadPrices(playgroundId);
        }
    };

    private void performChangePrices(
        final long playgroundId, @NonNull final Map<String, Integer> newPriceByPriceId) {
        Contracts.requireNonNull(newPriceByPriceId, "newPriceByPriceId == null");

        final val newPrices = new ArrayList<ChangePriceParams>();
        CollectionUtils.collect(newPriceByPriceId.entrySet(),
                                new Transformer<Map.Entry<String, Integer>, ChangePriceParams>() {
                                    @Override
                                    public ChangePriceParams transform(final Map.Entry<String,
                                        Integer> input) {
                                        return new ChangePriceParams(input.getKey(),
                                                                     input.getValue());
                                    }
                                },
                                newPrices);

        final val proSportApi = getProSportApiManager().getProSportApi();
        final val rxManager = getRxManager();
        final val proSportAccountHelper = getProSportAccountHelper();
        final val messageManager = getMessageManager();

        messageManager.showProgressMessage(R.string.price_editor_change_prices_progress_title,
                                           R.string.price_editor_change_prices_progress_message);

        proSportAccountHelper
            .withRequiredAccessToken(new Func1<String, Observable<Response<ResponseBody>>>() {
                @Override
                public Observable<Response<ResponseBody>> call(final String token) {
                    Contracts.requireWorkerThread();

                    return rxManager.autoManage(proSportApi.changePrices(playgroundId,
                                                                         newPrices,
                                                                         token));
                }
            }, true)
            .doOnNext(new Action1<Response<ResponseBody>>() {
                @Override
                public void call(final Response<ResponseBody> response) {
                    if (!response.isSuccessful()) {
                        throw new RuntimeException();
                    }
                }
            })
            .subscribeOn(rxManager.getIOScheduler())
            .observeOn(rxManager.getUIScheduler())
            .subscribe(new Action1<Response<ResponseBody>>() {
                @Override
                public void call(final Response<ResponseBody> response) {
                    Contracts.requireMainThread();

                    messageManager.showNotificationMessage(R.string
                                                               .price_editor_success_change_prices);

                    displayPricesChangedResult();
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Contracts.requireMainThread();

                    Log.w(_LOG_TAG, "Failed to change prices.", error);

                    messageManager.dismissProgressMessage();

                    messageManager.showErrorMessage(R.string.price_editor_fail_change_prices);
                }
            }, new Action0() {
                @Override
                public void call() {
                    Contracts.requireMainThread();

                    messageManager.dismissProgressMessage();
                }
            });
    }

    private void performLoadPrices(final long playgroundId) {
        final val proSportApi = getProSportApiManager().getProSportApi();
        final val rxManager = getRxManager();

        getProSportAccountHelper()
            .withAccessToken(new Func1<String, Observable<List<PriceEntity>>>() {
                @Override
                public Observable<List<PriceEntity>> call(final String token) {
                    Contracts.requireWorkerThread();

                    return proSportApi.getPrices(playgroundId, token);
                }
            }, true)
            .subscribeOn(rxManager.getIOScheduler())
            .observeOn(rxManager.getUIScheduler())
            .subscribe(new Action1<List<PriceEntity>>() {
                @Override
                public void call(final List<PriceEntity> prices) {
                    Contracts.requireMainThread();

                    displayPrices((List<Price>) (List<? extends Price>) prices);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Contracts.requireMainThread();

                    Log.w(_LOG_TAG, "Failed to load prices. Playground Id: " + playgroundId, error);

                    getMessageManager().showInfoMessage(R.string.price_editor_price_load_fail);
                    displayPricesLoadingError();
                }
            });
    }
}
