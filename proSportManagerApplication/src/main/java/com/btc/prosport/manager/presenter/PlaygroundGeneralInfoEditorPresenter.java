package com.btc.prosport.manager.presenter;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.util.Log;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import rx.functions.Action1;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.message.MessageManager;
import com.btc.common.control.manager.message.UserActionReaction;
import com.btc.common.control.manager.rx.RxManager;
import com.btc.common.event.generic.EventHandler;
import com.btc.common.event.notice.NoticeEventHandler;
import com.btc.common.extension.exception.InsufficientPermissionException;
import com.btc.common.mvp.presenter.BasePresenter;
import com.btc.prosport.api.model.City;
import com.btc.prosport.api.model.SubwayStation;
import com.btc.prosport.api.model.entity.CityEntity;
import com.btc.prosport.api.model.entity.SubwayStationEntity;
import com.btc.prosport.core.manager.navigation.ProSportNavigationManager;
import com.btc.prosport.core.manager.photo.PhotoManager;
import com.btc.prosport.core.manager.proSportApi.ProSportApiManager;
import com.btc.prosport.core.result.PhotoResult;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.eventArgs.PlacePickerEventArgs;
import com.btc.prosport.manager.core.eventArgs.UpdateSubwaysEventArgs;
import com.btc.prosport.manager.core.manager.navigation.ManagerNavigationManager;
import com.btc.prosport.manager.core.result.PlacePickerResult;
import com.btc.prosport.manager.screen.PlaygroundGeneralInfoEditorScreen;

import java.util.List;

@Accessors(prefix = "_")
public final class PlaygroundGeneralInfoEditorPresenter
    extends BasePresenter<PlaygroundGeneralInfoEditorScreen> {
    private static final String _LOG_TAG =
        ConstantBuilder.logTag(PlaygroundGeneralInfoEditorPresenter.class);

    public PlaygroundGeneralInfoEditorPresenter(
        @NonNull final RxManager rxManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final ManagerNavigationManager managerNavigationManager,
        @NonNull final PhotoManager photoManager,
        @NonNull final MessageManager messageManager,
        @NonNull final ProSportNavigationManager proSportNavigationManager) {
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(managerNavigationManager, "managerNavigationManager == null");
        Contracts.requireNonNull(photoManager, "photoManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(proSportNavigationManager, "proSportNavigationManager == null");

        _managerNavigationManager = managerNavigationManager;
        _proSportApiManager = proSportApiManager;
        _rxManager = rxManager;
        _photoManager = photoManager;
        _messageManager = messageManager;
        _proSportNavigationManager = proSportNavigationManager;
    }

    @CallSuper
    @Override
    protected void onScreenAppear(
        @NonNull final PlaygroundGeneralInfoEditorScreen screen) {
        super.onScreenAppear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getPickLocationEvent().addHandler(_pickLocationHandler);
        screen.getSubwaysUpdateEvent().addHandler(_updateSubwaysHandler);
        screen.getPickPhotoEvent().addHandler(_pickPhotoHandler);
        screen.getCitiesUpdateEvent().addHandler(_updateCities);
    }

    @CallSuper
    @Override
    protected void onScreenDisappear(@NonNull final PlaygroundGeneralInfoEditorScreen screen) {
        super.onScreenDisappear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getPickLocationEvent().removeHandler(_pickLocationHandler);
        screen.getSubwaysUpdateEvent().removeHandler(_updateSubwaysHandler);
        screen.getPickPhotoEvent().removeHandler(_pickPhotoHandler);
        screen.getCitiesUpdateEvent().removeHandler(_updateCities);
    }

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ManagerNavigationManager _managerNavigationManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final MessageManager _messageManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final PhotoManager _photoManager;

    @NonNull
    private final EventHandler<PlacePickerEventArgs> _pickLocationHandler =
        new EventHandler<PlacePickerEventArgs>() {
            @Override
            public void onEvent(@NonNull final PlacePickerEventArgs placePickerEventArgs) {
                pickLocation(placePickerEventArgs);
            }
        };

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ProSportApiManager _proSportApiManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ProSportNavigationManager _proSportNavigationManager;

    @NonNull
    private final NoticeEventHandler _pickPhotoHandler = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            pickPhoto();
        }
    };

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final RxManager _rxManager;

    private final NoticeEventHandler _updateCities = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            loadCities();
        }
    };

    private final EventHandler<UpdateSubwaysEventArgs> _updateSubwaysHandler =
        new EventHandler<UpdateSubwaysEventArgs>() {
            @Override
            public void onEvent(
                @NonNull final UpdateSubwaysEventArgs eventArgs) {
                loadSubways(eventArgs.getId());
            }
        };

    private void displayCities(@NonNull final List<City> city) {
        Contracts.requireNonNull(city, "city == null");

        val screen = getScreen();
        if (screen != null) {
            screen.displayCity(city);
        }
    }

    private void displayPhoto(@NonNull final PhotoResult photoResult) {
        Contracts.requireNonNull(photoResult, "photoResult == null");

        val screen = getScreen();
        if (screen != null) {
            screen.displayPhoto(photoResult);
        }
    }

    private void displayPickedLocation(@NonNull final PlacePickerResult placePickerResult) {
        Contracts.requireNonNull(placePickerResult, "placePickerResult == null");

        val screen = getScreen();
        if (screen != null) {
            screen.displayPickedLocation(placePickerResult);
        }
    }

    private void displaySubways(@NonNull final List<SubwayStation> subways) {
        Contracts.requireNonNull(subways, "subways == null");

        val screen = getScreen();
        if (screen != null) {
            screen.displaySubways(subways);
        }
    }

    private void loadCities() {
        val rxManager = getRxManager();
        rxManager
            .autoManage(getProSportApiManager().getProSportApi().getCities())
            .subscribeOn(rxManager.getIOScheduler())
            .observeOn(rxManager.getUIScheduler())
            .subscribe(new Action1<List<CityEntity>>() {
                @Override
                public void call(final List<CityEntity> cities) {
                    displayCities((List<City>) (List<? extends City>) cities);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable throwable) {
                    Log.w(_LOG_TAG, "Failed to load cities.", throwable);
                }
            });
    }

    private void loadSubways(final long cityId) {
        val rxManager = getRxManager();
        rxManager
            .autoManage(getProSportApiManager().getProSportApi().getSubwayStations(cityId))
            .subscribeOn(rxManager.getIOScheduler())
            .observeOn(rxManager.getUIScheduler())
            .subscribe(new Action1<List<SubwayStationEntity>>() {
                @Override
                public void call(final List<SubwayStationEntity> subwayStations) {
                    displaySubways((List<SubwayStation>) (List<? extends SubwayStation>)
                        subwayStations);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Log.w(_LOG_TAG, "Failed to load subways.", error);
                }
            });
    }

    private void pickLocation(@NonNull final PlacePickerEventArgs placePickerEventArgs) {
        Contracts.requireNonNull(placePickerEventArgs, "placePickerEventArgs == null");

        getManagerNavigationManager()
            .navigateToPlacePicker(placePickerEventArgs.getViewPort())
            .subscribe(new Action1<PlacePickerResult>() {
                @Override
                public void call(final PlacePickerResult placePickerResult) {
                    displayPickedLocation(placePickerResult);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Log.w(_LOG_TAG, "Failed to pick location.", error);
                }
            });
    }

    private void pickPhoto() {
        getPhotoManager().getPhotoWithPermissions(true).subscribe(new Action1<PhotoResult>() {
            @Override
            public void call(final PhotoResult photoResult) {
                displayPhoto(photoResult);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(final Throwable error) {
                if (error instanceof InsufficientPermissionException) {
                    getMessageManager()
                        .showActionMessage(R.string
                                               .message_error_fail_perform_get_photo_insufficient_permission,
                                           R.string.message_manager_fix_error)
                        .subscribe(new Action1<UserActionReaction>() {
                            @Override
                            public void call(final UserActionReaction userErrorResponse) {
                                if (userErrorResponse == UserActionReaction.PERFORM) {
                                    final val navigationManager = getProSportNavigationManager();
                                    final boolean settingsOpened =
                                        navigationManager.navigateToApplicationSettings();
                                    if (!settingsOpened) {
                                        getMessageManager().showErrorMessage(R.string
                                                                                 .message_error_settings_not_found_allow_permission_manual);
                                    }
                                }
                            }
                        });
                }
                Log.w(_LOG_TAG, "Failed to pick photo.", error);
            }
        });
    }
}
