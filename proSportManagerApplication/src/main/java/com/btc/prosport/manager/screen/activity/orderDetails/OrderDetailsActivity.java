package com.btc.prosport.manager.screen.activity.orderDetails;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import butterknife.BindView;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.mvp.presenter.Presenter;
import com.btc.prosport.api.ProSportContract;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.di.qualifier.PresenterNames;
import com.btc.prosport.manager.screen.OrderDetailsScreen;
import com.btc.prosport.manager.screen.activity.BaseManagerNavigationActivity;
import com.btc.prosport.manager.screen.fragment.OrderViewerFragment;

import org.parceler.Parcels;

import javax.inject.Inject;
import javax.inject.Named;

@Accessors(prefix = "_")
public class OrderDetailsActivity extends BaseManagerNavigationActivity
    implements OrderDetailsScreen {
    private static final String _LOG_TAG = ConstantBuilder.logTag(OrderDetailsActivity.class);

    public static final String _KEY_SAVED_STATE =
        ConstantBuilder.savedStateKey(OrderDetailsActivity.class, "saved_state");

    public static void startView(@NonNull final Context context, @NonNull final Uri orderUri) {
        Contracts.requireNonNull(context, "context == null");
        Contracts.requireNonNull(orderUri, "orderUri == null");

        context.startActivity(getViewIntent(context, orderUri));
    }

    @NonNull
    public static Intent getViewIntent(
        @NonNull final Context context, @NonNull final Uri orderUri) {
        Contracts.requireNonNull(context, "context == null");
        Contracts.requireNonNull(orderUri, "orderUri == null");

        final val intent = new Intent(context, OrderDetailsActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(orderUri);

        return intent;
    }

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
            final val orderId = _state.getOrderId();

            setContentView(R.layout.activity_order_details);
            bindViews();

            setSupportActionBar(_toolbarView);

            final val actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowTitleEnabled(true);
            }

            if (orderId != null) {
                setTitle(getString(R.string.order_viewer_label_format, (long) orderId));
            } else {
                setTitle(null);
            }

            final val fragmentManager = getSupportFragmentManager();
            final val orderViewerFragment =
                (OrderViewerFragment) fragmentManager.findFragmentById(R.id.order_viewer_fragment);
            orderViewerFragment.setOrderId(orderId);
        } else {
            finish();
        }
    }

    @Nullable
    @CallSuper
    protected OrderDetailsState onHandleIntent(@NonNull final Intent intent) {
        Contracts.requireNonNull(intent, "intent == null");

        final OrderDetailsState state;

        final val action = getIntent().getAction();
        switch (action) {
            case Intent.ACTION_VIEW: {
                state = onHandleViewIntent(intent);
                break;
            }
            default: {
                state = null;
                break;
            }
        }

        return state;
    }

    @Nullable
    @CallSuper
    protected OrderDetailsState onHandleSavedState(
        @NonNull final Bundle savedInstanceState) {
        OrderDetailsState state = null;

        if (savedInstanceState.containsKey(_KEY_SAVED_STATE)) {
            state = Parcels.unwrap(savedInstanceState.getParcelable(_KEY_SAVED_STATE));
        }
        return state;
    }

    @Nullable
    @CallSuper
    protected OrderDetailsState onHandleViewIntent(@NonNull final Intent intent) {
        Contracts.requireNonNull(intent, "intent == null");

        final OrderDetailsState state;

        final val data = intent.getData();
        final int code = ProSportContract.getCode(data);

        if (code == ProSportContract.CODE_ORDER) {
            state = new OrderDetailsState();
            state.setOrderId(ContentUris.parseId(data));
        } else {
            // TODO: 02.03.2017 handle incorrect data.
            state = null;
        }

        return state;
    }

    @CallSuper
    @Override
    protected void onInjectMembers() {
        super.onInjectMembers();
        getManagerScreenComponent().inject(this);

        final val presenter = getPresenter();
        if (presenter != null) {
            presenter.bindScreen(this);
        }
    }

    @CallSuper
    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        if (outState != null) {
            if (_state == null) {
                _state = new OrderDetailsState();
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

    @Named(PresenterNames.ORDER_DETAILS)
    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Nullable
    /*package-private*/ Presenter<OrderDetailsScreen> _presenter;

    @BindView(R.id.toolbar)
    @Nullable
    /*package-private*/ Toolbar _toolbarView;

    @Nullable
    private OrderDetailsState _state;
}
