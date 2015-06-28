package com.orhanobut.waspsample;

import android.app.Activity;
import android.widget.Toast;

/**
 * @author Orhan Obut
 */
public class BaseActivity extends Activity {

    private final MyService service = WaspApplication.getService();

    protected MyService getService() {
        return service;
    }

    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
