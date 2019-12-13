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
    private List<String> imgs;
    private LayoutInflater layoutInflater;
    private int i = 0;

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
        if(imgs == null){
            return null;
        }
        for (String url : imgs) {
                newModelList.add(url);
        }
        return newModelList;
    }

    @Override
    public int getCount() {
        if(imgs != null){
            return 1;
        }
     return 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        i++;
        System.out.println("getView i = " + i);
        System.out.println("getView convertView == null : " + convertView == null);

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

        if(imgs != null && imgs.size() != 0){
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
        }

        return convertView;

    }

    class ViewHolder {
       List<ImageView>  imgViews;
    }

}
