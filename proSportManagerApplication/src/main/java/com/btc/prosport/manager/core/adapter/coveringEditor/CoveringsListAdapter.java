package com.btc.prosport.manager.core.adapter.coveringEditor;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.event.Events;
import com.btc.common.event.generic.ManagedEvent;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.extension.view.recyclerView.adapter.RecyclerViewAdapter;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.api.model.Covering;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.adapter.coveringEditor.viewHolder.CoveringViewHolder;

import java.util.ArrayList;
import java.util.List;

@Accessors(prefix = "_")
public class CoveringsListAdapter extends RecyclerViewAdapter<ExtendedRecyclerViewHolder> {

    private static final String _LOG_TAG = ConstantBuilder.logTag(CoveringsListAdapter.class);

    public void addCoveringItems(
        @NonNull final List<Covering> coveringItems, final boolean notifyAdapter) {
        Contracts.requireNonNull(coveringItems, "coveringItems == null");

        val currentCoveringItems = getCoveringItems();
        final int positionStart = currentCoveringItems.size();

        currentCoveringItems.addAll(coveringItems);

        if (notifyAdapter) {
            notifyItemRangeInserted(positionStart, coveringItems.size());
        }
    }

    @NonNull
    public Covering getItem(final int position) {

        val coveringItems = getCoveringItems();

        return coveringItems.get(position);
    }

    public ManagedEvent<IdEventArgs> getPickCoveringEvent() {
        return _pickCoveringEvent;
    }

    @Override
    public ExtendedRecyclerViewHolder onCreateViewHolder(
        final ViewGroup parent, final int viewType) {

        val inflater = LayoutInflater.from(parent.getContext());

        val view =
            inflater.inflate(R.layout.fragment_playground_covering_editor_item, parent, false);

        final val coveringViewHolder = new CoveringViewHolder(view);

        return coveringViewHolder;
    }

    @Override
    public void onBindViewHolder(
        @NonNull final ExtendedRecyclerViewHolder holder, final int position) {

        final val item = getItem(position);

        if (holder instanceof CoveringViewHolder) {
            final val coveringHolder = (CoveringViewHolder) holder;

            final String coveringName = item.getName();
            coveringHolder.coveringNameView.setText(coveringName);

            final boolean isChecked = item.getId() == getSelectedCoveringId();
            coveringHolder.coveringOffButton.setChecked(isChecked);

            coveringHolder.coveringOffButton.setTag(R.id.tag_view_covering_id, item.getId());
            coveringHolder.coveringOffButton.setOnClickListener(_onClickListener);
        }
    }

    @Override
    public int getItemCount() {
        return getCoveringItems().size();
    }

    public void setSelectedCovering(final long selectedCoveringId) {
        _selectedCoveringId = selectedCoveringId;
    }

    @NonNull
    private final ManagedEvent<IdEventArgs> _pickCoveringEvent = Events.createEvent();

    @NonNull
    @Getter
    @Setter
    private List<Covering> _coveringItems = new ArrayList<>();

    @Getter(AccessLevel.PRIVATE)
    private long _selectedCoveringId;

    @NonNull
    private final View.OnClickListener _onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            final long selectedCoveringId = (long) v.getTag(R.id.tag_view_covering_id);
            _pickCoveringEvent.rise(new IdEventArgs(selectedCoveringId));
            setSelectedCovering(selectedCoveringId);
            notifyItemRangeChanged(0, getItemCount());
        }
    };
}
