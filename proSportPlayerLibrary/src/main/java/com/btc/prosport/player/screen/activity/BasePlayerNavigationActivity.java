package com.btc.prosport.player.screen.activity;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.event.Events;
import com.btc.common.event.notice.ManagedNoticeEvent;
import com.btc.common.event.notice.NoticeEvent;
import com.btc.common.extension.delegate.loading.FadeVisibilityHandler;
import com.btc.common.extension.delegate.loading.LoadingViewDelegate;
import com.btc.common.extension.delegate.loading.ProgressVisibilityHandler;
import com.btc.common.extension.view.ContentLoaderProgressBar;
import com.btc.prosport.api.model.User;
import com.btc.prosport.core.utility.UserUtils;
import com.btc.prosport.player.R;
import com.btc.prosport.player.screen.PlayerNavigationScreen;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

@Accessors(prefix = "_")
public abstract class BasePlayerNavigationActivity extends BasePlayerAccountActivity
    implements NavigationView.OnNavigationItemSelectedListener, PlayerNavigationScreen {
    private static final String _LOG_TAG =
        ConstantBuilder.logTag(BasePlayerNavigationActivity.class);

    @NonNull
    @Override
    public final NoticeEvent getPlayerLoginEvent() {
        return _loginPlayerEvent;
    }

    @NonNull
    @Override
    public final NoticeEvent getViewPlayerEvent() {
        return _viewPlayerEvent;
    }

    @NonNull
    @Override
    public final NoticeEvent getViewHomeScreenEvent() {
        return _viewHomeScreenEvent;
    }

    @NonNull
    @Override
    public final NoticeEvent getViewPlayerOrdersEvent() {
        return _viewPlayerOrdersEvent;
    }

    @NonNull
    @Override
    public final NoticeEvent getViewPlayerSettingsEvent() {
        return _viewPlayerSettingsEvent;
    }

    @NonNull
    @Override
    public final NoticeEvent getViewFeedbackEvent() {
        return _viewFeedbackEvent;
    }

    @NonNull
    @Override
    public NoticeEvent getChangePlayerAvatarEvent() {
        return _changePlayerAvatarEvent;
    }

    @NonNull
    private final View.OnClickListener _onAvatarViewClick = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            _changePlayerAvatarEvent.rise();
        }
    };

    @NonNull
    private final ManagedNoticeEvent _changePlayerAvatarEvent = Events.createNoticeEvent();

    @CallSuper
    @Override
    public void displayNoAccountsError() {
        super.displayNoAccountsError();

        final val loadingViewDelegate = getPlayerLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showContent(false);
        }
    }

    @CallSuper
    @Override
    public void displayPlayer(@Nullable final User player) {
        super.displayPlayer(player);

        setupPlayerAvatar();
        setupPlayerInfo();

        final val loadingViewDelegate = getPlayerLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showContent(player != null);
        }
    }

    @CallSuper
    @Override
    public void displayPlayerLoading() {
        super.displayPlayerLoading();

        final val loadingViewDelegate = getPlayerLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showLoading();
        }
    }

    @CallSuper
    @Override
    public void displayPlayerLoadingError() {
        super.displayPlayerLoadingError();

        final val loadingViewDelegate = getPlayerLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showError();
        }
    }

    @CallSuper
    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        final boolean consumed;

        final int itemId = item.getItemId();
        if (itemId == R.id.action_settings) {
            _viewPlayerSettingsEvent.rise();

            consumed = true;
        } else if (itemId == R.id.action_home) {
            if (!onNavigateToHomeScreen()) {
                _viewHomeScreenEvent.rise();
            }

            consumed = true;
        } else if (itemId == R.id.action_orders) {
            _viewPlayerOrdersEvent.rise();

            consumed = true;
        } else if (itemId == R.id.action_feedback) {
            _viewFeedbackEvent.rise();

            consumed = true;
        } else {
            consumed = false;
        }

        if (consumed) {
            if (_navigationContainerView != null) {
                _navigationContainerView.closeDrawers();
            }
        }

        return consumed;
    }

    @CallSuper
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final boolean consumed;

        if (_actionBarDrawerToggle != null && _actionBarDrawerToggle.onOptionsItemSelected(item)) {
            consumed = true;
        } else {
            consumed = super.onOptionsItemSelected(item);
        }

        return consumed;
    }

    protected boolean onNavigateToHomeScreen() {
        return false;
    }

    @CallSuper
    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (_actionBarDrawerToggle != null) {
            _actionBarDrawerToggle.syncState();
        }
    }

    @CallSuper
    @Override
    public void setContentView(@LayoutRes final int layoutResID) {
        super.setContentView(R.layout.activity_player_navigation);

        findNavigationViews();

        if (_navigationContainerView != null) {
            final val inflater = getLayoutInflater();
            final val view = inflater.inflate(layoutResID, _navigationContainerView, false);

            setNavigationContentView(view, null);
        }

        initializeNavigationView();
        initializeNavigationContainerView();
    }

    @CallSuper
    @Override
    public void setContentView(final View view) {
        super.setContentView(R.layout.activity_player_navigation);

        findNavigationViews();

        setNavigationContentView(view, null);

        initializeNavigationView();
        initializeNavigationContainerView();
    }

    @CallSuper
    @Override
    public void setContentView(final View view, final ViewGroup.LayoutParams params) {
        super.setContentView(R.layout.activity_player_navigation);

        findNavigationViews();

        setNavigationContentView(view, params);

        initializeNavigationView();
        initializeNavigationContainerView();
    }

    @CallSuper
    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (_actionBarDrawerToggle != null) {
            _actionBarDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @CallSuper
    @Override
    protected void onResume() {
        super.onResume();

        _viewPlayerEvent.rise();
    }

    @NonNull
    private final ManagedNoticeEvent _loginPlayerEvent = Events.createNoticeEvent();

    @NonNull
    private final View.OnClickListener _controlsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            final int i = v.getId();
            if (i == R.id.player_add_account) {
                _loginPlayerEvent.rise();
            }
        }
    };

    @NonNull
    private final ManagedNoticeEvent _viewFeedbackEvent = Events.createNoticeEvent();

    @NonNull
    private final ManagedNoticeEvent _viewHomeScreenEvent = Events.createNoticeEvent();

    @NonNull
    private final ManagedNoticeEvent _viewPlayerEvent = Events.createNoticeEvent();

    @NonNull
    private final ManagedNoticeEvent _viewPlayerOrdersEvent = Events.createNoticeEvent();

    @NonNull
    private final ManagedNoticeEvent _viewPlayerSettingsEvent = Events.createNoticeEvent();

    @Nullable
    private ActionBarDrawerToggle _actionBarDrawerToggle;

    private boolean _hasContentView = false;

    @Nullable
    private DrawerLayout _navigationContainerView;

    @Nullable
    private NavigationView _navigationView;

    @Nullable
    private View _playerAddAccountContainerView;

    @Nullable
    private View _playerAddAccountView;

    @Nullable
    private ImageView _playerAvatarView;

    @Nullable
    private View _playerInfoContainerView;

    @Nullable
    private View _playerLoadErrorView;

    @Nullable
    private ContentLoaderProgressBar _playerLoadingView;

    @Getter(AccessLevel.PRIVATE)
    @Nullable
    private LoadingViewDelegate _playerLoadingViewDelegate;

    @Nullable
    private TextView _playerNameView;

    @Nullable
    private TextView _playerPhoneNumberView;

    private void findNavigationViews() {
        _navigationContainerView = (DrawerLayout) findViewById(R.id.navigation_container);
        _navigationView = (NavigationView) findViewById(R.id.navigation);
    }

    private void initializeNavigationContainerView() {
        if (_navigationContainerView != null) {
            _actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                _navigationContainerView,
                R.string.action_open_drawer,
                R.string.action_close_drawer) {
                @Override
                public void onDrawerOpened(final View drawerView) {
                    super.onDrawerOpened(drawerView);

                    if (getPlayer() == null) {
                        _viewPlayerEvent.rise();
                    }
                }
            };
            _actionBarDrawerToggle.getDrawerArrowDrawable().setColor(Color.WHITE);
            _actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
            _navigationContainerView.addDrawerListener(_actionBarDrawerToggle);
        }
    }

    private void initializeNavigationView() {
        if (_navigationView != null) {
            _navigationView.setNavigationItemSelectedListener(this);

            final int headerCount = _navigationView.getHeaderCount();
            if (headerCount != 0) {
                final val headerView = _navigationView.getHeaderView(0);

                _playerAvatarView = (ImageView) headerView.findViewById(R.id.player_avatar);
                _playerNameView = (TextView) headerView.findViewById(R.id.player_name);
                _playerPhoneNumberView =
                    (TextView) headerView.findViewById(R.id.player_phone_number);
                _playerLoadingView =
                    (ContentLoaderProgressBar) headerView.findViewById(R.id.player_loading);
                _playerInfoContainerView = headerView.findViewById(R.id.player_info_container);
                _playerLoadErrorView = headerView.findViewById(R.id.player_load_error);
                _playerAddAccountContainerView =
                    headerView.findViewById(R.id.player_add_account_container);
                _playerAddAccountView = headerView.findViewById(R.id.player_add_account);

                _playerAddAccountView.setOnClickListener(_controlsClickListener);
                //@formatter:off
                _playerLoadingViewDelegate = LoadingViewDelegate
                    .builder()
                    .setContentView(_playerInfoContainerView)
                    .setLoadingView(_playerLoadingView)
                    .setNoContentView(_playerAddAccountContainerView)
                    .setErrorView(_playerLoadErrorView)
                    .setContentVisibilityHandler(new FadeVisibilityHandler(View.INVISIBLE,
                                                                           R.anim.fade_in_long,
                                                                           FadeVisibilityHandler.NO_ANIMATION))
                    .setLoadingVisibilityHandler(new ProgressVisibilityHandler())
                    .build();
                //@formatter:on

                _playerLoadingViewDelegate.hideAll();

                if (_playerAvatarView != null) {
                    _playerAvatarView.setOnClickListener(_onAvatarViewClick);
                }
            }
        }
    }

    private void setNavigationContentView(
        @NonNull final View view, @Nullable final ViewGroup.LayoutParams params) {
        Contracts.requireNonNull(view, "view == null");

        if (_navigationContainerView != null) {
            if (_hasContentView) {
                _navigationContainerView.removeViewAt(0);
            }

            if (params == null) {
                _navigationContainerView.addView(view, 0);
            } else {
                _navigationContainerView.addView(view, 0, params);
            }

            _hasContentView = true;
        }
    }

    private void setupPlayerAvatar() {
        final val player = getPlayer();

        if (_playerAvatarView != null) {
            final val playerAvatarUri = player == null ? null : player.getAvatarUri();

            Glide
                .with(this)
                .load(playerAvatarUri)
                .asBitmap()
                .error(R.drawable.ic_material_account_circle)
                .fallback(R.drawable.ic_material_account_circle)
                .placeholder(R.drawable.ic_material_account_circle)
                .centerCrop()
                .into(new BitmapImageViewTarget(_playerAvatarView) {
                    @Override
                    protected void setResource(final Bitmap resource) {
                        final val avatar =
                            RoundedBitmapDrawableFactory.create(getResources(), resource);
                        avatar.setCircular(true);
                        getView().setImageDrawable(avatar);
                    }
                });
        }
    }

    private void setupPlayerInfo() {
        final val player = getPlayer();

        if (_playerNameView != null) {
            final val userName = player != null ? UserUtils.getDisplayUserName(player) : null;
            if (!TextUtils.isEmpty(userName)) {
                _playerNameView.setVisibility(View.VISIBLE);
            } else {
                _playerNameView.setVisibility(View.GONE);
            }
            _playerNameView.setText(userName);
        }

        if (_playerPhoneNumberView != null) {
            _playerPhoneNumberView.setText(player != null ? player.getPhoneNumber() : null);
        }
    }
}
