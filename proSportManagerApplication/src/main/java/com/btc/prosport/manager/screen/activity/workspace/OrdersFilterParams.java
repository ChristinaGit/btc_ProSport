package com.btc.prosport.manager.screen.activity.workspace;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.contract.Contracts;
import com.btc.prosport.manager.core.OrderStatusFilter;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.util.Objects;

@Parcel(Parcel.Serialization.BEAN)
@Accessors(prefix = "_")
public final class OrdersFilterParams {
    public OrdersFilterParams() {
        this(OrderStatusFilter.ALL, null, null, null);
    }

    @ParcelConstructor
    public OrdersFilterParams(
        @NonNull final OrderStatusFilter orderStatusFilter,
        @Nullable final Long sportComplexId,
        @Nullable final Long playgroundId,
        @Nullable final String searchQuery) {
        Contracts.requireNonNull(orderStatusFilter, "orderStateFilter == null");

        _orderStatusFilter = orderStatusFilter;
        _sportComplexId = sportComplexId;
        _playgroundId = playgroundId;
        _searchQuery = searchQuery;
    }

    @NonNull
    public OrderStatusFilter getOrderStatusFilter() {
        return _orderStatusFilter;
    }

    @Nullable
    public Long getPlaygroundId() {
        return _playgroundId;
    }

    @Nullable
    public String getSearchQuery() {
        return _searchQuery;
    }

    @Nullable
    public Long getSportComplexId() {
        return _sportComplexId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOrderStatusFilter(),
                            getPlaygroundId(),
                            getSearchQuery(),
                            getSportComplexId());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        val that = (OrdersFilterParams) o;
        return getOrderStatusFilter() == that.getOrderStatusFilter() &&
               Objects.equals(getPlaygroundId(), that.getPlaygroundId()) &&
               Objects.equals(getSearchQuery(), that.getSearchQuery()) &&
               Objects.equals(getSportComplexId(), that.getSportComplexId());
    }

    @NonNull
    public OrdersFilterParams withOrderPlaygroundFilter(
        @Nullable final Long playgroundId) {
        return Objects.equals(getPlaygroundId(), playgroundId)
               ? this
               : new OrdersFilterParams(getOrderStatusFilter(),
                                        getSportComplexId(),
                                        playgroundId,
                                        null);
    }

    @NonNull
    public OrdersFilterParams withOrderSportComplexFilter(
        @Nullable final Long sportComplexId) {
        return Objects.equals(getSportComplexId(), sportComplexId)
               ? this
               : new OrdersFilterParams(getOrderStatusFilter(),
                                        sportComplexId,
                                        getPlaygroundId(),
                                        null);
    }

    @NonNull
    public OrdersFilterParams withOrderStateFilter(
        @NonNull final OrderStatusFilter orderStatusFilter) {
        Contracts.requireNonNull(orderStatusFilter, "orderStateFilter == null");

        return getOrderStatusFilter() == orderStatusFilter
               ? this
               : new OrdersFilterParams(orderStatusFilter,
                                        getSportComplexId(),
                                        getPlaygroundId(),
                                        null);
    }

    @NonNull
    public OrdersFilterParams withSearchQueryFilter(
        @Nullable final String searchQuery) {
        return Objects.equals(getSearchQuery(), searchQuery)
               ? this
               : new OrdersFilterParams(getOrderStatusFilter(),
                                        getSportComplexId(),
                                        getPlaygroundId(),
                                        searchQuery);
    }

    @NonNull
    private final OrderStatusFilter _orderStatusFilter;

    @Nullable
    private final Long _playgroundId;

    @Nullable
    private final String _searchQuery;

    @Nullable
    private final Long _sportComplexId;
}
