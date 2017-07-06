package com.btc.prosport.manager.core.adapter.playgroundScheduleEditor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.contract.Contracts;
import com.btc.common.event.Events;
import com.btc.common.event.generic.Event;
import com.btc.common.event.generic.ManagedEvent;
import com.btc.common.extension.view.recyclerView.adapter.RecyclerViewAdapter;
import com.btc.common.extension.view.recyclerView.decoration.DividerItemDecoration;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.adapter.playgroundScheduleEditor.item.HeaderItem;
import com.btc.prosport.manager.core.adapter.playgroundScheduleEditor.item.TimeItem;
import com.btc.prosport.manager.core.adapter.playgroundScheduleEditor.viewHolder.DayOffViewHolder;
import com.btc.prosport.manager.core.adapter.playgroundScheduleEditor.viewHolder.TimeViewHolder;
import com.btc.prosport.manager.core.eventArgs.ScheduleEditorPickTimeEvent;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Accessors(prefix = "_")
public class ScheduleAdapter extends RecyclerViewAdapter<ExtendedRecyclerViewHolder> {
    public static final int VIEW_TYPE_DAY_OFF;

    public static final int VIEW_TYPE_TIME;

    public static final int[] VIEW_TYPE_ORDER;

    static {
        int viewTypeIndexer = 0;

        VIEW_TYPE_DAY_OFF = ++viewTypeIndexer;
        VIEW_TYPE_TIME = ++viewTypeIndexer;

        VIEW_TYPE_ORDER = new int[]{VIEW_TYPE_DAY_OFF, VIEW_TYPE_TIME};
    }

    public int getDayItemsIndex(final int position) {
        int index = 0;
        int itemCount = 0;
        val dayItems = getWeekdayItems();
search:
        if (dayItems != null) {
            for (int i = 0; i < dayItems.size(); i++) {
                final WeekdayItem weekdayItem = dayItems.get(i);
                for (final val viewType : VIEW_TYPE_ORDER) {
                    itemCount += getItemCount(weekdayItem, viewType);
                    if (position < itemCount) {
                        index = i;
                        break search;
                    }
                }
            }
        }

        return index;
    }

    @NonNull
    public Item getItem(final int position) {
        Item item = null;
        int itemCount = 0;
        val dayItems = this.getWeekdayItems();
search:
        if (dayItems != null) {
            for (final val dayItem : dayItems) {
                for (final val viewType : VIEW_TYPE_ORDER) {
                    itemCount += getItemCount(dayItem, viewType);
                    if (position < itemCount) {
                        if (VIEW_TYPE_DAY_OFF == viewType) {
                            item = dayItem.getHeaderItem();
                        } else if (VIEW_TYPE_TIME == viewType) {
                            item = dayItem.getTimeItem();
                        }
                        break search;
                    }
                }
            }
        }

        if (item == null) {
            throw new IllegalArgumentException("Item not found. Position: " + position);
        } else {
            return item;
        }
    }

    public int getItemCount(final WeekdayItem weekdayItem, final int viewType) {
        final int count;
        if (viewType == VIEW_TYPE_DAY_OFF) {
            count = 1;
        } else if (viewType == VIEW_TYPE_TIME) {
            if (weekdayItem.getHeaderItem().isDayOff()) {
                count = 0;
            } else {
                count = 1;
            }
        } else {
            throw new IllegalArgumentException("Unknown view type: " + viewType);
        }
        return count;
    }

    @NonNull
    public Event<ScheduleEditorPickTimeEvent> getPickTimeEvent() {
        return _pickTimeManagedEvent;
    }

    @Nullable
    public Integer getTimeItemPosition(final int dayItemIndex) {
        int count = 0;
        val dayItems = this.getWeekdayItems();
search:
        if (dayItems != null) {
            for (int i = 0; i < dayItems.size(); i++) {
                final WeekdayItem weekdayItem = dayItems.get(i);
                for (final val viewType : VIEW_TYPE_ORDER) {
                    final int itemCount = getItemCount(weekdayItem, viewType);
                    count += itemCount;
                    if (dayItemIndex == i && VIEW_TYPE_TIME == viewType) {
                        if (itemCount == 0) {
                            return null;
                        }
                        break search;
                    }
                }
            }
        }

        return count - 1;
    }

    @Override
    public ExtendedRecyclerViewHolder onCreateViewHolder(
        final ViewGroup parent, final int viewType) {

        final ExtendedRecyclerViewHolder viewHolder;
        if (VIEW_TYPE_DAY_OFF == viewType) {
            viewHolder = onCreateDayOffViewHolder(parent);
        } else if (VIEW_TYPE_TIME == viewType) {
            viewHolder = onCreateTimeViewHolder(parent);
        } else {
            throw new IllegalArgumentException("Unknown view type: " + viewType);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(
        @NonNull final ExtendedRecyclerViewHolder viewHolder, final int position) {
        final val item = getItem(position);

        if (VIEW_TYPE_DAY_OFF == viewHolder.getItemViewType()) {
            onBindDayOffViewHolder((DayOffViewHolder) viewHolder, (HeaderItem) item);
        } else if (VIEW_TYPE_TIME == viewHolder.getItemViewType()) {
            onBindTimeViewHolder((TimeViewHolder) viewHolder, (TimeItem) item);
        }
    }

    @Override
    public int getItemViewType(final int position) {
        return getItem(position).getItemType();
    }

    @Override
    public int getItemCount() {
        int count = 0;
        val dayItems = getWeekdayItems();
        if (dayItems != null) {
            for (final val dayItem : dayItems) {
                for (final val viewType : VIEW_TYPE_ORDER) {
                    count += getItemCount(dayItem, viewType);
                }
            }
        }
        return count;
    }

    public void setWeekdayItems(@Nullable final List<WeekdayItem> weekdayItems) {
        if (weekdayItems != null) {
            _weekdayItems = Collections.unmodifiableList(weekdayItems);
        } else {
            _weekdayItems = null;
        }
    }

    public void updateTime(
        @NonNull final TimeItem.Type type, final int dayIndex, final long time) {
        Contracts.requireNonNull(type, "type == null");

        val dayItems = getWeekdayItems();
        if (dayItems != null) {
            val timeItem = dayItems.get(dayIndex).getTimeItem();
            switch (type) {
                case START_TIME: {
                    timeItem.setStartTime(time);
                    notifyTimeItem(dayIndex);
                    break;
                }
                case END_TIME: {
                    timeItem.setEndTime(time);
                    notifyTimeItem(dayIndex);
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unknown time item type " + type);
            }
        }
    }

    protected void onBindDayOffViewHolder(
        @NonNull final DayOffViewHolder dayOffViewHolder, @NonNull final HeaderItem headerItem) {
        Contracts.requireNonNull(dayOffViewHolder, "dayOffViewHolder == null");
        Contracts.requireNonNull(headerItem, "dayOffItem == null");

        if (headerItem.isDayOff()) {
            DividerItemDecoration.setDecorationMode(dayOffViewHolder.itemView,
                                                    DividerItemDecoration.DecorationMode.ALL);
        } else {
            DividerItemDecoration.setDecorationMode(dayOffViewHolder.itemView,
                                                    DividerItemDecoration.DecorationMode.NONE);
        }

        val weekdayName = headerItem.getWeekdayName();
        val displayWeekdayName =
            weekdayName.substring(0, 1).toUpperCase() + weekdayName.substring(1);
        dayOffViewHolder.day.setText(displayWeekdayName);

        dayOffViewHolder.setDayOffChangeListen(false);
        dayOffViewHolder.dayOffCheckBox.setChecked(headerItem.isDayOff());
        dayOffViewHolder.setDayOffChangeListen(true);
    }

    protected void onBindTimeViewHolder(
        @NonNull final TimeViewHolder timeViewHolder, @NonNull final TimeItem timeItem) {
        Contracts.requireNonNull(timeViewHolder, "timeViewHolder == null");
        Contracts.requireNonNull(timeItem, "timeItem == null");

        val context = timeViewHolder.getContext();
        timeViewHolder.startTime.setText(formatTime(context, timeItem.getStartTime()));
        timeViewHolder.endTime.setText(formatTime(context, timeItem.getEndTime()));
    }

    protected ExtendedRecyclerViewHolder onCreateDayOffViewHolder(@NonNull final ViewGroup parent) {
        Contracts.requireNonNull(parent, "parent == null");

        val inflater = LayoutInflater.from(parent.getContext());

        val view = inflater.inflate(R.layout.fragment_playground_schedule_editor_day_of_item,
                                    parent,
                                    false);

        val dayOffViewHolder = new DayOffViewHolder(view);
        dayOffViewHolder.setDayOffChangeListener(new DayOffViewHolder.DayOffChangeListener() {
            @Override
            public void onCollapseItem(final int adapterPosition) {
                val dayOffItem = (HeaderItem) getItem(adapterPosition);
                dayOffItem.setDayOff(true);
                notifyItemRemoved(adapterPosition + 1);
                notifyItemChanged(adapterPosition);
            }

            @Override
            public void onExpandItem(final int adapterPosition) {
                val dayOffItem = (HeaderItem) getItem(adapterPosition);
                dayOffItem.setDayOff(false);

                notifyItemInserted(adapterPosition + 1);
                notifyItemChanged(adapterPosition);
            }
        });
        return dayOffViewHolder;
    }

    protected ExtendedRecyclerViewHolder onCreateTimeViewHolder(@NonNull final ViewGroup parent) {
        Contracts.requireNonNull(parent, "parent == null");

        val inflater = LayoutInflater.from(parent.getContext());

        val view =
            inflater.inflate(R.layout.fragment_playground_schedule_editor_time_item, parent, false);

        val timeViewHolder = new TimeViewHolder(view);
        timeViewHolder.setOnTimeClickListener(new TimeViewHolder.OnTimeClickListener() {
            @Override
            public void onTimeClick(@NonNull final TimeItem.Type type, final int adapterPosition) {
                Contracts.requireNonNull(type, "type == null");

                final int dayItemsIndex = getDayItemsIndex(adapterPosition);

                _pickTimeManagedEvent.rise(new ScheduleEditorPickTimeEvent(type, dayItemsIndex));
            }
        });
        return timeViewHolder;
    }

    @NonNull
    private final ManagedEvent<ScheduleEditorPickTimeEvent> _pickTimeManagedEvent =
        Events.createEvent();

    @Getter
    @Nullable
    private List<WeekdayItem> _weekdayItems;

    private String formatTime(@NonNull final Context context, final long time) {
        Contracts.requireNonNull(context, "context == null");

        return DateFormat.getTimeFormat(context).format(new Date(time));
    }

    private void notifyTimeItem(final int dayIndex) {
        val timeItemPosition = getTimeItemPosition(dayIndex);
        if (timeItemPosition != null) {
            notifyItemChanged(timeItemPosition);
        }
    }
}
