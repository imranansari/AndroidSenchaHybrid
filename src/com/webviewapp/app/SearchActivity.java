package com.webviewapp.app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by IntelliJ IDEA.
 * User: imranansari
 * Date: 11/3/11
 * Time: 6:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setText("Search Panel");
        setContentView(tv);
    }
}