package com.btc.prosport.manager.presenter;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
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
import com.btc.common.event.notice.NoticeEventHandler;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.mvp.presenter.BasePresenter;
import com.btc.prosport.api.ProSportDataContract;
import com.btc.prosport.api.model.Sale;
import com.btc.prosport.api.model.entity.PageEntity;
import com.btc.prosport.api.model.entity.SaleEntity;
import com.btc.prosport.core.manager.proSportApi.ProSportApiManager;
import com.btc.prosport.core.manager.proSportServiceHelper.ProSportAccountHelper;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.screen.SalesListScreen;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

@Accessors(prefix = "_")
public final class SalesListPresenter extends BasePresenter<SalesListScreen> {
    private static final String _LOG_TAG = ConstantBuilder.logTag(OrdersListPresenter.class);

    public SalesListPresenter(
        @NonNull final RxManager rxManager,
        @NonNull final MessageManager messageManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper) {
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");

        _rxManager = rxManager;
        _messageManager = messageManager;
        _proSportApiManager = proSportApiManager;
        _proSportAccountHelper = proSportAccountHelper;
    }

    protected final void displayDeletedSale(@Nullable final Long saleId) {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayDeletedSale(saleId);
        }
    }

    protected final void displaySales(@Nullable final List<Sale> sales, final boolean lastPage) {
        final val screen = getScreen();
        if (screen != null) {
            screen.displaySales(sales, lastPage);
        }
    }

    protected final void displaySalesLoading() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displaySalesLoading();
        }
    }

    protected final void displaySalesLoadingError() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displaySalesLoadingError();
        }
    }

    protected final void displaySalesPage(
        @Nullable final List<Sale> sales, final boolean lastPage) {
        final val screen = getScreen();
        if (screen != null) {
            screen.displaySalesPage(sales, lastPage);
        }
    }

    @CallSuper
    @Override
    protected void onScreenAppear(@NonNull final SalesListScreen screen) {
        super.onScreenAppear(Contracts.requireNonNull(screen, "screen == null"));

        resetCurrentSalesPage();

        screen.getViewSalesEvent().addHandler(_viewSalesHandler);
        screen.getViewNextSalesPageEvent().addHandler(_viewNextSalesPageHandler);
        screen.getRefreshSalesEvent().addHandler(_refreshSalesHandler);
        screen.getDeleteSaleEvent().addHandler(_deleteSaleHandler);
    }

    @CallSuper
    @Override
    protected void onScreenDisappear(@NonNull final SalesListScreen screen) {
        super.onScreenDisappear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getViewSalesEvent().removeHandler(_viewSalesHandler);
        screen.getViewNextSalesPageEvent().removeHandler(_viewNextSalesPageHandler);
        screen.getRefreshSalesEvent().removeHandler(_refreshSalesHandler);
        screen.getDeleteSaleEvent().removeHandler(_deleteSaleHandler);
    }

    protected void performLoadSalesPage(final int page) {
        final val messageManager = getMessageManager();
        final val proSportApiManager = getProSportApiManager();
        final val proSportApi = proSportApiManager.getProSportApi();

        // TODO: 02.03.2017 Fix double update when permissions asked
        final val rxManager = getRxManager();

        getProSportAccountHelper()
            .withAccessToken(new Func1<String, Observable<PageEntity<SaleEntity>>>() {
                @Override
                public Observable<PageEntity<SaleEntity>> call(final String token) {
                    final val orders = proSportApi.getManagerSales(token, page);
                    return rxManager.autoManage(orders);
                }
            }, true)
            .subscribeOn(rxManager.getIOScheduler())
            .observeOn(rxManager.getUIScheduler())
            .subscribe(new Action1<PageEntity<SaleEntity>>() {
                @Override
                public void call(final PageEntity<SaleEntity> response) {
                    Contracts.requireMainThread();

                    final val sales = (List<Sale>) (List<? extends Sale>) response.getEntries();
                    if (page == ProSportDataContract.FIRST_PAGE_INDEX) {
                        displaySales(sales, response.getNextPageUri() == null);
                    } else {
                        displaySalesPage(sales, response.getNextPageUri() == null);
                    }
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Contracts.requireMainThread();

                    Log.w(_LOG_TAG, "Failed to load sales. Page: " + page, error);

                    messageManager.showInfoMessage(R.string.sales_list_load_fail);
                    displaySalesLoadingError();
                }
            });
    }

    protected void resetCurrentSalesPage() {
        _currentSalesPage = ProSportDataContract.FIRST_PAGE_INDEX;
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
    private final EventHandler<IdEventArgs> _deleteSaleHandler = new EventHandler<IdEventArgs>() {
        @Override
        public void onEvent(@NonNull final IdEventArgs eventArgs) {
            Contracts.requireNonNull(eventArgs, "eventArgs == null");

            final val saleId = eventArgs.getId();

            final val proSportApiManager = getProSportApiManager();
            final val proSportApi = proSportApiManager.getProSportApi();
            final val rxManager = getRxManager();
            final val messageManager = getMessageManager();

            messageManager.showProgressMessage(
                R.string.sales_list_delete_sale_progress_title,
                R.string.sales_list_delete_sale_progress_message);

            getProSportAccountHelper()
                .withRequiredAccessToken(new Func1<String, Observable<Response<ResponseBody>>>() {
                    @Override
                    public Observable<Response<ResponseBody>> call(final String token) {
                        return rxManager.autoManage(proSportApi.deleteSale(saleId, token));
                    }
                }, true)
                .subscribeOn(rxManager.getIOScheduler())
                .observeOn(rxManager.getUIScheduler())
                .subscribe(new Action1<Response<ResponseBody>>() {
                    @Override
                    public void call(final Response<ResponseBody> response) {
                        Contracts.requireMainThread();

                        displayDeletedSale(saleId);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(final Throwable error) {
                        Contracts.requireMainThread();

                        Log.w(_LOG_TAG, "Failed to delete sale.", error);

                        messageManager.dismissProgressMessage();

                        messageManager.showInfoMessage(R.string.sales_list_delete_sale_fail);

                        displayDeletedSale(null);
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        Contracts.requireMainThread();

                        messageManager.dismissProgressMessage();
                    }
                });
        }
    };

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private int _currentSalesPage;

    @NonNull
    private final NoticeEventHandler _viewNextSalesPageHandler = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            setCurrentSalesPage(getCurrentSalesPage() + 1);
            performLoadSalesPage(getCurrentSalesPage());
        }
    };

    @NonNull
    private final NoticeEventHandler _refreshSalesHandler = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            resetCurrentSalesPage();
            performLoadSalesPage(ProSportDataContract.FIRST_PAGE_INDEX);
        }
    };

    @NonNull
    private final NoticeEventHandler _viewSalesHandler = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            resetCurrentSalesPage();

            displaySalesLoading();

            performLoadSalesPage(ProSportDataContract.FIRST_PAGE_INDEX);
        }
    };
}
