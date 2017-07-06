package com.btc.prosport.player.presenter;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import rx.functions.Action1;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.message.MessageManager;
import com.btc.common.control.manager.picker.DatePickerResult;
import com.btc.common.control.manager.picker.TimePickerManager;
import com.btc.common.control.manager.picker.TimePickerResult;
import com.btc.common.control.manager.rx.RxManager;
import com.btc.common.event.generic.EventHandler;
import com.btc.common.event.notice.NoticeEventHandler;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.prosport.core.manager.account.AccountManager;
import com.btc.prosport.core.manager.account.ProSportAccount;
import com.btc.prosport.core.manager.navigation.ProSportNavigationManager;
import com.btc.prosport.core.manager.photo.PhotoManager;
import com.btc.prosport.core.manager.proSportApi.ProSportApiManager;
import com.btc.prosport.core.manager.proSportServiceHelper.ProSportAccountHelper;
import com.btc.prosport.player.core.eventArgs.SelectSearchTimeEventArgs;
import com.btc.prosport.player.core.manager.navigation.PlayerNavigationManager;
import com.btc.prosport.player.screen.SportComplexesViewerScreen;

import java.util.Calendar;

@Accessors(prefix = "_")
public final class SportComplexesViewerPresenter
    extends BasePlayerNavigationPresenter<SportComplexesViewerScreen> {
    private static final String _LOG_TAG =
        ConstantBuilder.logTag(SportComplexesViewerPresenter.class);

    public SportComplexesViewerPresenter(
        @NonNull final AccountManager<ProSportAccount> proSportAccountManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull final RxManager rxManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final PlayerNavigationManager playerNavigationManager,
        @NonNull final TimePickerManager timePickerManager,
        @NonNull final MessageManager messageManager,
        @NonNull final PhotoManager photoManager,
        @NonNull final ProSportNavigationManager proSportNavigationManager) {
        super(
            Contracts.requireNonNull(proSportAccountManager, "proSportAccountManager == null"),
            Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null"),
            Contracts.requireNonNull(rxManager, "rxManager == null"),
            Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null"),
            Contracts.requireNonNull(playerNavigationManager, "playerNavigationManager == null"),
            Contracts.requireNonNull(messageManager, "messageManager == null"),
            Contracts.requireNonNull(photoManager, "photoManager == null"),
            Contracts.requireNonNull(
                proSportNavigationManager,
                "proSportNavigationManager == null"));
        Contracts.requireNonNull(timePickerManager, "dataPickerManager == null");

        _timePickerManager = timePickerManager;
        _playerNavigationManager = playerNavigationManager;
    }

    protected void displaySelectedSportComplexesEndTimeSearch(
        final int hourOfDay, final int minute) {
        final val screen = getScreen();
        if (screen != null) {
            screen.displaySelectedSportComplexesEndTimeSearchParam(hourOfDay, minute);
        }
    }

    protected void displaySelectedSportComplexesSearchDateParam(
        final int year, final int month, final int dayOfMonth) {
        final val screen = getScreen();
        if (screen != null) {
            screen.displaySelectedSportComplexesSearchDateParam(year, month, dayOfMonth);
        }
    }

    protected void displaySelectedSportComplexesStartTimeSearchParam(
        final int hourOfDay, final int minute) {
        final val screen = getScreen();
        if (screen != null) {
            screen.displaySelectedSportComplexesStartTimeSearchParam(hourOfDay, minute);
        }
    }

    @CallSuper
    @Override
    protected void onScreenAppear(@NonNull final SportComplexesViewerScreen screen) {
        super.onScreenAppear(Contracts.requireNonNull(screen, "screen == null"));

        screen
            .getSelectSportComplexesSearchTimeEvent()
            .addHandler(_selectSportComplexesSearchTimeHandler);
        screen
            .getSelectSportComplexesSearchDateEvent()
            .addHandler(_selectSportComplexesSearchDateHandler);
        screen.getViewSportComplexDetailsEvent().addHandler(_viewSportComplexDetailsHandler);
    }

    @CallSuper
    @Override
    protected void onScreenDisappear(@NonNull final SportComplexesViewerScreen screen) {
        super.onScreenDisappear(Contracts.requireNonNull(screen, "screen == null"));

        screen
            .getSelectSportComplexesSearchTimeEvent()
            .removeHandler(_selectSportComplexesSearchTimeHandler);
        screen
            .getSelectSportComplexesSearchDateEvent()
            .removeHandler(_selectSportComplexesSearchDateHandler);
        screen.getViewSportComplexDetailsEvent().removeHandler(_viewSportComplexDetailsHandler);
    }

    protected void performSelectSportComplexesSearchDate() {
        final val rxManager = getRxManager();
        rxManager
            .autoManage(this.getTimePickerManager().pickDate())
            .subscribeOn(rxManager.getIOScheduler())
            .observeOn(rxManager.getUIScheduler())
            .subscribe(new Action1<DatePickerResult>() {
                @Override
                public void call(final DatePickerResult datePickerResult) {
                    Contracts.requireMainThread();

                    if (datePickerResult.isSelected()) {
                        displaySelectedSportComplexesSearchDateParam(
                            datePickerResult.getYear(),
                            datePickerResult.getMonth(),
                            datePickerResult.getDayOfMonth());
                    }
                }
            });
    }

    protected void performSelectSportComplexesSearchTime(
        @NonNull final SelectSearchTimeEventArgs.Mode mode) {
        final val calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.add(Calendar.HOUR, 1);

        final val rxManager = getRxManager();
        rxManager
            .autoManage(this.getTimePickerManager().pickTime(calendar))
            .subscribeOn(rxManager.getIOScheduler())
            .observeOn(rxManager.getUIScheduler())
            .subscribe(new Action1<TimePickerResult>() {
                @Override
                public void call(final TimePickerResult timePickerResult) {
                    Contracts.requireMainThread();

                    if (timePickerResult.isSelected()) {
                        final int hourOfDay = timePickerResult.getHourOfDay();
                        final int minute = timePickerResult.getMinute();
                        if (mode == SelectSearchTimeEventArgs.Mode.START) {
                            displaySelectedSportComplexesStartTimeSearchParam(hourOfDay, minute);
                        } else if (mode == SelectSearchTimeEventArgs.Mode.END) {
                            displaySelectedSportComplexesEndTimeSearch(hourOfDay, minute);
                        }
                    }
                }
            });
    }

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final TimePickerManager _timePickerManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final PlayerNavigationManager _playerNavigationManager;

    @NonNull
    private final NoticeEventHandler _selectSportComplexesSearchDateHandler =
        new NoticeEventHandler() {
            @Override
            public void onEvent() {
                performSelectSportComplexesSearchDate();
            }
        };

    @NonNull
    private final EventHandler<SelectSearchTimeEventArgs> _selectSportComplexesSearchTimeHandler =
        new EventHandler<SelectSearchTimeEventArgs>() {
            @Override
            public void onEvent(@NonNull final SelectSearchTimeEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                performSelectSportComplexesSearchTime(eventArgs.getMode());
            }
        };

    @NonNull
    private final EventHandler<IdEventArgs> _viewSportComplexDetailsHandler =
        new EventHandler<IdEventArgs>() {
            @Override
            public void onEvent(@NonNull final IdEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                getPlayerNavigationManager().navigateToSportComplexDetails(eventArgs.getId());
            }
        };
}
