package com.electrotechnique.guide;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.WindowInsets;
final class Screen {
    static final int BG=0xff05070a, PANEL=0xff10151c, BLUE=0xff00a8ff, TEXT=0xfff5f5f5;
    static boolean light(Activity a){return "light".equals(a.getSharedPreferences("appearance",0).getString("theme","dark"));}
    static int background(Activity a){return light(a)?0xfff3f6fa:BG;}
    static int panel(Activity a){return light(a)?0xffedf2f7:PANEL;}
    static int text(Activity a){return light(a)?0xff152433:TEXT;}
    static int blue(Activity a){return light(a)?0xff006caf:BLUE;}
    static int style(Activity a){return light(a)?R.style.AppThemeLight:R.style.AppTheme;}
    static void palette(Activity a,View root){
        int bg=background(a);root.setBackgroundColor(bg);
        a.getWindow().setStatusBarColor(bg);a.getWindow().setNavigationBarColor(bg);
        int flags=View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        if(light(a))flags|=View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR|View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        a.getWindow().getDecorView().setSystemUiVisibility(flags);
        if(Build.VERSION.SDK_INT>=30 && a.getWindow().getInsetsController()!=null)a.getWindow().getInsetsController().setSystemBarsAppearance(light(a)?android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS|android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS:0,android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS|android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS);
    }
    static int dp(Activity a,float x){return (int)(a.getResources().getDisplayMetrics().density*x+.5f);}
    static void fit(Activity a,View root) {
        palette(a,root);
        root.setLayoutDirection("ar".equals(a.getSharedPreferences("appearance",0).getString("language","fr"))?View.LAYOUT_DIRECTION_RTL:View.LAYOUT_DIRECTION_LTR);
        root.setOnApplyWindowInsetsListener((view,insets)->{
            if(Build.VERSION.SDK_INT>=30){android.graphics.Insets i=insets.getInsets(WindowInsets.Type.systemBars()|WindowInsets.Type.displayCutout());view.setPadding(i.left,i.top,i.right,i.bottom);}
            else view.setPadding(insets.getSystemWindowInsetLeft(),insets.getSystemWindowInsetTop(),insets.getSystemWindowInsetRight(),insets.getSystemWindowInsetBottom());
            return insets;
        });
        root.requestApplyInsets();
    }
}