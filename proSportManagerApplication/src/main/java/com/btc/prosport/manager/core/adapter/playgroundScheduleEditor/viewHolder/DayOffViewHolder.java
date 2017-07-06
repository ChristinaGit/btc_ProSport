package com.btc.prosport.manager.core.adapter.playgroundScheduleEditor.viewHolder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;

import butterknife.BindView;

import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.manager.R;

@Accessors(prefix = "_")
public class DayOffViewHolder extends ExtendedRecyclerViewHolder {
    @BindView(R.id.day)
    public TextView day;

    @BindView(R.id.day_off_checkbox)
    public CheckBox dayOffCheckBox;

    public DayOffViewHolder(@NonNull final View itemView) {
        super(itemView);

        bindViews(itemView);

        dayOffCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                val dayOffChangeListener = getDayOffChangeListener();
                if (dayOffChangeListener != null && isDayOffChangeListen()) {
                    final int adapterPosition = getAdapterPosition();
                    if (isChecked) {
                        dayOffChangeListener.onCollapseItem(adapterPosition);
                    } else {
                        dayOffChangeListener.onExpandItem(adapterPosition);
                    }
                }
            }
        });
    }

    @Getter
    @Setter
    @Nullable
    DayOffChangeListener _dayOffChangeListener;

    @Getter
    @Setter
    private boolean _dayOffChangeListen;

    public interface DayOffChangeListener {
        void onCollapseItem(int adapterPosition);

        void onExpandItem(int adapterPosition);
    }
}
