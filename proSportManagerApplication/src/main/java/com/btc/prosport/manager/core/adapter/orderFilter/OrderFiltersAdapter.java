package com.btc.prosport.manager.core.adapter.orderFilter;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.contract.Contracts;
import com.btc.common.event.Events;
import com.btc.common.event.generic.Event;
import com.btc.common.event.generic.ManagedEvent;
import com.btc.common.extension.view.recyclerView.adapter.HierarchyRecyclerViewAdapter;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.adapter.orderFilter.item.FilterGroupItem;
import com.btc.prosport.manager.core.adapter.orderFilter.item.FilterItem;
import com.btc.prosport.manager.core.adapter.orderFilter.viewHolder.FilterGroupViewHolder;
import com.btc.prosport.manager.core.adapter.orderFilter.viewHolder.FilterViewHolder;
import com.btc.prosport.manager.core.eventArgs.OrdersFilterEventArgs;

import java.util.List;

@Accessors(prefix = "_")
public class OrderFiltersAdapter extends HierarchyRecyclerViewAdapter<FilterGroupItem, FilterItem> {
    private static final int FILTERS_COLUMN_COUNT = 3;

    private static final int FILTERS_TITLE_COLUMN_COUNT = 1;

    @NonNull
    @Override
    public FilterItem getGroupChild(
        final int groupParentIndex, final int groupChildIndex) {
        val orderFilterGroupItems = getOrderFilterGroupItems();
        if (orderFilterGroupItems != null) {
            return orderFilterGroupItems
                .get(groupParentIndex)
                .getFilterItems()
                .get(groupChildIndex);
        } else {
            throw new RuntimeException("Filters not found");
        }
    }

    @Override
    public int getGroupChildCountOfGroup(final int groupParentIndex) {
        val orderFilterGroupItems = getOrderFilterGroupItems();
        if (orderFilterGroupItems != null) {
            return orderFilterGroupItems.get(groupParentIndex).getFilterItems().size();
        } else {
            throw new RuntimeException("Filters not found");
        }
    }

    @NonNull
    @Override
    public FilterGroupItem getGroupParent(final int groupParentIndex) {
        val orderFilterGroupItems = getOrderFilterGroupItems();
        if (orderFilterGroupItems != null) {
            return orderFilterGroupItems.get(groupParentIndex);
        } else {
            throw new RuntimeException("Filters not found");
        }
    }

    @Override
    public int getGroupParentCount() {
        final int count;

        val orderFilterGroupItems = getOrderFilterGroupItems();
        if (orderFilterGroupItems != null) {
            count = orderFilterGroupItems.size();
        } else {
            count = 0;
        }

        return count;
    }

    @Override
    protected void onBindGroupChildViewHolder(
        @NonNull final ExtendedRecyclerViewHolder holder,
        @NonNull final FilterItem orderFilterItem,
        final int position,
        final int groupParentIndex,
        final int groupChildIndex) {
        super.onBindGroupChildViewHolder(Contracts.requireNonNull(holder, "holder == null"),
                                         Contracts.requireNonNull(orderFilterItem,
                                                                  "orderFilterItem == null"),
                                         position,
                                         groupParentIndex,
                                         groupChildIndex);

        val filterViewHolder = (FilterViewHolder) holder;

        filterViewHolder.filterName.setText(orderFilterItem.getName());
        filterViewHolder.filterIcon.setImageResource(orderFilterItem.getIcon());
    }

    @Override
    protected void onBindGroupParentViewHolder(
        @NonNull final ExtendedRecyclerViewHolder holder,
        @NonNull final FilterGroupItem orderFilterGroupItem,
        final int position,
        final int groupParentIndex) {
        super.onBindGroupParentViewHolder(Contracts.requireNonNull(holder, "holder == null"),
                                          Contracts.requireNonNull(orderFilterGroupItem,
                                                                   "orderFilterGroupItem == null"),
                                          position,
                                          groupParentIndex);

        val filterGroupViewHolder = (FilterGroupViewHolder) holder;

        filterGroupViewHolder.groupTitle.setText(orderFilterGroupItem.getTitle());
    }

    @NonNull
    @Override
    protected ExtendedRecyclerViewHolder onCreateGroupChildViewHolder(
        final ViewGroup parent, final int viewType) {

        val view = inflateView(R.layout.fragment_orders_filter_list_filter_item, parent);

        val filterViewHolder = new FilterViewHolder(view);
        filterViewHolder.setOnFilterClickListener(_onFilterClickListener);
        return filterViewHolder;
    }

    @NonNull
    @Override
    protected ExtendedRecyclerViewHolder onCreateGroupParentViewHolder(
        final ViewGroup parent, final int viewType) {

        val view = inflateView(R.layout.fragment_orders_filter_list_filter_group_item, parent);

        val holder = new FilterGroupViewHolder(view);

        //        SpacingItemDecoration.setDecorationMode(holder.itemView,
        //                                                SpacingItemDecoration.DecorationMode
        // .NONE);
        return holder;
    }

    @NonNull
    public Event<OrdersFilterEventArgs> getOrdersFilterEvent() {
        return _ordersFilterEvent;
    }

    @CallSuper
    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        recyclerView.setLayoutManager(createLayoutManager(recyclerView.getContext()));
    }

    public void removeAdditionalFilters() {
        val orderFilterGroupItems = getOrderFilterGroupItems();
        if (orderFilterGroupItems != null) {
            final int groupSize = orderFilterGroupItems.size();

            final int innerItemCount = getInnerItemCount();

            final int positionStart = getGroupChildCountOfGroup(0) + 1;

            orderFilterGroupItems.subList(1, groupSize).clear();

            notifyItemRangeRemoved(positionStart, innerItemCount - positionStart);
        }
    }

    @NonNull
    protected GridLayoutManager createLayoutManager(@NonNull final Context context) {
        Contracts.requireNonNull(context, "context == null");

        val layoutManager =
            new GridLayoutManager(context, FILTERS_COLUMN_COUNT, GridLayoutManager.VERTICAL, false);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(final int position) {
                final int spanSize;

                final int itemViewType = getItemViewType(position);
                if (itemViewType == VIEW_TYPE_GROUP_PARENT) {
                    spanSize = FILTERS_COLUMN_COUNT;
                } else {
                    spanSize = FILTERS_TITLE_COLUMN_COUNT;
                }

                return spanSize;
            }
        });

        return layoutManager;
    }

    @Getter
    @Setter
    @Nullable
    List<FilterGroupItem> _orderFilterGroupItems;

    @NonNull
    private final ManagedEvent<OrdersFilterEventArgs> _ordersFilterEvent = Events.createEvent();

    @NonNull
    private final FilterViewHolder.onFilterClickListener _onFilterClickListener =
        new FilterViewHolder.onFilterClickListener() {
            @Override
            public void onFilterClick(final int adapterPosition) {
                performFilterClick(adapterPosition);
            }
        };

    private void performFilterClick(final int adapterPosition) {
        final OrdersFilterEventArgs ordersFilterEventArgs;

        val orderFilterGroupItems = getOrderFilterGroupItems();
        if (orderFilterGroupItems != null) {
            final int groupParentIndex = getGroupParentIndex(adapterPosition);
            val filterGroupItem = orderFilterGroupItems.get(groupParentIndex);
            final int groupChildIndex = getGroupChildIndex(adapterPosition);
            val filterItem = filterGroupItem.getFilterItems().get(groupChildIndex);
            ordersFilterEventArgs = new OrdersFilterEventArgs(filterGroupItem.getFilterType(),
                                                              filterItem.getId(),
                                                              filterItem.getName());
        } else {
            ordersFilterEventArgs = null;
        }

        if (ordersFilterEventArgs != null) {
            _ordersFilterEvent.rise(ordersFilterEventArgs);
        }
    }
}
