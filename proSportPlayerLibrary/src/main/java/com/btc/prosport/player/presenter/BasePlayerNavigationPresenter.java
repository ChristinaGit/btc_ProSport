package com.btc.prosport.player.presenter;

import android.net.Uri;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

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
import com.btc.common.event.notice.NoticeEventHandler;
import com.btc.common.extension.exception.InsufficientPermissionException;
import com.btc.prosport.api.model.entity.UserEntity;
import com.btc.prosport.core.manager.account.AccountManager;
import com.btc.prosport.core.manager.account.ProSportAccount;
import com.btc.prosport.core.manager.navigation.ProSportNavigationManager;
import com.btc.prosport.core.manager.photo.PhotoManager;
import com.btc.prosport.core.manager.proSportApi.ProSportApiManager;
import com.btc.prosport.core.manager.proSportServiceHelper.ProSportAccountHelper;
import com.btc.prosport.core.result.PhotoResult;
import com.btc.prosport.player.R;
import com.btc.prosport.player.core.manager.navigation.PlayerNavigationManager;
import com.btc.prosport.player.screen.PlayerNavigationScreen;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.HttpURLConnection;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.adapter.rxjava.HttpException;

@Accessors(prefix = "_")
public abstract class BasePlayerNavigationPresenter<TScreen extends PlayerNavigationScreen>
    extends BasePlayerAccountPresenter<TScreen> {
    private static final String _LOG_TAG =
        ConstantBuilder.logTag(BasePlayerNavigationPresenter.class);

    private static final String PHOTO_MEDIA_TYPE = "image/*";

    private static final String PHOTO_FIELD = "avatar";

    protected BasePlayerNavigationPresenter(
        @NonNull final AccountManager<ProSportAccount> proSportAccountManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull final RxManager rxManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final PlayerNavigationManager playerNavigationManager,
        @NonNull final MessageManager messageManager,
        @NonNull final PhotoManager photoManager,
        @NonNull final ProSportNavigationManager proSportNavigationManager) {
        super(
            Contracts.requireNonNull(proSportAccountManager, "proSportAccountManager == null"),
            Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null"),
            Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null"),
            Contracts.requireNonNull(rxManager, "rxManager == null"));
        Contracts.requireNonNull(playerNavigationManager, "playerNavigationManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(photoManager, "photoManager == null");
        Contracts.requireNonNull(proSportNavigationManager, "proSportNavigationManager == null");

        _playerNavigationManager = playerNavigationManager;
        _photoManager = photoManager;
        _messageManager = messageManager;
        _proSportNavigationManager = proSportNavigationManager;
    }

    @CallSuper
    @Override
    protected void onScreenAppear(@NonNull final TScreen screen) {
        super.onScreenAppear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getViewPlayerSettingsEvent().addHandler(_viewPlayerSettingsHandler);
        screen.getViewHomeScreenEvent().addHandler(_viewHomeScreenHandler);
        screen.getViewPlayerOrdersEvent().addHandler(_viewPlayerOrdersHandler);
        screen.getViewFeedbackEvent().addHandler(_viewFeedbackHandler);
        screen.getChangePlayerAvatarEvent().addHandler(_changePlayerPhotoHandler);
    }

    @CallSuper
    @Override
    protected void onScreenDisappear(@NonNull final TScreen screen) {
        super.onScreenDisappear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getViewPlayerSettingsEvent().removeHandler(_viewPlayerSettingsHandler);
        screen.getViewHomeScreenEvent().removeHandler(_viewHomeScreenHandler);
        screen.getViewPlayerOrdersEvent().removeHandler(_viewPlayerOrdersHandler);
        screen.getViewFeedbackEvent().removeHandler(_viewFeedbackHandler);
        screen.getChangePlayerAvatarEvent().removeHandler(_changePlayerPhotoHandler);
    }

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final MessageManager _messageManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final PhotoManager _photoManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final PlayerNavigationManager _playerNavigationManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ProSportNavigationManager _proSportNavigationManager;

    @NonNull
    private final NoticeEventHandler _changePlayerPhotoHandler = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            performChangePlayerAvatar();
        }
    };

    @NonNull
    private final NoticeEventHandler _viewFeedbackHandler = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            getPlayerNavigationManager().navigateToFeedbackScreen();
        }
    };

    @NonNull
    private final NoticeEventHandler _viewHomeScreenHandler = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            getPlayerNavigationManager().navigateToHomeScreen();
        }
    };

    @NonNull
    private final NoticeEventHandler _viewPlayerOrdersHandler = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            getPlayerNavigationManager().navigateToPlayerOrders();
        }
    };

    @NonNull
    private final NoticeEventHandler _viewPlayerSettingsHandler = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            getPlayerNavigationManager().navigateToPlayerSettings();
        }
    };

    private void performChangePlayerAvatar() {

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
                    final String message =
                        getMessageManager().getMessage(R.string.message_photo_changing_in_progress);
                    getMessageManager().showProgressMessage(null, message);
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
                                final String photoUri =
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
                            return proSportApi.changePlayerAvatar(token, avatar);
                        }
                    }, true);
                }
            })
            .observeOn(rxManager.getUIScheduler())
            .subscribe(new Action1<UserEntity>() {
                @Override
                public void call(final UserEntity userEntity) {
                    Contracts.requireMainThread();

                    displayPlayer(userEntity);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Contracts.requireMainThread();

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
                    getMessageManager().dismissProgressMessage();
                }
            });
    }
}
