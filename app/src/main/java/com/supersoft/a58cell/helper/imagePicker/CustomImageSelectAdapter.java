package com.supersoft.a58cell.helper.imagePicker;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.supersoft.a58cell.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Centaury on 19/04/2018.
 */
public class CustomImageSelectAdapter extends CustomGenericAdapter<Image> {
    public CustomImageSelectAdapter(Activity activity, Context context, ArrayList<Image> images) {
        super(activity, context, images);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.grid_view_image_select, null);

            viewHolder = new ViewHolder();
            viewHolder.imageView = convertView.findViewById(R.id.image_view_image_select);
            viewHolder.view = convertView.findViewById(R.id.view_alpha);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.imageView.getLayoutParams().width = size;
        viewHolder.imageView.getLayoutParams().height = size;

        viewHolder.view.getLayoutParams().width = size;
        viewHolder.view.getLayoutParams().height = size;

        if (arrayList.get(position).isSelected) {
            viewHolder.view.setAlpha(0.5f);
            convertView.setForeground(context.getResources().getDrawable(R.drawable.ic_done_white));

        } else {
            viewHolder.view.setAlpha(0.0f);
            convertView.setForeground(null);
        }

        Uri uri = Uri.fromFile(new File(arrayList.get(position).path));

        Glide.with(context).load(uri)
                .placeholder(R.color.colorAccent)
                .override(200, 200)
                .crossFade()
                .centerCrop()
                .into(viewHolder.imageView);

        return convertView;
    }

    private static class ViewHolder {
        public ImageView imageView;
        public View view;
    }
}
