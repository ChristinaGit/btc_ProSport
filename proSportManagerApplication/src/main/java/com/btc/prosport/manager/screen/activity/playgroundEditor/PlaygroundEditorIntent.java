package com.btc.prosport.manager.screen.activity.playgroundEditor;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.val;

import com.btc.common.contract.Contracts;
import com.btc.prosport.api.ProSportContract;

public final class PlaygroundEditorIntent {
    @NonNull
    public static IntentBuilder builder(@NonNull final Context context) {
        Contracts.requireNonNull(context, "context == null");

        return new IntentBuilder(context);
    }

    @Nullable
    public static Long getPlaygroundId(@NonNull final Intent intent) {
        Contracts.requireNonNull(intent, "intent == null");

        Long playgroundId = null;

        final val data = intent.getData();
        if (data != null) {
            final val code = ProSportContract.getCode(data);

            if (code == ProSportContract.CODE_PLAYGROUND) {
                playgroundId = ContentUris.parseId(data);
            }
        }

        return playgroundId;
    }

    private PlaygroundEditorIntent() {
        Contracts.unreachable();
    }

    public static final class IntentBuilder {
        @NonNull
        public final Intent build(final long playgroundId) {
            final val intent = new Intent(_context, PlaygroundEditorActivity.class);

            intent.setAction(Intent.ACTION_EDIT);
            intent.setData(ProSportContract.getPlaygroundUri(playgroundId));

            return intent;
        }

        @NonNull
        private final Context _context;

        private IntentBuilder(@NonNull final Context context) {
            Contracts.requireNonNull(context, "context == null");

            _context = context;
        }
    }
}
