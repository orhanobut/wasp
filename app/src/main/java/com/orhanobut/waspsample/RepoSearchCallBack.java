package com.orhanobut.waspsample;

import android.util.Log;

import com.github.nr4bt.wasp.CallBack;
import com.github.nr4bt.wasp.WaspError;

import java.util.List;

/**
 * @author Orhan Obut
 */
public class RepoSearchCallBack implements CallBack<List<Repo>> {

    @Override
    public void onSuccess(List<Repo> repos) {
        Log.d("asfd", repos.toString());
    }

    @Override
    public void onError(WaspError error) {
        Log.d("asfd", "callback");
    }
}
