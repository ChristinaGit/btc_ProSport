package com.btc.prosport.manager.core.adapter.orderFilter.viewHolder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.manager.R;

@Accessors(prefix = "_")
public class FilterViewHolder extends ExtendedRecyclerViewHolder {
    @NonNull
    public final ImageView filterIcon;

    @NonNull
    public final TextView filterName;

    public FilterViewHolder(@NonNull final View itemView) {
        super(Contracts.requireNonNull(itemView, "itemView == null"));

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (_onFilterClickListener != null) {
                    _onFilterClickListener.onFilterClick(getAdapterPosition());
                }
            }
        });
        filterName = (TextView) itemView.findViewById(R.id.filter_name);
        filterIcon = (ImageView) itemView.findViewById(R.id.filter_icon);
    }

    @Getter
    @Setter
    @Nullable
    private onFilterClickListener _onFilterClickListener;

    public interface onFilterClickListener {
        void onFilterClick(int adapterPosition);
    }
}
