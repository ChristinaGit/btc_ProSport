package com.btc.prosport.player.screen.activity.booking;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.var;
import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.mvp.presenter.Presenter;
import com.btc.common.utility.AnimationViewUtils;
import com.btc.prosport.api.ProSportContract;
import com.btc.prosport.player.R;
import com.btc.prosport.player.di.qualifier.PresenterNames;
import com.btc.prosport.player.screen.BookingScreen;
import com.btc.prosport.player.screen.activity.BasePlayerNavigationActivity;
import com.btc.prosport.player.screen.fragment.IntervalsFragment;

import org.apache.commons.lang3.BooleanUtils;
import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.SimpleTimeZone;

import javax.inject.Inject;
import javax.inject.Named;

@Accessors(prefix = "_")
public final class BookingActivity extends BasePlayerNavigationActivity
    implements BookingScreen, CalendarView.OnDateChangeListener {
    private static final String _LOG_TAG = ConstantBuilder.logTag(BookingActivity.class);

    private static final String _KEY_SAVED_STATE =
        ConstantBuilder.savedStateKey(BookingActivity.class, "saved_state");

    @NonNull
    public static Intent getIntent(
        @NonNull final Context context, @NonNull final Uri playgroundUri) {
        Contracts.requireNonNull(context, "context == null");
        Contracts.requireNonNull(playgroundUri, "playgroundUri == null");

        final val intent = new Intent(context, BookingActivity.class);

        intent.setData(playgroundUri);

        return intent;
    }

    public static void start(
        @NonNull final Context context, @NonNull final Uri playgroundUri) {
        Contracts.requireNonNull(context, "context == null");
        Contracts.requireNonNull(playgroundUri, "playgroundUri == null");

        context.startActivity(getIntent(context, playgroundUri));
    }

    public BookingActivity() {
        _reservationStartTimeConverter = Calendar.getInstance(new SimpleTimeZone(0, "GMT"));

        final val titleDatePattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), "MMMM");
        final val titleDateFormat = new SimpleDateFormat(titleDatePattern, Locale.getDefault());
        titleDateFormat.setCalendar(_reservationStartTimeConverter);

        final val titleDatePatternLong =
            DateFormat.getBestDateTimePattern(Locale.getDefault(), "MMMM yyyy");
        final val titleDateFormatLong =
            new SimpleDateFormat(titleDatePatternLong, Locale.getDefault());
        titleDateFormat.setCalendar(_reservationStartTimeConverter);

        _titleDateFormat = titleDateFormat;
        _titleDateFormatLong = titleDateFormatLong;
    }

    @CallSuper
    @Override
    public void bindViews() {
        super.bindViews();

        _appbarView = (AppBarLayout) findViewById(R.id.appbar);
        _toolbarView = (Toolbar) findViewById(R.id.toolbar);
        _datePickerView = (CalendarView) findViewById(R.id.date_picker);
    }

    @CallSuper
    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        if (outState != null) {
            if (_state == null) {
                _state = new BookingState();
            }

            _state.setPlaygroundId(getPlaygroundId());
            _state.setDatePickerExpanded(isDatePickerExpanded());
            _state.setStartReservationDate(getReservationStartTime());

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

    @CallSuper
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.booking, menu);

        return true;
    }

    @CallSuper
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        boolean consumed = false;

        final int itemId = item.getItemId();
        if (R.id.action_pick_date == itemId) {
            setDatePickerExpanded(!isDatePickerExpanded(), true);

            consumed = true;
        } else {
            consumed = super.onOptionsItemSelected(item);
        }

        return consumed;
    }

    @CallSuper
    @Override
    public void onSelectedDayChange(
        @NonNull final CalendarView view, final int year, final int month, final int dayOfMonth) {
        Contracts.requireNonNull(view, "view == null");

        _reservationStartTimeConverter.clear();
        _reservationStartTimeConverter.set(year, month, dayOfMonth);

        view.setDate(_reservationStartTimeConverter.getTimeInMillis());

        onReservationStartTimeChanged();

        setDatePickerExpanded(false, false);
    }

    @Nullable
    protected final String getFormattedTitle() {
        final String formattedTitle;

        final val reservationStartTime = getReservationStartTime();

        if (reservationStartTime != null) {
            _reservationStartTimeConverter.setTimeInMillis(reservationStartTime);

            final int startTimeYear = _reservationStartTimeConverter.get(Calendar.YEAR);

            _reservationStartTimeConverter.setTimeInMillis(System.currentTimeMillis());
            final int currentTimeYear = _reservationStartTimeConverter.get(Calendar.YEAR);

            // TODO: 17.04.2017 Check correctness
            final val titleDateFormat =
                startTimeYear == currentTimeYear ? _titleDateFormat : _titleDateFormatLong;

            formattedTitle = titleDateFormat.format(reservationStartTime);
        } else {
            formattedTitle = null;
        }

        return formattedTitle;
    }

    protected final boolean isDatePickerExpanded() {
        return BooleanUtils.isTrue(_datePickerExpanded);
    }

    protected final void setDatePickerExpanded(
        final boolean datePickerExpanded, final boolean animate) {
        if (!Objects.equals(_datePickerExpanded, datePickerExpanded)) {
            _datePickerExpanded = datePickerExpanded;

            onDatePickerExpandedStateChanged(animate);
        }
    }

    protected final void setPlaygroundId(@Nullable final Long playgroundId) {
        if (!Objects.equals(_playgroundId, playgroundId)) {
            _playgroundId = playgroundId;

            onPlaygroundIdChanged();
        }
    }

    @Nullable
    protected Long getReservationStartTime() {
        final Long reservationStartTime;

        if (_datePickerView != null) {
            reservationStartTime = _datePickerView.getDate();
        } else {
            reservationStartTime = null;
        }

        return reservationStartTime;
    }

    protected final void setReservationStartTime(final long reservationStartTime) {
        if (_datePickerView != null) {
            _datePickerView.setDate(reservationStartTime);
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
            setContentView(R.layout.activity_booking);

            bindViews();

            _intervalsFragment =
                (IntervalsFragment) getSupportFragmentManager().findFragmentById(R.id.intervals_fragment);

            if (_toolbarView != null) {
                setSupportActionBar(_toolbarView);
            }

            final val actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowTitleEnabled(true);
            }

            if (_datePickerView != null) {
                _datePickerView.setOnDateChangeListener(this);
            }
            if (_appbarView != null) {
                _appbarView.addOnOffsetChangedListener(_removeDatePickerViewOnToolbarCollapsed);
            }

            setPlaygroundId(_state.getPlaygroundId());
            setDatePickerExpanded(_state.isDatePickerExpanded(), false);

            var startDisplayedDate = _state.getStartReservationDate();
            if (startDisplayedDate == null) {
                startDisplayedDate = System.currentTimeMillis();
            }

            setReservationStartTime(startDisplayedDate);
            onReservationStartTimeChanged();
        } else {
            finish();
        }
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

    @Nullable
    @CallSuper
    protected BookingState onHandleIntent(@NonNull final Intent intent) {
        Contracts.requireNonNull(intent, "intent == null");

        final BookingState state;

        final val data = intent.getData();
        final int code = ProSportContract.getCode(data);

        if (ProSportContract.CODE_PLAYGROUND == code) {
            state = new BookingState();
            state.setPlaygroundId(ContentUris.parseId(data));
        } else {
            // TODO: 13.04.2017 handle state = null
            state = null;
        }

        return state;
    }

    @Nullable
    @CallSuper
    protected BookingState onHandleSavedState(@NonNull final Bundle savedInstanceState) {
        BookingState state = null;

        if (savedInstanceState.containsKey(_KEY_SAVED_STATE)) {
            state = Parcels.unwrap(savedInstanceState.getParcelable(_KEY_SAVED_STATE));
        }

        return state;
    }

    @CallSuper
    @Override
    protected void onInjectMembers() {
        super.onInjectMembers();

        getPlayerScreenComponent().inject(this);

        final val presenter = getPresenter();
        if (presenter != null) {
            presenter.bindScreen(this);
        }
    }

    @CallSuper
    protected void onPlaygroundIdChanged() {
        if (_intervalsFragment != null) {
            _intervalsFragment.setPlaygroundId(getPlaygroundId());
        }

        invalidateActionBar();
    }

    @CallSuper
    protected void onReservationStartTimeChanged() {
        invalidateActionBar();

        final val reservationStartTime = getReservationStartTime();

        if (_intervalsFragment != null) {
            _intervalsFragment.setReservationStartTime(reservationStartTime);
        }
    }

    @Named(PresenterNames.BOOKING)
    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Nullable
    /*package-private*/ Presenter<BookingScreen> _presenter;

    @Getter
    @NonNull
    private final Calendar _reservationStartTimeConverter;

    @NonNull
    private final java.text.DateFormat _titleDateFormat;

    @NonNull
    private final java.text.DateFormat _titleDateFormatLong;

    @Nullable
    private AppBarLayout _appbarView;

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
    private IntervalsFragment _intervalsFragment;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private Long _playgroundId;

    @Nullable
    private BookingState _state;

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

    private void invalidateActionBar() {
        supportInvalidateOptionsMenu();

        setTitle(getFormattedTitle());
    }
}
