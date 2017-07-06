package com.btc.prosport.manager.core.utility;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

import lombok.val;

import com.btc.common.contract.Contracts;
import com.btc.prosport.manager.R;

public final class PlaygroundUtils {
    @ColorInt
    public static int getPlaygroundColor(@NonNull final Context context, final long playgroundId) {
        final val playgroundColors = context.getResources().getIntArray(R.array.playgroundColors);

        return playgroundColors[(int) (playgroundId % (playgroundColors.length))];
    }

    private PlaygroundUtils() {
        Contracts.unreachable();
    }
}
