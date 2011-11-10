package com.webviewapp.app;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.Toast;

/**
 * Created by IntelliJ IDEA.
 * User: imranansari
 * Date: 11/2/11
 * Time: 7:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class MainContainer extends TabActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, appActivity.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("search").setIndicator("Search Contacts",
                res.getDrawable(R.drawable.user_search))
                .setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        Intent myContactsIntent = new Intent().setClass(this, SearchResults.class);
        spec = tabHost.newTabSpec("myContacts").setIndicator("My Contacts",
                res.getDrawable(R.drawable.group))
                .setContent(myContactsIntent);
        tabHost.addTab(spec);


        tabHost.setCurrentTab(0);

/*        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {
                if ("myContacts".equals(tabId)) {
                    Toast.makeText(MainContainer.this, "My contacks called", Toast.LENGTH_LONG).show();
                }

            }
        });*/

        tabHost.getTabWidget().getChildAt(0).getLayoutParams().height = 70;
        tabHost.getTabWidget().getChildAt(1).getLayoutParams().height = 70;
    }
}
