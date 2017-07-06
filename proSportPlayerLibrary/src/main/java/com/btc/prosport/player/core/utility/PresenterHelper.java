package com.btc.prosport.player.core.utility;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import rx.functions.Action1;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.dialer.DialerManager;
import com.btc.common.control.manager.message.MessageManager;
import com.btc.common.control.manager.message.UserActionReaction;
import com.btc.common.control.manager.rx.RxManager;
import com.btc.common.extension.exception.InsufficientPermissionException;
import com.btc.prosport.core.manager.navigation.ProSportNavigationManager;
import com.btc.prosport.player.R;

@Accessors(prefix = "_")
public final class PresenterHelper {
    private static final String _LOG_TAG = ConstantBuilder.logTag(PresenterHelper.class);

    public PresenterHelper(
        @NonNull final DialerManager dialerManager,
        @NonNull final MessageManager messageManager,
        @NonNull final ProSportNavigationManager proSportNavigationManager,
        @NonNull final RxManager rxManager) {
        Contracts.requireNonNull(dialerManager, "dialerManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(proSportNavigationManager, "proSportNavigationManager == null");
        Contracts.requireNonNull(rxManager, "rxManager == null");

        _dialerManager = dialerManager;
        _messageManager = messageManager;
        _proSportNavigationManager = proSportNavigationManager;
        _rxManager = rxManager;
    }

    public void performPhoneCall(@NonNull final Uri phoneUri) {
        Contracts.requireNonNull(phoneUri, "phoneUri == null");

        getDialerManager()
            .performPhoneCallWithPermissions(phoneUri, true)
            .observeOn(getRxManager().getUIScheduler())
            .subscribe(new Action1<Boolean>() {
                @Override
                public void call(final Boolean success) {
                    Contracts.requireMainThread();

                    if (!success) {
                        getMessageManager().showInfoMessage(R.string
                                                                .message_error_dialer_not_found);
                    }
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    if (error instanceof InsufficientPermissionException) {
                        getMessageManager()
                            .showActionMessage(
                                R.string
                                    .message_error_fail_perform_phone_call_insufficient_permission,
                                R.string.message_manager_fix_error)
                            .subscribe(new Action1<UserActionReaction>() {
                                @Override
                                public void call(final UserActionReaction userActionReaction) {
                                    if (userActionReaction == UserActionReaction.PERFORM) {
                                        final val navigationManager =
                                            getProSportNavigationManager();
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

                    Log.w(_LOG_TAG, "Failed to perform sport complex phone call.", error);
                }
            });
    }

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final DialerManager _dialerManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final MessageManager _messageManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ProSportNavigationManager _proSportNavigationManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final RxManager _rxManager;
}
