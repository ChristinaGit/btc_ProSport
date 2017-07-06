package com.btc.prosport.manager.core.adapter.attributeEditor.viewHolder;

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
public class AttributeViewHolder extends ExtendedRecyclerViewHolder {
    @BindView(R.id.attribute)
    @NonNull
    public TextView attributeNameView;

    @BindView(R.id.attribute_off_button)
    @NonNull
    public CheckBox attributeOffButton;

    public AttributeViewHolder(@NonNull final View itemView) {
        super(itemView);

        bindViews(itemView);

        attributeOffButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                val attributeOffChangeListener = getAttributeOffChangeListener();
                if (attributeOffChangeListener != null && isAttributeOffChangeListen()) {
                    final int adapterPosition = getAdapterPosition();
                    if (isChecked) {
                        attributeOffChangeListener.onCollapseItem(adapterPosition);
                    } else {
                        attributeOffChangeListener.onExpandItem(adapterPosition);
                    }
                }
            }
        });
    }

    @Getter
    @Setter
    @Nullable
    AttributeOffChangeListener _attributeOffChangeListener;

    @Getter
    @Setter
    private boolean _attributeOffChangeListen;

    public interface AttributeOffChangeListener {
        void onCollapseItem(int adapterPosition);

        void onExpandItem(int adapterPosition);
    }
}
