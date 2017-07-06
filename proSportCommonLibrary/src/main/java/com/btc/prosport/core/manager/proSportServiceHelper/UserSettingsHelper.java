package com.btc.prosport.core.manager.proSportServiceHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.experimental.Accessors;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.sharedPreferences.SharedPreferencesEditorWrapper;
import com.btc.prosport.common.R;

@Accessors(prefix = "_")
public final class UserSettingsHelper extends SharedPreferencesEditorWrapper {

    public UserSettingsHelper(
        @NonNull final SharedPreferences sharedPreferences, @NonNull final Context context) {
        super(Contracts.requireNonNull(sharedPreferences, "sharedPreferences == null"));
        _context = context;
    }

    @Nullable
    public String getNotificationSound() {
        return getString(_context.getString(R.string.preferences_key_notifications_sound));
    }

    public boolean getNotificationVibration() {
        return getBoolean(
            _context.getString(R.string.preferences_key_notifications_vibration),
            _context.getResources().getBoolean(R.bool.user_vibration_default));
    }

    public String getUserCityId() {
        return getString(_context.getString(R.string.preferences_key_user_city));
    }

    public void setUserCityId(@Nullable final String cityId) {
        putString(_context.getString(R.string.preferences_key_user_city), cityId);
    }

    @Nullable
    public String getUserEmail() {
        return getString(_context.getString(R.string.preferences_key_user_email));
    }

    public void setUserEmail(@Nullable final String userEmail) {
        putString(_context.getString(R.string.preferences_key_user_email), userEmail);
    }

    @Nullable
    public String getUserFirstName() {
        return getString(_context.getString(R.string.preferences_key_user_first_name));
    }

    public void setUserFirstName(@Nullable final String firstName) {
        putString(_context.getString(R.string.preferences_key_user_first_name), firstName);
    }

    @Nullable
    public String getUserLastName() {
        return getString(_context.getString(R.string.preferences_key_user_last_name));
    }

    public void setUserLastName(@Nullable final String lastName) {
        putString(_context.getString(R.string.preferences_key_user_last_name), lastName);
    }

    public boolean getUserNotifications() {
        return getBoolean(
            _context.getString(R.string.preferences_key_user_notifications),
            _context.getResources().getBoolean(R.bool.user_enable_notifications_default));
    }

    @Nullable
    public String getUserPhoneNumber() {
        return getString(_context.getString(R.string.preferences_key_user_phone_number));
    }

    public void setUserPhoneNumber(@Nullable final String phoneNumber) {
        putString(_context.getString(R.string.preferences_key_user_phone_number), phoneNumber);
    }

    @NonNull
    private final Context _context;
}
