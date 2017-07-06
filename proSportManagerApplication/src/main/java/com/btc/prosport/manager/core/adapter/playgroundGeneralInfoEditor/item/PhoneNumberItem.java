package com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.item;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.GeneralInfoAdapter;
import com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.GeneralInfoItem;

@Accessors(prefix = "_")
public class PhoneNumberItem implements GeneralInfoItem {
    public PhoneNumberItem() {
    }

    @Override
    public int getItemType() {
        return GeneralInfoAdapter.VIEW_TYPE_PHONE_NUMBER;
    }

    @Getter
    @Setter
    @Nullable
    private String _phoneNumber;
}
