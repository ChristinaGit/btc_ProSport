package com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.item;

import android.support.annotation.StringRes;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.contract.Contracts;
import com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.GeneralInfoAdapter;
import com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.GeneralInfoItem;

@Accessors(prefix = "_")
public class TitleItem implements GeneralInfoItem {
    public TitleItem(@StringRes final int title) {
        Contracts.requireNonNull(title, "_title == null");
        _title = title;
    }

    @Override
    public int getItemType() {
        return GeneralInfoAdapter.VIEW_TYPE_TITLE;
    }

    @StringRes
    @Getter
    private final int _title;
}
