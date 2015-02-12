package com.orhanobut.waspsample;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * @author Orhan Obut
 */
@SuppressWarnings("unused")
public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private final String[] imageList;
    private final LayoutInflater layoutInflater;
    private final Context context;

    public MyRecyclerViewAdapter(Context context, String[] imageList) {
        this.imageList = imageList;
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // ImageHandler.volley(context, holder.image, imageList[position]);
        ImageHandler.wasp(context, holder.image, imageList[position]);
    }

    @Override
    public int getItemCount() {
        return imageList.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;

        public ViewHolder(View v) {
            super(v);

            image = (ImageView) v.findViewById(R.id.image);
        }
    }

}
