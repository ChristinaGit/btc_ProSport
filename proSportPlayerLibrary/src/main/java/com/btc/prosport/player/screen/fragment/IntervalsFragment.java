package com.btc.prosport.player.screen.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.event.Events;
import com.btc.common.event.generic.Event;
import com.btc.common.event.generic.ManagedEvent;
import com.btc.common.event.notice.NoticeEventHandler;
import com.btc.common.extension.delegate.loading.FadeVisibilityHandler;
import com.btc.common.extension.delegate.loading.LoadingViewDelegate;
import com.btc.common.extension.delegate.loading.ProgressVisibilityHandler;
import com.btc.common.extension.eventArgs.UriEventArgs;
import com.btc.common.extension.view.ContentLoaderProgressBar;
import com.btc.common.mvp.presenter.Presenter;
import com.btc.common.utility.SwipeRefreshUtils;
import com.btc.common.utility.UriFactoryUtils;
import com.btc.prosport.api.ProSportDataContract;
import com.btc.prosport.api.model.Interval;
import com.btc.prosport.api.model.Order;
import com.btc.prosport.api.model.PlaygroundTitle;
import com.btc.prosport.api.model.SportComplexTitle;
import com.btc.prosport.core.adapter.cell.binder.IntervalViewHolderBinder;
import com.btc.prosport.core.adapter.intervalBooking.IntervalsBookingAdapter;
import com.btc.prosport.core.eventArgs.ViewIntervalsEventArgs;
import com.btc.prosport.core.utility.ProSportFormat;
import com.btc.prosport.player.R;
import com.btc.prosport.player.core.eventArgs.CreateOrderEventArgs;
import com.btc.prosport.player.di.qualifier.PresenterNames;
import com.btc.prosport.player.screen.IntervalsScreen;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

@Accessors(prefix = "_")
public final class IntervalsFragment extends BasePlayerFragment
    implements SwipeRefreshLayout.OnRefreshListener, IntervalsScreen {
    private static final String _LOG_TAG = ConstantBuilder.logTag(IntervalsFragment.class);

    @Override
    public final void displayCreateOrderProgress() {
        showCreateOrderProgressDialog();
    }

    @Override
    public void displayCreatedOrder(@Nullable final Order order) {
        dismissCreateOrderProgressDialog();

        if (order != null) {
            showCreatedOrderDialog(order);
        } else {
            showFailCrateOrderDialog(getSportComplex());
        }

        finishIntervalSelectionActionMode();
        final val intervalsAdapter = getIntervalsAdapter();
        if (intervalsAdapter != null) {
            intervalsAdapter.clearSelection();
        }
    }

    @CallSuper
    @Override
    public void displayIntervals(
        @Nullable final SportComplexTitle sportComplex,
        @Nullable final PlaygroundTitle playground,
        @Nullable final List<Interval> intervals) {
        finishIntervalSelectionActionMode();

        if (_intervalsRefreshView != null) {
            _intervalsRefreshView.setRefreshing(false);
        }

        _sportComplex = sportComplex;
        _playground = playground;

        final val intervalsAdapter = getIntervalsAdapter();
        if (intervalsAdapter != null) {
            intervalsAdapter.setCells(intervals);
            intervalsAdapter.notifyDataSetChanged();
        }

        final val loadingViewDelegate = getIntervalsLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showContent(
                intervals != null && !intervals.isEmpty() && getReservationStartTime() != null);
        }
    }

    @Override
    public final void displayIntervalsLoading() {
        finishIntervalSelectionActionMode();

        final val loadingViewDelegate = getIntervalsLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showLoading();
        }
    }

    @Override
    public final void displayIntervalsLoadingError() {
        finishIntervalSelectionActionMode();

        if (_intervalsRefreshView != null) {
            _intervalsRefreshView.setRefreshing(false);
        }

        final val loadingViewDelegate = getIntervalsLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showError();
        }
    }

    @NonNull
    @Override
    public final Event<CreateOrderEventArgs> getCreateOrderEvent() {
        return _createOrderEvent;
    }

    @NonNull
    @Override
    public final Event<ViewIntervalsEventArgs> getRefreshIntervalsEvent() {
        return _refreshIntervalsEvent;
    }

    @NonNull
    @Override
    public final Event<UriEventArgs> getSportComplexPhoneCallEvent() {
        return _playgroundPhoneCallEvent;
    }

    @NonNull
    @Override
    public final Event<ViewIntervalsEventArgs> getViewIntervalsEvent() {
        return _viewIntervalsEvent;
    }

    public final void setPlaygroundId(@Nullable final Long playgroundId) {
        if (!Objects.equals(_playgroundId, playgroundId)) {
            _playgroundId = playgroundId;

            onPlaygroundIdChanged();
        }
    }

    public final void setReservationStartTime(@Nullable final Long reservationStartTime) {
        if (!Objects.equals(_reservationStartTime, reservationStartTime)) {
            _reservationStartTime = reservationStartTime;

            onReservationStartTimeChanged();
        }
    }

    @CallSuper
    @Override
    public void bindViews(@NonNull final View view) {
        super.bindViews(view);

        _intervalsErrorView = view.findViewById(R.id.loading_error);
        _intervalsView = (RecyclerView) view.findViewById(R.id.intervals);
        _intervalsLoadingView = (ContentLoaderProgressBar) view.findViewById(R.id.loading);
        _intervalsNoContentView = view.findViewById(R.id.no_content);
        _intervalsRefreshView = (SwipeRefreshLayout) view.findViewById(R.id.intervals_refresh);
        _intervalsOverlayHeaderView =
            (LinearLayout) view.findViewById(R.id.intervals_overlay_header);
    }

    @CallSuper
    @Nullable
    @Override
    public final View onCreateView(
        final LayoutInflater inflater,
        @Nullable final ViewGroup container,
        @Nullable final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final val view = inflater.inflate(R.layout.fragment_intervals, container, false);

        bindViews(view);

        initializePlaygroundIntervalsView();

        final val intervalsAdapter = getIntervalsAdapter();
        if (intervalsAdapter != null) {
            intervalsAdapter
                .getCellsSelectionStateChangedEvent()
                .addHandler(_cellsSelectionStateChangedHandler);
            intervalsAdapter
                .getCellsSelectionChangedEvent()
                .addHandler(_cellsSelectionChangedHandler);
        }

        if (_intervalsRefreshView != null) {
            SwipeRefreshUtils.setupPrimaryColors(_intervalsRefreshView);
            _intervalsRefreshView.setOnRefreshListener(this);
        }

        _intervalsLoadingViewDelegate = LoadingViewDelegate
            .builder()
            .setContentView(_intervalsView)
            .setLoadingView(_intervalsLoadingView)
            .setNoContentView(_intervalsNoContentView)
            .setErrorView(_intervalsErrorView)
            .setContentVisibilityHandler(new FadeVisibilityHandler(R.anim.fade_in_long, 0) {
                @Override
                public void changeVisibility(@NonNull final View view, final boolean visible) {
                    super.changeVisibility(view, visible);

                    final val intervalsAdapter = getIntervalsAdapter();
                    if (intervalsAdapter != null) {
                        intervalsAdapter.invalidateOverlayHeaderVisibility(_intervalsView);
                    }
                }
            })
            .setLoadingVisibilityHandler(new ProgressVisibilityHandler())
            .build();

        _intervalsLoadingViewDelegate.hideAll();

        return view;
    }

    @CallSuper
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        final val intervalsAdapter = getIntervalsAdapter();
        if (intervalsAdapter != null && _intervalsView != null) {
            intervalsAdapter.unbindOverlayHeaderView(_intervalsView);

            intervalsAdapter
                .getCellsSelectionStateChangedEvent()
                .removeHandler(_cellsSelectionStateChangedHandler);
            intervalsAdapter
                .getCellsSelectionChangedEvent()
                .removeHandler(_cellsSelectionChangedHandler);
        }

        unbindViews();
    }

    @CallSuper
    @Override
    public void onRefresh() {
        final val intervalsAdapter = getIntervalsAdapter();
        if (intervalsAdapter != null) {
            intervalsAdapter.clearSelection();

            finishIntervalSelectionActionMode();
        }

        final val viewIntervalsEventArgs = getViewIntervalsEventArgs();
        if (viewIntervalsEventArgs != null) {
            _refreshIntervalsEvent.rise(viewIntervalsEventArgs);
        } else {
            if (_intervalsRefreshView != null) {
                _intervalsRefreshView.setRefreshing(false);
            }
        }
    }

    @CallSuper
    @Override
    public void onResume() {
        super.onResume();

        final val intervalsAdapter = getIntervalsAdapter();
        if (intervalsAdapter != null) {
            final val viewIntervalsEventArgs = getViewIntervalsEventArgs();
            if (viewIntervalsEventArgs != null) {
                _viewIntervalsEvent.rise(viewIntervalsEventArgs);
            }
        }

        if (intervalsAdapter != null && _intervalsView != null) {
            intervalsAdapter.invalidateOverlayHeader(_intervalsView);
            intervalsAdapter.invalidateOverlayHeaderVisibility(_intervalsView);
        }
    }

    @CallSuper
    @Override
    public void onPause() {
        super.onPause();

        dismissCreatedOrderDialog();
        dismissCreateOrderProgressDialog();
        dismissCreateOrderConfirmationDialog();
    }

    @Nullable
    protected final ViewIntervalsEventArgs getViewIntervalsEventArgs() {
        final ViewIntervalsEventArgs eventArgs;

        final val playgroundId = getPlaygroundId();
        final val reservationStartTime = getReservationStartTime();
        if (playgroundId != null && reservationStartTime != null) {
            eventArgs = new ViewIntervalsEventArgs(playgroundId, reservationStartTime);
        } else {
            eventArgs = null;
        }

        return eventArgs;
    }

    @Nullable
    protected CreateOrderEventArgs getCreateOrderEventArgs() {
        CreateOrderEventArgs eventArgs = null;

        final val playgroundId = getPlaygroundId();
        final val intervalsAdapter = getIntervalsAdapter();
        if (playgroundId != null && intervalsAdapter != null) {
            final val timeStart = intervalsAdapter.getSelectionStartTime();
            final val dateStart = intervalsAdapter.getSelectionStartDay();
            final val timeEnd = intervalsAdapter.getSelectionEndTime();
            final val dateEnd = intervalsAdapter.getSelectionEndDay();

            if (timeEnd != null && timeStart != null && dateStart != null && dateEnd != null) {
                eventArgs =
                    new CreateOrderEventArgs(playgroundId, dateStart, dateEnd, timeStart, timeEnd);
            }
        }

        return eventArgs;
    }

    @Nullable
    protected Integer getReservationPrice() {
        Integer reservationPrice = null;

        final val intervalsAdapter = getIntervalsAdapter();
        if (intervalsAdapter != null) {
            final val selectedCellRange = intervalsAdapter.getSelectedCellRange();

            if (selectedCellRange != null) {
                final int startPosition = selectedCellRange.getMinimum();
                final int endPosition = selectedCellRange.getMaximum();

                int price = 0;
                for (int i = startPosition; i <= endPosition; i++) {
                    final val interval = intervalsAdapter.getCellItemByRelativePosition(i);
                    if (interval == null) {
                        throw new IllegalStateException(
                            "Selected reservation interval is not found.");
                    }

                    final val intervalPrice = interval.getPrice();
                    if (intervalPrice == null) {
                        throw new IllegalStateException(
                            "Selected reservation interval's price is not found.");
                    }

                    price += intervalPrice;
                }

                reservationPrice = price;
            }
        }

        return reservationPrice;
    }

    @CallSuper
    @Override
    protected void onInjectMembers() {
        super.onInjectMembers();

        getPlayerSubscreenComponent().inject(this);

        final val presenter = getPresenter();
        if (presenter != null) {
            presenter.bindScreen(this);
        }
    }

    @CallSuper
    protected void onPlaygroundIdChanged() {
        if (isResumed()) {
            final val viewIntervalsEventArgs = getViewIntervalsEventArgs();
            if (viewIntervalsEventArgs != null) {
                _viewIntervalsEvent.rise(viewIntervalsEventArgs);
            }
        }
    }

    @CallSuper
    protected void onReservationStartTimeChanged() {
        finishIntervalSelectionActionMode();

        if (isResumed()) {
            final val viewIntervalsEventArgs = getViewIntervalsEventArgs();
            if (viewIntervalsEventArgs != null) {
                _viewIntervalsEvent.rise(viewIntervalsEventArgs);
            }
        }

        final val intervalsAdapter = getIntervalsAdapter();
        if (intervalsAdapter != null) {
            intervalsAdapter.setCells(null);
            intervalsAdapter.setStartTime(getReservationStartTime());
            intervalsAdapter.notifyDataSetChanged();
        }
    }

    protected void showCreateOrderConfirmationDialog(
        @NonNull final PlaygroundTitle playground,
        @NonNull final SportComplexTitle sportComplex,
        @NonNull final Interval startInterval,
        @NonNull final Interval endInterval,
        final int reservationPrice,
        final long startDayTime,
        final long endDayTime) {
        Contracts.requireNonNull(playground, "playground == null");
        Contracts.requireNonNull(sportComplex, "sportComplex == null");
        Contracts.requireNonNull(startInterval, "startInterval == null");
        Contracts.requireNonNull(endInterval, "endInterval == null");

        dismissCreateOrderConfirmationDialog();

        final val context = getContext();

        final val message = getString(R.string.intervals_create_order_confirmation_message,
                                      sportComplex.getName(),
                                      playground.getName());

        final val dateTime = getString(R.string.intervals_create_order_confirmation_message_time,
                                       ProSportFormat.getFormattedIntervalsDateTime(context,
                                                                                    startInterval,
                                                                                    endInterval,
                                                                                    startDayTime,
                                                                                    endDayTime));

        final val price =
            getString(R.string.intervals_create_order_confirmation_message_price, reservationPrice);

        final val messageParts = Arrays.asList(message, StringUtils.EMPTY, dateTime, price);

        _dialogCreateOrderConfirmation = new AlertDialog.Builder(context)
            .setTitle(R.string.intervals_create_order_confirmation_title)
            .setMessage(TextUtils.join("\n", messageParts))
            .setCancelable(false)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    final val createOrderEventArgs = getCreateOrderEventArgs();
                    if (createOrderEventArgs != null) {
                        _createOrderEvent.rise(createOrderEventArgs);
                    }
                }
            })
            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    dismissCreateOrderConfirmationDialog();
                }
            })
            .show();
    }

    @Named(PresenterNames.INTERVALS)
    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Nullable
    /*package-private*/ Presenter<IntervalsScreen> _presenter;

    @NonNull
    private final ManagedEvent<CreateOrderEventArgs> _createOrderEvent = Events.createEvent();

    @NonNull
    private final ManagedEvent<UriEventArgs> _playgroundPhoneCallEvent = Events.createEvent();

    @NonNull
    private final ManagedEvent<ViewIntervalsEventArgs> _refreshIntervalsEvent =
        Events.createEvent();

    @NonNull
    private final ManagedEvent<ViewIntervalsEventArgs> _viewIntervalsEvent = Events.createEvent();

    @Nullable
    private AlertDialog _dialogCreateOrderConfirmation;

    @Nullable
    private ProgressDialog _dialogCreateOrderProgress;

    @Nullable
    private AlertDialog _dialogCreatedOrder;

    @Nullable
    private AlertDialog _dialogFailCrateOrder;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private IntervalsBookingAdapter _intervalsAdapter;

    @Nullable
    private View _intervalsErrorView;

    @Nullable
    private ContentLoaderProgressBar _intervalsLoadingView;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private LoadingViewDelegate _intervalsLoadingViewDelegate;

    @Nullable
    private View _intervalsNoContentView;

    @Nullable
    private LinearLayout _intervalsOverlayHeaderView;

    @Nullable
    private SwipeRefreshLayout _intervalsRefreshView;

    @Nullable
    private ActionMode _intervalsSelectionActionMode;

    @NonNull
    private final NoticeEventHandler _cellsSelectionChangedHandler = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            if (_intervalsSelectionActionMode != null) {
                _intervalsSelectionActionMode.invalidate();
            }
        }
    };

    @Nullable
    private RecyclerView _intervalsView;

    @Getter
    @Setter
    @Nullable
    private PlaygroundTitle _playground;

    @Getter
    @Nullable
    private Long _playgroundId;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private GridLayoutManager _playgroundIntervalsLayoutManger;

    @Getter
    @Nullable
    private Long _reservationStartTime;

    @Getter
    @Setter
    @Nullable
    private SportComplexTitle _sportComplex;

    @NonNull
    private final NoticeEventHandler _cellsSelectionStateChangedHandler = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            final val intervalsAdapter = getIntervalsAdapter();
            if (intervalsAdapter != null) {
                if (intervalsAdapter.hasSelection()) {
                    startIntervalSelectionActionMode();
                } else {
                    finishIntervalSelectionActionMode();
                }
            }
        }
    };

    private void dismissCreateOrderConfirmationDialog() {
        if (_dialogCreateOrderConfirmation != null) {
            _dialogCreateOrderConfirmation.dismiss();

            _dialogCreateOrderConfirmation = null;
        }
    }

    private void dismissCreateOrderProgressDialog() {
        if (_dialogCreateOrderProgress != null) {
            _dialogCreateOrderProgress.dismiss();

            _dialogCreateOrderProgress = null;
        }
    }

    private void dismissCreatedOrderDialog() {
        if (_dialogCreatedOrder != null) {
            _dialogCreatedOrder.dismiss();

            _dialogCreatedOrder = null;
        }
    }

    private void dismissFailCrateOrderDialog() {
        if (_dialogFailCrateOrder != null) {
            _dialogFailCrateOrder.dismiss();
        }
    }

    private void finishIntervalSelectionActionMode() {
        if (_intervalsSelectionActionMode != null) {
            _intervalsSelectionActionMode.finish();
        }
    }

    private void initializePlaygroundIntervalsView() {
        if (_intervalsView != null) {
            final long intervalLength = ProSportDataContract.INTERVAL_LENGTH;
            final int columnCount = (int) (DateUtils.WEEK_IN_MILLIS / DateUtils.DAY_IN_MILLIS);
            final int rowCount = (int) (DateUtils.DAY_IN_MILLIS / intervalLength);

            _intervalsAdapter = new IntervalsBookingAdapter();

            _intervalsAdapter.setCellViewHolderBinder(new IntervalViewHolderBinder<>(
                _intervalsAdapter));
            _intervalsAdapter.setIntervalLength(intervalLength);
            _intervalsAdapter.setTableColumnCount(columnCount);
            _intervalsAdapter.setTableRowCount(rowCount);
            _intervalsAdapter.setStartTime(getReservationStartTime());

            if (_intervalsOverlayHeaderView != null) {
                _intervalsAdapter.bindOverlayHeaderView(_intervalsView,
                                                        _intervalsOverlayHeaderView);
            }
            _playgroundIntervalsLayoutManger =
                (GridLayoutManager) _intervalsView.getLayoutManager();

            _intervalsView.setItemViewCacheSize(400);
            _intervalsView.setAdapter(_intervalsAdapter);
        }
    }

    private void showCreateOrderProgressDialog() {
        dismissCreateOrderProgressDialog();

        final val title = getString(R.string.intervals_create_order_progress_title);
        final val message = getString(R.string.intervals_create_order_progress_message);
        _dialogCreateOrderProgress = ProgressDialog.show(getContext(), title, message, true, false);
    }

    private void showCreatedOrderDialog(@NonNull final Order order) {
        Contracts.requireNonNull(order, "order == null");

        dismissCreatedOrderDialog();

        final val title = getString(R.string.intervals_order_created_title_format, order.getId());
        _dialogCreatedOrder = new AlertDialog.Builder(getContext())
            .setTitle(title)
            .setMessage(R.string.intervals_order_created_message)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    dismissCreatedOrderDialog();
                }
            })
            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(final DialogInterface dialog) {
                    final val viewIntervalsEventArgs = getViewIntervalsEventArgs();
                    if (viewIntervalsEventArgs != null) {
                        _viewIntervalsEvent.rise(viewIntervalsEventArgs);
                    }
                }
            })
            .show();
    }

    private void showFailCrateOrderDialog(@Nullable final SportComplexTitle sportComplex) {
        dismissFailCrateOrderDialog();

        final val dialogBuilder = new AlertDialog.Builder(getContext());

        if (sportComplex != null) {
            final val phoneNumbers = sportComplex.getPhoneNumbers();
            if (phoneNumbers != null && !phoneNumbers.isEmpty()) {
                //@formatter:off
                dialogBuilder
                    .setNeutralButton(
                        R.string.intervals_fail_create_order_call,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                final DialogInterface dialog,
                                final int which) {
                                dismissFailCrateOrderDialog();

                                final val telephoneNumber = phoneNumbers.get(0);
                                final val telephoneUri = UriFactoryUtils.getTelephoneUri(telephoneNumber);
                                _playgroundPhoneCallEvent.rise(new UriEventArgs(telephoneUri));
                            }
                    });
                //@formatter:on
            }
        }

        _dialogFailCrateOrder = dialogBuilder
            .setTitle(R.string.intervals_fail_create_order_title)
            .setMessage(R.string.intervals_fail_create_order_message)
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    dismissFailCrateOrderDialog();
                }
            })
            .show();
    }

    private void startIntervalSelectionActionMode() {
        final val activity = getSupportActivity();
        if (activity != null) {
            _intervalsSelectionActionMode =
                activity.startSupportActionMode(new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(final ActionMode mode, final Menu menu) {
                        mode.setTitle(R.string.intervals_selection_mode_title);

                        mode.getMenuInflater().inflate(R.menu.inervals_selection, menu);

                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(final ActionMode mode, final Menu menu) {
                        String subtitle = null;

                        final val intervalsAdapter = getIntervalsAdapter();
                        if (intervalsAdapter != null) {
                            final val firstInterval = intervalsAdapter.getFirstSelectedCell();
                            final val lastInterval = intervalsAdapter.getLastSelectedCell();
                            final val startDayTime = intervalsAdapter.getSelectionStartDay();
                            final val endDayTime = intervalsAdapter.getSelectionEndDay();
                            if (firstInterval != null && lastInterval != null &&
                                startDayTime != null && endDayTime != null) {
                                //@formatter:off
                                subtitle = ProSportFormat.getFormattedIntervalsDateTime(
                                    getContext(),
                                    firstInterval,
                                    lastInterval,
                                    startDayTime,
                                    endDayTime);
                                //@formatter:on
                            }
                        }

                        mode.setSubtitle(subtitle);

                        return true;
                    }

                    @Override
                    public boolean onActionItemClicked(final ActionMode mode, final MenuItem item) {
                        final boolean consumed;

                        final int id = item.getItemId();
                        if (id == R.id.action_selection_done) {
                            final val adapter = getIntervalsAdapter();
                            if (adapter != null) {
                                final val playground = getPlayground();
                                final val sportComplex = getSportComplex();
                                final val reservationPrice = getReservationPrice();
                                final val firstInterval = adapter.getFirstSelectedCell();
                                final val lastInterval = adapter.getLastSelectedCell();
                                final val startDay = adapter.getSelectionStartDay();
                                final val endDay = adapter.getSelectionEndDay();

                                if (playground != null && sportComplex != null &&
                                    firstInterval != null && lastInterval != null &&
                                    reservationPrice != null && startDay != null &&
                                    endDay != null) {
                                    showCreateOrderConfirmationDialog(playground,
                                                                      sportComplex,
                                                                      firstInterval,
                                                                      lastInterval,
                                                                      reservationPrice,
                                                                      startDay,
                                                                      endDay);
                                } else {
                                    // TODO: 27.03.2017 Toast
                                }
                            }

                            consumed = true;
                        } else {
                            consumed = false;
                        }

                        return consumed;
                    }

                    @Override
                    public void onDestroyActionMode(final ActionMode mode) {
                        final val reservationAdapter = getIntervalsAdapter();
                        if (reservationAdapter != null) {
                            reservationAdapter.clearSelection();
                        }

                        _intervalsSelectionActionMode = null;
                    }
                });
        }
    }
}
