package com.btc.prosport.manager.core.adapter.navigation;

import android.graphics.Color;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.LongSparseArray;
import android.view.View;
import android.view.ViewGroup;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.android.internal.util.Predicate;
import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.event.Events;
import com.btc.common.event.generic.ManagedEvent;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.extension.view.recyclerView.adapter.ExpandableRecyclerViewAdapter;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.common.utility.NumberUtils;
import com.btc.common.utility.ThemeUtils;
import com.btc.prosport.api.model.PlaygroundReport;
import com.btc.prosport.api.model.SportComplexReport;
import com.btc.prosport.core.utility.ProSportApiDataUtils;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.adapter.navigation.viewHolder.NavigationViewHolder;
import com.btc.prosport.manager.core.adapter.navigation.viewHolder.PlaygroundViewHolder;
import com.btc.prosport.manager.core.adapter.navigation.viewHolder.SportComplexViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Accessors(prefix = "_")
public final class NavigationAdapter
    extends ExpandableRecyclerViewAdapter<SportComplexReport, PlaygroundReport> {
    private static final String _LOG_TAG = ConstantBuilder.logTag(NavigationAdapter.class);

    public static final int VIEW_TYPE_HEADER_NAVIGATION = newViewType();

    public static final int VIEW_TYPE_HEADER_LOADING = newViewType();

    public static final int VIEW_TYPE_FOOTER_NAVIGATION = newViewType();

    @NonNull
    public final ManagedEvent<IdEventArgs> getOnPlaygroundClickEvent() {
        return _onPlaygroundClickEvent;
    }

    public final void setSelectedPlaygroundId(@Nullable final Long selectedPlaygroundId) {
        if (!Objects.equals(_selectedPlaygroundId, selectedPlaygroundId)) {
            final val olsSelectedPlaygroundId = _selectedPlaygroundId;

            _selectedPlaygroundId = selectedPlaygroundId;

            if (olsSelectedPlaygroundId != null) {
                final val oldSelectedPosition = findPlaygroundPosition(olsSelectedPlaygroundId);
                if (oldSelectedPosition != null) {
                    notifyItemChanged(oldSelectedPosition);
                }
            }

            if (selectedPlaygroundId != null) {
                final val selectedPosition = findPlaygroundPosition(selectedPlaygroundId);
                if (selectedPosition != null) {
                    notifyItemChanged(selectedPosition);
                }
            }

            onSelectedPlaygroundIdChanged();
        }
    }

    public final void setSportComplexes(@Nullable final List<SportComplexReport> sportComplexes) {
        _sportComplexes = sportComplexes;

        notifySportComplexesChanged();
    }

    public void changeNotConfirmedOrdersCount(
        final long playgroundId, @Nullable final Integer count) {
        if (count != null) {
            _notConfirmedOrdersCount.put(playgroundId, count);
        } else {
            _notConfirmedOrdersCount.remove(playgroundId);
        }

        final val playgroundPosition = findPlaygroundPosition(playgroundId);
        if (playgroundPosition != null) {
            notifyItemChanged(playgroundPosition);
        }
    }

    @Override
    public int getFooterItemCount() {
        return super.getFooterItemCount() + getFooterNavigationItems().size();
    }

    @Override
    public int getHeaderItemCount() {
        return super.getHeaderItemCount() + (isSportComplexesLoading() ? 1 : 0) +
               getHeaderNavigationItems().size();
    }

    @Override
    protected int getFooterItemViewType(final int position) {
        return VIEW_TYPE_FOOTER_NAVIGATION;
    }

    @Override
    protected int getHeaderItemViewType(final int position) {
        final int viewType;

        if (isSportComplexesLoading() && position == getHeaderItemCount() - 1) {
            viewType = VIEW_TYPE_HEADER_LOADING;
        } else {
            viewType = VIEW_TYPE_HEADER_NAVIGATION;
        }

        return viewType;
    }

    @Override
    protected boolean isFooterItemViewType(final int viewType) {
        return super.isFooterItemViewType(viewType) || viewType == VIEW_TYPE_FOOTER_NAVIGATION;
    }

    @Override
    protected boolean isHeaderItemViewType(final int viewType) {
        return super.isHeaderItemViewType(viewType) || viewType == VIEW_TYPE_HEADER_NAVIGATION ||
               viewType == VIEW_TYPE_HEADER_LOADING;
    }

    @Override
    protected void onBindFooterItemViewHolder(
        @NonNull final ExtendedRecyclerViewHolder holder, final int position) {
        Contracts.requireNonNull(holder, "holder == null");

        final int viewType = getItemViewType(position);

        if (VIEW_TYPE_FOOTER_NAVIGATION == viewType) {
            final int footerItemPosition =
                position - getHeaderItemCount() - getGroupParentCount() - getGroupChildCount();
            final val item = getFooterNavigationItems().get(footerItemPosition);

            onBindNavigationViewHolder((NavigationViewHolder) holder, item, position);
        } else {
            super.onBindFooterItemViewHolder(holder, position);
        }
    }

    @Override
    protected void onBindHeaderItemViewHolder(
        @NonNull final ExtendedRecyclerViewHolder holder, final int position) {
        Contracts.requireNonNull(holder, "holder == null");

        final int viewType = getItemViewType(position);

        if (VIEW_TYPE_HEADER_NAVIGATION == viewType) {
            final val item = getHeaderNavigationItems().get(position);
            onBindNavigationViewHolder((NavigationViewHolder) holder, item, position);
        } else if (VIEW_TYPE_HEADER_LOADING == viewType) {
            onBindLoadingViewHolder(holder);
        } else {
            super.onBindHeaderItemViewHolder(holder, position);
        }
    }

    @NonNull
    @Override
    protected ExtendedRecyclerViewHolder onCreateFooterItemViewHolder(
        final ViewGroup parent, final int viewType) {
        final ExtendedRecyclerViewHolder viewHolder;

        if (VIEW_TYPE_FOOTER_NAVIGATION == viewType) {
            viewHolder = onCreateNavigationViewHolder(parent);
        } else {
            viewHolder = super.onCreateFooterItemViewHolder(parent, viewType);
        }

        return viewHolder;
    }

    @NonNull
    @Override
    protected ExtendedRecyclerViewHolder onCreateHeaderItemViewHolder(
        final ViewGroup parent, final int viewType) {
        final ExtendedRecyclerViewHolder viewHolder;

        if (VIEW_TYPE_HEADER_NAVIGATION == viewType) {
            viewHolder = onCreateNavigationViewHolder(parent);
        } else if (VIEW_TYPE_HEADER_LOADING == viewType) {
            viewHolder = onCreateLoadingViewHolder(parent);
        } else {
            viewHolder = super.onCreateHeaderItemViewHolder(parent, viewType);
        }

        return viewHolder;
    }

    @NonNull
    @Override
    public PlaygroundReport getGroupChild(final int groupParentIndex, final int groupChildIndex) {
        final PlaygroundReport groupChild;

        final val sportComplexes = getSportComplexes();

        if (sportComplexes != null && 0 <= groupParentIndex &&
            groupParentIndex < sportComplexes.size()) {
            final val sportComplex = sportComplexes.get(groupParentIndex);
            final val playgrounds = sportComplex.getPlaygrounds();

            if (playgrounds != null && 0 <= groupChildIndex &&
                groupChildIndex < playgrounds.size()) {
                groupChild = playgrounds.get(groupChildIndex);
            } else {
                throw new IndexOutOfBoundsException(
                    "No such child: " + groupChildIndex + " in group: " + groupParentIndex);
            }
        } else {
            throw new IndexOutOfBoundsException("No such group: " + groupParentIndex);
        }

        return groupChild;
    }

    @Override
    public int getGroupChildCountOfGroup(final int groupParentIndex) {
        final int groupChildCount;

        final val sportComplexes = getSportComplexes();

        if (sportComplexes != null && 0 <= groupParentIndex &&
            groupParentIndex < sportComplexes.size()) {
            final val sportComplex = sportComplexes.get(groupParentIndex);
            final val playgrounds = sportComplex.getPlaygrounds();
            groupChildCount = playgrounds == null ? 0 : playgrounds.size();
        } else {
            throw new IndexOutOfBoundsException("No such group: " + groupParentIndex);
        }

        return groupChildCount;
    }

    @NonNull
    @Override
    public SportComplexReport getGroupParent(final int groupParentIndex) {
        final SportComplexReport groupParent;

        final val sportComplexes = getSportComplexes();

        if (sportComplexes != null && 0 <= groupParentIndex &&
            groupParentIndex < sportComplexes.size()) {
            groupParent = sportComplexes.get(groupParentIndex);
        } else {
            throw new IndexOutOfBoundsException("No such group: " + groupParentIndex);
        }

        return groupParent;
    }

    @Override
    public int getGroupParentCount() {
        final int groupParentCount;

        final val sportComplexes = getSportComplexes();
        groupParentCount = sportComplexes == null ? 0 : sportComplexes.size();

        return groupParentCount;
    }

    @NonNull
    @Override
    protected ExtendedRecyclerViewHolder onCreateGroupChildViewHolder(
        final ViewGroup parent, final int viewType) {
        final val view = inflateView(R.layout.layout_navigation_playground_item, parent);

        return new PlaygroundViewHolder(view);
    }

    @NonNull
    @Override
    protected ExtendedRecyclerViewHolder onCreateGroupParentViewHolder(
        final ViewGroup parent, final int viewType) {
        final val view = inflateView(R.layout.layout_navigation_sport_complex_item, parent);

        return new SportComplexViewHolder(view);
    }

    public void notifySportComplexesChanged() {
        resetGroupExpandState();
        _notConfirmedOrdersCount.clear();

        notifyDataSetChanged();
    }

    public void setSportComplexesLoading(final boolean sportComplexesLoading) {
        if (_sportComplexesLoading != sportComplexesLoading) {
            _sportComplexesLoading = sportComplexesLoading;

            if (_sportComplexesLoading) {
                notifyItemInserted(getHeaderItemCount());
            } else {
                notifyItemRemoved(getHeaderItemCount() - 1);
            }
        }
    }

    @Nullable
    protected final Integer findPlaygroundPosition(final long playgroundId) {
        return findGroupChildPosition(new Predicate<PlaygroundReport>() {
            @Override
            public boolean apply(final PlaygroundReport playground) {
                return playground.getId() == playgroundId;
            }
        });
    }

    @Override
    protected void onBindGroupChildViewHolder(
        @NonNull final ExtendedRecyclerViewHolder holder,
        @NonNull final PlaygroundReport playground,
        final int position,
        final int groupParentIndex,
        final int groupChildIndex) {
        super.onBindGroupChildViewHolder(holder,
                                         playground,
                                         position,
                                         groupParentIndex,
                                         groupChildIndex);

        final val context = holder.getContext();

        final val selected = Objects.equals(playground.getId(), getSelectedPlaygroundId());
        final val playgroundHolder = (PlaygroundViewHolder) holder;
        playgroundHolder.playgroundNameView.setText(playground.getName());

        final val notConfirmedOrdersCount =
            NumberUtils.defaultIfNull(getNotConfirmedOrdersCount(playground));

        if (notConfirmedOrdersCount > 0) {
            playgroundHolder.playgroundOrdersCountView.setVisibility(View.VISIBLE);

            final val formattedCount = String.valueOf(notConfirmedOrdersCount);
            playgroundHolder.playgroundOrdersCountView.setText(formattedCount);
        } else {
            playgroundHolder.playgroundOrdersCountView.setVisibility(View.GONE);
        }

        setupPlaygroundMarker(playgroundHolder, playground);

        //@formatter:off
        final int backgroundColor = selected
                                    ? ThemeUtils.resolveColor(
                                        context,
                                        R.attr.colorControlHighlight)
                                    : Color.TRANSPARENT;
        //@formatter:on
        playgroundHolder.itemView.setBackgroundColor(backgroundColor);
    }

    @Override
    protected void onBindGroupParentViewHolder(
        @NonNull final ExtendedRecyclerViewHolder holder,
        @NonNull final SportComplexReport sportComplex,
        final int position,
        final int groupParentIndex) {
        super.onBindGroupParentViewHolder(holder, sportComplex, position, groupParentIndex);

        final val sportComplexHolder = (SportComplexViewHolder) holder;

        final val expandIconId = isExpanded(groupParentIndex)
                                 ? R.drawable.ic_material_expand_more
                                 : R.drawable.ic_material_expand_less;
        sportComplexHolder.sportComplexExpandIconView.setImageResource(expandIconId);

        sportComplexHolder.sportComplexNameView.setText(sportComplex.getName());
    }

    @CallSuper
    @Override
    protected void onGroupChildClick(final int groupParentIndex, final int groupChildIndex) {
        super.onGroupChildClick(groupParentIndex, groupChildIndex);

        final val playground = getGroupChild(groupParentIndex, groupChildIndex);
        _onPlaygroundClickEvent.rise(new IdEventArgs(playground.getId()));
    }

    protected void onBindLoadingViewHolder(final ExtendedRecyclerViewHolder holder) {
    }

    protected void onBindNavigationViewHolder(
        @NonNull final NavigationViewHolder holder,
        @NonNull final NavigationItem item,
        final int position) {
        Contracts.requireNonNull(holder, "holder == null");
        Contracts.requireNonNull(item, "item == null");

        final val context = holder.getContext();

        holder.labelView.setText(item.getTitleId());
        holder.itemView.setTag(R.id.tag_view_action, item.getNavigateAction());
        holder.itemView.setOnClickListener(_runActionOnClick);

        final val icon = ContextCompat.getDrawable(context, item.getIconId());
        DrawableCompat.setTint(icon,
                               ThemeUtils.resolveColor(context, android.R.attr.textColorSecondary));
        holder.labelView.setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null);

        holder.navigationDividerView.setVisibility(
            position == getHeaderItemCount() + getInnerItemCount() ? View.VISIBLE : View.GONE);
    }

    @NonNull
    protected ExtendedRecyclerViewHolder onCreateLoadingViewHolder(final ViewGroup parent) {
        final val view = inflateView(R.layout.layout_navigation_loading, parent);

        return new ExtendedRecyclerViewHolder(view) {
        };
    }

    @NonNull
    protected NavigationViewHolder onCreateNavigationViewHolder(final ViewGroup parent) {
        final val view = inflateView(R.layout.layout_navigation_item, parent);

        return new NavigationViewHolder(view);
    }

    @CallSuper
    protected void onSelectedPlaygroundIdChanged() {
    }

    @Getter
    @NonNull
    private final List<NavigationItem> _footerNavigationItems = new ArrayList<>();

    @Getter
    @NonNull
    private final List<NavigationItem> _headerNavigationItems = new ArrayList<>();

    @NonNull
    private final LongSparseArray<Integer> _notConfirmedOrdersCount = new LongSparseArray<>();

    @NonNull
    private final ManagedEvent<IdEventArgs> _onPlaygroundClickEvent = Events.createEvent();

    @NonNull
    private final View.OnClickListener _runActionOnClick = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            final val action = (Runnable) v.getTag(R.id.tag_view_action);
            action.run();
        }
    };

    @Getter
    @Nullable
    private Long _selectedPlaygroundId;

    @Getter
    @Nullable
    private List<SportComplexReport> _sportComplexes;

    @Getter
    private boolean _sportComplexesLoading;

    @Nullable
    private Integer getNotConfirmedOrdersCount(@NonNull final PlaygroundReport playground) {
        Contracts.requireNonNull(playground, "playground == null");

        final val playgroundId = playground.getId();

        final val notConfirmedOrdersCount = _notConfirmedOrdersCount.get(playgroundId);

        return notConfirmedOrdersCount == null
               ? playground.getNotConfirmedOrdersCount()
               : notConfirmedOrdersCount;
    }

    private void setupPlaygroundMarker(
        @NonNull final PlaygroundViewHolder holder, @NonNull final PlaygroundReport playground) {
        Contracts.requireNonNull(holder, "holder == null");
        Contracts.requireNonNull(playground, "playground == null");

        final val context = holder.getContext();
        final val marker = ContextCompat.getDrawable(context, R.drawable.ic_circle);

        final val color = ProSportApiDataUtils.parseColor(playground.getColor());
        if (color != null) {
            DrawableCompat.setTint(marker, color);
        } else {
            // TODO: 20.04.2017 fix playground default color
            DrawableCompat.setTint(marker, ThemeUtils.resolveColor(context, R.attr.colorPrimary));
        }

        holder.playgroundNameView.setCompoundDrawablesRelativeWithIntrinsicBounds(marker,
                                                                                  null,
                                                                                  null,
                                                                                  null);
    }
}
