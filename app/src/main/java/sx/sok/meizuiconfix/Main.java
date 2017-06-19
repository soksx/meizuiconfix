package sx.sok.meizuiconfix;

/**
 * Created by R9280 on 10/03/2017.
 */



import android.content.ComponentName;
import android.content.Intent;
import android.content.res.XModuleResources;
import android.content.res.XResources;
import android.view.View;
import android.widget.LinearLayout;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Main implements IXposedHookLoadPackage, IXposedHookInitPackageResources, IXposedHookZygoteInit {
    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("Loaded app: " + lpparam.packageName);
        ReplaceMethod.InitResources(lpparam);
    }
    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) throws Throwable {
        XResources.setSystemWideReplacement("android", "bool", "config_unplugTurnsOnScreen", false);
    }
    @Override
    public void handleInitPackageResources(final InitPackageResourcesParam resparam) throws Throwable {
        XposedBridge.log("Loaded res from app: " + resparam.packageName);
        ReplaceResources.InitResources(resparam);
    }
}
