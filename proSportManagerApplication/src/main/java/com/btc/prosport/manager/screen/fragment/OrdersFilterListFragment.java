package com.btc.prosport.manager.screen.fragment;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.contract.Contracts;
import com.btc.common.event.Events;
import com.btc.common.event.generic.Event;
import com.btc.common.event.generic.EventHandler;
import com.btc.common.event.generic.ManagedEvent;
import com.btc.prosport.api.model.SportComplexReport;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.FilterType;
import com.btc.prosport.manager.core.OrderStatusFilter;
import com.btc.prosport.manager.core.adapter.orderFilter.OrderFiltersAdapter;
import com.btc.prosport.manager.core.adapter.orderFilter.item.FilterGroupItem;
import com.btc.prosport.manager.core.adapter.orderFilter.item.FilterItem;
import com.btc.prosport.manager.core.eventArgs.OrdersFilterEventArgs;

import java.util.ArrayList;
import java.util.List;

@Accessors(prefix = "_")
public final class OrdersFilterListFragment extends BaseManagerFragment {
    @Override
    public void bindViews(@NonNull final View source) {
        super.bindViews(source);

        _filterList = (RecyclerView) source.findViewById(R.id.filter_list);
    }

    @Nullable
    @Override
    public View onCreateView(
        final LayoutInflater inflater,
        @Nullable final ViewGroup container,
        @Nullable final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        setHasOptionsMenu(true);

        val view = inflater.inflate(R.layout.fragment_orders_filter_list, container, false);

        bindViews(view);

        if (_filterList != null) {
            _ordersFilterAdapter = new OrderFiltersAdapter();
            // TODO: 25.05.2017 Fix spacing for list with spans
            //            _filterList.addItemDecoration(SpacingItemDecoration
            //                                              .builder()
            //                                              .collapseBorders()
            //                                              .setSpacing(50)
            //                                              .build());
            _filterList.setAdapter(_ordersFilterAdapter);
        }

        onFiltersUpdated();

        if (_ordersFilterAdapter != null) {
            _ordersFilterAdapter.getOrdersFilterEvent().addHandler(_ordersFilterHandler);
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (_ordersFilterAdapter != null) {
            _ordersFilterAdapter.getOrdersFilterEvent().removeHandler(_ordersFilterHandler);
        }
    }

    public void displaySportComplexesLoading() {
        // TODO: 17.05.2017  Add footer.
    }

    @NonNull
    public Event<OrdersFilterEventArgs> getOrdersFilterEvent() {
        return _ordersFilterEvent;
    }

    public void setSportComplexes(@Nullable final List<SportComplexReport> sportComplexes) {
        _sportComplexes = sportComplexes;

        onFiltersUpdated();
    }

    @CallSuper
    protected void onFiltersUpdated() {
        val ordersFilterAdapter = getOrdersFilterAdapter();

        if (ordersFilterAdapter != null) {
            ordersFilterAdapter.setOrderFilterGroupItems(buildFilters());
            ordersFilterAdapter.notifyDataSetChanged();
        }
    }

    @NonNull
    private final ManagedEvent<OrdersFilterEventArgs> _ordersFilterEvent = Events.createEvent();

    @Nullable
    private RecyclerView _filterList;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private OrderFiltersAdapter _ordersFilterAdapter;

    private final EventHandler<OrdersFilterEventArgs> _ordersFilterHandler =
        new EventHandler<OrdersFilterEventArgs>() {
            @Override
            public void onEvent(@NonNull final OrdersFilterEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                val ordersFilterAdapter = getOrdersFilterAdapter();
                if (eventArgs.getFilterType() != FilterType.STATUS && ordersFilterAdapter != null) {
                    ordersFilterAdapter.removeAdditionalFilters();
                }

                _ordersFilterEvent.rise(eventArgs);
            }
        };

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private List<SportComplexReport> _sportComplexes;

    @NonNull
    private List<FilterGroupItem> buildFilters() {
        val orderFilterGroupItems = new ArrayList<FilterGroupItem>(FilterType.values().length);

        for (final val filterType : FilterType.values()) {
            switch (filterType) {
                case STATUS: {
                    val orderStatusFilters = OrderStatusFilter.values();
                    val statusFilterItems = new ArrayList<FilterItem>(orderStatusFilters.length);

                    for (final val orderStatusFilter : orderStatusFilters) {
                        @StringRes final int name;
                        @DrawableRes final int icon;
                        switch (orderStatusFilter) {
                            case ALL:
                                continue;
                            case CONFIRMED:
                                name = R.string.order_filter_confirmed_short;
                                icon = R.drawable.ic_material_done;
                                break;
                            case NOT_CONFIRMED:
                                name = R.string.order_filter_not_confirmed_short;
                                icon = R.drawable.ic_material_flag;
                                break;
                            case CANCELED:
                                name = R.string.orders_filter_canceled;
                                icon = R.drawable.ic_material_clear;
                                break;
                            default:
                                throw new RuntimeException("Unknown status");
                        }

                        statusFilterItems.add(new FilterItem(orderStatusFilter.ordinal(),
                                                             getString(name),
                                                             icon));
                    }

                    val statusTitle = getString(R.string.order_filter_status_title);
                    orderFilterGroupItems.add(new FilterGroupItem(filterType,
                                                                  statusTitle,
                                                                  statusFilterItems));
                    break;
                }
                case SPORT_COMPLEX: {
                    val sportComplexes = getSportComplexes();
                    if (sportComplexes != null) {
                        val sportComplexFilterItems =
                            new ArrayList<FilterItem>(sportComplexes.size());

                        for (final val sportComplex : sportComplexes) {
                            val name = sportComplex.getName();
                            if (name != null) {
                                sportComplexFilterItems.add(new FilterItem(sportComplex.getId(),
                                                                           name,
                                                                           R.drawable.ic_stadium));
                            }
                        }

                        val sportComplex = getString(R.string.order_filter_sport_complex_title);
                        if (!sportComplexFilterItems.isEmpty()) {
                            orderFilterGroupItems.add(new FilterGroupItem(filterType,
                                                                          sportComplex,
                                                                          sportComplexFilterItems));
                        }
                    }
                    break;
                }
                case PLAYGROUND: {
                    val sportComplexes = getSportComplexes();
                    if (sportComplexes != null) {
                        for (final val sportComplex : sportComplexes) {
                            val playgrounds = sportComplex.getPlaygrounds();
                            if (playgrounds != null) {
                                val playgroundFilterItems =
                                    new ArrayList<FilterItem>(playgrounds.size());
                                for (final val playground : playgrounds) {
                                    val playgroundName = playground.getName();
                                    if (playgroundName != null) {
                                        playgroundFilterItems.add(new FilterItem(playground.getId(),
                                                                                 playgroundName,
                                                                                 R.drawable
                                                                                     .ic_football_field));
                                    }
                                }
                                final String playgroundTitle;
                                if (sportComplexes.size() == 1) {
                                    playgroundTitle =
                                        getString(R.string.order_filter_playground_title);
                                } else {
                                    playgroundTitle = sportComplex.getName();
                                }
                                if (playgroundTitle != null && !playgroundFilterItems.isEmpty()) {
                                    orderFilterGroupItems.add(new FilterGroupItem(filterType,
                                                                                  playgroundTitle,
                                                                                  playgroundFilterItems));
                                }
                            }
                        }
                    }
                    break;
                }
                default:
                    throw new RuntimeException("Unknown filter type");
            }
        }

        return orderFilterGroupItems;
    }
}
