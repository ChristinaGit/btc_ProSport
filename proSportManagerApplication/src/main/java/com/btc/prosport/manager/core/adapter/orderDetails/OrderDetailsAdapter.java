package com.btc.prosport.manager.core.adapter.orderDetails;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.common.utility.ThemeUtils;
import com.btc.prosport.api.model.Interval;
import com.btc.prosport.api.model.Order;
import com.btc.prosport.api.model.OrderMetadataInterval;
import com.btc.prosport.core.adapter.cell.CellsAdapter;
import com.btc.prosport.core.utility.ProSportApiDataUtils;
import com.btc.prosport.core.utility.ProSportFormat;
import com.btc.prosport.core.utility.UserUtils;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.screen.activity.orderEditor.OrderEditorIntent;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Accessors(prefix = "_")
public class OrderDetailsAdapter extends CellsAdapter<Interval> {
    private static final String _LOG_TAG = ConstantBuilder.logTag(OrderDetailsAdapter.class);

    public static final int VIEW_TYPE_ORDER_GENERAL_INFO = newViewType();

    public OrderDetailsAdapter(@NonNull final Context context) {
        Contracts.requireNonNull(context, "context == null");

        _context = context;
    }

    @Override
    public int getHeaderItemCount() {
        return super.getHeaderItemCount() + /*general info*/ 1;
    }

    @Override
    public int getHeaderItemViewType(final int position) {
        final int viewType;

        if (position == 0) {
            viewType = VIEW_TYPE_ORDER_GENERAL_INFO;
        } else {
            viewType = super.getHeaderItemViewType(position);
        }

        return viewType;
    }

    @Override
    protected boolean isHeaderItemViewType(final int viewType) {
        return super.isHeaderItemViewType(viewType) || viewType == VIEW_TYPE_ORDER_GENERAL_INFO;
    }

    @CallSuper
    @Override
    public void onBindHeaderItemViewHolder(
        @NonNull final ExtendedRecyclerViewHolder holder, final int position) {
        Contracts.requireNonNull(holder, "holder == null");

        final int viewType = getHeaderItemViewType(position);

        if (VIEW_TYPE_ORDER_GENERAL_INFO == viewType) {
            onBindOrderGeneralInfoViewHolder((OrderGeneralInfoViewHolder) holder, position);
        } else {
            super.onBindHeaderItemViewHolder(holder, position);
        }
    }

    @NonNull
    @CallSuper
    @Override
    public ExtendedRecyclerViewHolder onCreateHeaderItemViewHolder(
        final ViewGroup parent, final int viewType) {
        final ExtendedRecyclerViewHolder viewHolder;

        if (VIEW_TYPE_ORDER_GENERAL_INFO == viewType) {
            viewHolder = onCreateOrderGeneralInfoViewHolder(parent, viewType);
        } else {
            viewHolder = super.onCreateHeaderItemViewHolder(parent, viewType);
        }

        return viewHolder;
    }

    @CallSuper
    public void onBindOrderGeneralInfoViewHolder(
        final OrderGeneralInfoViewHolder holder, final int position) {
        final val context = holder.getContext();

        final val order = getOrder();

        if (order != null) {
            final val player = order.getPlayer();
            final val sportComplex = order.getSportComplex();
            final val playground = order.getPlayground();

            final val intervals = (List<OrderMetadataInterval>) order.getIntervals();
            if (intervals != null) {
                holder.orderIntervalsView.setVisibility(View.VISIBLE);

                final val formattedIntervals =
                    ProSportFormat.getFormattedOrderIntervals(context, order, StringUtils.LF);

                holder.orderIntervalsView.setText(formattedIntervals);
            } else {
                holder.orderIntervalsView.setVisibility(View.GONE);
            }

            final val noDataPlaceholder = context.getString(R.string.orders_list_no_data);

            holder.itemView.setVisibility(View.VISIBLE);

            holder.orderIdView.setText(context.getString(R.string.order_number, order.getId()));

            final val formattedDate = getFormattedDate(context, order);
            holder.orderDateView.setText(formattedDate);

            final String displayUserName;
            if (player != null) {
                displayUserName = UserUtils.getDisplayContactUserName(player);
                holder.userNameView.setVisibility(View.VISIBLE);
            } else {
                displayUserName = null;
                holder.userNameView.setVisibility(View.GONE);
            }
            holder.userNameView.setText(displayUserName);

            final val sportComplexName =
                StringUtils.defaultString(sportComplex == null ? null : sportComplex.getName(),
                                          noDataPlaceholder);
            holder.orderSportComplexView.setText(sportComplexName);

            final val playgroundName =
                StringUtils.defaultString(playground == null ? null : playground.getName(),
                                          noDataPlaceholder);
            holder.orderPlaygroundView.setText(playgroundName);

            final val formattedPrice = ProSportFormat.getFormattedOrderSalePrice(order);
            holder.orderPriceView.setText(formattedPrice);
        } else {
            holder.itemView.setVisibility(View.GONE);
        }
    }

    @CallSuper
    public OrderGeneralInfoViewHolder onCreateOrderGeneralInfoViewHolder(
        final ViewGroup parent, final int viewType) {
        final val view = inflateView(R.layout.fragment_order_viewer_item_general_info, parent);

        final val holder = new OrderGeneralInfoViewHolder(view);

        holder.orderMoreView.setVisibility(View.GONE);
        holder.orderEditView.setOnClickListener(_editOrderOnClick);

        return holder;
    }

    public void setOrder(@Nullable final Order order) {
        if (_order != order) {
            _order = order;

            onOrderChanged();
        }
    }

    @NonNull
    @Override
    protected GridLayoutManager createLayoutManager(@NonNull final Context context) {
        Contracts.requireNonNull(context, "context == null");

        final val layoutManager = super.createLayoutManager(context);

        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(final int position) {
                final int spanSize;

                final int itemViewType = getItemViewType(position);
                if (itemViewType == VIEW_TYPE_ORDER_GENERAL_INFO) {
                    spanSize = getRowLength();
                } else {
                    spanSize = 1;
                }
                return spanSize;
            }
        });

        return layoutManager;
    }

    @Override
    protected void onBindCellViewHolder(
        @NonNull final ExtendedRecyclerViewHolder holder, final int position) {
        super.onBindCellViewHolder(holder, position);

        final int relativePosition = getCellVerticalRelativePosition(position);
        final val interval = getCellItemByRelativePosition(relativePosition);

        final val context = holder.getContext();

        boolean reserved = false;
        final val order = getOrder();

        if (order != null && interval != null) {
            final val orderId = interval.getOrderId();
            if (orderId != null) {
                reserved = orderId == order.getId();
            }
        }

        if (reserved) {
            final val primaryColor = ThemeUtils.resolveColor(context, R.attr.colorPrimary);
            setIntervalDrawableForeground(holder.itemView.getBackground(), primaryColor);
        } else {
            // From super.
        }

        holder.itemView.setEnabled(false);
    }

    @CallSuper
    protected void onOrderChanged() {
    }

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final Context _context;

    @Getter
    @Nullable
    private Order _order;

    @NonNull
    private final View.OnClickListener _editOrderOnClick = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            final val order = getOrder();
            if (order != null) {
                final val context = v.getContext();
                // TODO: 25.05.2017 Move to presenter
                context.startActivity(OrderEditorIntent.Edit.build(context, order.getId()));
            }
        }
    };

    @Nullable
    private String getFormattedDate(
        @NonNull final Context context, @Nullable final Order order) {
        Contracts.requireNonNull(context, "context == null");

        final String formattedDate;

        if (order != null) {
            final val orderDateFormat = ProSportFormat.getIntervalLongDateFormat();

            final val orderDate = order.getCreateDate();
            if (orderDate != null) {
                final val date = ProSportApiDataUtils.parseDate(orderDate);
                formattedDate = orderDateFormat.format(date);
            } else {
                formattedDate = null;
            }
        } else {
            formattedDate = null;
        }

        return formattedDate;
    }
}
