// WebViewApp (LGPL 3.0)
// Activity class
// Author: Kristo Vaher - www.waher.net - kristo@waher.net

// This is main activity of the application

package com.webviewapp.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class appActivity extends Activity {

    /* Customizations */
    //String baseUrl = "http://webviewapp.waher.net/"; //Web service URL
    //String baseUrl = "http://10.0.2.2:4567/app";
    String baseUrl = "http://192.168.0.195:4567/app?1";
    String currentUrl = baseUrl;

    /* Additional variables */
    WebView appBrowser;

    private static final int OK_MENU_ITEM = Menu.FIRST;
    private static final int SAVE_MENU_ITEM = OK_MENU_ITEM + 1;
    private static final int BACK_MENU_ITEM = SAVE_MENU_ITEM + 1;
    String currentView;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        /* Instance */
        super.onCreate(savedInstanceState);

        /* Set the Content View */
        setContentView(R.layout.webview);


        /* Get the WebView */
        appBrowser = (WebView) findViewById(R.id.web_engine);


        /* WebView settings and configuration */
        WebSettings appBrowserSettings = appBrowser.getSettings();
        appBrowserSettings.setSavePassword(false);
        appBrowserSettings.setSaveFormData(false);
        appBrowserSettings.setJavaScriptEnabled(true);
        appBrowserSettings.setLoadsImagesAutomatically(true);
        appBrowserSettings.setSupportZoom(false);
        appBrowserSettings.setAllowFileAccess(true);
        appBrowserSettings.setSupportMultipleWindows(false);
        appBrowserSettings.setAppCacheEnabled(true);

        String packageName = "com.lfg.bcp";
        appBrowserSettings.setDatabaseEnabled(true);
        appBrowserSettings.setDomStorageEnabled(true);
        //appBrowserSettings.setDatabasePath("/data/data/" + packageName + "/databases");
        appBrowserSettings.setDatabasePath(this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath());

        appBrowserSettings.setPluginState(WebSettings.PluginState.OFF);
        appBrowserSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        appBrowserSettings.setCacheMode(WebSettings.LOAD_NORMAL);


// clear cache
        appBrowser.clearCache(true);

        // this is necessary for "alert()" to work
        appBrowser.setWebChromeClient(new WebChromeClient());
        appBrowser.setWebViewClient(new appWebViewClient());
        appBrowser.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        /* Adding JavaScript interface */
        appBrowser.addJavascriptInterface(this, "Android");


        /* Using HTTP */
        /*          String webContent = "";
              try {
                  webContent = getWebContents(baseUrl);
              } catch (Exception e) {
                  Toast.makeText(appActivity.this, "Cannot connect to API", Toast.LENGTH_LONG).show();
              }*/

        appBrowser.loadUrl(currentUrl);

    }

    /*   @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Toast.makeText(this, "currentView before menu :"+currentView, Toast.LENGTH_SHORT).show();
        super.onPrepareOptionsMenu(menu);
        MenuItem item;
        menu.clear();

        if (currentView != null && currentView.equalsIgnoreCase("search")) {
            item = menu.add(0, 0, 0, "Select");
        } else if (currentView != null && currentView.equalsIgnoreCase("searchResults")) {
            item = menu.add(0, 1, 0, "Settings");
            item = menu.add(0, 2, 0, "Compose");
        }
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addContact:
                //Toast.makeText(this, "Invoking JS!", Toast.LENGTH_LONG).show();
                appBrowser.loadUrl("javascript:addContact()");
                break;
            case R.id.emailContact:
                //Toast.makeText(this, "You pressed the text!", Toast.LENGTH_LONG).show();
                appBrowser.loadUrl("javascript:emailContact()");
                break;
/*            case R.id.settings:
                Toast.makeText(this, "You pressed the icon and text!", Toast.LENGTH_LONG).show();
                break;*/
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        Toast.makeText(appActivity.this, "currentView from menu  " + currentView, Toast.LENGTH_LONG).show();
        if (currentView != null && currentView.equalsIgnoreCase("search")) {
            inflater.inflate(R.menu.main_menu, menu);
        } else if (currentView != null && currentView.equalsIgnoreCase("viewDetails")) {
            inflater.inflate(R.menu.viewdetails_menu, menu);

        } else if (currentView != null && currentView.equalsIgnoreCase("searchResults")) {
            inflater.inflate(R.menu.search_result_menu, menu);
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Toast.makeText(appActivity.this, "onStart", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStop() {
        super.onStop();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /* Class that prevents opening the Browser, opening URL's in WebView */
    private class appWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("mailto:") || url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(url));
                startActivity(intent);
            }
            //view.loadUrl(url);
            return true;
        }

    }

    /* Controlling Android hardware buttons */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //appBrowser.loadUrl("javascript:keyEvent.searchTrigger()");
            appBrowser.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    /* JavaScript interface */
/*    public class JavaScriptInterface {
        Context appContext;

        *//* Instantiate the interface and set the context *//*
        JavaScriptInterface(Context c) {
            appContext = c;
        }

        public void setCurrentView(String currentView) {
            //this.currentView = currentView;
            Toast.makeText(appActivity.this, "currentView " + currentView, Toast.LENGTH_LONG).show();
        }
    }*/

    public void setCurrentView(String _currentView) {
        this.currentView = _currentView;
        Toast.makeText(appActivity.this, "currentView " + this.currentView, Toast.LENGTH_LONG).show();
    }


}
