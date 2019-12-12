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

    public List<String> getImgs() {
        List<String> newModelList = new ArrayList<>();
        for (String url : imgs) {
                newModelList.add(url);
        }
        return newModelList;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {



        ViewHolder holder = null;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.photo_album_item, null);
            holder = new ViewHolder();
            holder.imgViews = new ArrayList<>();
            holder.imgViews.add((ImageView) convertView.findViewById(R.id.img1));
            holder.imgViews.add((ImageView) convertView.findViewById(R.id.img2));
            holder.imgViews.add((ImageView) convertView.findViewById(R.id.img3));
            holder.imgViews.add((ImageView) convertView.findViewById(R.id.img4));
            holder.imgViews.add((ImageView) convertView.findViewById(R.id.img5));
            holder.imgViews.add((ImageView) convertView.findViewById(R.id.img6));
            holder.imgViews.add((ImageView) convertView.findViewById(R.id.img7));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        List<String> imgs = getImgs();

        if(imgs.size() == 0){
            return convertView;
        }

        int size;
        if(imgs.size() > 7){
            size = 7;
        }else{
            size = imgs.size();
        }

        for (int i = 0; i < 7; i++) {
            String uri = imgs.get(i);
            holder.imgViews.get(i).setImageURI(Uri.fromFile(new File(uri)));
        }



        return convertView;

    }

    class ViewHolder {
       List<ImageView>  imgViews;
    }

}
