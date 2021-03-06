package com.rfachrur.sunshine.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.rfachrur.sunshine.R;
import com.rfachrur.sunshine.util.Utility;
import com.rfachrur.sunshine.sync.SunshineSyncAdapter;


public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onPause() {
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add 'general' preferences, defined in the XML file
        addPreferencesFromResource(R.xml.pref_general);

        // For all preferences, attach an OnPreferenceChangeListener so the UI summary can be
        // updated when the preference changes.
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_location_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_units_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_notification_key)));

    }

    /**
     * Attaches a listener so the summary is always updated with the preference value.
     * Also fires the listener once, to initialize the summary (so it shows up before the value
     * is changed.)
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);

        if (preference instanceof CheckBoxPreference) {
            // Trigger the listener immediately with the preference's
            // current value.
            onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getBoolean(preference.getKey(), true));
        } else {
            // Trigger the listener immediately with the preference's
            // current value.
            onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        }

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            String prefKey = preference.getKey();

            if (prefKey.equals(getString(R.string.pref_location_key))) {
                @SunshineSyncAdapter.LocationStatus int locationStatus = Utility.getSyncStatus(this);
                switch (locationStatus) {
                    case SunshineSyncAdapter.LOCATION_STATUS_OK:
                        break;
                    case SunshineSyncAdapter.LOCATION_STATUS_INVALID:
                        stringValue = getString(R.string.pref_location_error_description, stringValue);
                        break;
                    case SunshineSyncAdapter.LOCATION_STATUS_UNKNOWN:
                        stringValue = getString(R.string.pref_location_unknown_description, stringValue);
                        break;
                    default:
                        break;
                }

            } else if (prefKey.equals(getString(R.string.pref_notification_key))) {
                if (value.equals(Boolean.FALSE)) {
                    stringValue = getString(R.string.pref_notification_disabled_description);
                } else {
                    stringValue = getString(R.string.pref_notification_enabled_description);
                }
            }

            preference.setSummary(stringValue);

        }

        SunshineSyncAdapter.syncImmediately(this);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public Intent getParentActivityIntent() {
        return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_location_key))) {
            //location changed
            Utility.setSynchStatus(this, SunshineSyncAdapter.LOCATION_STATUS_UNKNOWN);
            SunshineSyncAdapter.syncImmediately(this);
        }
    }

}
