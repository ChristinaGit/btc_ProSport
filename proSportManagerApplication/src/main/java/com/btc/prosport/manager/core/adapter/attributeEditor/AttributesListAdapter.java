package com.btc.prosport.manager.core.adapter.attributeEditor;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.extension.view.recyclerView.adapter.RecyclerViewAdapter;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.adapter.attributeEditor.viewHolder.AttributeViewHolder;

import java.util.ArrayList;
import java.util.List;

@Accessors(prefix = "_")
public class AttributesListAdapter extends RecyclerViewAdapter<ExtendedRecyclerViewHolder> {

    private static final String _LOG_TAG = ConstantBuilder.logTag(AttributesListAdapter.class);

    @NonNull
    public AttributeItem getItem(final int position) {

        val attributeItems = getAttributeItems();

        return attributeItems.get(position);
    }

    @Override
    public ExtendedRecyclerViewHolder onCreateViewHolder(
        final ViewGroup parent, final int viewType) {

        val inflater = LayoutInflater.from(parent.getContext());

        val view =
            inflater.inflate(R.layout.fragment_playground_attributes_editor_item, parent, false);

        final val attributeViewHolder = new AttributeViewHolder(view);

        attributeViewHolder.setAttributeOffChangeListener(_attributeOffChangeListener);

        return attributeViewHolder;
    }

    @Override
    public void onBindViewHolder(
        @NonNull final ExtendedRecyclerViewHolder holder, final int position) {

        final val item = getItem(position);

        if (holder instanceof AttributeViewHolder) {
            final val attributeHolder = (AttributeViewHolder) holder;

            final String attributeName = item.getName();
            attributeHolder.attributeNameView.setText(attributeName);


            attributeHolder.setAttributeOffChangeListen(false);
            attributeHolder.attributeOffButton.setChecked(!item.isOff());
            attributeHolder.setAttributeOffChangeListen(true);
        }
    }

    @Override
    public int getItemCount() {
        return getAttributeItems().size();
    }

    @NonNull
    @Getter
    @Setter
    private List<AttributeItem> _attributeItems = new ArrayList<>();

    @NonNull
    private final AttributeViewHolder.AttributeOffChangeListener _attributeOffChangeListener =
        new AttributeViewHolder.AttributeOffChangeListener() {

            @Override
            public void onCollapseItem(final int adapterPosition) {
                val attributeItem = getItem(adapterPosition);
                attributeItem.setOff(false);
                notifyItemChanged(adapterPosition);
            }

            @Override
            public void onExpandItem(final int adapterPosition) {
                val attributeItem = getItem(adapterPosition);
                attributeItem.setOff(true);
                notifyItemChanged(adapterPosition);
            }
        };
}
