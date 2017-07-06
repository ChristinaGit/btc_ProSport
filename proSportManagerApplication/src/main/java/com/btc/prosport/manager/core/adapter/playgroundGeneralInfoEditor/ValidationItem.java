package com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor;

import android.support.annotation.StringRes;

public interface ValidationItem {
    void clearError();

    @StringRes
    int getError();

    boolean isShowError();

    void validate();
}
