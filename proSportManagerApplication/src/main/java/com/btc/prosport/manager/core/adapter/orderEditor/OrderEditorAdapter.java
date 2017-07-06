package com.btc.prosport.manager.core.adapter.orderEditor;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.event.Events;
import com.btc.common.event.notice.ManagedNoticeEvent;
import com.btc.common.event.notice.NoticeEvent;
import com.btc.common.extension.view.recyclerView.decoration.DividerItemDecoration;
import com.btc.common.extension.view.recyclerView.decoration.DividerItemDecoration.DecorationMode;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.common.utility.listener.SimpleTextWatcher;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.adapter.orderEditor.viewHolder.PlayerNameViewHolder;
import com.btc.prosport.manager.core.adapter.orderEditor.viewHolder.PlayerPhoneViewHolder;
import com.btc.prosport.manager.core.adapter.repeatableIntervals.RepeatableIntervalsAdapter;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

@Accessors(prefix = "_")
public class OrderEditorAdapter extends RepeatableIntervalsAdapter {
    private static final String _LOG_TAG = ConstantBuilder.logTag(OrderEditorAdapter.class);

    public static final int VIEW_TYPE_PLAYER_PHONE = newViewType();

    public static final int VIEW_TYPE_PLAYER_FIRST_NAME = newViewType();

    public static final int VIEW_TYPE_PLAYER_LAST_NAME = newViewType();

    private static final int[] HEADER_ITEMS_VIEW_TYPES = new int[]{
        VIEW_TYPE_PLAYER_PHONE, VIEW_TYPE_PLAYER_FIRST_NAME, VIEW_TYPE_PLAYER_LAST_NAME};

    @NonNull
    public final NoticeEvent getPickPlayerPhoneEvent() {
        return _pickPlayerPhoneEvent;
    }

    public final void setPlayerFirstName(@Nullable final String playerFirstName) {
        if (!Objects.equals(_playerFirstName, playerFirstName)) {
            _playerFirstName = playerFirstName;

            onPlayerFirstNameChanged();
        }
    }

    public final void setPlayerLastName(@Nullable final String playerLastName) {
        if (!Objects.equals(_playerLastName, playerLastName)) {
            _playerLastName = playerLastName;

            onPlayerLastNameChanged();
        }
    }

    public final void setPlayerPhone(@Nullable final String playerPhone) {
        if (!Objects.equals(_playerPhone, playerPhone)) {
            _playerPhone = playerPhone;

            onPlayerPhoneChanged();
        }
    }

    public final boolean validateUserInput() {
        boolean allValid = true;

        if (!isValidPlayerPhone()) {
            allValid = false;
        } else if (!isValidIntervals()) {
            allValid = false;
        }

        if (!allValid) {
            setShowInputErrors(true);
            notifyDataSetChanged();
        }

        return allValid;
    }

    @Override
    public int getHeaderItemCount() {
        return super.getHeaderItemCount() + HEADER_ITEMS_VIEW_TYPES.length;
    }

    @Override
    protected int getHeaderItemViewType(final int position) {
        return HEADER_ITEMS_VIEW_TYPES[getHeaderItemRelativePosition(position)];
    }

    @Override
    protected boolean isHeaderItemViewType(final int viewType) {
        return super.isHeaderItemViewType(viewType) ||
               ArrayUtils.contains(HEADER_ITEMS_VIEW_TYPES, viewType);
    }

    @Override
    public void onBindHeaderItemViewHolder(
        @NonNull final ExtendedRecyclerViewHolder holder, final int position) {
        Contracts.requireNonNull(holder, "holder == null");

        final val viewType = getHeaderItemViewType(position);

        if (VIEW_TYPE_PLAYER_PHONE == viewType) {
            onBindPlayerPhoneViewHolder((PlayerPhoneViewHolder) holder);
        } else if (VIEW_TYPE_PLAYER_FIRST_NAME == viewType) {
            onBindFirstPlayerNameViewHolder((PlayerNameViewHolder) holder);
        } else if (VIEW_TYPE_PLAYER_LAST_NAME == viewType) {
            onBindLastPlayerNameViewHolder((PlayerNameViewHolder) holder);
        } else {
            throw new IllegalArgumentException("Unknown view type: " + viewType);
        }
    }

    @NonNull
    @Override
    public ExtendedRecyclerViewHolder onCreateHeaderItemViewHolder(
        final ViewGroup parent, final int viewType) {
        final ExtendedRecyclerViewHolder holder;

        if (VIEW_TYPE_PLAYER_PHONE == viewType) {
            holder = onCreatePlayerPhoneViewHolder(parent);
        } else if (VIEW_TYPE_PLAYER_FIRST_NAME == viewType) {
            holder = onCreatePlayerFirstNameViewHolder(parent);
        } else if (VIEW_TYPE_PLAYER_LAST_NAME == viewType) {
            holder = onCreatePlayerLastNameViewHolder(parent);
        } else {
            holder = super.onCreateHeaderItemViewHolder(parent, viewType);
        }

        DividerItemDecoration.setDecorationMode(
            holder.itemView,
            VIEW_TYPE_PLAYER_LAST_NAME != viewType ? DecorationMode.NONE : DecorationMode.ALL);

        return holder;
    }

    public void setPlayerFrozen(final boolean playerFrozen) {
        if (playerFrozen != _playerFrozen) {
            _playerFrozen = playerFrozen;

            onPlayerFrozenStateChanged();
        }
    }

    protected boolean isValidPlayerPhone() {
        final val playerPhone = getPlayerPhone();
        return !StringUtils.isEmpty(playerPhone) && !StringUtils.isWhitespace(playerPhone);
    }

    protected void onBindFirstPlayerNameViewHolder(@NonNull final PlayerNameViewHolder holder) {
        Contracts.requireNonNull(holder, "holder == null");

        holder.playerNameView.removeTextChangedListener(_playerFirstNameWatcher);

        holder.playerNameView.setText(getPlayerFirstName());

        holder.playerNameView.addTextChangedListener(_playerFirstNameWatcher);

        final val playerFrozen = isPlayerFrozen();
        holder.playerNameView.setEnabled(!playerFrozen);
        holder.playerNameView.setFocusable(!playerFrozen);
        holder.playerNameView.setFocusableInTouchMode(!playerFrozen);
    }

    protected void onBindLastPlayerNameViewHolder(@NonNull final PlayerNameViewHolder holder) {
        Contracts.requireNonNull(holder, "holder == null");

        holder.playerNameView.removeTextChangedListener(_playerLastNameWatcher);

        holder.playerNameView.setText(getPlayerLastName());

        holder.playerNameView.addTextChangedListener(_playerLastNameWatcher);

        final val playerFrozen = isPlayerFrozen();
        holder.playerNameView.setEnabled(!playerFrozen);
        holder.playerNameView.setFocusable(!playerFrozen);
        holder.playerNameView.setFocusableInTouchMode(!playerFrozen);
    }

    protected void onBindPlayerPhoneViewHolder(@NonNull final PlayerPhoneViewHolder holder) {
        Contracts.requireNonNull(holder, "holder == null");

        final val context = holder.getContext();

        final String playerPhoneError;

        final val playerPhone = getPlayerPhone();
        holder.playerPhoneView.setText(playerPhone);

        if (isShowInputErrors() && !isValidPlayerPhone()) {
            playerPhoneError = context.getString(R.string.message_error_required_field);
        } else {
            playerPhoneError = null;
        }

        holder.playerPhoneContainerView.setError(playerPhoneError);
    }

    @NonNull
    protected PlayerNameViewHolder onCreatePlayerFirstNameViewHolder(final ViewGroup parent) {
        final val view = inflateView(R.layout.activity_order_editor_player_name, parent);

        final val holder = new PlayerNameViewHolder(view);

        final val context = parent.getContext();
        final val hint = context.getString(R.string.order_editor_player_first_name_hint);
        holder.playerNameContainerView.setHint(hint);

        return holder;
    }

    @NonNull
    protected PlayerNameViewHolder onCreatePlayerLastNameViewHolder(
        @NonNull final ViewGroup parent) {
        Contracts.requireNonNull(parent, "parent == null");

        final val view = inflateView(R.layout.activity_order_editor_player_name, parent);

        final val holder = new PlayerNameViewHolder(view);

        final val context = parent.getContext();
        final val hint = context.getString(R.string.order_editor_player_last_name_hint);
        holder.playerNameContainerView.setHint(hint);

        return holder;
    }

    @NonNull
    protected PlayerPhoneViewHolder onCreatePlayerPhoneViewHolder(@NonNull final ViewGroup parent) {
        Contracts.requireNonNull(parent, "parent == null");

        final val view = inflateView(R.layout.activity_order_editor_player_phone, parent);

        final val holder = new PlayerPhoneViewHolder(view);

        holder.playerPhoneView.setOnClickListener(_risePickPlayerPhoneOnClick);

        return holder;
    }

    @CallSuper
    protected void onPlayerFirstNameChanged() {
        notifyItemChanged(ArrayUtils.indexOf(HEADER_ITEMS_VIEW_TYPES, VIEW_TYPE_PLAYER_FIRST_NAME));
    }

    @CallSuper
    protected void onPlayerFrozenStateChanged() {
        notifyItemChanged(ArrayUtils.indexOf(HEADER_ITEMS_VIEW_TYPES, VIEW_TYPE_PLAYER_FIRST_NAME));
        notifyItemChanged(ArrayUtils.indexOf(HEADER_ITEMS_VIEW_TYPES, VIEW_TYPE_PLAYER_LAST_NAME));
    }

    @CallSuper
    protected void onPlayerLastNameChanged() {
        notifyItemChanged(ArrayUtils.indexOf(HEADER_ITEMS_VIEW_TYPES, VIEW_TYPE_PLAYER_LAST_NAME));
    }

    @CallSuper
    protected void onPlayerPhoneChanged() {
        notifyItemChanged(ArrayUtils.indexOf(HEADER_ITEMS_VIEW_TYPES, VIEW_TYPE_PLAYER_PHONE));
    }

    @NonNull
    private final ManagedNoticeEvent _pickPlayerPhoneEvent = Events.createNoticeEvent();

    @NonNull
    private final View.OnClickListener _risePickPlayerPhoneOnClick = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            _pickPlayerPhoneEvent.rise();
        }
    };

    @Getter
    @Nullable
    private String _playerFirstName;

    @NonNull
    private final TextWatcher _playerFirstNameWatcher = new SimpleTextWatcher() {
        @Override
        public void onTextChanged(
            final CharSequence s, final int start, final int before, final int count) {
            _playerFirstName = s.toString();
        }
    };

    @Getter
    private boolean _playerFrozen = false;

    @Getter
    @Nullable
    private String _playerLastName;

    @NonNull
    private final TextWatcher _playerLastNameWatcher = new SimpleTextWatcher() {
        @Override
        public void onTextChanged(
            final CharSequence s, final int start, final int before, final int count) {
            _playerLastName = s.toString();
        }
    };

    @Getter
    @Nullable
    private String _playerPhone;
}
