package com.btc.prosport.screen.fragment.settings;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.event.Events;
import com.btc.common.event.generic.Event;
import com.btc.common.event.generic.ManagedEvent;
import com.btc.common.utility.DrawableUtils;
import com.btc.prosport.api.model.City;
import com.btc.prosport.api.model.User;
import com.btc.prosport.common.R;
import com.btc.prosport.core.eventArgs.ChangeUserCityEventArgs;
import com.btc.prosport.core.eventArgs.ChangeUserEmailEventArgs;
import com.btc.prosport.core.eventArgs.ChangeUserFirstNameEventArgs;
import com.btc.prosport.core.eventArgs.ChangeUserLastNameEventArgs;
import com.btc.prosport.core.eventArgs.ChangeUserPhoneNumberEventArgs;
import com.btc.prosport.core.eventArgs.LogoutEventArgs;
import com.btc.prosport.core.manager.account.ProSportAccount;

import java.util.ArrayList;
import java.util.List;

@Accessors(prefix = "_")
public final class UserSettingsFragment extends PreferenceFragment {
    private static final String _LOG_TAG = ConstantBuilder.logTag(UserSettingsFragment.class);

    private static final String SETTINGS_USER_NO_CITY_ID = "";

    @NonNull
    public final Event<LogoutEventArgs> getLogoutEvent() {
        return _logoutEvent;
    }

    public final void notifyAccountsChanged() {
        final val accounts = getAccounts();
        if (_logoutPreference != null) {
            _logoutPreference.setEnabled(accounts != null && !accounts.isEmpty());
        }
    }

    public void displayCities(final List<? extends City> cities) {
        if (cities != null && !cities.isEmpty()) {
            _cities.clear();
            _cities.addAll(cities);
            if (_citiesView != null) {
                final int size = cities.size();
                final val cityNames = new CharSequence[size + 1];
                final val cityIds = new CharSequence[size + 1];
                cityIds[0] = SETTINGS_USER_NO_CITY_ID;
                cityNames[0] = getString(R.string.settings_user_no_city);
                for (int i = 0; i < size; i++) {
                    cityNames[i + 1] = cities.get(i).getName();
                    cityIds[i + 1] = String.valueOf(cities.get(i).getId());
                }
                _citiesView.setEntries(cityNames);
                _citiesView.setEntryValues(cityIds);
                selectUserCity();
            }
        }
    }

    public void displayUser(@Nullable final User user) {
        _user = user;

        final val sharedPreferences = getPreferenceScreen().getSharedPreferences();

        if (_firstNameView != null) {
            final val key = getString(R.string.preferences_key_user_first_name);
            final val firstName =
                user != null ? user.getFirstName() : sharedPreferences.getString(key, null);
            _firstNameView.setSummary(firstName);
            _firstNameView.setText(firstName);
        }
        if (_lastNameView != null) {
            final val key = getString(R.string.preferences_key_user_last_name);
            final val lastName =
                user != null ? user.getLastName() : sharedPreferences.getString(key, null);
            _lastNameView.setSummary(lastName);
            _lastNameView.setText(lastName);
        }
        if (_phoneNumberView != null) {
            final val key = getString(R.string.preferences_key_user_phone_number);
            final val phoneNumber =
                user != null ? user.getPhoneNumber() : sharedPreferences.getString(key, null);
            _phoneNumberView.setSummary(phoneNumber);
            _phoneNumberView.setText(phoneNumber);
        }
        if (_emailView != null) {
            final val key = getString(R.string.preferences_key_user_email);
            final val email =
                user != null ? user.getEmail() : sharedPreferences.getString(key, null);
            _emailView.setSummary(email);
            _emailView.setText(email);
        }

        if (_citiesView != null) {
            selectUserCity();
        }
    }

    @NonNull
    public Event<ChangeUserCityEventArgs> getChangeCityEvent() {
        return _changeCityEvent;
    }

    @NonNull
    public Event<ChangeUserEmailEventArgs> getChangeEmailEvent() {
        return _changeEmailEvent;
    }

    @NonNull
    public Event<ChangeUserFirstNameEventArgs> getChangeFirstNameEvent() {
        return _changeFirstNameEvent;
    }

    @NonNull
    public Event<ChangeUserLastNameEventArgs> getChangeLastNameEvent() {
        return _changeLastNameEvent;
    }

    @NonNull
    public Event<ChangeUserPhoneNumberEventArgs> getChangePhoneNumberEvent() {
        return _changePhoneNumberEvent;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

        bindViews();

        final val context = getActivity();
        final int colorAccent = R.attr.colorAccent;
        if (_phoneNumberView != null) {
            _phoneNumberView.setOnPreferenceChangeListener(_onChangePhoneNumberPreference);
            final val icon = ContextCompat.getDrawable(context, R.drawable.ic_material_local_phone);
            DrawableUtils.setTint(context, icon, colorAccent);
            _phoneNumberView.setIcon(icon);
        }
        if (_citiesView != null) {
            _citiesView.setOnPreferenceChangeListener(_onChangeCityPreference);
            final val icon =
                ContextCompat.getDrawable(context, R.drawable.ic_material_location_city);
            DrawableUtils.setTint(context, icon, colorAccent);
            _citiesView.setIcon(icon);
        }
        if (_emailView != null) {
            _emailView.setOnPreferenceChangeListener(_onChangeEmailPreference);
            final val icon = ContextCompat.getDrawable(context, R.drawable.ic_material_email);
            DrawableUtils.setTint(context, icon, colorAccent);
            _emailView.setIcon(icon);
        }
        if (_firstNameView != null) {
            _firstNameView.setOnPreferenceChangeListener(_onChangeFirstNamePreference);
            final val icon = ContextCompat.getDrawable(context, R.drawable.ic_material_person);
            DrawableUtils.setTint(context, icon, colorAccent);
            _firstNameView.setIcon(icon);
        }

        if (_lastNameView != null) {
            _lastNameView.setOnPreferenceChangeListener(_onChangeLastNamePreference);
            final val icon = ContextCompat.getDrawable(context, R.drawable.ic_material_person);
            DrawableUtils.setTint(context, icon, colorAccent);
            _lastNameView.setIcon(icon);
        }
        if (_citiesView != null) {
            _citiesView.setEntries(new String[]{getString(R.string.settings_user_no_city)});
            _citiesView.setEntryValues(new String[]{String.valueOf(SETTINGS_USER_NO_CITY_ID)});
            _citiesView.setValueIndex(0);
        }
        if (_logoutPreference != null) {
            _logoutPreference.setOnPreferenceClickListener(_logoutOnClick);
        }
    }

    @NonNull
    private final ManagedEvent<ChangeUserCityEventArgs> _changeCityEvent = Events.createEvent();

    @NonNull
    private final ManagedEvent<ChangeUserEmailEventArgs> _changeEmailEvent = Events.createEvent();

    @NonNull
    private final ManagedEvent<ChangeUserFirstNameEventArgs> _changeFirstNameEvent =
        Events.createEvent();

    @NonNull
    private final ManagedEvent<ChangeUserLastNameEventArgs> _changeLastNameEvent =
        Events.createEvent();

    @NonNull
    private final ManagedEvent<ChangeUserPhoneNumberEventArgs> _changePhoneNumberEvent =
        Events.createEvent();

    @NonNull
    private final List<City> _cities = new ArrayList<>();

    @NonNull
    private final ManagedEvent<LogoutEventArgs> _logoutEvent = Events.createEvent();

    @Setter
    @Getter
    @Nullable
    private List<ProSportAccount> _accounts;

    @NonNull
    private final Preference.OnPreferenceClickListener _logoutOnClick =
        new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(final Preference preference) {
                final val accounts = getAccounts();

                if (accounts != null && !accounts.isEmpty()) {
                    _logoutEvent.rise(new LogoutEventArgs(accounts.get(0)));
                }

                return true;
            }
        };

    @Nullable
    private ListPreference _citiesView;

    @NonNull
    private final Preference.OnPreferenceChangeListener _onChangeCityPreference =
        new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(final Preference preference, final Object newValue) {
                if (_citiesView != null) {
                    final val cityId = newValue.toString();
                    _changeCityEvent.rise(new ChangeUserCityEventArgs(cityId,
                                                                      _citiesView.getValue()));

                    return true;
                } else {
                    return false;
                }
            }
        };

    @Nullable
    private EditTextPreference _emailView;

    @NonNull
    private final Preference.OnPreferenceChangeListener _onChangeEmailPreference =
        new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(final Preference preference, final Object newValue) {
                if (newValue != null) {
                    final val email =
                        String.valueOf(_emailView != null ? _emailView.getSummary() : null);
                    _changeEmailEvent.rise(new ChangeUserEmailEventArgs(newValue.toString(),
                                                                        email));
                    return true;
                }
                return false;
            }
        };

    @Nullable
    private EditTextPreference _firstNameView;

    @Nullable
    private final Preference.OnPreferenceChangeListener _onChangeFirstNamePreference =
        new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(final Preference preference, final Object newValue) {
                final val userName =
                    String.valueOf(_firstNameView != null ? _firstNameView.getSummary() : null);
                _changeFirstNameEvent.rise(new ChangeUserFirstNameEventArgs(newValue.toString(),
                                                                            userName));
                return true;
            }
        };

    @Nullable
    private EditTextPreference _lastNameView;

    @NonNull
    private final Preference.OnPreferenceChangeListener _onChangeLastNamePreference =
        new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(final Preference preference, final Object newValue) {
                final String userName =
                    String.valueOf(_lastNameView != null ? _lastNameView.getSummary() : null);
                _changeLastNameEvent.rise(new ChangeUserLastNameEventArgs(newValue.toString(),
                                                                          userName));
                return true;
            }
        };

    @Nullable
    private Preference _logoutPreference;

    @Nullable
    private EditTextPreference _phoneNumberView;

    @NonNull
    private final Preference.OnPreferenceChangeListener _onChangePhoneNumberPreference =
        new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(final Preference preference, final Object newValue) {
                final val phone =
                    String.valueOf(_phoneNumberView != null ? _phoneNumberView.getSummary() : null);
                _changePhoneNumberEvent.rise(new ChangeUserPhoneNumberEventArgs(newValue.toString(),
                                                                                phone));

                return true;
            }
        };

    @Nullable
    private User _user;

    private void bindViews() {
        _citiesView = (ListPreference) findPreference(R.string.preferences_key_user_city);
        _firstNameView =
            (EditTextPreference) findPreference(R.string.preferences_key_user_first_name);
        _lastNameView =
            (EditTextPreference) findPreference(R.string.preferences_key_user_last_name);
        _phoneNumberView =
            (EditTextPreference) findPreference(R.string.preferences_key_user_phone_number);
        _emailView = (EditTextPreference) findPreference(R.string.preferences_key_user_email);
        _logoutPreference = findPreference(R.string.preferences_key_logout);
    }

    @Nullable
    private Preference findPreference(@StringRes final int keyId) {
        return findPreference(getString(keyId));
    }

    private void selectUserCity() {
        int position = -1;
        if (_user != null && !_cities.isEmpty()) {
            final val city = _user.getCity();
            if (city != null) {
                final String cityName = city.getName();
                _citiesView.setSummary(cityName);
                final int size = _cities.size();
                for (int i = 0; i < size; i++) {
                    final String newCityName =
                        _cities.get(i) != null ? _cities.get(i).getName() : null;
                    if (newCityName != null && newCityName.equals(cityName)) {
                        position = i;
                        break;
                    }
                }
            } else {
                _citiesView.setSummary(null);
            }
        }
        _citiesView.setValueIndex(position + 1);
    }
}