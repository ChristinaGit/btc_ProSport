package com.btc.prosport.manager.core.adapter.playgroundScheduleEditor.viewHolder;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;

import butterknife.BindView;

import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.adapter.playgroundScheduleEditor.item.TimeItem;

@Accessors(prefix = "_")
public class TimeViewHolder extends ExtendedRecyclerViewHolder {
    @BindView(R.id.end_time)
    public TextView endTime;

    @BindView(R.id.start_time)
    public TextView startTime;

    public TimeViewHolder(@NonNull final View itemView) {
        super(itemView);

        bindViews(itemView);
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                val timeChangeListener = getOnTimeClickListener();
                if (timeChangeListener != null) {
                    timeChangeListener.onTimeClick(TimeItem.Type.END_TIME, getAdapterPosition());
                }
            }
        });
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                val timeChangeListener = getOnTimeClickListener();
                if (timeChangeListener != null) {
                    timeChangeListener.onTimeClick(TimeItem.Type.START_TIME, getAdapterPosition());
                }
            }
        });
    }

    @Getter
    @Setter
    OnTimeClickListener _onTimeClickListener;

    // TODO: Remove adapter position. Add weekday id.
    public interface OnTimeClickListener {
        void onTimeClick(@NonNull final TimeItem.Type type, int adapterPosition);
    }
}
