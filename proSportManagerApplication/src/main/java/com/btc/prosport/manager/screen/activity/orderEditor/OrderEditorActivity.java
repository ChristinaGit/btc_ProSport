package com.btc.prosport.manager.screen.activity.orderEditor;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.picker.TimePickerManager;
import com.btc.common.event.Events;
import com.btc.common.event.generic.Event;
import com.btc.common.event.generic.ManagedEvent;
import com.btc.common.event.notice.NoticeEventHandler;
import com.btc.common.extension.delegate.loading.FadeVisibilityHandler;
import com.btc.common.extension.delegate.loading.LoadingViewDelegate;
import com.btc.common.extension.delegate.loading.ProgressVisibilityHandler;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.extension.view.recyclerView.decoration.DividerItemDecoration;
import com.btc.common.extension.view.recyclerView.decoration.SpacingItemDecoration;
import com.btc.common.mvp.presenter.Presenter;
import com.btc.common.utility.ImeUtils;
import com.btc.common.utility.NumberUtils;
import com.btc.common.utility.TimeUtils;
import com.btc.prosport.api.model.Order;
import com.btc.prosport.api.model.OrderMetadataInterval;
import com.btc.prosport.api.model.User;
import com.btc.prosport.core.utility.ProSportApiDataUtils;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.adapter.orderEditor.OrderEditorAdapter;
import com.btc.prosport.manager.core.adapter.repeatableIntervals.item.RepeatableIntervalItem;
import com.btc.prosport.manager.core.eventArgs.ChangeOrderEventArgs;
import com.btc.prosport.manager.core.eventArgs.ChangeOrderForUnknownPlayerEventArgs;
import com.btc.prosport.manager.core.eventArgs.CreateOrderEventArgs;
import com.btc.prosport.manager.core.eventArgs.CreateOrderForUnknownPlayerEventArgs;
import com.btc.prosport.manager.core.eventArgs.UserSearchEventArgs;
import com.btc.prosport.manager.di.qualifier.PresenterNames;
import com.btc.prosport.manager.screen.OrderEditorScreen;
import com.btc.prosport.manager.screen.activity.BaseManagerActivity;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;
import org.parceler.Parcels;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;

@Accessors(prefix = "_")
public final class OrderEditorActivity extends BaseManagerActivity implements OrderEditorScreen {
    private static final String _LOG_TAG = ConstantBuilder.logTag(OrderEditorActivity.class);

    public static final String _KEY_SAVED_STATE =
        ConstantBuilder.savedStateKey(OrderEditorActivity.class, "saved_state");

    @Override
    @CallSuper
    public void bindViews() {
        super.bindViews();

        _toolbarView = (Toolbar) findViewById(R.id.toolbar);
        _orderEditorView = (RecyclerView) findViewById(R.id.order_editor);
        _orderPlacesLoadingView = (ProgressBar) findViewById(R.id.loading);
        _orderPlacesErrorView = (TextView) findViewById(R.id.loading_error);
        _orderPlacesNoContentView = (TextView) findViewById(R.id.no_content);
    }

    @CallSuper
    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        if (outState != null) {
            if (_state == null) {
                _state = new OrderEditorSavedState();
            }

            if (_modeDelegate != null) {
                _modeDelegate.onSaveState(_state);
            }

            final val orderEditorAdapter = getOrderEditorAdapter();
            final val orderIntervals = orderEditorAdapter.getItems();

            final val intervals =
                new ArrayList<OrderEditorSavedState.Interval>(orderIntervals.size());
            for (final val orderInterval : orderIntervals) {
                final val interval = new OrderEditorSavedState.Interval();

                interval.setTimeStart(orderInterval.getTimeStart());
                interval.setTimeEnd(orderInterval.getTimeEnd());
                interval.setDateStart(orderInterval.getDateStart());
                interval.setDateEnd(orderInterval.getDateEnd());
                interval.setRepeatable(orderInterval.isRepeatable());
                interval.setRepeatWeekDays(orderInterval.getRepeatWeekDays());

                intervals.add(interval);
            }

            _state.setIntervals(intervals);

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
    public void displayChangedOrder(@Nullable final Order order) {
        if (order != null) {
            onBackPressed();
        }
    }

    @Override
    public void displayOrder(@Nullable final Order order) {
        if (_modeDelegate != null) {
            _modeDelegate.displayEditedOrder(order);
        }
    }

    @Override
    public final void displayPlayerSearchError() {
        if (_modeDelegate != null) {
            _modeDelegate.displayPlayerSearchError();
        }
    }

    @Override
    public final void displayPlayerSearchResult(@Nullable final User player) {
        if (_modeDelegate != null) {
            _modeDelegate.displayPlayerSearchResult(player);
        }
    }

    @NonNull
    @Override
    public final Event<ChangeOrderEventArgs> getChangeOrderEvent() {
        return _changeOrderEvent;
    }

    @NonNull
    @Override
    public final Event<ChangeOrderForUnknownPlayerEventArgs> getChangeOrderForUnknownPlayerEvent() {
        return _changeOrderForUnknownPlayerEvent;
    }

    @NonNull
    @Override
    public final Event<CreateOrderEventArgs> getCreateOrderEvent() {
        return _createOrderEvent;
    }

    @NonNull
    @Override
    public final Event<CreateOrderForUnknownPlayerEventArgs> getCreateOrderForUnknownPlayerEvent() {
        return _createOrderForUnknownPlayerEvent;
    }

    @NonNull
    @Override
    public final Event<UserSearchEventArgs> getSearchPlayerEvent() {
        return _searchPlayerEvent;
    }

    @NonNull
    @Override
    public final Event<IdEventArgs> getViewOrderEvent() {
        return _viewOrderEvent;
    }

    @NonNull
    @Override
    public final Event<IdEventArgs> getViewPlayerEvent() {
        return _viewPlayerEvent;
    }

    @CallSuper
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.order_editor, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final boolean consumed;

        final val id = item.getItemId();
        if (android.R.id.home == id) {
            onBackPressed();

            consumed = true;
        } else if (R.id.action_complete_editing == id) {
            if (_modeDelegate != null) {
                _modeDelegate.onCompleteEditing();
            }

            consumed = true;
        } else {
            consumed = super.onOptionsItemSelected(item);
        }

        return consumed;
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
            final val mode = _state.getMode();

            if (mode == null) {
                throw new IllegalStateException("Activity mode is not specified");
            }

            switch (mode) {
                case INSERT:
                    _modeDelegate = new InsertModeDelegate();
                    break;
                case EDIT:
                    _modeDelegate = new EditModeDelegate();
                    break;
                default:
                    throw new IllegalArgumentException("Unknown activity mode: " + mode);
            }

            setContentView(R.layout.activity_order_editor);
            bindViews();

            _orderLoadingDelegate = LoadingViewDelegate
                .builder()
                .setContentView(_orderEditorView)
                .setLoadingView(_orderPlacesLoadingView)
                .setNoContentView(_orderPlacesNoContentView)
                .setErrorView(_orderPlacesErrorView)
                .setContentVisibilityHandler(new FadeVisibilityHandler(R.anim.fade_in_long,
                                                                       FadeVisibilityHandler
                                                                           .NO_ANIMATION))
                .setLoadingVisibilityHandler(new ProgressVisibilityHandler())
                .build();

            final val orderEditorAdapter = getOrderEditorAdapter();

            orderEditorAdapter.setTimePickerManager(getTimePickerManager());

            final val intervals = _state.getIntervals();
            if (intervals != null) {
                for (final val interval : intervals) {
                    final val intervalItem = RepeatableIntervalItem.createEmpty();

                    intervalItem.setDateStart(interval.getDateStart());
                    intervalItem.setDateEnd(interval.getDateEnd());
                    intervalItem.setTimeStart(interval.getTimeStart());
                    intervalItem.setTimeEnd(interval.getTimeEnd());

                    intervalItem.setRepeatable(interval.isRepeatable());
                    final val repeatWeekDays = interval.getRepeatWeekDays();
                    if (repeatWeekDays != null) {
                        intervalItem.getRepeatWeekDays().addAll(repeatWeekDays);
                    }

                    orderEditorAdapter.addItem(intervalItem);
                }
            }

            if (_orderEditorView != null) {
                final val orderParamsLayoutManager = new LinearLayoutManager(this);

                final val res = getResources();
                final val spacing = res.getDimensionPixelOffset(R.dimen.grid_large_spacing);
                final val padding = res.getDimensionPixelOffset(R.dimen.activity_horizontal_margin);
                final val spacingDecorator = SpacingItemDecoration
                    .builder()
                    .setSpacing(spacing)
                    .setHorizontalPadding(padding)
                    .collapseBorders()
                    .build();
                _orderEditorView.addItemDecoration(spacingDecorator);

                final int orientation = orderParamsLayoutManager.getOrientation();
                final val dividerDecoration = new DividerItemDecoration(this, orientation);
                _orderEditorView.addItemDecoration(dividerDecoration);

                orderEditorAdapter.enableIntervalRemovalBySwipe(_orderEditorView);
                _orderEditorView.setAdapter(orderEditorAdapter);
                _orderEditorView.setLayoutManager(orderParamsLayoutManager);
            }

            if (_toolbarView != null) {
                setSupportActionBar(_toolbarView);
            }

            final val actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowTitleEnabled(true);
            }

            orderEditorAdapter.getPickPlayerPhoneEvent().addHandler(_pickPlayerPhoneHandler);

            if (_modeDelegate != null) {
                _modeDelegate.onCreate(_state);
            }
        } else {
            finish();
        }
    }

    @CallSuper
    @Override
    protected void onDestroy() {
        super.onDestroy();

        final val orderEditorAdapter = getOrderEditorAdapter();
        orderEditorAdapter.getPickPlayerPhoneEvent().removeHandler(_pickPlayerPhoneHandler);

        unbindViews();
    }

    @CallSuper
    @Nullable
    protected OrderEditorSavedState onHandleEditIntent(@NonNull final Intent intent) {
        Contracts.requireNonNull(intent, "intent == null");

        final OrderEditorSavedState state;

        final val orderId = OrderEditorIntent.Edit.getOrderId(intent);
        if (orderId != null) {
            state = new OrderEditorSavedState();
            state.setMode(OrderEditorMode.EDIT);
            state.setOrderId(orderId);
        } else {
            state = null;
        }
        return state;
    }

    @CallSuper
    @NonNull
    protected OrderEditorSavedState onHandleInsertIntent(@NonNull final Intent intent) {
        Contracts.requireNonNull(intent, "intent == null");

        final val state = new OrderEditorSavedState();
        state.setMode(OrderEditorMode.INSERT);

        state.setPlaygroundId(OrderEditorIntent.Insert.getPlaygroundId(intent));
        state.setPlayerId(OrderEditorIntent.Insert.getPlayerId(intent));
        state.setOrderStartDate(OrderEditorIntent.Insert.getOrderDateStart(intent));

        final val intentDateStart = OrderEditorIntent.Insert.getOrderDateStart(intent);
        final val intentDateEnd = OrderEditorIntent.Insert.getOrderDateEnd(intent);
        final val intentTimeStart = OrderEditorIntent.Insert.getOrderTimeStart(intent);
        final val intentTimeEnd = OrderEditorIntent.Insert.getOrderTimeEnd(intent);

        final val intervals = new ArrayList<OrderEditorSavedState.Interval>();

        final val newItem = new OrderEditorSavedState.Interval();

        final val currentDateTime = System.currentTimeMillis();
        final val currentDay = TimeUtils.getAlignedDay(currentDateTime);
        final val currentTime = TimeUtils.getTime(currentDateTime);

        final val dateStart = NumberUtils.defaultIfNull(intentDateStart, currentDay);
        final val dateEnd = NumberUtils.defaultIfNull(intentDateEnd, dateStart);
        final val timeStart = NumberUtils.defaultIfNull(intentTimeStart, currentTime);
        final val timeEnd = NumberUtils.defaultIfNull(intentTimeEnd, timeStart);

        newItem.setDateStart(dateStart);
        newItem.setDateEnd(dateEnd);
        newItem.setTimeStart(timeStart);
        newItem.setTimeEnd(timeEnd);

        intervals.add(newItem);

        state.setIntervals(intervals);

        return state;
    }

    @Nullable
    @CallSuper
    protected OrderEditorSavedState onHandleIntent(@NonNull final Intent intent) {
        Contracts.requireNonNull(intent, "intent == null");

        final OrderEditorSavedState state;

        final val action = intent.getAction();
        switch (action) {
            case Intent.ACTION_INSERT: {
                state = onHandleInsertIntent(intent);

                break;
            }
            case Intent.ACTION_EDIT: {
                state = onHandleEditIntent(intent);

                break;
            }
            default: {
                state = null;

                break;
            }
        }

        return state;
    }

    @Nullable
    @CallSuper
    protected OrderEditorSavedState onHandleSavedState(@NonNull final Bundle savedInstanceState) {
        OrderEditorSavedState state = null;

        if (savedInstanceState.containsKey(_KEY_SAVED_STATE)) {
            state = Parcels.unwrap(savedInstanceState.getParcelable(_KEY_SAVED_STATE));
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
    protected void performPickPlayerPhone() {
        final val dialog = new AlertDialog.Builder(this)
            .setView(R.layout.layout_phone_dialog)
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialogInterface, final int which) {
                    if (dialogInterface instanceof Dialog) {
                        final val dialog = (Dialog) dialogInterface;
                        final val phoneView = (TextView) dialog.findViewById(R.id.phone);

                        final val playerPhone = phoneView.getText().toString();
                        getOrderEditorAdapter().setPlayerPhone(playerPhone);

                        ImeUtils.hideIme(phoneView);

                        _searchPlayerEvent.rise(new UserSearchEventArgs(playerPhone));
                    }
                }
            })
            .setNegativeButton(android.R.string.cancel, null)
            .setTitle(R.string.order_editor_player_phone_hint)
            .show();
        final val phoneView = (EditText) dialog.findViewById(R.id.phone);
        if (phoneView != null) {
            phoneView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(
                    final TextView v, final int actionId, final KeyEvent event) {
                    final boolean consumed;

                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();

                        consumed = true;
                    } else {
                        consumed = false;
                    }

                    return consumed;
                }
            });

            phoneView.post(new Runnable() {
                @Override
                public void run() {
                    phoneView.setText(getOrderEditorAdapter().getPlayerPhone());
                    phoneView.setSelection(phoneView.getText().length());
                    phoneView.requestFocus();
                    ImeUtils.showIme(phoneView);
                }
            });
        }
    }

    @Named(PresenterNames.ORDER_EDITOR)
    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Nullable
    /*package-private*/ Presenter<OrderEditorScreen> _presenter;

    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Nullable
    /*package-private*/ TimePickerManager _timePickerManager;

    @NonNull
    private final ManagedEvent<ChangeOrderEventArgs> _changeOrderEvent = Events.createEvent();

    @NonNull
    private final ManagedEvent<ChangeOrderForUnknownPlayerEventArgs>
        _changeOrderForUnknownPlayerEvent = Events.createEvent();

    @NonNull
    private final ManagedEvent<CreateOrderEventArgs> _createOrderEvent = Events.createEvent();

    @NonNull
    private final ManagedEvent<CreateOrderForUnknownPlayerEventArgs>
        _createOrderForUnknownPlayerEvent = Events.createEvent();

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final OrderEditorAdapter _orderEditorAdapter = new OrderEditorAdapter();

    @NonNull
    private final ManagedEvent<UserSearchEventArgs> _searchPlayerEvent = Events.createEvent();

    @NonNull
    private final NoticeEventHandler _pickPlayerPhoneHandler = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            performPickPlayerPhone();
        }
    };

    @NonNull
    private final ManagedEvent<IdEventArgs> _viewOrderEvent = Events.createEvent();

    @NonNull
    private final ManagedEvent<IdEventArgs> _viewPlayerEvent = Events.createEvent();

    @Nullable
    private ModeDelegate _modeDelegate;

    @Nullable
    private RecyclerView _orderEditorView;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private LoadingViewDelegate _orderLoadingDelegate;

    @Nullable
    private TextView _orderPlacesErrorView;

    @Nullable
    private ProgressBar _orderPlacesLoadingView;

    @Nullable
    private TextView _orderPlacesNoContentView;

    @Nullable
    private OrderEditorSavedState _state;

    @Nullable
    private Toolbar _toolbarView;

    private void invalidatePlayerView(@Nullable final User player) {
        final val orderEditorAdapter = getOrderEditorAdapter();

        if (player != null) {
            final val playerNamePlaceholder =
                getString(R.string.order_editor_player_name_placeholder);
            final val firstName =
                StringUtils.defaultIfBlank(player.getFirstName(), playerNamePlaceholder);
            orderEditorAdapter.setPlayerFirstName(firstName);

            final val lastName =
                StringUtils.defaultIfBlank(player.getLastName(), playerNamePlaceholder);
            orderEditorAdapter.setPlayerLastName(lastName);

            orderEditorAdapter.setPlayerPhone(player.getPhoneNumber());

            orderEditorAdapter.setPlayerFrozen(true);
        } else {
            orderEditorAdapter.setPlayerFirstName(null);
            orderEditorAdapter.setPlayerLastName(null);

            orderEditorAdapter.setPlayerFrozen(false);
        }
    }

    private interface ModeDelegate {
        void displayEditedOrder(@Nullable Order order);

        void displayPlayerSearchError();

        void displayPlayerSearchResult(@Nullable User player);

        void onCompleteEditing();

        void onCreate(@NonNull final OrderEditorSavedState state);

        void onSaveState(@NonNull final OrderEditorSavedState state);
    }

    @Accessors(prefix = "_")
    private class EditModeDelegate implements ModeDelegate {
        @Override
        public void displayEditedOrder(@Nullable final Order order) {
            _order = order;

            final val editorAdapter = getOrderEditorAdapter();
            editorAdapter.removeItems();

            if (_order != null) {
                final val intervals = _order.getIntervals();
                final val intervalToItem =
                    new Transformer<OrderMetadataInterval, RepeatableIntervalItem>() {
                        @Override
                        public RepeatableIntervalItem transform(final OrderMetadataInterval
                                                                    interval) {
                            final val intervalItem = RepeatableIntervalItem.createEmpty();

                            final val repeatWeekDaysString = interval.getRepeatWeekDays();
                            final val repeatWeekDays =
                                ProSportApiDataUtils.parseRepeatIntervalWeekDays(
                                    repeatWeekDaysString);

                            intervalItem.setId(interval.getId());

                            intervalItem.setRepeatable(
                                repeatWeekDays != null && !repeatWeekDays.isEmpty());
                            if (repeatWeekDays != null) {
                                intervalItem.getRepeatWeekDays().addAll(repeatWeekDays);
                            }

                            final val timeStart =
                                ProSportApiDataUtils.parseTime(interval.getTimeStart());
                            final val timeEnd =
                                ProSportApiDataUtils.parseTime(interval.getTimeEnd());
                            final val dateStart =
                                ProSportApiDataUtils.parseDate(interval.getDateStart());
                            final val dateEnd =
                                ProSportApiDataUtils.parseDate(interval.getDateEnd());

                            if (timeStart != null) {
                                intervalItem.setTimeStart(timeStart.getTime());
                            }
                            if (timeEnd != null) {
                                intervalItem.setTimeEnd(timeEnd.getTime());
                            }
                            if (dateStart != null) {
                                intervalItem.setDateStart(dateStart.getTime());
                            }
                            if (dateEnd != null) {
                                intervalItem.setDateEnd(dateEnd.getTime());
                            }

                            return intervalItem;
                        }
                    };
                final val intervalItems = CollectionUtils.collect(intervals, intervalToItem);
                editorAdapter.addItems(intervalItems);

                final val player = _order.getPlayer();
                _newPlayer = player;
                _newPlayerId = _newPlayer != null ? _newPlayer.getId() : null;

                invalidatePlayerView(player);

                final val loadingDelegate = getOrderLoadingDelegate();
                if (loadingDelegate != null) {
                    loadingDelegate.showContent();
                }
            }
        }

        @Getter(AccessLevel.PROTECTED)
        @Nullable
        private User _newPlayer;

        @Getter(AccessLevel.PROTECTED)
        @Nullable
        private Long _newPlayerId;

        @Getter(AccessLevel.PROTECTED)
        @Nullable
        private Order _order;

        @Getter(AccessLevel.PROTECTED)
        @Nullable
        private Long _orderId;

        @Override
        public void displayPlayerSearchError() {
            _newPlayer = null;
            _newPlayerId = null;

            invalidatePlayerView(null);
        }

        @Override
        public void displayPlayerSearchResult(@Nullable final User player) {
            _newPlayer = player;
            _newPlayerId = _newPlayer != null ? _newPlayer.getId() : null;

            invalidatePlayerView(player);
        }

        @Override
        public void onCompleteEditing() {
            final val order = getOrder();

            if (order != null) {
                final val playground = order.getPlayground();
                final val playgroundId = playground == null ? null : playground.getId();

                final val newPlayerId = _newPlayer == null ? null : _newPlayer.getId();

                final val orderEditorAdapter = getOrderEditorAdapter();
                if (orderEditorAdapter.validateUserInput()) {
                    if (playgroundId != null) {
                        if (newPlayerId != null) {
                            final val intervals = orderEditorAdapter.getItems();

                            final val eventArgs = new ChangeOrderEventArgs(order.getId(),
                                                                           newPlayerId,
                                                                           playgroundId,
                                                                           intervals,
                                                                           null);
                            _changeOrderEvent.rise(eventArgs);
                        } else {
                            final val playerPhone = orderEditorAdapter.getPlayerPhone();
                            final val playerFirstName = orderEditorAdapter.getPlayerFirstName();
                            final val playerLastName = orderEditorAdapter.getPlayerLastName();
                            final val intervals = orderEditorAdapter.getItems();

                            if (playerPhone != null) {
                                final val eventArgs =
                                    new ChangeOrderForUnknownPlayerEventArgs(order.getId(),
                                                                             playerPhone,
                                                                             playerFirstName,
                                                                             playerLastName,
                                                                             playgroundId,
                                                                             intervals,
                                                                             null);
                                _changeOrderForUnknownPlayerEvent.rise(eventArgs);
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void onCreate(@NonNull final OrderEditorSavedState state) {
            Contracts.requireNonNull(state, "state == null");

            _orderId = state.getOrderId();

            final val orderId = getOrderId();
            if (orderId != null) {
                _viewOrderEvent.rise(new IdEventArgs(orderId));
            }

            final val loadingDelegate = getOrderLoadingDelegate();
            if (loadingDelegate != null) {
                loadingDelegate.showLoading();
            }
        }

        @Override
        public void onSaveState(@NonNull final OrderEditorSavedState state) {
            Contracts.requireNonNull(state, "state == null");

            state.setOrderId(getOrderId());
        }
    }

    @Accessors(prefix = "_")
    private class InsertModeDelegate implements ModeDelegate {
        @Getter(AccessLevel.PROTECTED)
        @Nullable
        private User _player;

        @Getter(AccessLevel.PROTECTED)
        @Nullable
        private Long _playerId;

        @Getter(AccessLevel.PROTECTED)
        private Long _playgroundId;

        @Override
        public void displayEditedOrder(@Nullable final Order order) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void displayPlayerSearchError() {
            _player = null;
            _playerId = null;

            invalidatePlayerView(null);
        }

        @Override
        public void displayPlayerSearchResult(@Nullable final User player) {
            _player = player;
            _playerId = _player != null ? _player.getId() : null;

            invalidatePlayerView(player);
        }

        @Override
        public void onCreate(@NonNull final OrderEditorSavedState state) {
            Contracts.requireNonNull(state, "state == null");

            _playerId = state.getPlayerId();
            _playgroundId = state.getPlaygroundId();

            if (_playerId != null) {
                _viewPlayerEvent.rise(new IdEventArgs(_playerId));
            }

            final val loadingDelegate = getOrderLoadingDelegate();
            if (loadingDelegate != null) {
                loadingDelegate.showContent();
            }
        }

        @Override
        public void onSaveState(@NonNull final OrderEditorSavedState state) {
            Contracts.requireNonNull(state, "state == null");

            state.setPlayerId(getPlayerId());
        }

        @Override
        public void onCompleteEditing() {
            final val playerId = getPlayerId();
            final val playgroundId = getPlaygroundId();

            if (playgroundId != null) {
                final val orderEditorAdapter = getOrderEditorAdapter();
                if (orderEditorAdapter.validateUserInput()) {
                    if (playerId != null) {
                        final val intervals = orderEditorAdapter.getItems();

                        final val eventArgs =
                            new CreateOrderEventArgs(playerId, playgroundId, intervals, null);

                        _createOrderEvent.rise(eventArgs);
                    } else {
                        final val playerPhone = orderEditorAdapter.getPlayerPhone();
                        final val playerFirstName = orderEditorAdapter.getPlayerFirstName();
                        final val playerLastName = orderEditorAdapter.getPlayerLastName();
                        final val intervals = orderEditorAdapter.getItems();

                        if (playerPhone != null) {
                            final val eventArgs = new CreateOrderForUnknownPlayerEventArgs(
                                playerPhone,
                                playerFirstName,
                                playerLastName,
                                playgroundId,
                                intervals,
                                null);
                            _createOrderForUnknownPlayerEvent.rise(eventArgs);
                        }
                    }
                }
            }
        }
    }
}
