package com.btc.prosport.manager.core.adapter.repeatableIntervals.viewHolder;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.manager.R;

public class IntervalViewHolder extends ExtendedRecyclerViewHolder {
    @NonNull
    public final TextInputLayout dateEndContainerView;

    @NonNull
    public final TextView dateEndView;

    @NonNull
    public final TextInputLayout dateStartContainerView;

    @NonNull
    public final TextView dateStartView;

    @NonNull
    public final CheckBox repeatView;

    @NonNull
    public final ViewGroup repeatWeekView;

    @NonNull
    public final TextInputLayout timeEndContainerView;

    @NonNull
    public final TextView timeEndView;

    @NonNull
    public final TextInputLayout timeStartContainerView;

    @NonNull
    public final TextView timeStartView;

    public IntervalViewHolder(@NonNull final View itemView) {
        super(Contracts.requireNonNull(itemView, "itemView == null"));

        timeStartView = (TextView) itemView.findViewById(R.id.interval_time_start);
        timeStartContainerView =
            (TextInputLayout) itemView.findViewById(R.id.interval_time_start_container);
        timeEndView = (TextView) itemView.findViewById(R.id.interval_time_end);
        timeEndContainerView =
            (TextInputLayout) itemView.findViewById(R.id.interval_time_end_container);
        dateStartView = (TextView) itemView.findViewById(R.id.interval_date_start);
        dateStartContainerView =
            (TextInputLayout) itemView.findViewById(R.id.interval_date_start_container);
        dateEndView = (TextView) itemView.findViewById(R.id.interval_date_end);
        dateEndContainerView =
            (TextInputLayout) itemView.findViewById(R.id.interval_date_end_container);
        repeatWeekView = (ViewGroup) itemView.findViewById(R.id.interval_repeat_week);
        repeatView = (CheckBox) itemView.findViewById(R.id.interval_repeat);
    }
}
