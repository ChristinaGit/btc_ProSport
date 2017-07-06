package com.btc.prosport.player.screen.activity.playerSettings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.event.Events;
import com.btc.common.event.generic.Event;
import com.btc.common.event.generic.EventHandler;
import com.btc.common.event.generic.ManagedEvent;
import com.btc.common.event.notice.ManagedNoticeEvent;
import com.btc.common.event.notice.NoticeEvent;
import com.btc.common.mvp.presenter.Presenter;
import com.btc.prosport.api.model.City;
import com.btc.prosport.api.model.User;
import com.btc.prosport.core.eventArgs.ChangeUserCityEventArgs;
import com.btc.prosport.core.eventArgs.ChangeUserEmailEventArgs;
import com.btc.prosport.core.eventArgs.ChangeUserFirstNameEventArgs;
import com.btc.prosport.core.eventArgs.ChangeUserLastNameEventArgs;
import com.btc.prosport.core.eventArgs.ChangeUserPhoneNumberEventArgs;
import com.btc.prosport.core.eventArgs.LogoutEventArgs;
import com.btc.prosport.core.manager.account.AccountManager;
import com.btc.prosport.core.manager.account.ProSportAccount;
import com.btc.prosport.player.R;
import com.btc.prosport.player.di.qualifier.PresenterNames;
import com.btc.prosport.player.screen.activity.BasePlayerActivity;
import com.btc.prosport.screen.ProSportSettingsScreen;
import com.btc.prosport.screen.fragment.settings.UserSettingsFragment;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

@Accessors(prefix = "_")
public class PlayerSettingsActivity extends BasePlayerActivity implements ProSportSettingsScreen {
    private static final String _LOG_TAG = ConstantBuilder.logTag(PlayerSettingsActivity.class);

    @NonNull
    public static Intent getIntent(@NonNull final Context context) {
        Contracts.requireNonNull(context, "context == null");

        return new Intent(context, PlayerSettingsActivity.class);
    }

    public static void start(@NonNull final Context context) {
        Contracts.requireNonNull(context, "context == null");

        context.startActivity(getIntent(context));
    }

    @CallSuper
    @Override
    public void bindViews() {
        super.bindViews();

        _toolbarView = (Toolbar) findViewById(R.id.toolbar);
        _loadingView = (ProgressBar) findViewById(R.id.loading);
        _settingsRequireAuthorisationErrorView = findViewById(R.id.settings_require_authorisation);
        _contentView = findViewById(R.id.player_settings_view);
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
    public void disableLoading() {
        if (_loadingView != null && _loadingView.getVisibility() == View.VISIBLE) {
            _loadingView.setVisibility(View.GONE);
        }
        final val fragment = getSettingsFragment();
        if (fragment != null) {
            fragment.displayUser(getUser());
        }
    }

    @Override
    public void displayCities(@Nullable final List<? extends City> cities) {
        if (_loadingView != null && _loadingView.getVisibility() == View.VISIBLE) {
            _loadingView.setVisibility(View.GONE);
        }
        final val fragment = getSettingsFragment();
        if (fragment != null) {
            fragment.displayCities(cities);
        }
    }

    @Override
    public void displayLoading() {
        if (_loadingView != null && _loadingView.getVisibility() == View.GONE) {
            _loadingView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void displayPlayer(@Nullable final User user) {
        _user = user;
        if (_loadingView != null && _loadingView.getVisibility() == View.VISIBLE) {
            _loadingView.setVisibility(View.GONE);
        }
        final val fragment = getSettingsFragment();
        if (fragment != null) {
            fragment.displayUser(user);
        }
    }

    @Override
    public void displayRemoveAccountResult(final boolean success) {
        if (success) {
            onBackPressed();
        }
    }

    @Override
    public final void displayRequireAuthorizationError() {
        if (_loadingView != null && _loadingView.getVisibility() == View.VISIBLE) {
            _loadingView.setVisibility(View.GONE);
        }
        if (_settingsRequireAuthorisationErrorView != null) {
            _settingsRequireAuthorisationErrorView.setVisibility(View.VISIBLE);
        }
        if (_contentView != null && _contentView.getVisibility() == View.VISIBLE) {
            _contentView.setVisibility(View.GONE);
        }
        _canTryLoadUser = false;
    }

    @NonNull
    @Override
    public Event<ChangeUserCityEventArgs> getChangeCityEvent() {
        return _changeCityEvent;
    }

    @Override
    @NonNull
    public Event<ChangeUserEmailEventArgs> getChangeEmailEvent() {
        return _changeEmailEvent;
    }

    @NonNull
    @Override
    public Event<ChangeUserFirstNameEventArgs> getChangeFirstNameEvent() {
        return _changeFirstNameEvent;
    }

    @NonNull
    @Override
    public Event<ChangeUserLastNameEventArgs> getChangeLastNameEvent() {
        return _changeLastNameEvent;
    }

    @Override
    @NonNull
    public Event<ChangeUserPhoneNumberEventArgs> getChangePhoneNumberEvent() {
        return _changePhoneNumberEvent;
    }

    @NonNull
    @Override
    public final Event<LogoutEventArgs> getLogoutEvent() {
        return _logoutEvent;
    }

    @NonNull
    @Override
    public NoticeEvent getViewCitiesEvent() {
        return _viewCitiesEvent;
    }

    @NonNull
    @Override
    public NoticeEvent getViewUserEvent() {
        return _viewPlayerEvent;
    }

    @CallSuper
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        boolean consumed = false;

        final int itemId = item.getItemId();
        if (android.R.id.home == itemId) {
            onBackPressed();

            consumed = true;
        } else {
            consumed = super.onOptionsItemSelected(item);
        }

        return consumed;
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
    @Override
    protected void onPause() {
        super.onPause();

        final val settingsFragment = getSettingsFragment();
        if (settingsFragment != null) {
            settingsFragment.getChangeCityEvent().removeHandler(_changeCityHandler);
            settingsFragment.getChangeFirstNameEvent().removeHandler(_changeFirstNameHandler);
            settingsFragment.getChangeLastNameEvent().removeHandler(_changeLastNameHandler);
            settingsFragment.getChangeEmailEvent().removeHandler(_changeEmailHandler);
            settingsFragment.getChangePhoneNumberEvent().removeHandler(_changePhoneNumberHandler);
            settingsFragment.getLogoutEvent().removeHandler(_logoutHandler);
        }
    }

    @CallSuper
    @Override
    protected void onResume() {
        super.onResume();

        if (_canTryLoadUser) {
            _viewPlayerEvent.rise();
            _viewCitiesEvent.rise();
        }

        final val settingsFragment = getSettingsFragment();
        if (settingsFragment != null) {
            final val accountManager = getAccountManager();
            if (accountManager != null) {
                settingsFragment.setAccounts(accountManager.getAccounts());
                settingsFragment.notifyAccountsChanged();
            }

            settingsFragment.getChangeCityEvent().addHandler(_changeCityHandler);
            settingsFragment.getChangeFirstNameEvent().addHandler(_changeFirstNameHandler);
            settingsFragment.getChangeEmailEvent().addHandler(_changeEmailHandler);
            settingsFragment.getChangePhoneNumberEvent().addHandler(_changePhoneNumberHandler);
            settingsFragment.getChangeLastNameEvent().addHandler(_changeLastNameHandler);
            settingsFragment.getLogoutEvent().addHandler(_logoutHandler);
        }
    }

    @CallSuper
    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_player_settings);

        bindViews();

        if (_toolbarView != null) {
            setSupportActionBar(_toolbarView);
        }
        final val actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
        }
        _canTryLoadUser = true;
    }

    @CallSuper
    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbindViews();
    }

    @Getter(AccessLevel.PROTECTED)
    @Inject
    @Nullable
    /*package-private*/ AccountManager<ProSportAccount> _accountManager;

    @Named(PresenterNames.SETTINGS)
    @Inject
    @Nullable
    @Getter(AccessLevel.PROTECTED)
    Presenter<ProSportSettingsScreen> _presenter;

    @NonNull
    private final ManagedEvent<ChangeUserCityEventArgs> _changeCityEvent = Events.createEvent();

    @NonNull
    private final EventHandler<ChangeUserCityEventArgs> _changeCityHandler =
        new EventHandler<ChangeUserCityEventArgs>() {
            @Override
            public void onEvent(@NonNull final ChangeUserCityEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                _changeCityEvent.rise(eventArgs);
            }
        };

    @NonNull
    private final ManagedEvent<ChangeUserEmailEventArgs> _changeEmailEvent = Events.createEvent();

    @NonNull
    private final EventHandler<ChangeUserEmailEventArgs> _changeEmailHandler =
        new EventHandler<ChangeUserEmailEventArgs>() {
            @Override
            public void onEvent(@NonNull final ChangeUserEmailEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                _changeEmailEvent.rise(eventArgs);
            }
        };

    @NonNull
    private final ManagedEvent<ChangeUserFirstNameEventArgs> _changeFirstNameEvent =
        Events.createEvent();

    @NonNull
    private final EventHandler<ChangeUserFirstNameEventArgs> _changeFirstNameHandler =
        new EventHandler<ChangeUserFirstNameEventArgs>() {
            @Override
            public void onEvent(@NonNull final ChangeUserFirstNameEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                _changeFirstNameEvent.rise(eventArgs);
            }
        };

    @NonNull
    private final ManagedEvent<ChangeUserLastNameEventArgs> _changeLastNameEvent =
        Events.createEvent();

    @NonNull
    private final EventHandler<ChangeUserLastNameEventArgs> _changeLastNameHandler =
        new EventHandler<ChangeUserLastNameEventArgs>() {
            @Override
            public void onEvent(@NonNull final ChangeUserLastNameEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                _changeLastNameEvent.rise(eventArgs);
            }
        };

    @NonNull
    private final ManagedEvent<ChangeUserPhoneNumberEventArgs> _changePhoneNumberEvent =
        Events.createEvent();

    @NonNull
    private final EventHandler<ChangeUserPhoneNumberEventArgs> _changePhoneNumberHandler =
        new EventHandler<ChangeUserPhoneNumberEventArgs>() {
            @Override
            public void onEvent(@NonNull final ChangeUserPhoneNumberEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                _changePhoneNumberEvent.rise(eventArgs);
            }
        };

    @NonNull
    private final ManagedEvent<LogoutEventArgs> _logoutEvent = Events.createEvent();

    @NonNull
    private final EventHandler<LogoutEventArgs> _logoutHandler =
        new EventHandler<LogoutEventArgs>() {
            @Override
            public void onEvent(@NonNull final LogoutEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                final val accountManager = getAccountManager();
                if (accountManager != null) {
                    _logoutEvent.rise(eventArgs);
                }
            }
        };

    @NonNull
    private final ManagedNoticeEvent _viewCitiesEvent = Events.createNoticeEvent();

    @NonNull
    private final ManagedNoticeEvent _viewPlayerEvent = Events.createNoticeEvent();

    private boolean _canTryLoadUser;

    @Nullable
    private View _contentView;

    @Nullable
    private ProgressBar _loadingView;

    @Nullable
    private View _settingsRequireAuthorisationErrorView;

    @Nullable
    private Toolbar _toolbarView;

    @Nullable
    @Getter(AccessLevel.PROTECTED)
    private User _user;

    @Nullable
    private UserSettingsFragment getSettingsFragment() {
        final val manager = getFragmentManager();
        if (manager != null) {
            return (UserSettingsFragment) manager.findFragmentById(R.id.player_settings);
        }
        return null;
    }
}
