package com.btc.prosport.manager.screen.fragment;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.event.Events;
import com.btc.common.event.generic.Event;
import com.btc.common.event.generic.EventHandler;
import com.btc.common.event.generic.ManagedEvent;
import com.btc.common.event.notice.NoticeEventHandler;
import com.btc.common.extension.delegate.loading.FadeVisibilityHandler;
import com.btc.common.extension.delegate.loading.LoadingViewDelegate;
import com.btc.common.extension.delegate.loading.ProgressVisibilityHandler;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.extension.view.ContentLoaderProgressBar;
import com.btc.common.mvp.presenter.Presenter;
import com.btc.common.utility.SwipeRefreshUtils;
import com.btc.prosport.api.ProSportDataContract;
import com.btc.prosport.api.model.Interval;
import com.btc.prosport.api.model.PlaygroundTitle;
import com.btc.prosport.core.adapter.cell.binder.IntervalViewHolderBinder;
import com.btc.prosport.core.eventArgs.ViewIntervalsEventArgs;
import com.btc.prosport.core.utility.ProSportFormat;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.adapter.schedule.ScheduleAdapter;
import com.btc.prosport.manager.core.eventArgs.CreateSimpleOrderEventArgs;
import com.btc.prosport.manager.di.qualifier.PresenterNames;
import com.btc.prosport.manager.screen.ScheduleScreen;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

@Accessors(prefix = "_")
public final class ScheduleFragment extends BaseWorkspaceFragment
    implements ScheduleScreen, SwipeRefreshLayout.OnRefreshListener {
    private static final String _LOG_TAG = ConstantBuilder.logTag(ScheduleFragment.class);

    @Override
    public final void displayIntervals(
        @Nullable final PlaygroundTitle playground, @Nullable final List<Interval> intervals) {
        finishIntervalSelectionActionMode();

        if (_scheduleRefreshView != null) {
            _scheduleRefreshView.setRefreshing(false);
        }

        _playground = playground;

        final val scheduleAdapter = getScheduleAdapter();
        if (scheduleAdapter != null) {
            scheduleAdapter.setCells(intervals);
            scheduleAdapter.notifyDataSetChanged();
        }

        final val loadingViewDelegate = getScheduleLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showContent(
                intervals != null && !intervals.isEmpty() && getStartTime() != null);
        }
    }

    @Override
    public final void displayIntervalsLoading() {
        finishIntervalSelectionActionMode();

        final val loadingViewDelegate = getScheduleLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showLoading();
        }
    }

    @Override
    public final void displayIntervalsLoadingError() {
        finishIntervalSelectionActionMode();

        if (_scheduleRefreshView != null) {
            _scheduleRefreshView.setRefreshing(false);
        }

        final val loadingViewDelegate = getScheduleLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showError();
        }
    }

    @NonNull
    @Override
    public final Event<CreateSimpleOrderEventArgs> getCreateOrderEvent() {
        return _createOrderEvent;
    }

    @NonNull
    @Override
    public final Event<ViewIntervalsEventArgs> getRefreshIntervalsEvent() {
        return _refreshIntervalsEvent;
    }

    @NonNull
    @Override
    public final Event<ViewIntervalsEventArgs> getViewIntervalsEvent() {
        return _viewIntervalsEvent;
    }

    @NonNull
    @Override
    public final Event<IdEventArgs> getViewOrderEvent() {
        return _viewOrderEvent;
    }

    public final void setPlaygroundId(final Long playgroundId) {
        if (!Objects.equals(_playgroundId, playgroundId)) {
            _playgroundId = playgroundId;

            onPlaygroundIdChanged();
        }
    }

    @CallSuper
    @Override
    public void bindViews(@NonNull final View view) {
        super.bindViews(view);

        _scheduleLoadingErrorView = view.findViewById(R.id.loading_error);
        _scheduleView = (RecyclerView) view.findViewById(R.id.schedule);
        _scheduleLoadingView = (ContentLoaderProgressBar) view.findViewById(R.id.loading);
        _scheduleNoContentView = view.findViewById(R.id.no_content);
        _scheduleRefreshView = (SwipeRefreshLayout) view.findViewById(R.id.schedule_refresh);
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

        final val view = inflater.inflate(R.layout.fragment_schedule, container, false);

        bindViews(view);

        initializeScheduleView();

        final val scheduleAdapter = getScheduleAdapter();
        if (scheduleAdapter != null) {
            scheduleAdapter.getViewOrderEvent().addHandler(_viewOrderHandler);
            scheduleAdapter
                .getCellsSelectionStateChangedEvent()
                .addHandler(_cellsSelectionStateChangedHandler);
            scheduleAdapter
                .getCellsSelectionChangedEvent()
                .addHandler(_cellsSelectionChangedHandler);
        }

        if (_scheduleRefreshView != null) {
            SwipeRefreshUtils.setupPrimaryColors(_scheduleRefreshView);
            _scheduleRefreshView.setOnRefreshListener(this);
        }

        _scheduleLoadingViewDelegate = LoadingViewDelegate
            .builder()
            .setContentView(_scheduleView)
            .setLoadingView(_scheduleLoadingView)
            .setNoContentView(_scheduleNoContentView)
            .setErrorView(_scheduleLoadingErrorView)
            .setContentVisibilityHandler(new FadeVisibilityHandler(R.anim.fade_in_long, 0) {
                @Override
                public void changeVisibility(@NonNull final View view, final boolean visible) {
                    super.changeVisibility(view, visible);

                    final val scheduleAdapter = getScheduleAdapter();
                    if (scheduleAdapter != null && _scheduleView != null) {
                        scheduleAdapter.invalidateOverlayHeaderVisibility(_scheduleView);
                    }
                }
            })
            .setLoadingVisibilityHandler(new ProgressVisibilityHandler())
            .build();

        return view;
    }

    @CallSuper
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        final val scheduleAdapter = getScheduleAdapter();
        if (scheduleAdapter != null) {
            scheduleAdapter.getViewOrderEvent().removeHandler(_viewOrderHandler);
            scheduleAdapter
                .getCellsSelectionStateChangedEvent()
                .removeHandler(_cellsSelectionStateChangedHandler);
            scheduleAdapter
                .getCellsSelectionChangedEvent()
                .removeHandler(_cellsSelectionChangedHandler);

            if (_scheduleView != null) {
                scheduleAdapter.unbindOverlayHeaderView(_scheduleView);
            }
        }

        unbindViews();
    }

    @CallSuper
    @Override
    public void onRefresh() {
        final val viewIntervalsEventArgs = getViewIntervalsEventArgs();
        if (viewIntervalsEventArgs != null) {
            _refreshIntervalsEvent.rise(viewIntervalsEventArgs);
        } else {
            if (_scheduleRefreshView != null) {
                _scheduleRefreshView.setRefreshing(false);
            }
        }
    }

    @CallSuper
    @Override
    public void onResume() {
        super.onResume();

        final val scheduleAdapter = getScheduleAdapter();
        if (scheduleAdapter != null && _scheduleView != null) {
            scheduleAdapter.invalidateOverlayHeader(_scheduleView);
            scheduleAdapter.invalidateOverlayHeaderVisibility(_scheduleView);
        }

        if (isPageActive()) {
            riseViewIntervalsEvent();
        }
    }

    public void setStartTime(@Nullable final Long startTime) {
        if (!Objects.equals(_startTime, startTime)) {
            _startTime = startTime;

            onStartTimeChanged();
        }
    }

    @Nullable
    protected final CreateSimpleOrderEventArgs getCreateOrderEventArgs() {
        CreateSimpleOrderEventArgs result = null;

        final val playgroundId = getPlaygroundId();
        final val scheduleAdapter = getScheduleAdapter();
        if (playgroundId != null && scheduleAdapter != null) {
            final val timeStart = scheduleAdapter.getSelectionStartTime();
            final val dateStart = scheduleAdapter.getSelectionStartDay();
            final val timeEnd = scheduleAdapter.getSelectionEndTime();
            final val dateEnd = scheduleAdapter.getSelectionEndDay();

            if (timeEnd != null && timeStart != null && dateStart != null && dateEnd != null) {
                result = new CreateSimpleOrderEventArgs(playgroundId,
                                                        dateStart,
                                                        dateEnd,
                                                        timeStart,
                                                        timeEnd);
            }
        }

        return result;
    }

    @Nullable
    protected final ViewIntervalsEventArgs getViewIntervalsEventArgs() {
        final ViewIntervalsEventArgs eventArgs;

        final val playgroundId = getPlaygroundId();
        final val startTime = getStartTime();
        if (playgroundId != null && startTime != null) {
            eventArgs = new ViewIntervalsEventArgs(playgroundId, startTime);
        } else {
            eventArgs = null;
        }

        return eventArgs;
    }

    @CallSuper
    @Override
    protected void onEnterPage() {
        super.onEnterPage();

        if (isResumed()) {
            riseViewIntervalsEvent();
        }
    }

    @CallSuper
    @Override
    protected void onLeavePage() {
        super.onLeavePage();

        finishIntervalSelectionActionMode();
    }

    @CallSuper
    @Override
    protected void onInjectMembers() {
        super.onInjectMembers();

        getManagerSubscreenComponent().inject(this);

        final val presenter = getPresenter();
        if (presenter != null) {
            presenter.bindScreen(this);
        }
    }

    @CallSuper
    protected void onPlaygroundIdChanged() {
        if (isResumed() && isPageActive()) {
            riseViewIntervalsEvent();
        }
    }

    protected void onStartTimeChanged() {
        finishIntervalSelectionActionMode();

        if (isResumed()) {
            riseViewIntervalsEvent();
        }

        final val scheduleAdapter = getScheduleAdapter();
        if (scheduleAdapter != null) {
            scheduleAdapter.setCells(null);
            scheduleAdapter.setStartTime(getStartTime());
            scheduleAdapter.notifyDataSetChanged();
        }
    }

    @Named(PresenterNames.SCHEDULE)
    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Nullable
    /*package-private*/ Presenter<ScheduleScreen> _presenter;

    @NonNull
    private final ManagedEvent<CreateSimpleOrderEventArgs> _createOrderEvent = Events.createEvent();

    @NonNull
    private final ManagedEvent<ViewIntervalsEventArgs> _refreshIntervalsEvent =
        Events.createEvent();

    @NonNull
    private final ManagedEvent<ViewIntervalsEventArgs> _viewIntervalsEvent = Events.createEvent();

    @NonNull
    private final ManagedEvent<IdEventArgs> _viewOrderEvent = Events.createEvent();

    @NonNull
    private final EventHandler<IdEventArgs> _viewOrderHandler = new EventHandler<IdEventArgs>() {
        @Override
        public void onEvent(@NonNull final IdEventArgs eventArgs) {
            _viewOrderEvent.rise(eventArgs);
        }
    };

    @Nullable
    private LinearLayout _intervalsOverlayHeaderView;

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

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private PlaygroundTitle _playground;

    @Getter
    private Long _playgroundId;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private ScheduleAdapter _scheduleAdapter;

    @NonNull
    private final NoticeEventHandler _cellsSelectionStateChangedHandler = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            final val scheduleAdapter = getScheduleAdapter();
            if (scheduleAdapter != null) {
                if (scheduleAdapter.hasSelection()) {
                    startIntervalSelectionActionMode();
                } else {
                    finishIntervalSelectionActionMode();
                }
            }
        }
    };

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private GridLayoutManager _scheduleLayoutManger;

    @Nullable
    private View _scheduleLoadingErrorView;

    @Nullable
    private ContentLoaderProgressBar _scheduleLoadingView;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private LoadingViewDelegate _scheduleLoadingViewDelegate;

    @Nullable
    private View _scheduleNoContentView;

    @Nullable
    private SwipeRefreshLayout _scheduleRefreshView;

    @Nullable
    private RecyclerView _scheduleView;

    @Getter
    @Nullable
    private Long _startTime;

    private void finishIntervalSelectionActionMode() {
        if (_intervalsSelectionActionMode != null) {
            _intervalsSelectionActionMode.finish();
        }
    }

    private void initializeScheduleView() {
        if (_scheduleView != null) {
            final long intervalLength = ProSportDataContract.INTERVAL_LENGTH;
            final int columnCount = (int) (DateUtils.WEEK_IN_MILLIS / DateUtils.DAY_IN_MILLIS);
            final int rowCount = (int) (DateUtils.DAY_IN_MILLIS / intervalLength);

            _scheduleAdapter = new ScheduleAdapter();

            _scheduleAdapter.setCellViewHolderBinder(new IntervalViewHolderBinder<>
                                                         (_scheduleAdapter));
            _scheduleAdapter.setIntervalLength(intervalLength);
            _scheduleAdapter.setTableColumnCount(columnCount);
            _scheduleAdapter.setTableRowCount(rowCount);
            _scheduleAdapter.setStartTime(getStartTime());

            if (_intervalsOverlayHeaderView != null) {
                _scheduleAdapter.bindOverlayHeaderView(_scheduleView, _intervalsOverlayHeaderView);
            }
            _scheduleLayoutManger = (GridLayoutManager) _scheduleView.getLayoutManager();

            _scheduleView.setItemViewCacheSize(400);
            _scheduleView.setAdapter(_scheduleAdapter);
        }
    }

    private void riseViewIntervalsEvent() {
        final val viewIntervalsEventArgs = getViewIntervalsEventArgs();

        if (viewIntervalsEventArgs != null) {
            _viewIntervalsEvent.rise(viewIntervalsEventArgs);
        } else {
            final val loadingViewDelegate = getScheduleLoadingViewDelegate();
            if (loadingViewDelegate != null) {
                loadingViewDelegate.showContent(false);
            }
        }
    }

    private void startIntervalSelectionActionMode() {
        final val activity = getSupportActivity();
        if (activity != null) {
            _intervalsSelectionActionMode =
                activity.startSupportActionMode(new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(final ActionMode mode, final Menu menu) {
                        mode.setTitle(R.string.workspace_intervals_selection_mode_title);

                        mode.getMenuInflater().inflate(R.menu.workspace_inervals_selection, menu);

                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(final ActionMode mode, final Menu menu) {
                        String subtitle = null;

                        final val scheduleAdapter = getScheduleAdapter();
                        if (scheduleAdapter != null) {
                            final val firstInterval = scheduleAdapter.getFirstSelectedCell();
                            final val lastInterval = scheduleAdapter.getLastSelectedCell();
                            final val startDayTime = scheduleAdapter.getSelectionStartDay();
                            final val endDayTime = scheduleAdapter.getSelectionEndDay();
                            if (firstInterval != null && lastInterval != null &&
                                startDayTime != null && endDayTime != null) {
                                final val context = getContext();

                                //@formatter:off
                                subtitle = ProSportFormat.getFormattedIntervalsDateTime(
                                    context,
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
                            final val createOrderEventArgs = getCreateOrderEventArgs();
                            if (createOrderEventArgs != null) {
                                _createOrderEvent.rise(createOrderEventArgs);
                                finishIntervalSelectionActionMode();
                            }

                            consumed = true;
                        } else {
                            consumed = false;
                        }

                        return consumed;
                    }

                    @Override
                    public void onDestroyActionMode(final ActionMode mode) {
                        final val adapter = getScheduleAdapter();
                        if (adapter != null) {
                            adapter.clearSelection();
                        }

                        _intervalsSelectionActionMode = null;
                    }
                });
        }
    }
}
