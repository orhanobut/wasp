package com.orhanobut.waspsample;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.orhanobut.wasp.Callback;
import com.orhanobut.wasp.WaspError;
import com.orhanobut.wasp.WaspResponse;


@SuppressWarnings("unused")
public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener {

  private static final String TAG = MainActivity.class.getSimpleName();

  private TextView textView;
  private ImageView imageView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ListView listView = (ListView) findViewById(R.id.list);

    String[] list = {
        "GET",
        "POST",
        "PUT",
        "PATCH",
        "DELETE",
        "HEAD",
        "FORM_URL_ENCODED",
        "MULTIPART"
    };

    ArrayAdapter<String> adapter = new ArrayAdapter<>(
        this, android.R.layout.simple_list_item_1, list
    );

    listView.setAdapter(adapter);
    listView.setOnItemClickListener(this);

  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    String key = (String) parent.getItemAtPosition(position);
    switch (key) {
      case "GET":
        get();
        break;
      case "POST":
        post();
        break;
      case "PUT":
        put();
        break;
      case "PATCH":
        patch();
        break;
      case "DELETE":
        delete();
        break;
      case "HEAD":
        head();
        break;
      case "FORM_URL_ENCODED":
        formUrlEncoded();
        break;
      case "MULTIPART":
        multipart();
        break;
    }
  }

  private final Callback<User> callback = new Callback<User>() {
    @Override
    public void onSuccess(WaspResponse response, User user) {
      if (user == null) {
        return;
      }
      showToast(user.toString());
    }

    @Override
    public void onError(WaspError error) {
      showToast(error.getErrorMessage());
    }
  };

  private void multipart() {
    //TODO
  }

  private void formUrlEncoded() {
    getService().postFormUrlEncoded("param1", "param2", callback);
  }

  private void head() {
    getService().head(callback);
  }

  private void delete() {
    getService().delete(callback);
  }

  private void patch() {
    getService().patch(new User("Wasp"), callback);
  }

  private void put() {
    getService().put(new User("Wasp"), callback);
  }

  private void post() {
    getService().post(new User("Wasp"), callback);
  }

  private void get() {
    getService().get(callback);
  }
}
