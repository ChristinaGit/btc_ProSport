package com.btc.prosport.manager.core.adapter.playgroundPicker;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.var;
import lombok.val;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.recyclerView.adapter.HierarchyRecyclerViewAdapter;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.api.model.PlaygroundTitle;
import com.btc.prosport.api.model.SportComplexTitle;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.adapter.playgroundPicker.viewHolder.PlaygroundViewHolder;
import com.btc.prosport.manager.core.adapter.playgroundPicker.viewHolder.SportComplexViewHolder;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Accessors(prefix = "_")
public final class PlaygroundPickerAdapter
    extends HierarchyRecyclerViewAdapter<SportComplexGroupItem, PlaygroundTitle> {
    public final void addSportComplex(
        @NonNull final SportComplexTitle sportComplex,
        @NonNull final List<PlaygroundTitle> playgrounds) {
        Contracts.requireNonNull(sportComplex, "sportComplex == null");
        Contracts.requireNonNull(playgrounds, "playgrounds == null");

        getItems().add(new SportComplexGroupItem(sportComplex, playgrounds));
    }

    @Nullable
    public final Set<Long> getSelectedSportComplexesIds() {
        Set<Long> result = null;

        for (final val groupItem : getItems()) {
            if (isSportComplexSelectedInternal(groupItem)) {
                if (result == null) {
                    result = new HashSet<>();
                }

                result.add(groupItem.getSportComplex().getId());
            }
        }

        return result;
    }

    public final boolean isPlaygroundSelected(final long playgroundId) {
        boolean selected = false;

        for (final val playgroundsIds : getSelection().values()) {
            if (playgroundsIds.contains(playgroundId)) {
                selected = true;
                break;
            }
        }

        return selected;
    }

    public final boolean isSportComplexSelected(final long sportComplexId) {
        final val groupItem =
            IterableUtils.find(getItems(), new Predicate<SportComplexGroupItem>() {
                @Override
                public boolean evaluate(final SportComplexGroupItem object) {
                    return object.getSportComplex().getId() == sportComplexId;
                }
            });

        return isSportComplexSelectedInternal(groupItem);
    }

    @NonNull
    @Override
    public PlaygroundTitle getGroupChild(final int groupParentIndex, final int groupChildIndex) {
        return getItems().get(groupParentIndex).getPlaygrounds().get(groupChildIndex);
    }

    @Override
    public int getGroupChildCountOfGroup(final int groupParentIndex) {
        return getItems().get(groupParentIndex).getPlaygrounds().size();
    }

    @NonNull
    @Override
    public SportComplexGroupItem getGroupParent(final int groupParentIndex) {
        return getItems().get(groupParentIndex);
    }

    @Override
    public int getGroupParentCount() {
        return getItems().size();
    }

    @Override
    protected void onBindGroupChildViewHolder(
        @NonNull final ExtendedRecyclerViewHolder genericHolder,
        @NonNull final PlaygroundTitle playground,
        final int position,
        final int groupParentIndex,
        final int groupChildIndex) {
        super.onBindGroupChildViewHolder(Contracts.requireNonNull(genericHolder,
                                                                  "genericHolder == null"),
                                         Contracts.requireNonNull(playground, "playground == null"),
                                         position,
                                         groupParentIndex,
                                         groupChildIndex);
        final val holder = (PlaygroundViewHolder) genericHolder;

        holder.playgroundSelectView.setTag(R.id.tag_view_position, position);

        holder.playgroundSelectView.setText(playground.getName());

        final val selected = isPlaygroundSelected(playground.getId());

        holder.playgroundSelectView.setOnCheckedChangeListener(null);

        holder.playgroundSelectView.setChecked(selected);

        holder.playgroundSelectView.setOnCheckedChangeListener(_togglePlaygroundSelectionOnCheck);
    }

    @Override
    protected void onBindGroupParentViewHolder(
        @NonNull final ExtendedRecyclerViewHolder genericHolder,
        @NonNull final SportComplexGroupItem groupItem,
        final int position,
        final int groupParentIndex) {
        super.onBindGroupParentViewHolder(Contracts.requireNonNull(genericHolder,
                                                                   "genericHolder == null"),
                                          Contracts.requireNonNull(groupItem, "groupItem == null"),
                                          position,
                                          groupParentIndex);
        final val holder = (SportComplexViewHolder) genericHolder;

        holder.sportComplexSelectView.setTag(R.id.tag_view_position, position);

        final val sportComplex = groupItem.getSportComplex();
        final val selected = isSportComplexSelected(sportComplex.getId());

        holder.sportComplexNameView.setText(sportComplex.getName());

        holder.sportComplexSelectView.setOnCheckedChangeListener(null);

        holder.sportComplexSelectView.setChecked(selected);

        holder.sportComplexSelectView.setOnCheckedChangeListener(_toggleSportComplexSectionOnCheck);
    }

    @NonNull
    @Override
    protected PlaygroundViewHolder onCreateGroupChildViewHolder(
        final ViewGroup parent, final int viewType) {
        final val view = inflateView(R.layout.layout_playground_picker_playground_item, parent);

        return new PlaygroundViewHolder(view);
    }

    @NonNull
    @Override
    protected SportComplexViewHolder onCreateGroupParentViewHolder(
        final ViewGroup parent, final int viewType) {
        final val view = inflateView(R.layout.layout_playground_picker_sport_complex_item, parent);

        return new SportComplexViewHolder(view);
    }

    public boolean setPlaygroundSelected(
        final long sportComplexId, final long playgroundId, final boolean selected) {
        boolean selectionChanged = false;

        final val selection = getSelection();
        if (selected) {
            var playgroundsIds = selection.get(sportComplexId);
            if (playgroundsIds == null) {
                playgroundsIds = createNewPlaygroundsIdsContainer();
                selection.put(sportComplexId, playgroundsIds);
            }
            selectionChanged = playgroundsIds.add(playgroundId);
        } else {
            final val playgroundsIds = selection.get(sportComplexId);
            if (playgroundsIds != null) {
                selectionChanged = playgroundsIds.remove(playgroundId);
                if (selectionChanged && playgroundsIds.isEmpty()) {
                    selection.remove(sportComplexId);
                }
            }
        }

        return selectionChanged;
    }

    @Getter(AccessLevel.PRIVATE)
    @NonNull
    private final List<SportComplexGroupItem> _items = new ArrayList<>();

    @Getter
    @NonNull
    private final Map<Long, Set<Long>> _selection = new HashMap<>();

    @NonNull
    private final CompoundButton.OnCheckedChangeListener _togglePlaygroundSelectionOnCheck =
        new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                final val position = (int) buttonView.getTag(R.id.tag_view_position);

                final val groupParentIndex = getGroupParentIndex(position);
                final val groupChildIndex = getGroupChildIndex(position);

                final val groupParent = getGroupParent(groupParentIndex);
                final val playground = getGroupChild(groupParentIndex, groupChildIndex);

                final val sportComplex = groupParent.getSportComplex();
                final val sportComplexId = sportComplex.getId();

                final val isSportComplexSelected = isSportComplexSelected(sportComplexId);

                setPlaygroundSelected(sportComplexId, playground.getId(), isChecked);

                if (isSportComplexSelected != isSportComplexSelected(sportComplexId)) {
                    notifyGroupParentItemChanged(groupParentIndex);
                }
            }
        };

    @NonNull
    private final CompoundButton.OnCheckedChangeListener _toggleSportComplexSectionOnCheck =
        new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                final val position = (int) buttonView.getTag(R.id.tag_view_position);

                final val groupParentIndex = getGroupParentIndex(position);

                final val groupParent = getGroupParent(groupParentIndex);

                final val sportComplexId = groupParent.getSportComplex().getId();
                final val playgrounds = groupParent.getPlaygrounds();
                for (int i = 0, count = playgrounds.size(); i < count; i++) {
                    final val playground = playgrounds.get(i);
                    final val playgroundId = playground.getId();

                    if (setPlaygroundSelected(sportComplexId, playgroundId, isChecked)) {
                        notifyGroupChildItemChanged(groupParentIndex, i);
                    }
                }
            }
        };

    @NonNull
    private Set<Long> createNewPlaygroundsIdsContainer() {
        return new HashSet<>();
    }

    private boolean isSportComplexSelectedInternal(@NonNull final SportComplexGroupItem groupItem) {
        Contracts.requireNonNull(groupItem, "groupItem == null");

        final val selection = getSelection();
        final val selectedPlaygrounds = selection.get(groupItem.getSportComplex().getId());

        // Unsafe check. Logic hack.
        return selectedPlaygrounds != null &&
               selectedPlaygrounds.size() == groupItem.getPlaygrounds().size();
    }
}
