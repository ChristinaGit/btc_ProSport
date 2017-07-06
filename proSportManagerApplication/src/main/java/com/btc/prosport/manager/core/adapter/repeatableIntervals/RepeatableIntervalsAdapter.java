package com.btc.prosport.manager.core.adapter.repeatableIntervals;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;

import rx.functions.Action1;

import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.picker.DatePickerResult;
import com.btc.common.control.manager.picker.TimePickerManager;
import com.btc.common.control.manager.picker.TimePickerResult;
import com.btc.common.extension.view.recyclerView.adapter.ModifiableRecyclerViewListAdapter;
import com.btc.common.extension.view.recyclerView.decoration.DividerItemDecoration;
import com.btc.common.extension.view.recyclerView.decoration.DividerItemDecoration.DecorationMode;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.common.utility.ThemeUtils;
import com.btc.prosport.api.model.utility.WeekDay;
import com.btc.prosport.core.utility.ProSportFormat;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.adapter.repeatableIntervals.item.RepeatableIntervalItem;
import com.btc.prosport.manager.core.adapter.repeatableIntervals.viewHolder.AddIntervalViewHolder;
import com.btc.prosport.manager.core.adapter.repeatableIntervals.viewHolder.IntervalViewHolder;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.SimpleTimeZone;

@Accessors(prefix = "_")
public class RepeatableIntervalsAdapter
    extends ModifiableRecyclerViewListAdapter<RepeatableIntervalItem, ExtendedRecyclerViewHolder> {
    public final void enableIntervalRemovalBySwipe(@NonNull final RecyclerView recyclerView) {
        _removeIntervalsHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public int getFooterItemCount() {
        return super.getFooterItemCount() + /*Add-interval item*/ 1;
    }

    @Override
    protected void onBindInnerItemViewHolder(
        @NonNull final ExtendedRecyclerViewHolder genericHolder, final int position) {
        Contracts.requireNonNull(genericHolder, "genericHolder == null");

        final val holder = (IntervalViewHolder) genericHolder;

        final val item = getItem(position);

        final val context = holder.getContext();

        holder.repeatView.setTag(R.id.tag_view_view_holder, holder);
        holder.repeatWeekView.setTag(R.id.tag_view_view_holder, holder);

        holder.timeStartView.setTag(R.id.tag_view_type, TimeType.START);
        holder.timeStartView.setTag(R.id.tag_view_view_holder, holder);

        holder.timeEndView.setTag(R.id.tag_view_type, TimeType.END);
        holder.timeEndView.setTag(R.id.tag_view_view_holder, holder);

        holder.dateStartView.setTag(R.id.tag_view_type, TimeType.START);
        holder.dateStartView.setTag(R.id.tag_view_view_holder, holder);

        holder.dateEndView.setTag(R.id.tag_view_type, TimeType.END);
        holder.dateEndView.setTag(R.id.tag_view_view_holder, holder);

        final val timeStart = item.getTimeStart();
        final val formattedStartTime = getFormattedIntervalTime(context, timeStart);
        holder.timeStartView.setText(formattedStartTime);

        final val timeEnd = item.getTimeEnd();
        final val formattedEndTime = getFormattedIntervalTime(context, timeEnd);
        holder.timeEndView.setText(formattedEndTime);

        final val repeatable = item.isRepeatable();
        //@formatter:off
        final val intervalDateStartHint = context.getString(
            repeatable
            ? R.string.repeatable_intervals_repeat_begin_hint
            : R.string.repeatable_intervals_date_start_hint);
        //@formatter:on
        holder.dateStartContainerView.setHint(intervalDateStartHint);
        final val dateStart = item.getDateStart();
        holder.dateStartView.setText(getFormattedIntervalDate(context, dateStart));

        //@formatter:off
        final val intervalDateEndHint = context.getString(
            repeatable
            ? R.string.repeatable_intervals_repeat_until_hint
            : R.string.repeatable_intervals_date_end_hint);
        //@formatter:on
        holder.dateEndContainerView.setHint(intervalDateEndHint);
        final val dateEnd = item.getDateEnd();
        holder.dateEndView.setText(getFormattedIntervalDate(context, dateEnd));

        //region Silent holder.repeatView
        holder.repeatView.setOnCheckedChangeListener(null);

        holder.repeatView.setChecked(repeatable);

        holder.repeatView.setOnCheckedChangeListener(_changeRepeatableStateOnCheck);
        //endregion

        if (repeatable) {
            invalidateWeekView(holder.repeatWeekView, item);
        }
        holder.repeatWeekView.setVisibility(repeatable ? View.VISIBLE : View.GONE);
    }

    @NonNull
    @Override
    protected ExtendedRecyclerViewHolder onCreateFooterItemViewHolder(
        final ViewGroup parent, final int viewType) {
        Contracts.requireNonNull(parent, "parent == null");

        final val view = inflateView(R.layout.layout_repeatable_intervals_item_add, parent);

        final val holder = new AddIntervalViewHolder(view);

        DividerItemDecoration.setDecorationMode(holder.itemView, DecorationMode.NONE);

        holder.addIntervalView.setOnClickListener(_addIntervalOnClick);

        return holder;
    }

    @NonNull
    @Override
    protected ExtendedRecyclerViewHolder onCreateInnerItemViewHolder(
        final ViewGroup parent, final int viewType) {
        Contracts.requireNonNull(parent, "parent == null");

        final val view = inflateView(R.layout.layout_repeatable_intervals_item, parent);

        final val holder = new IntervalViewHolder(view);

        holder.timeStartView.setOnClickListener(_risePickIntervalTimeOnClick);
        holder.timeEndView.setOnClickListener(_risePickIntervalTimeOnClick);
        holder.dateStartView.setOnClickListener(_risePickIntervalDateOnClick);
        holder.dateEndView.setOnClickListener(_risePickIntervalDateOnClick);

        setupWeekView(holder.repeatWeekView);

        return holder;
    }

    public void notifyIntervalChanged(final int relativePosition) {
        notifyItemChanged(getInnerItemAdapterPosition(relativePosition));
    }

    protected void invalidateWeekView(
        @NonNull final ViewGroup weekView, @NonNull final RepeatableIntervalItem item) {
        Contracts.requireNonNull(weekView, "weekView == null");
        Contracts.requireNonNull(item, "item == null");

        if (item.isRepeatable()) {
            final val repeatWeekDays = item.getRepeatWeekDays();
            final val weekDayViewCount = weekView.getChildCount();
            for (int i = 0; i < weekDayViewCount; i++) {
                final val dayViewCandidate = weekView.getChildAt(i);
                if (dayViewCandidate instanceof CheckedTextView) {
                    final val dayView = (CheckedTextView) dayViewCandidate;
                    final val dayOfWeek = (WeekDay) dayView.getTag(R.id.tag_view_interval_week_day);
                    final val checked = repeatWeekDays.contains(dayOfWeek);
                    if (dayView.isChecked() != checked) {
                        dayView.setChecked(checked);
                    }
                }
            }
        } else {
            final val weekDayViewCount = weekView.getChildCount();
            for (int i = 0; i < weekDayViewCount; i++) {
                final val dayViewCandidate = weekView.getChildAt(i);
                if (dayViewCandidate instanceof CheckedTextView) {
                    final val dayView = (CheckedTextView) dayViewCandidate;
                    if (dayView.isChecked()) {
                        dayView.setChecked(false);
                    }
                }
            }
        }
    }

    protected boolean isValidIntervals() {
        boolean isValid = true;

        return isValid;
    }

    protected void setupWeekView(@NonNull final ViewGroup weekView) {
        Contracts.requireNonNull(weekView, "weekView == null");

        final val context = weekView.getContext();

        if (_intervalWeekDays == null) {
            final val daysInWeek = (int) (DateUtils.WEEK_IN_MILLIS / DateUtils.DAY_IN_MILLIS);

            _intervalWeekDays = new ArrayList<>(daysInWeek);

            final val calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());

            final val weekdays = DateFormatSymbols.getInstance().getShortWeekdays();
            for (int i = 0; i < daysInWeek; i++) {
                final val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                final val name = weekdays[dayOfWeek].toUpperCase();
                _intervalWeekDays.add(new DisplayableWeekDay(WeekDay.byCode(dayOfWeek), name));
                calendar.add(Calendar.DATE, 1);
            }
        }

        final int weekDayItemCount = Math.min(weekView.getChildCount(), _intervalWeekDays.size());
        final int accentColor = ThemeUtils.resolveColor(context, R.attr.colorAccent);
        for (int i = 0; i < weekDayItemCount; i++) {
            final val dayViewCandidate = weekView.getChildAt(i);
            if (dayViewCandidate instanceof CheckedTextView) {
                final val dayView = (CheckedTextView) dayViewCandidate;
                dayView.setOnClickListener(_checkRepeatDayOnClick);
                final val displayableWeekDay = _intervalWeekDays.get(i);
                dayView.setText(displayableWeekDay.getDisplayName());
                dayView.setTag(R.id.tag_view_interval_week_day, displayableWeekDay.getWeekDay());

                final val background = dayView.getBackground();
                background.setColorFilter(accentColor, PorterDuff.Mode.SRC_IN);
            }
        }
    }

    @NonNull
    private final CompoundButton.OnCheckedChangeListener _changeRepeatableStateOnCheck =
        new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                final val holder =
                    (IntervalViewHolder) buttonView.getTag(R.id.tag_view_view_holder);
                final val position = holder.getAdapterPosition();

                final val item = getItem(position);
                if (isChecked != item.isRepeatable()) {
                    item.setRepeatable(isChecked);

                    if (isChecked) {
                        final val repeatWeekDays = item.getRepeatWeekDays();
                        if (repeatWeekDays.isEmpty()) {
                            repeatWeekDays.addAll(Arrays.asList(WeekDay.values()));
                        }
                    }

                    notifyItemChanged(position);
                }
            }
        };

    @NonNull
    private final View.OnClickListener _checkRepeatDayOnClick = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            final val weekDay = (WeekDay) v.getTag(R.id.tag_view_interval_week_day);
            final val container = v.getParent();
            if (container instanceof ViewGroup) {
                final val containerView = (ViewGroup) container;
                final val holder =
                    (IntervalViewHolder) containerView.getTag(R.id.tag_view_view_holder);

                final val position = holder.getAdapterPosition();
                final val item = getItem(position);

                final val repeatable = item.isRepeatable();

                final val repeatWeekDays = item.getRepeatWeekDays();
                if (!repeatWeekDays.remove(weekDay)) {
                    repeatWeekDays.add(weekDay);
                }

                if (repeatWeekDays.isEmpty()) {
                    item.setRepeatable(false);
                }

                final boolean repeatableStateChanged = item.isRepeatable() != repeatable;

                if (repeatableStateChanged) {
                    notifyItemChanged(position);
                } else {
                    invalidateWeekView(containerView, item);
                }
            }
        }
    };

    @NonNull
    private final Calendar _intervalTimeConverter =
        Calendar.getInstance(new SimpleTimeZone(0, "GMT"));

    @Getter(onMethod = @__(@Override))
    @NonNull
    private final List<RepeatableIntervalItem> _items = new ArrayList<>();

    @NonNull
    private final ItemTouchHelper _removeIntervalsHelper =
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                                                               ItemTouchHelper.LEFT |
                                                               ItemTouchHelper.RIGHT) {
            @Override
            public int getSwipeDirs(
                final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder) {
                final val position = viewHolder.getAdapterPosition();
                if (viewHolder.getItemViewType() == VIEW_TYPE_INNER &&
                    (getInnerItemRelativePosition(position) != 0 || getInnerItemCount() > 1)) {
                    return super.getSwipeDirs(recyclerView, viewHolder);
                } else {
                    return 0;
                }
            }

            @Override
            public boolean onMove(
                final RecyclerView recyclerView,
                final RecyclerView.ViewHolder viewHolder,
                final RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(
                final RecyclerView.ViewHolder viewHolder, final int direction) {
                final val position = viewHolder.getAdapterPosition();
                removeItem(getInnerItemRelativePosition(position));
            }
        });

    @Nullable
    private java.text.DateFormat _intervalDateFormat;

    @Nullable
    private java.text.DateFormat _intervalTimeFormat;

    private List<DisplayableWeekDay> _intervalWeekDays;

    @Getter
    @Setter
    private boolean _showInputErrors;

    @NonNull
    private final View.OnClickListener _addIntervalOnClick = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            setShowInputErrors(false);
            notifyDataSetChanged();

            addItem(RepeatableIntervalItem.createDefault());
        }
    };

    @Getter
    @Setter
    private TimePickerManager _timePickerManager;

    @NonNull
    private final View.OnClickListener _risePickIntervalDateOnClick = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            final val timePickerManager = getTimePickerManager();
            if (timePickerManager != null) {
                final val holder = (IntervalViewHolder) v.getTag(R.id.tag_view_view_holder);
                final val type = (TimeType) v.getTag(R.id.tag_view_type);
                final val position = holder.getAdapterPosition();

                final val item = getItem(position);

                final val initialDate =
                    type == TimeType.START ? item.getDateStart() : item.getDateEnd();

                _intervalTimeConverter.clear();
                _intervalTimeConverter.setTimeInMillis(initialDate);
                timePickerManager
                    .pickDate(_intervalTimeConverter)
                    .subscribe(new Action1<DatePickerResult>() {
                        @Override
                        public void call(final DatePickerResult datePickerResult) {
                            if (datePickerResult.isSelected()) {
                                _intervalTimeConverter.clear();
                                _intervalTimeConverter.set(datePickerResult.getYear(),
                                                           datePickerResult.getMonth(),
                                                           datePickerResult.getDayOfMonth());

                                final val item = getItem(position);

                                final val selectedDate = _intervalTimeConverter.getTimeInMillis();
                                if (type == TimeType.START) {
                                    item.setDateStart(selectedDate);
                                } else {
                                    item.setDateEnd(selectedDate);
                                }

                                final val dateStart = item.getDateStart();
                                final val dateEnd = item.getDateEnd();

                                if (type == TimeType.START) {
                                    if (dateStart > dateEnd) {
                                        item.setDateStart(dateStart);
                                        item.setDateEnd(dateStart);
                                    }
                                } else {
                                    if (dateStart > dateEnd) {
                                        item.setDateStart(dateEnd);
                                        item.setDateEnd(dateEnd);
                                    }
                                }

                                notifyItemChanged(position);
                            }
                        }
                    });
            }
        }
    };

    @NonNull
    private final View.OnClickListener _risePickIntervalTimeOnClick = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            final val timePickerManager = getTimePickerManager();
            if (timePickerManager != null) {
                final val holder = (IntervalViewHolder) v.getTag(R.id.tag_view_view_holder);
                final val type = (TimeType) v.getTag(R.id.tag_view_type);
                final val position = holder.getAdapterPosition();

                final val item = getItem(position);

                final val initialTime =
                    type == TimeType.START ? item.getTimeStart() : item.getTimeEnd();

                _intervalTimeConverter.clear();
                _intervalTimeConverter.setTimeInMillis(initialTime);
                timePickerManager
                    .pickTime(_intervalTimeConverter)
                    .subscribe(new Action1<TimePickerResult>() {
                        @Override
                        public void call(final TimePickerResult timePickerResult) {
                            if (timePickerResult.isSelected()) {
                                _intervalTimeConverter.clear();
                                _intervalTimeConverter.set(Calendar.HOUR_OF_DAY,
                                                           timePickerResult.getHourOfDay());
                                _intervalTimeConverter.set(Calendar.MINUTE,
                                                           timePickerResult.getMinute());

                                final val item = getItem(position);

                                final val selectedTime = _intervalTimeConverter.getTimeInMillis();
                                if (type == TimeType.START) {
                                    item.setTimeStart(selectedTime);
                                } else {
                                    item.setTimeEnd(selectedTime);
                                }

                                notifyItemChanged(position);
                            }
                        }
                    });
            }
        }
    };

    @Nullable
    private String getFormattedIntervalDate(
        @NonNull final Context context, @Nullable final Long date) {
        Contracts.requireNonNull(context, "context == null");

        final String formattedIntervalDate;

        if (_intervalDateFormat == null) {
            _intervalDateFormat = ProSportFormat.getIntervalLongDateFormat();
        }

        if (date != null) {
            formattedIntervalDate = _intervalDateFormat.format(new Date(date));
        } else {
            formattedIntervalDate = null;
        }

        return formattedIntervalDate;
    }

    @Nullable
    private String getFormattedIntervalTime(
        @NonNull final Context context, @Nullable final Long time) {
        Contracts.requireNonNull(context, "context == null");

        final String formattedIntervalTime;

        if (_intervalTimeFormat == null) {
            _intervalTimeFormat = ProSportFormat.getIntervalTimeFormat(context);
        }

        if (time != null) {
            formattedIntervalTime = _intervalTimeFormat.format(new Date(time));
        } else {
            formattedIntervalTime = null;
        }

        return formattedIntervalTime;
    }

    private enum TimeType {
        START, END
    }

    @Accessors(prefix = "_")
    private static final class DisplayableWeekDay {
        @Getter
        @NonNull
        private final String _displayName;

        @Getter
        @NonNull
        private final WeekDay _weekDay;

        private DisplayableWeekDay(
            @NonNull final WeekDay weekDay, @NonNull final String displayName) {
            Contracts.requireNonNull(weekDay, "weekDay == null");
            Contracts.requireNonNull(displayName, "displayName == null");

            _weekDay = weekDay;
            _displayName = displayName;
        }
    }
}
