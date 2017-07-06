package com.btc.prosport.manager.presenter;

import android.net.Uri;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

import com.btc.common.ConstantBuilder;
import com.btc.common.UriScheme;
import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.message.MessageManager;
import com.btc.common.control.manager.message.UserActionReaction;
import com.btc.common.control.manager.rx.RxManager;
import com.btc.common.event.generic.EventHandler;
import com.btc.common.event.notice.NoticeEventHandler;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.extension.exception.InsufficientPermissionException;
import com.btc.prosport.api.model.PlaygroundReport;
import com.btc.prosport.api.model.SportComplexReport;
import com.btc.prosport.api.model.entity.SportComplexReportEntity;
import com.btc.prosport.api.model.entity.UserEntity;
import com.btc.prosport.core.manager.account.AccountManager;
import com.btc.prosport.core.manager.account.ProSportAccount;
import com.btc.prosport.core.manager.navigation.ProSportNavigationManager;
import com.btc.prosport.core.manager.notificationEvent.ProSportMangerNotificationEventManager;
import com.btc.prosport.core.manager.notificationEvent.eventArgs.NotConfirmedOrdersChangedEventArgs;
import com.btc.prosport.core.manager.photo.PhotoManager;
import com.btc.prosport.core.manager.proSportApi.ProSportApiManager;
import com.btc.prosport.core.manager.proSportServiceHelper.ProSportAccountHelper;
import com.btc.prosport.core.result.PhotoResult;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.manager.navigation.ManagerNavigationManager;
import com.btc.prosport.manager.screen.ManagerNavigationScreen;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.adapter.rxjava.HttpException;

@Accessors(prefix = "_")
public abstract class BaseManagerNavigationPresenter<TScreen extends ManagerNavigationScreen>
    extends BaseManagerAccountPresenter<TScreen> {

    private static final String _LOG_TAG =
        ConstantBuilder.logTag(BaseManagerNavigationPresenter.class);

    private static final String PHOTO_MEDIA_TYPE = "image/*";

    private static final String PHOTO_FIELD = "avatar";

    public void displayChangedPlayground(@NonNull final PlaygroundReport playground) {
        Contracts.requireNonNull(playground, "playground == null");

        final val screen = getScreen();
        if (screen != null) {
            screen.displayChangedPlayground(playground);
        }
    }

    protected BaseManagerNavigationPresenter(
        @NonNull final AccountManager<ProSportAccount> proSportAccountManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull final RxManager rxManager,
        @NonNull final MessageManager messageManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final ManagerNavigationManager managerNavigationManager,
        @NonNull final ProSportMangerNotificationEventManager notificationEventManager,
        @NonNull final PhotoManager photoManager,
        @NonNull final ProSportNavigationManager proSportNavigationManager) {
        super(
            Contracts.requireNonNull(proSportAccountManager, "proSportAccountManager == null"),
            Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null"),
            Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null"),
            Contracts.requireNonNull(messageManager, "messageManager == null"),
            Contracts.requireNonNull(rxManager, "rxManager == null"));
        Contracts.requireNonNull(managerNavigationManager, "managerNavigationManager == null");
        Contracts.requireNonNull(notificationEventManager, "notificationEventManager == null");
        Contracts.requireNonNull(photoManager, "photoManager == null");
        Contracts.requireNonNull(proSportNavigationManager, "proSportNavigationManager == null");

        _notificationEventManager = notificationEventManager;
        _managerNavigationManager = managerNavigationManager;
        _photoManager = photoManager;
        _proSportNavigationManager = proSportNavigationManager;
    }

    protected final void displaySportComplexesLoadingError() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displaySportComplexesLoadingError();
        }
    }

    @CallSuper
    @Override
    protected void onScreenAppear(@NonNull final TScreen screen) {
        super.onScreenAppear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getViewManagerSettingsEvent().addHandler(_viewManagerSettingsHandler);
        screen.getViewHomeScreenEvent().addHandler(_viewHomeScreenHandler);
        screen.getViewManagerOrdersEvent().addHandler(_viewManagerOrdersHandler);
        screen.getViewFeedbackEvent().addHandler(_viewFeedbackHandler);
        screen.getViewManagerPricesEvent().addHandler(_viewManagerPricesHandler);
        screen.getViewManagerPlaygroundEvent().addHandler(_viewManagerPlaygroundHandler);
        screen.getViewManagerSportComplexesEvent().addHandler(_viewManagerSportComplexesHandler);
        screen.getCreatePlaygroundEvent().addHandler(_createPlaygroundHandler);
        screen.getEditPlaygroundPricesEvent().addHandler(_editPlaygroundPricesHandler);
        screen.getChangeManagerAvatarEvent().addHandler(_changeManagerAvatarHandler);
        getNotificationEventManager()
            .getNotConfirmedOrdersChangedEvent()
            .addHandler(_notConfirmedOrdersChangedHandler);
    }

    @CallSuper
    @Override
    protected void onScreenDisappear(@NonNull final TScreen screen) {
        super.onScreenDisappear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getViewManagerSettingsEvent().removeHandler(_viewManagerSettingsHandler);
        screen.getViewHomeScreenEvent().removeHandler(_viewHomeScreenHandler);
        screen.getViewManagerOrdersEvent().removeHandler(_viewManagerOrdersHandler);
        screen.getViewFeedbackEvent().removeHandler(_viewFeedbackHandler);
        screen.getViewManagerPricesEvent().removeHandler(_viewManagerPricesHandler);
        screen.getViewManagerPlaygroundEvent().removeHandler(_viewManagerPlaygroundHandler);
        screen.getViewManagerSportComplexesEvent().removeHandler(_viewManagerSportComplexesHandler);
        screen.getCreatePlaygroundEvent().removeHandler(_createPlaygroundHandler);
        screen.getEditPlaygroundPricesEvent().removeHandler(_editPlaygroundPricesHandler);
        screen.getChangeManagerAvatarEvent().removeHandler(_changeManagerAvatarHandler);
        getNotificationEventManager()
            .getNotConfirmedOrdersChangedEvent()
            .removeHandler(_notConfirmedOrdersChangedHandler);
    }

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ManagerNavigationManager _managerNavigationManager;

    @NonNull
    private final EventHandler<IdEventArgs> _editPlaygroundPricesHandler =
        new EventHandler<IdEventArgs>() {
            @Override
            public void onEvent(@NonNull final IdEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                getManagerNavigationManager().navigateToPlaygroundPriceEditor(eventArgs.getId());
            }
        };

    @NonNull
    private final NoticeEventHandler _createPlaygroundHandler = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            getManagerNavigationManager().navigateToPlaygroundCreator();
        }
    };

    @NonNull
    private final EventHandler<NotConfirmedOrdersChangedEventArgs>
        _notConfirmedOrdersChangedHandler = new EventHandler<NotConfirmedOrdersChangedEventArgs>() {
        @Override
        public void onEvent(@NonNull final NotConfirmedOrdersChangedEventArgs eventArgs) {
            Contracts.requireNonNull(eventArgs, "eventArgs == null");

            displayChangedPlayground(eventArgs.getPlayground());
        }
    };

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ProSportMangerNotificationEventManager _notificationEventManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final PhotoManager _photoManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ProSportNavigationManager _proSportNavigationManager;

    @NonNull
    private final NoticeEventHandler _changeManagerAvatarHandler = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            performChangeManagerAvatar();
        }
    };

    @NonNull
    private final NoticeEventHandler _viewFeedbackHandler = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            getManagerNavigationManager().navigateToFeedback();
        }
    };

    @NonNull
    private final NoticeEventHandler _viewHomeScreenHandler = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            getManagerNavigationManager().navigateToHomeScreen();
        }
    };

    @NonNull
    private final NoticeEventHandler _viewManagerOrdersHandler = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            getManagerNavigationManager().navigateToManagerOrders();
        }
    };

    @NonNull
    private final EventHandler<IdEventArgs> _viewManagerPlaygroundHandler =
        new EventHandler<IdEventArgs>() {
            @Override
            public void onEvent(@NonNull final IdEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                getManagerNavigationManager().navigateToPlaygroundSchedule(eventArgs.getId());
            }
        };

    @NonNull
    private final NoticeEventHandler _viewManagerPricesHandler = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            //TODO 04.04.2017
        }
    };

    @NonNull
    private final NoticeEventHandler _viewManagerSettingsHandler = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            getManagerNavigationManager().navigateToManagerSettings();
        }
    };

    @NonNull
    private final NoticeEventHandler _viewManagerSportComplexesHandler = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            displaySportComplexesLoading();

            performLoadManagerSportComplexes();
        }
    };

    private void displaySportComplexes(
        @Nullable final List<SportComplexReport> sportComplexes) {
        final val screen = getScreen();
        if (screen != null) {
            screen.displaySportComplexes(sportComplexes);
        }
    }

    private void displaySportComplexesLoading() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displaySportComplexesLoading();
        }
    }

    private void performChangeManagerAvatar() {
        final val photoManager = getPhotoManager();
        final val rxManager = getRxManager();
        final val proSportApiManager = getProSportApiManager();
        final val proSportApi = proSportApiManager.getProSportApi();

        final boolean isAllPermissionsGranted =
            photoManager.checkExternalStoragePermissions().isAllGranted();
        photoManager
            .getPhotoWithPermissions(!isAllPermissionsGranted)
            .filter(new Func1<PhotoResult, Boolean>() {
                @Override
                public Boolean call(final PhotoResult photoResult) {
                    return photoResult.getUri() != null;
                }
            })
            .doOnNext(new Action1<PhotoResult>() {
                @Override
                public void call(final PhotoResult photoResult) {
                    final val messageManager = getMessageManager();
                    final val message =
                        messageManager.getMessage(R.string.message_photo_changing_in_progress);
                    messageManager.showProgressMessage(null, message);
                }
            })
            .observeOn(rxManager.getIOScheduler())
            .flatMap(new Func1<PhotoResult, Observable<UserEntity>>() {
                @Override
                public Observable<UserEntity> call(final PhotoResult photoResult) {
                    return getProSportAccountHelper().withRequiredAccessToken(new Func1<String,
                        Observable<UserEntity>>() {
                        @Override
                        public Observable<UserEntity> call(final String token) {
                            final val uri = photoResult.getUri();
                            final File file;
                            if (uri.getScheme().equals(UriScheme.CONTENT.getSchemeName())) {
                                final val photoUri =
                                    photoManager.getPhotoFromGalleryAbsolutePath(uri);
                                file = FileUtils.getFile(Uri.parse(photoUri).getPath());
                            } else {
                                file = FileUtils.getFile(uri.getPath());
                            }
                            final val requestFile =
                                RequestBody.create(MediaType.parse(PHOTO_MEDIA_TYPE), file);
                            final val avatar = MultipartBody.Part.createFormData(PHOTO_FIELD,
                                                                                 file.getName(),
                                                                                 requestFile);
                            return proSportApi.changeManagerAvatar(token, avatar);
                        }
                    }, true);
                }
            })
            .observeOn(rxManager.getUIScheduler())
            .subscribe(new Action1<UserEntity>() {
                @Override
                public void call(final UserEntity user) {
                    Contracts.requireMainThread();

                    displayManager(user);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Contracts.requireMainThread();

                    Log.w(_LOG_TAG, "Failed to change avatar", error);

                    getMessageManager().dismissProgressMessage();

                    if (error instanceof HttpException) {
                        final val httpException = (HttpException) error;
                        if (httpException.code() == HttpURLConnection.HTTP_ENTITY_TOO_LARGE) {
                            getMessageManager().showModalMessage(R.string
                                                                     .message_error_image_size_is_big);
                        }
                    } else if (error instanceof InsufficientPermissionException) {
                        getMessageManager()
                            .showModalActionMessage(R.string
                                                        .message_error_fail_perform_pick_photo_insufficient_permission)
                            .subscribe(new Action1<UserActionReaction>() {
                                @Override
                                public void call(final UserActionReaction userActionReaction) {
                                    if (userActionReaction == UserActionReaction.PERFORM) {
                                        final val navigationManager =
                                            getProSportNavigationManager();
                                        final val settingsOpened =
                                            navigationManager.navigateToApplicationSettings();
                                        if (!settingsOpened) {
                                            getMessageManager().showErrorMessage(R.string
                                                                                     .message_error_settings_not_found_allow_permission_manual);
                                        }
                                    }
                                }
                            });
                    } else {
                        getMessageManager().showModalMessage(R.string
                                                                 .message_error_avatar_changing_failed);
                    }
                }
            }, new Action0() {
                @Override
                public void call() {
                    Contracts.requireMainThread();

                    getMessageManager().dismissProgressMessage();
                }
            });
    }

    private void performLoadManagerSportComplexes() {
        final val rxManager = getRxManager();
        final val proSportApiManager = getProSportApiManager();
        final val proSportApi = proSportApiManager.getProSportApi();

        getProSportAccountHelper()
            .withAccessToken(new Func1<String, Observable<List<SportComplexReportEntity>>>() {
                @Override
                public Observable<List<SportComplexReportEntity>> call(final String token) {
                    return rxManager.autoManage(proSportApi.getManagerSportComplexesReports(token));
                }
            }, false)
            .subscribeOn(rxManager.getIOScheduler())
            .observeOn(rxManager.getUIScheduler())
            .subscribe(new Action1<List<SportComplexReportEntity>>() {
                @Override
                public void call(final List<SportComplexReportEntity> sportComplexes) {
                    Contracts.requireMainThread();

                    displaySportComplexes((List<SportComplexReport>) (List<? extends
                        SportComplexReport>) sportComplexes);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Contracts.requireMainThread();

                    Log.w(_LOG_TAG, "Failed to get sport complexes.", error);

                    displaySportComplexesLoadingError();
                }
            });
    }
}
