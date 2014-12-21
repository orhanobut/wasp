package com.orhanobut.waspsample;

import android.os.Bundle;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        getService().fetchRepo("nr4bt", "wasp", new RepoCallBack());

        //      getService().fetchRepoBySearch("nr4bt", 1, "asc", new RepoSearchCallBack());

        getService().addName("nr4bt", "wasp", "", new RepoCallBack());
    }
}
