package com.btc.prosport.manager.core.adapter.salesList;

import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.event.Events;
import com.btc.common.event.generic.Event;
import com.btc.common.event.generic.ManagedEvent;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.extension.view.recyclerView.adapter.PaginationRecyclerViewListAdapter;
import com.btc.common.extension.view.recyclerView.decoration.DividerItemDecoration;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.api.model.Sale;
import com.btc.prosport.api.model.SaleMetadataInterval;
import com.btc.prosport.core.utility.ProSportFormat;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.adapter.ordersList.OrdersListAdapter;
import com.btc.prosport.manager.core.adapter.ordersList.viewHolder.OrdersLoadingViewHolder;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Accessors(prefix = "_")
public class SalesListAdapter extends PaginationRecyclerViewListAdapter<Sale> {
    private static final String _LOG_TAG = ConstantBuilder.logTag(OrdersListAdapter.class);

    @NonNull
    public final Event<IdEventArgs> getDeleteSaleEvent() {
        return _deleteSaleEvent;
    }

    public final int getSaleIndex(final long saleId) {
        return IterableUtils.indexOf(getItems(), new Predicate<Sale>() {
            @Override
            public boolean evaluate(final Sale object) {
                return object.getId() == saleId;
            }
        });
    }

    @Override
    public void onBindInnerItemViewHolder(
        @NonNull final ExtendedRecyclerViewHolder genericHolder, final int position) {
        Contracts.requireNonNull(genericHolder, "genericHolder == null");

        final val holder = (SaleViewHolder) genericHolder;
        final val sale = getItem(position);

        final val context = holder.getContext();

        final val intervals = (List<SaleMetadataInterval>) sale.getIntervals();
        if (intervals != null) {
            holder.saleIntervalsView.setVisibility(View.VISIBLE);

            final val formattedIntervals =
                ProSportFormat.getFormattedSaleIntervals(context, sale, StringUtils.LF);

            holder.saleIntervalsView.setText(formattedIntervals);
        } else {
            holder.saleIntervalsView.setVisibility(View.GONE);
        }
        holder.saleMoreView.setTag(R.id.tag_view_id, sale.getId());
        holder.saleMoreView.setOnClickListener(_showMoreOnClick);

        holder.salePlaceView.setText(ProSportFormat.getFormattedSalePlace(sale));
    }

    @NonNull
    @Override
    public SaleViewHolder onCreateInnerItemViewHolder(
        final ViewGroup parent, final int viewType) {

        final val view = inflateView(R.layout.fragment_sales_list_item, parent);

        return new SaleViewHolder(view);
    }

    @NonNull
    @Override
    protected ExtendedRecyclerViewHolder onCreateLoadingViewHolder(
        final ViewGroup parent, final int viewType) {
        final val view = inflateView(R.layout.fragment_orders_list_loading_item, parent);

        final val holder = new OrdersLoadingViewHolder(view);

        DividerItemDecoration.setDecorationMode(holder.itemView,
                                                DividerItemDecoration.DecorationMode.NONE);

        return holder;
    }

    @NonNull
    private final ManagedEvent<IdEventArgs> _deleteSaleEvent = Events.createEvent();

    @Getter(onMethod = @__(@Override))
    @NonNull
    private final List<Sale> _items = new ArrayList<>();

    @NonNull
    private final View.OnClickListener _showMoreOnClick = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            final val context = v.getContext();

            final val saleId = (long) v.getTag(R.id.tag_view_id);

            final val popupMenu = new PopupMenu(context, v);

            popupMenu.inflate(R.menu.sale);

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(final MenuItem item) {
                    final boolean consumed;

                    final int itemId = item.getItemId();
                    switch (itemId) {
                        case R.id.action_delete_sale: {
                            _deleteSaleEvent.rise(new IdEventArgs(saleId));

                            consumed = true;
                            break;
                        }
                        case R.id.action_edit_sale: {

                            consumed = true;
                            break;
                        }
                        default: {
                            consumed = false;
                            break;
                        }
                    }

                    return consumed;
                }
            });

            popupMenu.show();
        }
    };
}

