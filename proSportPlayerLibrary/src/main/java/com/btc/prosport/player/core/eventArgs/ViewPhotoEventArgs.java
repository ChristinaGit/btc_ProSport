package com.btc.prosport.player.core.eventArgs;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.extension.eventArgs.IdEventArgs;

@Accessors(prefix = "_")
public final class ViewPhotoEventArgs extends IdEventArgs {
    public ViewPhotoEventArgs(final long id, final int position) {
        super(id);

        _position = position;
    }

    @Getter
    private final int _position;
}
