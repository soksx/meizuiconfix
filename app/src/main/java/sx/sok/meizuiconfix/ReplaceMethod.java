package sx.sok.meizuiconfix;


import android.app.Notification;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.service.notification.StatusBarNotification;
import android.util.AttributeSet;
import android.view.View;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

/**
 * Created by R9280 on 10/03/2017.
 */

public class ReplaceMethod {
    private static String padding_str, scale_str, pkg;
    private static int scale, padding;
    private static boolean disable_MC;

    public static void InitResources(final LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.android.systemui"))
            return;
        XSharedPreferences pref = new XSharedPreferences("sx.sok.meizuiconfix", "settings");
        padding_str = pref.getString("padding", "2");
        padding = Integer.parseInt(padding_str);
        scale_str = pref.getString("scale", "46");
        scale = Integer.parseInt(scale_str);
        disable_MC = pref.getBoolean("disable_MC", false);

        ClassLoader classLoader = lpparam.classLoader;
        XC_MethodReplacement mTRUE = new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                return true;
            }
        };
        XC_MethodReplacement mFALSE = new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                return false;
            }
        };

        XposedHelpers.findAndHookMethod("com.flyme.systemui.statusbar.phone.FlymeStatusBarIconUtils", classLoader, "isInternalApp", Context.class, StatusBarNotification.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                param.setResult(true);
            }
        });

        XposedHelpers.findAndHookMethod("com.flyme.systemui.statusbar.phone.FlymeStatusBarIconUtils", classLoader, "isInternalApp", Context.class, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                param.setResult(true);
            }
        });

        if (disable_MC == true) {
            XposedHelpers.findAndHookMethod("com.flyme.systemui.statusbar.phone.MeizuCustomizedIcons",
                    classLoader, "isMeizuCustomizedIcon", String.class, mFALSE);
        }
        /*else {
            XposedHelpers.findAndHookMethod("com.flyme.systemui.statusbar.phone.MeizuCustomizedIcons",
                    classLoader, "isMeizuCustomizedIcon", String.class, mTRUE);
        }*/

        XposedHelpers.findAndHookMethod("com.android.systemui.statusbar.StatusBarIconView", classLoader, "getIcon", Context.class, "com.android.internal.statusbar.StatusBarIcon", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Context ctx = (Context) param.args[0];
                XposedHelpers.findAndHookMethod("com.flyme.systemui.statusbar.phone.MeizuCustomizedIcons", lpparam.classLoader, "isMeizuCustomizedIcon", String.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        pkg = (String) param.args[0];

                    }

                });
                if (pkg.length() == 0){
                    pkg = (String) XposedHelpers.getObjectField(param.args[1], "iconPackage");
                }
                if(isUserApp(ctx, pkg)){
                    if (param.getResult() instanceof BitmapDrawable) {
                        param.setResult(scaleIcon(ctx, (Drawable) param.getResult(), scale));
                    }
                    if (param.getResult() instanceof AnimationDrawable) {
                        param.setResult(scaleAnimation(ctx, (AnimationDrawable) param.getResult(), scale));
                    }
                }
            }
        });
        XposedHelpers.findAndHookConstructor("com.android.systemui.statusbar.StatusBarIconView", classLoader, Context.class, String.class, Notification.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                View v = (View) param.thisObject;
                v.setPadding(padding, 0, padding, 0);
            }
        });
        XposedHelpers.findAndHookConstructor("com.android.systemui.statusbar.StatusBarIconView", classLoader, Context.class, AttributeSet.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                View v = (View) param.thisObject;
                v.setPadding(padding, 0, padding, 0);
            }
        });
    }

    public static boolean isUserApp(Context context,String pkg) {
        if (pkg == null || isException(pkg) || !disable_MC) return false;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(pkg, 0);
            int mask = ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP;
            return (ai.flags & mask) == 0;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isException(String pkg) {
        if (pkg.equals("com.google.android.apps.gmm") ||
                pkg.equals("com.google.android.googlequicksearchbox") ||
                pkg.equals("com.google.android.gms") ||
                pkg.equals("eu.chainfire.supersu") ||
                pkg.equals("com.android.vending")
                ) return true;
        else return false;
    }

    public static BitmapDrawable scaleIcon(Context ctx, Drawable icon, int scale) {
        Bitmap src = ((BitmapDrawable) icon).getBitmap();
        Bitmap dest = Bitmap.createScaledBitmap(src, scale, scale, true);
        return new BitmapDrawable(ctx.getResources(), dest);
    }

    public static AnimationDrawable scaleAnimation(Context ctx, AnimationDrawable Aicon, int scale) {
        AnimationDrawable scaled_anim = null;
        BitmapDrawable frame00 = (BitmapDrawable) Aicon.getFrame(0);
        frame00 = scaleIcon(ctx, (Drawable) frame00, scale);
        scaled_anim = new AnimationDrawable();
        scaled_anim.setOneShot(false);
        scaled_anim.addFrame(frame00, 0);
        for (int i = 0; i <= Aicon.getNumberOfFrames() - 1; i++) {
            scaled_anim.addFrame(scaleIcon(ctx, (Drawable) Aicon.getFrame(i), scale), Aicon.getDuration(i));

        }
        return (AnimationDrawable) scaled_anim;
    }

}
