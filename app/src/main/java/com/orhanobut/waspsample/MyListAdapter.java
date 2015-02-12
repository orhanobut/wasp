package com.orhanobut.waspsample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * @author Orhan Obut
 */
public class MyListAdapter extends BaseAdapter {

    final String[] list;
    final LayoutInflater layoutInflater;
    final Context context;

    public MyListAdapter(Context context, String[] list) {
        this.list = list;
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.length;
    }

    @Override
    public String getItem(int position) {
        return list[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        Holder holder;

        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_list, parent, false);

            holder = new Holder();
            holder.image = (ImageView) view.findViewById(R.id.image);

            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        //  ImageHandler.volley(context, holder.image, getItem(position));
        ImageHandler.wasp(context, holder.image, getItem(position));

        return view;
    }

    static class Holder {
        ImageView image;
    }
}
