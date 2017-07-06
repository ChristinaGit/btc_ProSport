package com.btc.prosport.player.screen.activity.ordersViewer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.mvp.presenter.Presenter;
import com.btc.prosport.api.ProSportContract;
import com.btc.prosport.player.R;
import com.btc.prosport.player.di.qualifier.PresenterNames;
import com.btc.prosport.player.screen.OrdersViewerScreen;
import com.btc.prosport.player.screen.activity.BasePlayerNavigationActivity;

import org.parceler.Parcels;

import javax.inject.Inject;
import javax.inject.Named;

@Accessors(prefix = "_")
public class OrdersViewerActivity extends BasePlayerNavigationActivity
    implements OrdersViewerScreen {
    private static final String _KEY_SAVED_STATE =
        ConstantBuilder.savedStateKey(OrdersViewerState.class, "saved_state");

    @NonNull
    public static Intent getIntent(@NonNull final Context context) {
        Contracts.requireNonNull(context, "context == null");

        return new Intent(context, OrdersViewerActivity.class);
    }

    public static void start(@NonNull final Context context) {
        Contracts.requireNonNull(context, "context == null");

        context.startActivity(getIntent(context));
    }

    @Override
    @CallSuper
    public void bindViews() {
        super.bindViews();

        _toolbarView = (Toolbar) findViewById(R.id.toolbar);
        _appbarView = (AppBarLayout) findViewById(R.id.appbar);
    }

    @CallSuper
    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        if (outState != null) {
            if (_state == null) {
                _state = new OrdersViewerState();
            }

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
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            _state = onHandleSavedState(savedInstanceState);
        }

        if (_state == null) {
            _state = onHandleIntent(getIntent());
        }

        if (_state != null) {
            setContentView(R.layout.activity_orders_viewer);
            bindViews();

            if (_toolbarView != null) {
                setSupportActionBar(_toolbarView);
            }

            final val actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowTitleEnabled(true);
            }
        } else {
            finish();
        }
    }

    @CallSuper
    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbindViews();
    }

    @Nullable
    @CallSuper
    protected OrdersViewerState onHandleIntent(@NonNull final Intent intent) {
        Contracts.requireNonNull(intent, "intent == null");

        final OrdersViewerState state;

        final val action = getIntent().getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            state = onHandleViewIntent(intent);
        } else {
            state = new OrdersViewerState();
        }

        return state;
    }

    @Nullable
    @CallSuper
    protected OrdersViewerState onHandleSavedState(@NonNull final Bundle savedInstanceState) {
        OrdersViewerState state = null;

        if (savedInstanceState.containsKey(_KEY_SAVED_STATE)) {
            state = Parcels.unwrap(savedInstanceState.getParcelable(_KEY_SAVED_STATE));
        }

        return state;
    }

    @Nullable
    @CallSuper
    protected OrdersViewerState onHandleViewIntent(@NonNull final Intent intent) {
        Contracts.requireNonNull(intent, "intent == null");

        final OrdersViewerState state;

        final val data = intent.getData();
        final int code = ProSportContract.getCode(data);

        if (code == ProSportContract.CODE_ORDER_ALL) {
            state = new OrdersViewerState();
        } else {
            // TODO: 02.02.2017 handle incorrect data. (Toast or snackbar, error view)
            state = null;
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

    @Named(PresenterNames.ORDERS_VIEWER)
    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Nullable
    /*package-private*/ Presenter<OrdersViewerScreen> _presenter;

    @Nullable
    private AppBarLayout _appbarView;

    @Nullable
    private OrdersViewerState _state;

    @Nullable
    private Toolbar _toolbarView;
}
