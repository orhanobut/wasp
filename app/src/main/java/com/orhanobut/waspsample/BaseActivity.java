package com.orhanobut.waspsample;

import android.app.Activity;

/**
 * @author Orhan Obut
 */
public class BaseActivity extends Activity {

    private final MyService service = WaspApplication.getService();

    protected MyService getService(){
        return service;
    }
}
