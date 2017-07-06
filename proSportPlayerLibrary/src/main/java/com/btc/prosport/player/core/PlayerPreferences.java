package com.btc.prosport.player.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.sharedPreferences.SharedPreferencesEditorWrapper;
import com.btc.prosport.player.R;

public final class PlayerPreferences extends SharedPreferencesEditorWrapper {
    public static final boolean DEFAULT_PLAYER_NOTIFICATIONS_ENABLED = false;

    public boolean getPlayerNotificationsEnabled() {
        return getBoolean(_playerNotificationsKey, DEFAULT_PLAYER_NOTIFICATIONS_ENABLED);
    }

    public void setPlayerNotificationsEnabled(final boolean enabled) {
        putBoolean(_playerNotificationsKey, enabled);
    }

    protected PlayerPreferences(
        @NonNull final Context context, @NonNull final SharedPreferences baseSharedPreferences) {
        super(Contracts.requireNonNull(baseSharedPreferences, "baseSharedPreferences == null"));
        Contracts.requireNonNull(context, "context == null");

        _playerNotificationsKey = context.getString(R.string.player_settings_notifications_key);
        _playerNotificationsSoundKey =
            context.getString(R.string.player_settings_notifications_sound_key);
        _playerNotificationsVibrationKey =
            context.getString(R.string.player_settings_notifications_vibration_key);
    }

    @NonNull
    private final String _playerNotificationsKey;

    @NonNull
    private final String _playerNotificationsSoundKey;

    @NonNull
    private final String _playerNotificationsVibrationKey;
}
