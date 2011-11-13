package com.webviewapp.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Created by IntelliJ IDEA.
 * User: imranansari
 * Date: 11/11/11
 * Time: 9:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class account_pin extends Activity implements View.OnKeyListener {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_pin);
        View lastPinNumber = findViewById(R.id.pin4);
        lastPinNumber.setOnKeyListener(this);

        findViewById(R.id.pin1).setOnKeyListener(this);
        findViewById(R.id.pin2).setOnKeyListener(this);
        findViewById(R.id.pin3).setOnKeyListener(this);
    }

    /*   public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.pin4:
                Intent intent = new Intent(account_pin.this, MainContainer.class);
                startActivity(intent);
                //overridePendingTransition(R.anim.left_in, R.anim.left_out);
                break;
        }
    }*/

    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        Toast.makeText(account_pin.this, "keypressed : " + keyCode, Toast.LENGTH_LONG).show();
        switch (view.getId()) {
            case R.id.pin4:
                Intent intent = new Intent(account_pin.this, MainContainer.class);
                startActivity(intent);
                //overridePendingTransition(R.anim.left_in, R.anim.left_out);
                break;

            case R.id.pin1:
                findViewById(R.id.pin2).requestFocus();
                break;
            case R.id.pin2:
                findViewById(R.id.pin3).requestFocus();
                break;
            case R.id.pin3:
                findViewById(R.id.pin4).requestFocus();
                break;
        }
        return true;
    }
}