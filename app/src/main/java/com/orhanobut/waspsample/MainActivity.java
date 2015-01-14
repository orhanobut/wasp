package com.orhanobut.waspsample;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.wasp.CallBack;
import com.orhanobut.wasp.WaspError;
import com.orhanobut.wasp.WaspImage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends BaseActivity {

    private TextView textView;
    private ImageView imageView;
    private Object image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.text);
        imageView = (ImageView) findViewById(R.id.image);

        fetchIps();

        //  postFoo();

        //putFoo();

        //  putFooMap();
        loadImage();
    }

    private void fetchIp() {
        getService().fetchIp(new CallBack<Ip>() {
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
      WaspImage.from("http://developer.android.com/images/training/system-ui.png")
              .to(imageView)
              .load();

    }

}
