package com.btc.prosport.player.core;

import android.support.annotation.DrawableRes;

public interface SportTypeContext {
    @DrawableRes
    int getNotificationLargeIconId();

    @DrawableRes
    int getNotificationSmallIconId();
}
