package sx.sok.meizuiconfix;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;

/**
 * Created by sokk on 18/06/2017.
 */

public class ReplaceResources {
    private static boolean chk_set, chk_clo, chk_cal;
    public static void InitResources(final InitPackageResourcesParam resparam) throws Throwable {
        if (!resparam.packageName.equals("com.android.systemui"))
            return;
        XSharedPreferences pref = new XSharedPreferences("sx.sok.meizuiconfix", "settings");
        chk_set = pref.getBoolean("chk_settings", false);
        chk_cal = pref.getBoolean("chk_calendar", false);
        chk_clo = pref.getBoolean("chk_clock", false);
            resparam.res.hookLayout("com.android.systemui", "layout", "status_bar_expanded_header", new XC_LayoutInflated() {
                @Override
                public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {
                    if(chk_set) {
                        View filterPanel = (View) liparam.view.findViewById(
                                liparam.res.getIdentifier("show_filter_panel", "id", "com.android.systemui")
                        );
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) filterPanel.getLayoutParams();
                        params.rightMargin = 6;
                        filterPanel.setLayoutParams(params);
                        View settingsButton = (View) liparam.view.findViewById(
                                liparam.res.getIdentifier("settings_button", "id", "com.android.systemui")
                        );
                        settingsButton.setVisibility(View.VISIBLE);
                    }
                    if(chk_clo) {
                        View clockView = (View) liparam.view.findViewById(
                                liparam.res.getIdentifier("clock", "id", "com.android.systemui")
                        );
                        clockView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Runtime.getRuntime().exec("service call statusbar 2");
                                    Runtime.getRuntime().exec("am start -n com.android.alarmclock/com.meizu.flyme.alarmclock.DeskClock");
                                } catch (IOException ex) {
                                    XposedBridge.log("Error launching clock activity");
                                }

                            }
                        });
                    }
                    if(chk_cal) {
                        TextView carrierLabel = (TextView) liparam.view.findViewById(
                                liparam.res.getIdentifier("header_carrier_label", "id", "com.android.systemui")
                        );
                        LinearLayout dateLayout = (LinearLayout) carrierLabel.getParent().getParent();
                        dateLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Runtime.getRuntime().exec("service call statusbar 2");
                                    Runtime.getRuntime().exec("am start -n com.android.calendar/com.meizu.flyme.calendar.AllInOneActivity");
                                } catch (IOException ex) {
                                    XposedBridge.log("Error launching clock activity");
                                }

                            }
                        });
                    }
                }
            });
    }
}
