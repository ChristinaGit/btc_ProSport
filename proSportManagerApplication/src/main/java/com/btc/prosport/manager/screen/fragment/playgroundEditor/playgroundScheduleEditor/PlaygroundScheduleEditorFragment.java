package com.btc.prosport.manager.screen.fragment.playgroundEditor.playgroundScheduleEditor;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import butterknife.BindView;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.event.Events;
import com.btc.common.event.generic.Event;
import com.btc.common.event.generic.EventHandler;
import com.btc.common.event.generic.ManagedEvent;
import com.btc.common.extension.view.recyclerView.decoration.DividerItemDecoration;
import com.btc.common.extension.view.recyclerView.decoration.SpacingItemDecoration;
import com.btc.common.mvp.presenter.Presenter;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.adapter.playgroundScheduleEditor.ScheduleAdapter;
import com.btc.prosport.manager.core.adapter.playgroundScheduleEditor.WeekdayItem;
import com.btc.prosport.manager.core.adapter.playgroundScheduleEditor.item.HeaderItem;
import com.btc.prosport.manager.core.adapter.playgroundScheduleEditor.item.TimeItem;
import com.btc.prosport.manager.core.eventArgs.ScheduleEditorPickTimeEvent;
import com.btc.prosport.manager.core.result.ScheduleEditorPickTimeResult;
import com.btc.prosport.manager.di.qualifier.PresenterNames;
import com.btc.prosport.manager.screen.PlaygroundScheduleEditorScreen;
import com.btc.prosport.manager.screen.fragment.playgroundEditor.BasePlaygroundEditorPageFragment;

import org.parceler.Parcels;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

@Accessors(prefix = "_")
public class PlaygroundScheduleEditorFragment
//    extends BasePlaygroundEditorPageFragment
//    implements PlaygroundScheduleEditorScreen
{
//    private static final String _KEY_SAVED_STATE =
//        ConstantBuilder.savedStateKey(PlaygroundScheduleEditorFragment.class, "saved_state");
//
//    private static final int DEFAULT_END_HOUR = 19;
//
//    private static final int DEFAULT_START_HOUR = 10;
//
//    @Override
//    public void displayTime(
//        @NonNull final ScheduleEditorPickTimeResult pickTimeResult) {
//        Contracts.requireNonNull(pickTimeResult, "pickTimeResult == null");
//
//        val scheduleAdapter = getScheduleAdapter();
//        if (scheduleAdapter != null) {
//            scheduleAdapter.updateTime(pickTimeResult.getType(),
//                                       pickTimeResult.getDayIndex(),
//                                       buildTime(pickTimeResult.getHourOfDay(),
//                                                 pickTimeResult.getMinute()));
//        }
//    }
//
//    @NonNull
//    @Override
//    public Event<ScheduleEditorPickTimeEvent> getPickTimeEvent() {
//        return _pickTimeManagedEvent;
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(
//        final LayoutInflater inflater,
//        @Nullable final ViewGroup container,
//        @Nullable final Bundle savedInstanceState) {
//        super.onCreateView(inflater, container, savedInstanceState);
//
//        if (savedInstanceState != null) {
//            _state = onHandleSavedState(savedInstanceState);
//        }
//
//        val arguments = getArguments();
//        if (_state == null && arguments != null) {
//            _state = onHandleArguments(arguments);
//        }
//
//        val view = inflater.inflate(R.layout.fragment_playground_schedule_editor, container, false);
//
//        bindViews(view);
//
//        _layoutManager = new LinearLayoutManager(getContext());
//
//        _scheduleAdapter = new ScheduleAdapter();
//
//        _scheduleAdapter.setWeekdayItems(buildDayItems());
//
//        if (_scheduleList != null) {
//            val resources = getResources();
//            final int spacing =
//                resources.getDimensionPixelSize(R.dimen.playground_general_info_editor_spacing);
//            final int padding =
//                resources.getDimensionPixelSize(R.dimen.playground_general_info_editor_padding);
//            val spacingDecoration = SpacingItemDecoration
//                .builder()
//                .setVerticalSpacing(spacing, spacing)
//                .setPadding(padding, padding, padding, padding)
//                .build();
//            _scheduleList.addItemDecoration(spacingDecoration);
//            _scheduleList.addItemDecoration(new DividerItemDecoration(getContext(),
//                                                                      DividerItemDecoration
//                                                                          .VERTICAL));
//            _scheduleList.setLayoutManager(_layoutManager);
//            _scheduleList.setAdapter(_scheduleAdapter);
//        }
//
//        _scheduleAdapter.getPickTimeEvent().addHandler(_pickTimeEventHandler);
//
//        return view;
//    }
//
//    @Override
//    public void onSaveInstanceState(final Bundle outState) {
//        super.onSaveInstanceState(outState);
//
//        if (outState != null) {
//            if (_state == null) {
//                _state = new PlaygroundScheduleEditorState();
//            }
//
//            val scheduleAdapter = getScheduleAdapter();
//            if (scheduleAdapter != null) {
//                val weekdayItems = scheduleAdapter.getWeekdayItems();
//                if (weekdayItems != null && !weekdayItems.isEmpty()) {
//                    val scheduleDays = new HashMap<Integer, PlaygroundScheduleEditorState.Weekday>(weekdayItems.size());
//                    for (final val weekdayItem : weekdayItems) {
//                        val scheduleDay = new PlaygroundScheduleEditorState.Weekday();
//                        val timeItem = weekdayItem.getTimeItem();
//                        scheduleDay.setStartTime(timeItem.getStartTime());
//                        scheduleDay.setEndTime(timeItem.getEndTime());
//                        val dayOffItem = weekdayItem.getHeaderItem();
//                        scheduleDay.setDayOff(dayOffItem.isDayOff());
//                        scheduleDays.put(dayOffItem.getDay(), scheduleDay);
//                    }
//                    _state.setScheduleDayList(scheduleDays);
//                }
//            }
//
//            outState.putParcelable(_KEY_SAVED_STATE, Parcels.wrap(_state));
//        }
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//
//        val scheduleAdapter = getScheduleAdapter();
//        if (scheduleAdapter != null) {
//            scheduleAdapter.getPickTimeEvent().removeHandler(_pickTimeEventHandler);
//        }
//    }
//
//    @Override
//    protected void onReleaseInjectedMembers() {
//        super.onReleaseInjectedMembers();
//
//        final val presenter = getPresenter();
//        if (presenter != null) {
//            presenter.unbindScreen();
//        }
//    }
//
//    @CallSuper
//    @Nullable
//    protected PlaygroundScheduleEditorState onHandleArguments(@NonNull final Bundle arguments) {
//        Contracts.requireNonNull(arguments, "arguments == null");
//
//        return null;
//    }
//
//    @CallSuper
//    @Nullable
//    protected PlaygroundScheduleEditorState onHandleSavedState(
//        @NonNull final Bundle savedInstanceState) {
//        Contracts.requireNonNull(savedInstanceState, "savedInstanceState == null");
//
//        PlaygroundScheduleEditorState state = null;
//
//        if (savedInstanceState.containsKey(_KEY_SAVED_STATE)) {
//            state = Parcels.unwrap(savedInstanceState.getParcelable(_KEY_SAVED_STATE));
//        }
//
//        return state;
//    }
//
//    @Override
//    protected void onInjectMembers() {
//        super.onInjectMembers();
//
//        getManagerSubscreenComponent().inject(this);
//
//        final val presenter = getPresenter();
//        if (presenter != null) {
//            presenter.bindScreen(this);
//        }
//    }
//
//    @Named(PresenterNames.PLAYGROUND_SCHEDULE_EDITOR)
//    @Inject
//    @Getter(AccessLevel.PROTECTED)
//    @Nullable
//    /*package-private*/ Presenter<PlaygroundScheduleEditorScreen> _presenter;
//
//    @BindView(R.id.schedule_list)
//    @Nullable
//    /*package-private*/ RecyclerView _scheduleList;
//
//    @NonNull
//    private final ManagedEvent<ScheduleEditorPickTimeEvent> _pickTimeManagedEvent =
//        Events.createEvent();
//
//    @NonNull
//    private final EventHandler<ScheduleEditorPickTimeEvent> _pickTimeEventHandler =
//        new EventHandler<ScheduleEditorPickTimeEvent>() {
//            @Override
//            public void onEvent(
//                @NonNull final ScheduleEditorPickTimeEvent eventArgs) {
//                _pickTimeManagedEvent.rise(eventArgs);
//            }
//        };
//
//    @Getter(AccessLevel.PROTECTED)
//    @Nullable
//    private LinearLayoutManager _layoutManager;
//
//    @Getter(AccessLevel.PROTECTED)
//    @Nullable
//    private ScheduleAdapter _scheduleAdapter;
//
//    @Nullable
//    private PlaygroundScheduleEditorState _state;
//
//    @NonNull
//    private List<WeekdayItem> buildDayItems() {
//        val weekdays = DateFormatSymbols.getInstance().getWeekdays();
//        val scheduleItems = new ArrayList<WeekdayItem>(7);
//        final int firstDayOfWeek = Calendar.getInstance().getFirstDayOfWeek();
//
//        for (int i = 0, length = weekdays.length; i < length; i++) {
//            final int weekdayId = (i + firstDayOfWeek) % length;
//            val weekdayName = weekdays[weekdayId];
//            if (!TextUtils.isEmpty(weekdayName)) {
//                val dayItem = new WeekdayItem();
//
//                val headerItem = new HeaderItem(weekdayId, weekdayName);
//                val timeItem = new TimeItem();
//
//                if (_state != null && _state.getScheduleDayList() != null) {
//                    val scheduleDayList = _state.getScheduleDayList();
//                    val weekday = scheduleDayList.get(weekdayId);
//                    headerItem.setDayOff(weekday.isDayOff());
//                    timeItem.setStartTime(weekday.getStartTime());
//                    timeItem.setEndTime(weekday.getEndTime());
//                } else {
//                    timeItem.setStartTime(buildTime(DEFAULT_START_HOUR, 0));
//                    timeItem.setEndTime(buildTime(DEFAULT_END_HOUR, 0));
//                }
//
//                dayItem.setHeaderItem(headerItem);
//                dayItem.setTimeItem(timeItem);
//
//                scheduleItems.add(dayItem);
//            }
//        }
//
//        return scheduleItems;
//    }
//
//    private long buildTime(final int hour, final int minute) {
//        val calendar = Calendar.getInstance();
//        calendar.set(Calendar.MINUTE, minute);
//        calendar.set(Calendar.HOUR_OF_DAY, hour);
//        return calendar.getTimeInMillis();
//    }
}
