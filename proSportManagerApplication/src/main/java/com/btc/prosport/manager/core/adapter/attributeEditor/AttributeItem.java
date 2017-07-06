package com.btc.prosport.manager.core.adapter.attributeEditor;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.btc.prosport.api.model.Attribute;

@ToString
@Accessors(prefix = "_")
public class AttributeItem implements Attribute {

    public AttributeItem(final long id, @Nullable final String name) {
        _id = id;
        _name = name;
    }

    @Getter
    private final long _id;

    @Getter
    @Nullable
    private final String _name;

    @Getter
    @Nullable
    private String _icon;

    @Getter
    @Setter
    private boolean _isOff;
}
