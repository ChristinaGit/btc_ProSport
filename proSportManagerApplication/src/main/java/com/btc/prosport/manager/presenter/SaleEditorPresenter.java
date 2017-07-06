package com.btc.prosport.manager.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import lombok.AccessLevel;
import lombok.Getter;
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
import com.btc.common.event.notice.NoticeEventHandler;
import com.btc.common.extension.exception.AccountNotFoundException;
import com.btc.common.mvp.presenter.BasePresenter;
import com.btc.common.utility.tuple.Tuple2;
import com.btc.common.utility.tuple.Tuples;
import com.btc.prosport.api.exception.ProSportApiException;
import com.btc.prosport.api.model.PlaygroundTitle;
import com.btc.prosport.api.model.Sale;
import com.btc.prosport.api.model.SportComplexTitle;
import com.btc.prosport.api.model.entity.PlaygroundTitleEntity;
import com.btc.prosport.api.model.entity.SaleEntity;
import com.btc.prosport.api.model.entity.SportComplexTitleEntity;
import com.btc.prosport.api.model.utility.SaleType;
import com.btc.prosport.api.request.CreateSaleParams;
import com.btc.prosport.core.manager.proSportApi.ProSportApiManager;
import com.btc.prosport.core.manager.proSportServiceHelper.ProSportAccountHelper;
import com.btc.prosport.core.utility.ProSportApiDataUtils;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.adapter.repeatableIntervals.item.RepeatableIntervalItem;
import com.btc.prosport.manager.core.eventArgs.CreateSaleEventArgs;
import com.btc.prosport.manager.screen.SaleEditorScreen;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Accessors(prefix = "_")
public final class SaleEditorPresenter extends BasePresenter<SaleEditorScreen> {
    private static final String _LOG_TAG = ConstantBuilder.logTag(SaleEditorPresenter.class);

    public SaleEditorPresenter(
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

    public void displayCreatedSale(@Nullable final Sale sale) {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayCreatedSale(sale);
        }
    }

    protected final void displayLoadSalePlacesError() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayLoadSalePlacesError();
        }
    }

    protected final void displaySalePlaces(
        @Nullable final Map<SportComplexTitle, List<PlaygroundTitle>> places) {
        final val screen = getScreen();
        if (screen != null) {
            screen.displaySalePlaces(places);
        }
    }

    @Override
    protected void onScreenCreate(@NonNull final SaleEditorScreen screen) {
        super.onScreenCreate(Contracts.requireNonNull(screen, "screen == null"));

        screen.getLoadSalePlacesEvent().addHandler(_loadSalePlacesHandler);
        screen.getCreateSaleEvent().addHandler(_createSaleHandler);
    }

    @Override
    protected void onScreenDestroy(@NonNull final SaleEditorScreen screen) {
        super.onScreenDestroy(Contracts.requireNonNull(screen, "screen == null"));

        screen.getLoadSalePlacesEvent().removeHandler(_loadSalePlacesHandler);
        screen.getCreateSaleEvent().removeHandler(_createSaleHandler);
    }

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final MessageManager _messageManager;

    @NonNull
    private final Transformer<RepeatableIntervalItem, CreateSaleParams.Interval>
        _orderIntervalsTransformer =
        new Transformer<RepeatableIntervalItem, CreateSaleParams.Interval>() {
            @Override
            public CreateSaleParams.Interval transform(
                final RepeatableIntervalItem input) {
                final val dateStart = input.getDateStart();
                final val dateEnd = input.getDateEnd();
                final val timeStart = input.getTimeStart();
                final val timeEnd = input.getTimeEnd();
                final val repeatWeekDays = input.getRepeatWeekDays();

                return new CreateSaleParams.Interval(ProSportApiDataUtils.formatDate(dateStart),
                                                     ProSportApiDataUtils.formatTime(timeStart),
                                                     ProSportApiDataUtils.formatDate(dateEnd),
                                                     ProSportApiDataUtils.formatTime(timeEnd),
                                                     ProSportApiDataUtils
                                                         .makeRepeatingIntervalWeekDays(
                                                         repeatWeekDays));
            }
        };

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
    private final EventHandler<CreateSaleEventArgs> _createSaleHandler =
        new EventHandler<CreateSaleEventArgs>() {
            @Override
            public void onEvent(@NonNull final CreateSaleEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                final val intervals = eventArgs.getSaleIntervals();
                final val playgroundIds = eventArgs.getPlaygroundIds();
                final val saleType = eventArgs.getSaleType();
                final val saleValue = eventArgs.getSaleValue();

                performCreateSale(playgroundIds, saleType, saleValue, intervals);
            }
        };

    @NonNull
    private final Comparator<SportComplexTitle> _sportComplexTitleComparator =
        new Comparator<SportComplexTitle>() {
            @Override
            public int compare(
                final SportComplexTitle lhs, final SportComplexTitle rhs) {
                int difference;

                difference = StringUtils.compare(lhs.getName(), rhs.getName(), false);

                if (difference == 0) {
                    difference = Long.compare(lhs.getId(), rhs.getId());
                }

                return difference;
            }
        };

    @NonNull
    private final NoticeEventHandler _loadSalePlacesHandler = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            performLoadSalePlaces();
        }
    };

    private void handleProSportApiError(@NonNull final ProSportApiException error) {
        Contracts.requireNonNull(error, "error == null");

        final val errorResponse = error.getErrorResponse();
        if (errorResponse != null) {
            final val allErrors = errorResponse.getAllErrors();

            final val errorMessage = TextUtils.join(StringUtils.LF + StringUtils.LF, allErrors);
            getMessageManager().showModalMessage(errorMessage);
        }
    }

    private void performCreateSale(
        @NonNull final List<Long> playgroundIds,
        @NonNull final SaleType saleType,
        final int saleValue,
        @NonNull final List<RepeatableIntervalItem> saleIntervals) {
        Contracts.requireNonNull(playgroundIds, "playgroundIds == null");
        Contracts.requireNonNull(saleType, "saleType == null");
        Contracts.requireNonNull(saleIntervals, "saleIntervals == null");

        final val rxManager = getRxManager();
        final val proSportApi = getProSportApiManager().getProSportApi();

        getProSportAccountHelper()
            .withRequiredAccessToken(new Func1<String, Observable<SaleEntity>>() {
                @Override
                public Observable<SaleEntity> call(final String token) {
                    final val intervals =
                        new ArrayList<CreateSaleParams.Interval>(saleIntervals.size());
                    CollectionUtils.collect(saleIntervals, _orderIntervalsTransformer, intervals);

                    final val createSaleParams = new CreateSaleParams(playgroundIds,
                                                                      intervals,
                                                                      saleType.getCode(),
                                                                      saleValue,
                                                                      null);

                    return rxManager.autoManage(proSportApi.createSale(token, createSaleParams));
                }
            }, true)
            .observeOn(rxManager.getUIScheduler())
            .subscribeOn(rxManager.getIOScheduler())
            .subscribe(new Action1<SaleEntity>() {
                @Override
                public void call(final SaleEntity sale) {
                    Contracts.requireMainThread();

                    displayCreatedSale(sale);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Contracts.requireMainThread();

                    Log.w(_LOG_TAG, "Failed to create sale.", error);

                    if (error instanceof AccountNotFoundException) {
                        getMessageManager().showErrorMessage(R.string
                                                                 .sale_editor_fail_create_sale_without_account);
                    } else if (error instanceof ProSportApiException) {
                        handleProSportApiError((ProSportApiException) error);
                    } else {
                        getMessageManager().showErrorMessage(R.string.sale_editor_fail_create_sale);
                    }

                    displayCreatedSale(null);
                }
            });
    }

    private void performLoadSalePlaces() {
        final val proSportApi = getProSportApiManager().getProSportApi();
        final val rxManager = getRxManager();

        getProSportAccountHelper()
            .withAccessToken(new Func1<String, Observable<Map<SportComplexTitle,
                List<PlaygroundTitle>>>>() {
                @Override
                public Observable<Map<SportComplexTitle, List<PlaygroundTitle>>> call(
                    final String token) {
                    Contracts.requireWorkerThread();

                    final val loadData = proSportApi
                        .getManagerSportComplexesTitles(token)
                        .flatMap(new Func1<List<SportComplexTitleEntity>,
                            Observable<SportComplexTitleEntity>>() {
                            @Override
                            public Observable<SportComplexTitleEntity> call(
                                final List<SportComplexTitleEntity> sportComplexes) {
                                return Observable.from(sportComplexes);
                            }
                        })
                        .flatMap(new Func1<SportComplexTitleEntity,
                            Observable<Tuple2<SportComplexTitleEntity,
                                List<PlaygroundTitleEntity>>>>() {
                            @Override
                            public Observable<Tuple2<SportComplexTitleEntity,
                                List<PlaygroundTitleEntity>>> call(
                                final SportComplexTitleEntity sportComplex) {
                                Contracts.requireWorkerThread();

                                return proSportApi
                                    .getPlaygroundsTitles(sportComplex.getId(), token)
                                    .map(new Func1<List<PlaygroundTitleEntity>,
                                        Tuple2<SportComplexTitleEntity,
                                            List<PlaygroundTitleEntity>>>() {
                                        @Override
                                        public Tuple2<SportComplexTitleEntity,
                                            List<PlaygroundTitleEntity>> call(
                                            final List<PlaygroundTitleEntity> playgroundTitles) {
                                            return Tuples.from(sportComplex, playgroundTitles);
                                        }
                                    });
                            }
                        })
                        .toMap(new Func1<Tuple2<SportComplexTitleEntity,
                                   List<PlaygroundTitleEntity>>, SportComplexTitle>() {
                                   @Override
                                   public SportComplexTitle call(
                                       final Tuple2<SportComplexTitleEntity,
                                           List<PlaygroundTitleEntity>> arg) {
                                       return arg.get1();
                                   }
                               },
                               new Func1<Tuple2<SportComplexTitleEntity,
                                   List<PlaygroundTitleEntity>>, List<PlaygroundTitle>>() {
                                   @Override
                                   public List<PlaygroundTitle> call(
                                       final Tuple2<SportComplexTitleEntity,
                                           List<PlaygroundTitleEntity>> arg) {
                                       return (List<PlaygroundTitle>) (List<? extends PlaygroundTitle>) arg.get2();
                                   }
                               })
                        .map(new Func1<Map<SportComplexTitle, List<PlaygroundTitle>>,
                            Map<SportComplexTitle, List<PlaygroundTitle>>>() {
                            @Override
                            public Map<SportComplexTitle, List<PlaygroundTitle>> call(
                                final Map<SportComplexTitle, List<PlaygroundTitle>> arg) {
                                final val sortedMap =
                                    new TreeMap<SportComplexTitle, List<PlaygroundTitle>>(
                                        _sportComplexTitleComparator);
                                sortedMap.putAll(arg);
                                return sortedMap;
                            }
                        });
                    return rxManager.autoManage(loadData);
                }
            }, true)
            .subscribeOn(rxManager.getIOScheduler())
            .observeOn(rxManager.getUIScheduler())
            .subscribe(new Action1<Map<SportComplexTitle, List<PlaygroundTitle>>>() {
                @Override
                public void call(final Map<SportComplexTitle, List<PlaygroundTitle>> salePlaces) {
                    Contracts.requireMainThread();

                    displaySalePlaces(salePlaces);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Contracts.requireMainThread();

                    Log.w(_LOG_TAG, "Failed to load sale places.", error);

                    if (error instanceof ProSportApiException) {
                        handleProSportApiError((ProSportApiException) error);
                    } else {
                        // TODO: 15.05.2017 Make snackbar. Added activity result handlig for caller
                        // activity.
                        getMessageManager().showNotificationMessage(R.string
                                                                        .sale_editor_sale_places_load_fail);
                        displayLoadSalePlacesError();
                    }
                }
            });
    }
}
