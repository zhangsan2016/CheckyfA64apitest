package smartcity.ldgd.com.checkyfa64apitest.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import smartcity.ldgd.com.checkyfa64apitest.R;

/**
 * Created by ldgd on 2019/12/4.
 * 功能：
 * 说明：
 */

public class ProvinceAdapter extends BaseAdapter {
    private List<String> imgs = new ArrayList<>();
    private LayoutInflater layoutInflater;

    public ProvinceAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    public ProvinceAdapter(Context context, List<String> imgs) {
        this.imgs = imgs;
        layoutInflater = LayoutInflater.from(context);
    }


    public void setImgs(List<String> imglist) {
        this.imgs = imglist;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return imgs.size();
    }

    @Override
    public Object getItem(int position) {
        return imgs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.province_grid_view_item_layout, null);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.imageview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        String uri = imgs.get(position);
        holder.imageView.setImageURI(Uri.fromFile(new File(uri)));


        return convertView;

    }

    class ViewHolder {
        ImageView imageView;
    }

}
