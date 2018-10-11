package com.yasn.purchase.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yasn.purchase.R;
import com.yasn.purchase.model.ClassifyRightModel;

import java.util.List;

/**
 * Created by gs on 2018/1/3.
 */

public class ClassifyRightGridAdapter extends BaseAdapter{
    private LayoutInflater mInflater;
    private List<ClassifyRightModel> rightList;
    private Context context;

    public ClassifyRightGridAdapter(Context context){
        this.context = context;
        this.mInflater=LayoutInflater.from(context);
    }

    public void setData(List<ClassifyRightModel> contentList) {
        this.rightList = contentList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return rightList ==null?0: rightList.size();
    }

    @Override
    public Object getItem(int position) {
        return rightList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.recycleritem_right_classify, null);
            viewHolder = new ViewHolder();
            viewHolder.rightclassify = (TextView)convertView.findViewById(R.id.rightclassify);
            viewHolder.rightiamge = (ImageView)convertView.findViewById(R.id.rightimage);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ClassifyRightModel classifyRightModel = rightList.get(position);
        int itemType = classifyRightModel.getItemType();
        if (itemType == 2){//品牌分类
//            int rightClassifyBrandId = classifyRightModel.getRightClassifyBrandId();
            String rightClassifyBrandImg = classifyRightModel.getRightClassifyBrandImg();
            Glide.with(context.getApplicationContext())
                    .load(rightClassifyBrandImg)
                    .fitCenter()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.mipmap.errorimage)
                    .error(R.mipmap.errorimage)
                    .into(viewHolder.rightiamge);
            String rightClassifyBrandName = classifyRightModel.getRightClassifyBrandName();
            viewHolder.rightclassify.setText(rightClassifyBrandName==null?"雅森":rightClassifyBrandName);
        }else  if (itemType == 1){//普通分类
            String imageUrl = classifyRightModel.getRightClassifyImg();
            Glide.with(context.getApplicationContext())
                    .load(imageUrl)
                    .fitCenter()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.mipmap.errorimage)
                    .error(R.mipmap.errorimage)
                    .into(viewHolder.rightiamge);
            String titleString = classifyRightModel.getRightClassifyName();
            viewHolder.rightclassify.setText(titleString==null?"雅森":titleString);

        }

        return convertView;

    }

    class ViewHolder {
        public TextView rightclassify;
        public ImageView rightiamge;
    }
}
