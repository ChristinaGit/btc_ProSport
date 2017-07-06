package com.btc.prosport.manager.core.eventArgs;

import android.support.annotation.NonNull;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.prosport.api.model.Attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Accessors(prefix = "_")
public class ChangePlaygroundAttributesEventArgs extends IdEventArgs {
    public ChangePlaygroundAttributesEventArgs(
        final long playgroundId, @NonNull final ArrayList<Long> newAttributes) {
        super(playgroundId);
        Contracts.requireNonNull(newAttributes, "newAttributes == null");

        _newAttributes = newAttributes;
    }

    @Getter
    @NonNull
    private final List<Long> _newAttributes;
}
