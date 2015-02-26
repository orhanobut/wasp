package com.orhanobut.waspsample;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.wasp.CallBack;
import com.orhanobut.wasp.Wasp;
import com.orhanobut.wasp.WaspError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SuppressWarnings("unused")
public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView textView;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.text);
        imageView = (ImageView) findViewById(R.id.image);

        // fetchIp();
        // postFoo();
        putFoo();
        //  putFooMap();
        //  loadImage();
        // getFoo();

        findViewById(R.id.listView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startListViewActivity();
            }
        });

        findViewById(R.id.recycler_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecyclerViewActivity();
            }
        });

        findViewById(R.id.clear_cache).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Wasp.Image.clearCache();
            }
        });

    }

    private void startListViewActivity() {
        startActivity(ListViewActivity.newIntent(this));
    }

    private void startRecyclerViewActivity() {
        startActivity(RecyclerViewActivity.newIntent(this));
    }

    private void fetchIp() {
        Ip ip = new Ip("origin", "foo");
        Map<String, String> map = new HashMap<>();
        map.put("QueryMapKey1", "QueryMapValue1");
        map.put("QueryMapKey2", "QueryMapValue2");

        getService().fetchIp("ParamHeaderValue1", map, ip, new CallBack<Ip>() {
            @Override
            public void onSuccess(Ip ip) {
                textView.setText(ip.toString());
            }

            @Override
            public void onError(WaspError error) {
                showToast(error.toString());
            }
        });
    }

    private void fetchIps() {
        getService().fetchIps(new CallBack<List<Ip>>() {
            @Override
            public void onSuccess(List<Ip> ips) {
                textView.setText(ips.toString());
            }

            @Override
            public void onError(WaspError error) {
                showToast(error.toString());
            }
        });
    }


    private void postFoo() {
        getService().postFoo(new Ip("test", "test2"), new CallBack<Ip>() {
            @Override
            public void onSuccess(Ip ip) {
                textView.append(ip.toString());
            }

            @Override
            public void onError(WaspError error) {
                showToast(error.toString());
            }
        });
    }

    private void putFoo() {
        getService().putFoo(new Ip("test", "test2"), new CallBack<Ip>() {
            @Override
            public void onSuccess(Ip ip) {
                textView.append(ip.toString());
            }

            @Override
            public void onError(WaspError error) {
                showToast(error.toString());
            }
        });
    }

    private void putFooMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("origin", "Test");

        getService().putFooMap(map, new CallBack<Ip>() {
            @Override
            public void onSuccess(Ip ip) {
                textView.append(ip.toString());
            }

            @Override
            public void onError(WaspError error) {
                showToast(error.toString());
            }
        });
    }


    public void loadImage() {
        String url = "http://developer.android.com/images/training/system-ui.png";
        int defaultImage = R.drawable.ic_launcher;
        int errorImage = R.drawable.error;
        Wasp.Image.from(url)
                .to(imageView)
                .setError(errorImage)
                .setDefault(defaultImage)
                .load();
    }

    public void getFoo() {
        getService().get("Selam naber", new CallBack<Foo>() {
            @Override
            public void onSuccess(Foo foo) {
                Log.d(TAG, foo.toString());
            }

            @Override
            public void onError(WaspError error) {
                Log.d(TAG, error.getErrorMessage());
            }
        });
    }
}
