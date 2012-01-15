
package com.reindeermobile.mvpexample;

import com.reindeermobile.mvpexample.view.Tab1Activity;
import com.reindeermobile.mvpexample.view.Tab2Activity;
import com.reindeermobile.mvpexample.view.Tab3Activity;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class AndroidMvpExampleActivity extends TabActivity {
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main);

        final Resources res = this.getResources();
        final TabHost tabHost = this.getTabHost();
        TabHost.TabSpec spec;
        Intent intent;

        intent = new Intent().setClass(this, Tab1Activity.class);

        spec = tabHost.newTabSpec("tab1")
                .setIndicator("Tab1", res.getDrawable(R.drawable.ic_tab_tab1))
                .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, Tab2Activity.class);
        spec = tabHost.newTabSpec("tab2")
                .setIndicator("Tab2", res.getDrawable(R.drawable.ic_tab_tab1))
                .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, Tab3Activity.class);
        spec = tabHost.newTabSpec("tab3")
                .setIndicator("Tab3", res.getDrawable(R.drawable.ic_tab_tab1))
                .setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);
    }
}
