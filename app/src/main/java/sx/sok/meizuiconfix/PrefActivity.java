package sx.sok.meizuiconfix;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.MenuItem;


public class PrefActivity extends PreferenceActivity {

    static SharedPreferences sp, sp1;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(android.R.style.Theme_Light);
        setTheme(android.R.style.Theme_Material_Settings);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);
        setTheme(android.R.style.Theme_Material_Settings);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp1 = getSharedPreferences("settings", MODE_WORLD_READABLE);
        setTheme(android.R.style.Theme_Material_Settings);

        this.findPreference("hide_app").setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newVal) {
                        boolean b = (Boolean) newVal;
                        b = !b;
                        PackageManager pm = PrefActivity.this.getPackageManager();
                        pm.setComponentEnabledSetting(
                                new ComponentName(PrefActivity.this, "sx.sok.meizuiconfix.PrefActivityAlias"), b ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                                PackageManager.DONT_KILL_APP);
                        return true;
                    }
                });

    }
    protected void onStop() { // save preferences for ability to read it in xposed
        super.onStop();
        sp1 = getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp1.edit();
        editor.putString("scale", sp.getString("scale", "46"));
        editor.putString("padding", sp.getString("padding", "2"));
        editor.putBoolean("disable_MC", sp.getBoolean("disable_MC", false));
        editor.putBoolean("chk_settings", sp.getBoolean("chk_settings", false));
        editor.putBoolean("chk_clock", sp.getBoolean("chk_clock", false));
        editor.putBoolean("chk_calendar", sp.getBoolean("chk_calendar", false));
        editor.commit();
    }
}
