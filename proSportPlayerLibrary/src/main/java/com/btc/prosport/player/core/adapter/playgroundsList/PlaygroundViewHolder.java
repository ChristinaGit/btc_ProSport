package com.btc.prosport.player.core.adapter.playgroundsList;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.player.R;

public final class PlaygroundViewHolder extends ExtendedRecyclerViewHolder {
    @NonNull
    public final TextView playgroundCoveringView;

    @NonNull
    public final TextView playgroundNameView;

    @NonNull
    public final ImageView playgroundPhotoView;

    @NonNull
    public final View playgroundReserveActionView;

    @NonNull
    public final TextView playgroundSizeView;

    public PlaygroundViewHolder(@NonNull final View itemView) {
        super(Contracts.requireNonNull(itemView, "itemView == null"));

        playgroundNameView = (TextView) itemView.findViewById(R.id.playground_name);
        playgroundPhotoView = (ImageView) itemView.findViewById(R.id.playground_photo);
        playgroundCoveringView = (TextView) itemView.findViewById(R.id.playground_covering);
        playgroundSizeView = (TextView) itemView.findViewById(R.id.playground_size);
        playgroundReserveActionView = itemView.findViewById(R.id.playground_reserve_action);
    }
}
