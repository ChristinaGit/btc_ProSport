package com.btc.prosport.manager.screen.activity.workspace;

import android.app.SearchManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.Spinner;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.event.Events;
import com.btc.common.event.generic.Event;
import com.btc.common.event.generic.EventHandler;
import com.btc.common.event.generic.ManagedEvent;
import com.btc.common.event.notice.ManagedNoticeEvent;
import com.btc.common.event.notice.NoticeEvent;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.extension.pager.ActivePage;
import com.btc.common.mvp.presenter.Presenter;
import com.btc.common.utility.AnimationViewUtils;
import com.btc.common.utility.HandlerUtils;
import com.btc.prosport.api.ProSportContract;
import com.btc.prosport.api.model.SportComplexReport;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.OrderStatusFilter;
import com.btc.prosport.manager.core.OrdersSortOrder;
import com.btc.prosport.manager.core.adapter.orderFilterAdapter.OrdersFilterAdapter;
import com.btc.prosport.manager.core.eventArgs.OrdersFilterEventArgs;
import com.btc.prosport.manager.di.qualifier.PresenterNames;
import com.btc.prosport.manager.screen.WorkspaceScreen;
import com.btc.prosport.manager.screen.activity.BaseManagerNavigationActivity;
import com.btc.prosport.manager.screen.fragment.OrdersFilterListFragment;
import com.btc.prosport.manager.screen.fragment.SalesListFragment;
import com.btc.prosport.manager.screen.fragment.ScheduleFragment;
import com.btc.prosport.manager.screen.fragment.playgroundOrdersList.OrdersListFragment;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import org.apache.commons.lang3.ObjectUtils;
import org.parceler.Parcels;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.SimpleTimeZone;

import javax.inject.Inject;
import javax.inject.Named;

@Accessors(prefix = "_")
public final class WorkspaceActivity extends BaseManagerNavigationActivity
    implements WorkspaceScreen, OnTabSelectListener, CalendarView.OnDateChangeListener {
    private static final String _LOG_TAG = ConstantBuilder.logTag(WorkspaceActivity.class);

    private static final String _KEY_SAVED_STATE =
        ConstantBuilder.savedStateKey(WorkspaceActivity.class, "saved_state");

    @NonNull
    @Override
    public final Event<IdEventArgs> getCreateOrderEvent() {
        return _createOrderEvent;
    }

    @NonNull
    @Override
    public final NoticeEvent getCreateSaleEvent() {
        return _createSaleEvent;
    }

    @Override
    public final void onTabSelected(@IdRes final int tabId) {
        if (_scheduleFragmentContainerView != null) {
            _scheduleFragmentContainerView.setVisibility(View.INVISIBLE);
        }
        if (_ordersListFragmentContainerView != null) {
            _ordersListFragmentContainerView.setVisibility(View.INVISIBLE);
        }
        if (_salesListFragmentContainerView != null) {
            _salesListFragmentContainerView.setVisibility(View.INVISIBLE);
        }

        switch (tabId) {
            case R.id.tab_schedule: {
                final val scheduleFragment = getScheduleFragment();
                if (scheduleFragment != null) {
                    scheduleFragment.setStartTime(getScheduleStartTime());
                    scheduleFragment.setPlaygroundId(getSelectedPlaygroundId());
                }

                if (_scheduleFragmentContainerView != null) {
                    _scheduleFragmentContainerView.setVisibility(View.VISIBLE);
                }
                break;
            }
            case R.id.tab_sales: {
                if (_salesListFragmentContainerView != null) {
                    _salesListFragmentContainerView.setVisibility(View.VISIBLE);
                }
                break;
            }
            case R.id.tab_orders: {
                if (_ordersListFragmentContainerView != null) {
                    _ordersListFragmentContainerView.setVisibility(View.VISIBLE);
                }
                break;
            }
        }

        setActiveTabId(tabId);
    }

    @CallSuper
    @Override
    public void bindViews() {
        super.bindViews();

        _appbarView = (AppBarLayout) findViewById(R.id.appbar);
        _bottomBar = (BottomBar) findViewById(R.id.bottom_bar);
        _toolbarView = (Toolbar) findViewById(R.id.toolbar);
        _spinnerView = (Spinner) findViewById(R.id.orders_filter);
        _datePickerView = (CalendarView) findViewById(R.id.date_picker);
        _scheduleFragmentContainerView = findViewById(R.id.schedule_fragment_container);
        _ordersListFragmentContainerView = findViewById(R.id.orders_list_fragment_container);
        _salesListFragmentContainerView = findViewById(R.id.sales_list_fragment_container);
        _ordersFilterListFragmentContainerView = findViewById(R.id.filters_list_fragment_container);
        _fab = (FloatingActionButton) findViewById(R.id.fab);
    }

    @CallSuper
    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        if (outState != null) {
            if (_state == null) {
                _state = new WorkspaceState();
            }

            _state.setSelectedPlaygroundId(getSelectedPlaygroundId());
            _state.setDatePickerExpanded(isDatePickerExpanded());
            _state.setStartDisplayedDate(getScheduleStartTime());

            if (_bottomBar != null) {
                _state.setActiveTabId(_bottomBar.getCurrentTabId());
            } else {
                _state.setActiveTabId(R.id.tab_schedule);
            }

            _state.setOrdersFilterParams(getOrdersFilterParams());
            _state.setStatusOrdersFilterParams(getStatusOrdersFilterParams());
            _state.setOrdersSortOrder(getOrdersSortOrder());
            _state.setOrdersSearchEnabled(isOrdersSearchEnabled());

            outState.putParcelable(_KEY_SAVED_STATE, Parcels.wrap(_state));
        }
    }

    @CallSuper
    @Override
    protected void onReleaseInjectedMembers() {
        super.onReleaseInjectedMembers();

        final val presenter = getPresenter();
        if (presenter != null) {
            presenter.unbindScreen();
        }
    }

    @Override
    public void displaySportComplexes(@Nullable final List<SportComplexReport> sportComplexes) {
        super.displaySportComplexes(sportComplexes);

        val ordersFilterListFragment = getOrdersFilterListFragment();
        if (ordersFilterListFragment != null) {
            ordersFilterListFragment.setSportComplexes(sportComplexes);
        }
    }

    @Override
    public void displaySportComplexesLoading() {
        super.displaySportComplexesLoading();

        val ordersFilterListFragment = getOrdersFilterListFragment();
        if (ordersFilterListFragment != null) {
            ordersFilterListFragment.displaySportComplexesLoading();
        }
    }

    @CallSuper
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final boolean consumed;

        final int itemId = item.getItemId();
        if (itemId == R.id.action_pick_date) {
            setDatePickerExpanded(!isDatePickerExpanded(), true);

            consumed = true;
        } else if (itemId == R.id.action_sort_date_ascending) {
            setOrdersSortOrder(OrdersSortOrder.DATE_ASCENDING);
            item.setChecked(true);
            consumed = true;
        } else if (itemId == R.id.action_sort_date_descending) {
            setOrdersSortOrder(OrdersSortOrder.DATE_DESCENDING);
            item.setChecked(true);
            consumed = true;
        } else if (itemId == R.id.action_sort_price_ascending) {
            item.setChecked(true);
            setOrdersSortOrder(OrdersSortOrder.PRICE_ASCENDING);
            consumed = true;
        } else if (itemId == R.id.action_sort_price_descending) {
            item.setChecked(true);
            setOrdersSortOrder(OrdersSortOrder.PRICE_DESCENDING);
            consumed = true;
        } else if (itemId == android.R.id.home && isOrdersSearchEnabled()) {
            onBackPressed();

            consumed = true;
        } else {
            consumed = super.onOptionsItemSelected(item);
        }

        return consumed;
    }

    @CallSuper
    @Override
    protected boolean onNavigateToPlayground(final long id) {
        if (_bottomBar != null) {
            _bottomBar.selectTabWithId(R.id.tab_schedule);
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        val ordersFilterListFragment = getOrdersFilterListFragment();
        if (ordersFilterListFragment != null) {
            ordersFilterListFragment.getOrdersFilterEvent().addHandler(_ordersFilterHandler);
        }
    }

    @CallSuper
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            _state = onHandleSavedState(savedInstanceState);
        }

        if (_state == null) {
            _state = onHandleIntent(getIntent());
        }

        if (_state != null) {
            setContentView(R.layout.activity_manager_workspace);

            _inputDelayUserSearch = getResources().getInteger(R.integer.input_delay_user_search);

            bindViews();

            final val fragmentManager = getSupportFragmentManager();
            _scheduleFragment =
                (ScheduleFragment) fragmentManager.findFragmentById(R.id.schedule_fragment);
            _ordersListFragment =
                (OrdersListFragment) fragmentManager.findFragmentById(R.id.orders_list_fragment);
            _salesListFragment =
                (SalesListFragment) fragmentManager.findFragmentById(R.id.sales_list_fragment);
            _ordersFilterListFragment =
                (OrdersFilterListFragment) fragmentManager.findFragmentById(R.id.filter_list_fragment);

            if (_toolbarView != null) {
                setSupportActionBar(_toolbarView);
            }

            final val actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowTitleEnabled(true);
            }

            if (_bottomBar != null) {
                _bottomBar.setOnTabSelectListener(this);
            }
            if (_spinnerView != null) {
                _ordersFilterAdapter = new OrdersFilterAdapter(this);
                _spinnerView.setAdapter(_ordersFilterAdapter);
                _spinnerView.setOnItemSelectedListener(_onOrderStatusSelectedListener);
            }
            if (_datePickerView != null) {
                _datePickerView.setOnDateChangeListener(this);
            }
            if (_appbarView != null) {
                _appbarView.addOnOffsetChangedListener(_removeDatePickerViewOnToolbarCollapsed);
            }

            if (_bottomBar != null) {
                final val activeTabId = _state.getActiveTabId();
                if (activeTabId != null) {
                    setActiveTabId(activeTabId);
                    _bottomBar.selectTabWithId(activeTabId);
                } else {
                    setActiveTabId(_bottomBar.getCurrentTabId());
                }
            }

            if (_fab != null) {
                _fab.setOnClickListener(_controlsClickListener);
            }

            final val sportComplexesAdapter = getNavigationAdapter();
            if (sportComplexesAdapter != null) {
                sportComplexesAdapter.setSelectedPlaygroundId(_state.getSelectedPlaygroundId());
            }
            // TODO: 17.04.2017 Fix behaviour after rotation
            setDatePickerExpanded(_state.isDatePickerExpanded(), false);

            val startDisplayedDate = ObjectUtils.defaultIfNull(_state.getStartDisplayedDate(),
                                                               System.currentTimeMillis());

            setOrdersSearchEnabled(_state.isOrdersSearchEnabled());

            val ordersFilterParams = _state.getOrdersFilterParams();
            if (ordersFilterParams != null) {
                setOrdersFilterParams(ordersFilterParams);
            }

            val statusOrdersFilterParams = _state.getStatusOrdersFilterParams();
            if (statusOrdersFilterParams != null) {
                setStatusOrdersFilterParams(statusOrdersFilterParams);
            }

            val ordersSortOrder = _state.getOrdersSortOrder();
            if (ordersSortOrder != null) {
                setOrdersSortOrder(ordersSortOrder);
            }

            setScheduleStartTime(startDisplayedDate);
            onScheduleStartTimeChanged();
        } else {
            finish();
        }
    }

    @CallSuper
    @Override
    protected void onSelectedPlaygroundIdChanged() {
        super.onSelectedPlaygroundIdChanged();

        final val scheduleFragment = getScheduleFragment();
        if (scheduleFragment != null) {
            scheduleFragment.setPlaygroundId(getSelectedPlaygroundId());
        }

        invalidateActionBar();
    }

    @CallSuper
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.workspace, menu);

        final val searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final val searchItem = menu.findItem(R.id.action_search);
        MenuItemCompat.setOnActionExpandListener(searchItem, _onActionExpandListener);
        _searchView = (SearchView) searchItem.getActionView();
        if (_searchView != null) {
            _searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            _searchView.setOnQueryTextListener(_searchViewQueryTextListener);
        }

        return true;
    }

    @CallSuper
    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        final boolean showMenu = super.onPrepareOptionsMenu(menu);

        final val activeTabId = getActiveTabId();

        final val pickDateItem = menu.findItem(R.id.action_pick_date);
        pickDateItem.setVisible(Objects.equals(activeTabId, R.id.tab_schedule));

        final boolean isOrdersTab = Objects.equals(activeTabId, R.id.tab_orders);

        val searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible(isOrdersTab);
        if (isOrdersSearchEnabled()) {
            if (!searchItem.isActionViewExpanded()) {
                searchItem.expandActionView();
            }
        } else {
            if (searchItem.isActionViewExpanded()) {
                searchItem.collapseActionView();
            }
        }

        val sortItem = menu.findItem(R.id.action_sort);
        sortItem.setVisible(isOrdersTab);

        if (isOrdersTab) {
            val ordersSortOrder = getOrdersSortOrder();
            switch (ordersSortOrder) {
                case DATE_ASCENDING:
                    menu.findItem(R.id.action_sort_date_ascending).setChecked(true);
                    break;
                case DATE_DESCENDING:
                    menu.findItem(R.id.action_sort_date_descending).setChecked(true);
                    break;
                case PRICE_ASCENDING:
                    menu.findItem(R.id.action_sort_price_ascending).setChecked(true);
                    break;
                case PRICE_DESCENDING:
                    menu.findItem(R.id.action_sort_price_descending).setChecked(true);
                    break;
                default:
                    throw new RuntimeException("Unknown sort order " + ordersSortOrder);
            }
        }

        return showMenu;
    }

    @CallSuper
    @Override
    public void onSelectedDayChange(
        @NonNull final CalendarView view, final int year, final int month, final int dayOfMonth) {
        Contracts.requireNonNull(view, "view == null");

        _scheduleStartTimeConverter.clear();
        _scheduleStartTimeConverter.set(year, month, dayOfMonth);

        view.setDate(_scheduleStartTimeConverter.getTimeInMillis());

        onScheduleStartTimeChanged();

        setDatePickerExpanded(false, false);
    }

    protected final boolean isDatePickerExpanded() {
        return _datePickerExpanded == null ? false : _datePickerExpanded;
    }

    protected final void setDatePickerExpanded(
        final boolean datePickerExpanded, final boolean animate) {
        if (!Objects.equals(_datePickerExpanded, datePickerExpanded)) {
            _datePickerExpanded = datePickerExpanded;

            onDatePickerExpandedStateChanged(animate);
        }
    }

    protected final void setOrdersFilterParams(
        @NonNull final OrdersFilterParams ordersFilterParams) {
        Contracts.requireNonNull(ordersFilterParams, "ordersFilterParams == null");

        if (!Objects.equals(_ordersFilterParams, ordersFilterParams)) {
            _ordersFilterParams = ordersFilterParams;

            onOrdersFilterParamsChanged();
        }
    }

    protected final void setOrdersSortOrder(@NonNull final OrdersSortOrder ordersSortOrder) {
        Contracts.requireNonNull(ordersSortOrder, "ordersSortOrder == null");

        if (ordersSortOrder != getOrdersSortOrder()) {
            _ordersSortOrder = ordersSortOrder;

            onOrdersSortOrderChanged();
        }
    }

    protected final void setStatusOrdersFilterParams(
        @NonNull final OrdersFilterParams ordersFilterParams) {
        Contracts.requireNonNull(ordersFilterParams, "ordersFilterParams == null");

        if (!Objects.equals(_statusOrdersFilterParams, ordersFilterParams)) {
            _statusOrdersFilterParams = ordersFilterParams;

            onStatusOrdersFilterParamsChanged();
        }
    }

    @Nullable
    protected Long getScheduleStartTime() {
        final Long scheduleStartTime;

        if (_datePickerView != null) {
            scheduleStartTime = _datePickerView.getDate();
        } else {
            scheduleStartTime = null;
        }

        return scheduleStartTime;
    }

    protected final void setScheduleStartTime(final long scheduleStartTime) {
        if (_datePickerView != null) {
            _datePickerView.setDate(scheduleStartTime);
        }
    }

    @CallSuper
    protected void onActivePageChanged() {
        if (_currentActionPage != null) {
            _currentActionPage.setPageActive(false);

            _currentActionPage = null;
        }

        final boolean fabActive;
        final ActivePage activePage;

        final val activePageId = getActiveTabId();
        if (activePageId != null) {
            switch (activePageId) {
                case R.id.tab_schedule:
                    fabActive = false;
                    activePage = getScheduleFragment();
                    break;
                case R.id.tab_orders: {
                    fabActive = true;
                    activePage = getOrdersListFragment();
                    break;
                }
                case R.id.tab_sales: {
                    fabActive = true;
                    activePage = getSalesListFragment();
                    break;
                }
                default: {
                    fabActive = false;
                    activePage = null;
                    break;
                }
            }
        } else {
            fabActive = false;
            activePage = null;
        }

        if (_fab != null) {
            if (fabActive) {
                _fab.show();
            } else {
                _fab.hide();
            }
        }

        if (activePage != null) {
            activePage.setPageActive(true);
        }

        _currentActionPage = activePage;

        invalidateActionBar();

        setOrdersSearchEnabled(false);
    }

    @CallSuper
    protected void onDatePickerExpandedStateChanged(final boolean animate) {
        final boolean datePickerExpanded = isDatePickerExpanded();

        if (datePickerExpanded) {
            if (_appbarView != null) {
                _appbarView.removeCallbacks(_removeDatePickerView);

                if (_datePickerView != null && _appbarView.indexOfChild(_datePickerView) < 0) {
                    final int toolbarIndex = _appbarView.indexOfChild(_toolbarView);
                    _appbarView.addView(_datePickerView, toolbarIndex + 1);

                    if (animate) {
                        AnimationViewUtils.animateSetVisibility(_datePickerView,
                                                                View.VISIBLE,
                                                                R.anim.fade_in_short);
                    } else {
                        _datePickerView.setVisibility(View.VISIBLE);
                    }
                }
            }
        } else {
            if (_datePickerView != null) {
                _datePickerView.setVisibility(View.INVISIBLE);
            }

            if (_appbarView != null) {
                if (animate) {
                    _appbarView.postDelayed(_removeDatePickerView, /*TODO: DELAY HACK!*/ 15);
                } else {
                    _removeDatePickerView.run();
                }
            }
        }
    }

    @CallSuper
    protected void onFabClick() {
        final val activeTabId = getActiveTabId();
        if (Objects.equals(activeTabId, R.id.tab_sales)) {
            _createSaleEvent.rise();
        } else if (Objects.equals(activeTabId, R.id.tab_orders)) {
            final val playgroundId = getSelectedPlaygroundId();
            if (playgroundId != null) {
                _createOrderEvent.rise(new IdEventArgs(playgroundId));
            }
        }
    }

    @Nullable
    @CallSuper
    protected WorkspaceState onHandleIntent(@NonNull final Intent intent) {
        Contracts.requireNonNull(intent, "intent == null");

        final WorkspaceState state;

        final val action = getIntent().getAction();
        if (Intent.ACTION_MAIN.equals(action)) {
            state = onHandleMainIntent(intent);
        } else if (Intent.ACTION_VIEW.equals(action)) {
            state = onHandleViewIntent(intent);
        } else {
            state = new WorkspaceState();

            state.setActiveTabId(WorkspaceIntent.getTabId(intent));
            val ordersStatusFilter = WorkspaceIntent.getOrdersStatusFilter(intent);
            if (ordersStatusFilter != null) {
                state.setStatusOrdersFilterParams(new OrdersFilterParams(ordersStatusFilter,
                                                                         null,
                                                                         null,
                                                                         null));
            }
        }

        return state;
    }

    @Nullable
    @CallSuper
    protected WorkspaceState onHandleMainIntent(@NonNull final Intent intent) {
        Contracts.requireNonNull(intent, "intent == null");

        return new WorkspaceState();
    }

    @Nullable
    @CallSuper
    protected WorkspaceState onHandleSavedState(@NonNull final Bundle savedInstanceState) {
        WorkspaceState state = null;

        if (savedInstanceState.containsKey(_KEY_SAVED_STATE)) {
            state = Parcels.unwrap(savedInstanceState.getParcelable(_KEY_SAVED_STATE));
        }

        return state;
    }

    @Nullable
    @CallSuper
    protected WorkspaceState onHandleViewIntent(@NonNull final Intent intent) {
        Contracts.requireNonNull(intent, "intent == null");

        final WorkspaceState state;

        final val data = intent.getData();
        final int code = ProSportContract.getCode(data);

        if (ProSportContract.CODE_PLAYGROUND_ALL == code) {
            state = new WorkspaceState();
        } else if (ProSportContract.CODE_PLAYGROUND == code) {
            state = new WorkspaceState();
            state.setSelectedPlaygroundId(ContentUris.parseId(data));
        } else {
            // TODO: 02.02.2017 handle incorrect data. (Toast or snackbar, error view)
            state = null;
        }

        return state;
    }

    @CallSuper
    @Override
    protected void onInjectMembers() {
        super.onInjectMembers();

        getManagerScreenComponent().inject(this);

        final val presenter = getPresenter();
        if (presenter != null) {
            presenter.bindScreen(this);
        }
    }

    @CallSuper
    protected void onOrdersFilterParamsChanged() {
        if (isOrdersSearchEnabled()) {
            final val ordersListFragment = getOrdersListFragment();
            if (ordersListFragment != null) {
                ordersListFragment.setOrdersFilterParams(getOrdersFilterParams());
            }
        }
    }

    @CallSuper
    protected void onOrdersSortOrderChanged() {
        val ordersListFragment = getOrdersListFragment();
        if (ordersListFragment != null) {
            ordersListFragment.setSortOrder(getOrdersSortOrder());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        cancelSetOrdersFilterParams();

        val ordersFilterListFragment = getOrdersFilterListFragment();
        if (ordersFilterListFragment != null) {
            ordersFilterListFragment.getOrdersFilterEvent().removeHandler(_ordersFilterHandler);
        }
    }

    @CallSuper
    protected void onScheduleStartTimeChanged() {
        invalidateActionBar();

        final val scheduleFragment = getScheduleFragment();
        if (scheduleFragment != null) {
            scheduleFragment.setStartTime(getScheduleStartTime());
        }
    }

    protected void onStatusOrdersFilterParamsChanged() {
        if (!isOrdersSearchEnabled()) {
            val ordersListFragment = getOrdersListFragment();
            if (ordersListFragment != null) {
                ordersListFragment.setOrdersFilterParams(getStatusOrdersFilterParams());
            }
        }
    }

    protected void setActiveTabId(@Nullable final Integer activeTabId) {
        if (!Objects.equals(_activeTabId, activeTabId)) {
            _activeTabId = activeTabId;

            onActivePageChanged();
        }
    }

    protected void setOrdersSearchEnabled(final boolean enabled) {
        if (enabled != _ordersSearchEnabled) {
            showOrdersFilters(enabled);

            _ordersSearchEnabled = enabled;

            if (!enabled) {
                onStatusOrdersFilterParamsChanged();
                setOrdersFilterParams(new OrdersFilterParams());
            }

            invalidateActionBar();
        }
    }

    protected void showOrdersFilters(final boolean show) {
        if (_ordersFilterListFragmentContainerView != null) {
            if (show && _ordersFilterListFragmentContainerView.getVisibility() != View.VISIBLE) {
                val ordersFilterListFragment = getOrdersFilterListFragment();
                if (ordersFilterListFragment != null) {
                    ordersFilterListFragment.setSportComplexes(getSportComplexes());
                }
                _ordersFilterListFragmentContainerView.setVisibility(View.VISIBLE);
            } else if (!show &&
                       _ordersFilterListFragmentContainerView.getVisibility() == View.VISIBLE) {
                _ordersFilterListFragmentContainerView.setVisibility(View.GONE);
                if (_appbarView != null) {
                    // FIXME: 18.05.2017
                    _appbarView.setExpanded(true);
                }
            }
        }
    }

    @Named(PresenterNames.WORKSPACE)
    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Nullable
    /*package-private*/ Presenter<WorkspaceScreen> _presenter;

    @NonNull
    private final ManagedEvent<IdEventArgs> _createOrderEvent = Events.createEvent();

    @NonNull
    private final ManagedNoticeEvent _createSaleEvent = Events.createNoticeEvent();

    @NonNull
    private final Handler _handler = HandlerUtils.getMainThreadHandler();

    @Getter
    @NonNull
    private final Calendar _scheduleStartTimeConverter =
        Calendar.getInstance(new SimpleTimeZone(0, "GMT"));

    @Getter(AccessLevel.PROTECTED)
    @IdRes
    @Nullable
    private Integer _activeTabId;

    @NonNull
    private final View.OnClickListener _controlsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            final val id = v.getId();
            if (R.id.fab == id) {
                onFabClick();
            }
        }
    };

    @Nullable
    private AppBarLayout _appbarView;

    @Nullable
    private BottomBar _bottomBar;

    @Nullable
    private ActivePage _currentActionPage;

    @Nullable
    private Boolean _datePickerExpanded = true;

    @Nullable
    private CalendarView _datePickerView;

    @NonNull
    private final Runnable _removeDatePickerView = new Runnable() {
        @Override
        public void run() {
            if (_appbarView != null) {
                _appbarView.removeView(_datePickerView);
            }
        }
    };

    @Nullable
    private FloatingActionButton _fab;

    private int _inputDelayUserSearch;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private OrdersFilterAdapter _ordersFilterAdapter;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private OrdersFilterListFragment _ordersFilterListFragment;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private View _ordersFilterListFragmentContainerView;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private OrdersFilterParams _ordersFilterParams = new OrdersFilterParams();

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private OrdersListFragment _ordersListFragment;

    @Nullable
    private View _ordersListFragmentContainerView;

    @Getter(AccessLevel.PROTECTED)
    private boolean _ordersSearchEnabled;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private OrdersSortOrder _ordersSortOrder = OrdersSortOrder.DATE_DESCENDING;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private SalesListFragment _salesListFragment;

    @Nullable
    private View _salesListFragmentContainerView;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private ScheduleFragment _scheduleFragment;

    @Nullable
    private View _scheduleFragmentContainerView;

    @Nullable
    private SearchView _searchView;

    @NonNull
    private final EventHandler<OrdersFilterEventArgs> _ordersFilterHandler =
        new EventHandler<OrdersFilterEventArgs>() {
            @Override
            public void onEvent(@NonNull final OrdersFilterEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                val filterType = eventArgs.getFilterType();
                val ordersFilterParams = getOrdersFilterParams();

                switch (filterType) {
                    case STATUS:
                        setOrdersFilterParams(ordersFilterParams.withOrderStateFilter(
                            OrderStatusFilter.values()[(int) eventArgs.getId()]));

                        showOrdersFilters(false);
                        break;
                    case SPORT_COMPLEX:
                        setOrdersFilterParams(ordersFilterParams.withOrderSportComplexFilter(
                            eventArgs.getId()));
                        break;
                    case PLAYGROUND:
                        setOrdersFilterParams(ordersFilterParams.withOrderPlaygroundFilter(eventArgs
                                                                                               .getId()));
                        break;
                    default:
                        new RuntimeException("Unknown filter type");
                }

                if (_searchView != null) {
                    _searchView.setQueryHint(_searchView.getQueryHint() + " " +
                                             getString(R.string.orders_filter_search_query_hint,
                                                       eventArgs.getName()));
                }
            }
        };

    private Runnable _setOrdersFilterParams;

    @NonNull
    private final SearchView.OnQueryTextListener _searchViewQueryTextListener =
        new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                if (!newText.isEmpty()) {
                    showOrdersFilters(false);
                }

                setOrdersFilterParamsDelayed(newText);

                return true;
            }
        };

    @Nullable
    private Spinner _spinnerView;

    @Nullable
    private WorkspaceState _state;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private OrdersFilterParams _statusOrdersFilterParams = new OrdersFilterParams();

    private final MenuItemCompat.OnActionExpandListener _onActionExpandListener =
        new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(final MenuItem item) {
                final boolean consumed;

                if (item.getItemId() == R.id.action_search) {
                    setOrdersSearchEnabled(true);

                    consumed = true;
                } else {

                    consumed = false;
                }
                return consumed;
            }

            @Override
            public boolean onMenuItemActionCollapse(final MenuItem item) {
                final boolean consumed;

                if (item.getItemId() == R.id.action_search) {
                    setOrdersSearchEnabled(false);

                    consumed = true;
                } else {

                    consumed = false;
                }
                return consumed;
            }
        };

    @NonNull
    private final AdapterView.OnItemSelectedListener _onOrderStatusSelectedListener =
        new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(
                final AdapterView<?> parent, final View view, final int position, final long id) {
                val ordersState = ((OrdersFilterAdapter) parent.getAdapter()).getItem(position);
                if (ordersState != null) {
                    val ordersFilterParams = getStatusOrdersFilterParams();

                    setStatusOrdersFilterParams(ordersFilterParams.withOrderStateFilter
                        (ordersState));
                }
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
            }
        };

    @Nullable
    private Toolbar _toolbarView;

    @NonNull
    private final AppBarLayout.OnOffsetChangedListener _removeDatePickerViewOnToolbarCollapsed =
        new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(
                final AppBarLayout appBarLayout, final int verticalOffset) {
                if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    setDatePickerExpanded(false, true);
                }
            }
        };

    private void cancelSetOrdersFilterParams() {
        if (_setOrdersFilterParams != null) {
            _handler.removeCallbacks(_setOrdersFilterParams);
        }
    }

    private void invalidateActionBar() {
        final val activePageId = getActiveTabId();
        if (_bottomBar != null && activePageId != null) {
            switch (activePageId) {
                case R.id.tab_schedule: {
                    if (_spinnerView != null) {
                        _spinnerView.setVisibility(View.GONE);
                    }

                    final val playgroundSelection = getPlaygroundSelection();
                    setTitle(playgroundSelection.get1(), playgroundSelection.get2());

                    break;
                }
                case R.id.tab_sales: {
                    if (_spinnerView != null) {
                        _spinnerView.setVisibility(View.GONE);
                    }

                    final val playgroundSelection = getPlaygroundSelection();
                    setTitle(playgroundSelection.get1(), playgroundSelection.get2());

                    break;
                }
                case R.id.tab_orders: {
                    final boolean ordersSearchEnabled = isOrdersSearchEnabled();
                    if (ordersSearchEnabled) {
                        if (_spinnerView != null) {
                            _spinnerView.setVisibility(View.GONE);
                        }
                    } else {
                        if (_spinnerView != null) {
                            _spinnerView.setVisibility(View.VISIBLE);
                        }
                    }

                    setTitle(null);
                    setSubtitle(null);

                    break;
                }
            }
        }

        if (!Objects.equals(activePageId, R.id.tab_schedule)) {
            setDatePickerExpanded(false, true);
        }

        supportInvalidateOptionsMenu();
    }

    private void setOrdersFilterParamsDelayed(@NonNull final String newText) {
        cancelSetOrdersFilterParams();
        _setOrdersFilterParams = new Runnable() {
            @Override
            public void run() {
                setOrdersFilterParams(getOrdersFilterParams().withSearchQueryFilter(newText));
            }
        };
        _handler.postDelayed(_setOrdersFilterParams, _inputDelayUserSearch);
    }
}
