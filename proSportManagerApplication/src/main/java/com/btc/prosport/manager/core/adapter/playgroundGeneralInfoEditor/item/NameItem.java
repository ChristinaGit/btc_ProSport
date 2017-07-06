package com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.item;

import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.GeneralInfoAdapter;
import com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.GeneralInfoItem;
import com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.ValidationItem;

@Accessors(prefix = "_")
public class NameItem implements GeneralInfoItem, ValidationItem {
    @Override
    public void clearError() {
        _showError = false;
        _error = 0;
    }

    @StringRes
    @Override
    public int getError() {
        return _error;
    }

    @Override
    public boolean isShowError() {
        return _showError;
    }

    @Override
    public void validate() {
        if (TextUtils.isEmpty(getName())) {
            _showError = true;
            _error = R.string.message_error_field_required;
        } else {
            _showError = false;
        }
    }

    @Override
    public int getItemType() {
        return GeneralInfoAdapter.VIEW_TYPE_NAME;
    }

    @StringRes
    private int _error;

    @Getter
    @Setter
    @Nullable
    private String _name;

    private boolean _showError;
}
