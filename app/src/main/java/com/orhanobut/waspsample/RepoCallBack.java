package com.orhanobut.waspsample;

import android.util.Log;

import com.orhanobut.wasp.CallBack;
import com.orhanobut.wasp.WaspError;

/**
 * @author Orhan Obut
 */
public class RepoCallBack implements CallBack<Repo> {

    @Override
    public void onSuccess(Repo repo) {
        Log.d("asfd", repo.toString());
    }

    @Override
    public void onError(WaspError error) {
        Log.d("RepoCallBack error: ", error.toString());
    }
}
