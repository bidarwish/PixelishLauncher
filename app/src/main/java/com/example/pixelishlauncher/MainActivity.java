package com.example.pixelishlauncher;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {
    LinearLayout root, dock;
    GridLayout grid;
    EditText search;
    final List<AppEntry> apps = new ArrayList<>();
    int ink = Color.rgb(35, 48, 67);
    int panel = Color.argb(235, 250, 251, 255);
    static class AppEntry { String label, pkg; Drawable icon; AppEntry(String l,String p,Drawable i){label=l;pkg=p;icon=i;} }

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(Color.rgb(215,228,247));
        getWindow().setNavigationBarColor(Color.rgb(215,228,247));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        buildUi(); loadApps(); renderApps("");
    }
    int dp(float v){ return (int)(v*getResources().getDisplayMetrics().density+.5f); }
    TextView text(String s,int size,int color){ TextView t=new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color); return t; }
    void buildUi(){
        ScrollView sc=new ScrollView(this); sc.setFillViewport(true);
        root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(dp(20),dp(20),dp(20),dp(16));
        root.setBackgroundColor(Color.rgb(215,228,247)); sc.addView(root); setContentView(sc);
        TextView date=text(new SimpleDateFormat("EEE, MMM d",Locale.getDefault()).format(new Date()),16,ink); date.setTypeface(null,Typeface.MEDIUM); root.addView(date);
        TextView weather=text("Good day",14,ink); LinearLayout.LayoutParams wp=new LinearLayout.LayoutParams(-2,-2); wp.topMargin=dp(5); root.addView(weather,wp);
        View spacer=new View(this); root.addView(spacer,new LinearLayout.LayoutParams(1,dp(100)));
        TextView all=text("All apps",22,ink); all.setTypeface(null,Typeface.BOLD); root.addView(all);
        search=new EditText(this); search.setSingleLine(true); search.setTextSize(15); search.setHint("Search apps"); search.setCompoundDrawablePadding(dp(8)); search.setPadding(dp(16),0,dp(16),0); search.setBackground(round(panel,dp(28))); LinearLayout.LayoutParams sp=new LinearLayout.LayoutParams(-1,dp(52)); sp.topMargin=dp(14); sp.bottomMargin=dp(14); root.addView(search,sp);
        search.addTextChangedListener(new android.text.TextWatcher(){ public void beforeTextChanged(CharSequence s,int st,int c,int a){} public void onTextChanged(CharSequence s,int st,int before,int count){renderApps(s.toString());} public void afterTextChanged(android.text.Editable e){} });
        grid=new GridLayout(this); grid.setColumnCount(4); root.addView(grid,new LinearLayout.LayoutParams(-1,-2));
        View gap=new View(this); root.addView(gap,new LinearLayout.LayoutParams(1,dp(24)));
        TextView dockLabel=text("Favorites",14,ink); root.addView(dockLabel);
        dock=new LinearLayout(this); dock.setGravity(Gravity.CENTER); dock.setBackground(round(panel,dp(32))); LinearLayout.LayoutParams dp0=new LinearLayout.LayoutParams(-1,dp(76)); dp0.topMargin=dp(8); root.addView(dock,dp0);
    }
    android.graphics.drawable.GradientDrawable round(int color,int radius){ android.graphics.drawable.GradientDrawable d=new android.graphics.drawable.GradientDrawable(); d.setColor(color); d.setCornerRadius(radius); return d; }
    void loadApps(){
        PackageManager pm=getPackageManager(); Intent i=new Intent(Intent.ACTION_MAIN,null); i.addCategory(Intent.CATEGORY_LAUNCHER);
        for(android.content.pm.ResolveInfo r:pm.queryIntentActivities(i,0)){ String p=r.activityInfo.packageName; if(p.equals(getPackageName())) continue; apps.add(new AppEntry(r.loadLabel(pm).toString(),p,r.loadIcon(pm))); }
        apps.sort((a,b)->a.label.compareToIgnoreCase(b.label));
    }
    void renderApps(String filter){
        if(grid==null)return; grid.removeAllViews(); dock.removeAllViews(); int count=0;
        for(AppEntry a:apps){ if(!a.label.toLowerCase(Locale.ROOT).contains(filter.toLowerCase(Locale.ROOT)))continue; grid.addView(appView(a),new ViewGroup.LayoutParams(0,dp(104))); if(count<4){ dock.addView(iconOnly(a),new LinearLayout.LayoutParams(0,-1,1)); } count++; }
    }
    View appView(AppEntry a){
        LinearLayout item=new LinearLayout(this); item.setOrientation(LinearLayout.VERTICAL); item.setGravity(Gravity.CENTER); item.setPadding(dp(2),dp(6),dp(2),dp(6));
        ImageView iv=new ImageView(this); iv.setImageDrawable(a.icon); item.addView(iv,new LinearLayout.LayoutParams(dp(48),dp(48)));
        TextView label=text(a.label,11,ink); label.setGravity(Gravity.CENTER); label.setMaxLines(2); LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2); lp.topMargin=dp(5); item.addView(label,lp);
        item.setOnClickListener(v->{ try { Intent launch=getPackageManager().getLaunchIntentForPackage(a.pkg); if(launch!=null)startActivity(launch); }catch(Exception e){Toast.makeText(this,"Cannot open app",Toast.LENGTH_SHORT).show();} });
        item.setOnLongClickListener(v->{ Intent info=new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,android.net.Uri.parse("package:"+a.pkg)); startActivity(info); return true; }); return item;
    }
    View iconOnly(AppEntry a){ ImageView iv=new ImageView(this); iv.setImageDrawable(a.icon); iv.setPadding(dp(12),dp(12),dp(12),dp(12)); iv.setScaleType(ImageView.ScaleType.FIT_CENTER); iv.setOnClickListener(v->{Intent i=getPackageManager().getLaunchIntentForPackage(a.pkg);if(i!=null)startActivity(i);}); iv.setOnLongClickListener(v->{startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,android.net.Uri.parse("package:"+a.pkg)));return true;}); return iv; }
}
