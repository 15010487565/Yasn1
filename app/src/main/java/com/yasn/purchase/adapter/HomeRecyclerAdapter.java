package com.yasn.purchase.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yasn.purchase.R;
import com.yasn.purchase.activity.AuthorActivity;
import com.yasn.purchase.activity.LoginActivity;
import com.yasn.purchase.activity.MainActivity;
import com.yasn.purchase.activityold.WebViewH5Activity;
import com.yasn.purchase.listener.OnRcItemClickListener;
import com.yasn.purchase.model.HomeRecyModel;

import java.util.List;

import www.xcd.com.mylibrary.base.activity.SimpleTopbarActivity;
import www.xcd.com.mylibrary.utils.SharePrefHelper;

import static com.yasn.purchase.common.ItemTypeConfig.ITEM_FOOTER;
import static com.yasn.purchase.common.ItemTypeConfig.TYPE_ONE;
import static com.yasn.purchase.common.ItemTypeConfig.TYPE_TWO;

/**
 * Created by gs on 2017/12/26.
 */

public class HomeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<HomeRecyModel> contentList;
    String priceType = "0";
    String regionName;
    private String place= " ";
    private int placeNum= 3;
    private LayoutInflater inflater;

    public HomeRecyclerAdapter( Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setData(List contentList,String priceType) {
        regionName = SharePrefHelper.getInstance(context).getSpString("regionName");
        Log.e("TAG_regionName","regionName="+regionName);
        this.contentList = contentList;
        this.priceType = priceType;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        HomeRecyModel baserecymodel = contentList.get(position);
        return baserecymodel.getItemType();

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case TYPE_ONE:
                view = inflater.inflate(R.layout.fragment_homelistitemone, parent, false);
                holder = new ViewHolderOne(view);
                break;
            case TYPE_TWO:
                view = inflater.inflate(R.layout.fragment_homelistitemtwo, parent, false);
                holder = new ViewHolderTwo(view);
                break;
            case ITEM_FOOTER:
                view = inflater.inflate(R.layout.footview_listview, parent, false);
                holder = new ViewHolderFootView(view);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_ONE:
                final ViewHolderOne holderTab = (ViewHolderOne) holder;
                HomeRecyModel homeTab = contentList.get(position);
                holderTab.titleTab.setText(homeTab.getText());
                onItemEventClick(holderTab);
                break;
            case TYPE_TWO:
                ViewHolderTwo holderRecy = (ViewHolderTwo) holder;
                HomeRecyModel homeRecy = contentList.get(position);

                StringBuffer sb = new StringBuffer();
                int goneNum = 0;
                boolean autotrophy = homeRecy.isAutotrophy();
                if (autotrophy){
                    sb.append("自营"+place);
                    goneNum += placeNum;
                    holderRecy.autotrophy.setVisibility(View.VISIBLE);
                    holderRecy.autotrophy.setText("自营");
                }else {
                    boolean isRegionName = homeRecy.isRegionName();
                    if (isRegionName){
                        holderRecy.autotrophy.setVisibility(View.VISIBLE);
                        holderRecy.autotrophy.setText(regionName+"直供");
                        sb.append(regionName+"直供"+place);
                        goneNum =goneNum+ regionName.length()+placeNum;
                    }else {
                        holderRecy.autotrophy.setVisibility(View.GONE);
                        holderRecy.autotrophy.setText("");
                    }
                }

                boolean purchase = homeRecy.isPurchase();
                if (purchase){
                    holderRecy.purchase.setVisibility(View.VISIBLE);
                    sb.append("限购"+place);
                    goneNum += placeNum;
                }else {
                    holderRecy.purchase.setVisibility(View.GONE);
                }

                boolean presell = homeRecy.isPresell();
                if (presell){
                    holderRecy.presell.setVisibility(View.VISIBLE);
                    goneNum += placeNum;
                    sb.append("预售"+place);
                }else {
                    holderRecy.presell.setVisibility(View.GONE);
                }
                int button2 = homeRecy.getButton2();
                if (button2==1){
                    holderRecy.button2.setVisibility(View.VISIBLE);
                }else {
                    holderRecy.button2.setVisibility(View.GONE);
                }
                int button3 = homeRecy.getButton3();
                if (button3==1){
                    holderRecy.button3.setVisibility(View.VISIBLE);
                }else {
                    holderRecy.button3.setVisibility(View.GONE);
                }
                SpannableStringBuilder span = new SpannableStringBuilder(sb + homeRecy.getText());
                span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.white)), 0, goneNum,
                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                holderRecy.nameRecy.setText(span);
                String advert = homeRecy.getAdvert();
                if (advert==null||"".equals(advert)){
                    holderRecy.advertRecy.setVisibility(View.GONE);
                }else {
                    holderRecy.advertRecy.setVisibility(View.VISIBLE);
                    holderRecy.advertRecy.setText(advert);
                }
                String money = homeRecy.getMoney();
//                Log.e("TAG_首页价格","money="+money);
                if ("0".equals(priceType)){
                    holderRecy.moneyRecy.setText("￥"+money);
                }else {
                    holderRecy.moneyRecy.setText(priceType==null?"登录看价格":priceType);
                }

                String minNumberString = String.format("已售%s笔", homeRecy.getTotalBuyCount());
                SpannableStringBuilder countRecySpan = new SpannableStringBuilder(minNumberString);
                countRecySpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.black_99)), 0, 2,
                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                countRecySpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.black_99)), minNumberString.length() - 1, minNumberString.length(),
                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                holderRecy.countRecy.setText(countRecySpan);

                holderRecy.advertRecy.setText(homeRecy.getAdvert());
//                int market_enable = homeRecy.getMarket_enable();
//                if (market_enable == 0){//上架1, 下架0
//
//                    holderRecy.iv_shroud.setVisibility(View.VISIBLE);
//                }else {
//                    holderRecy.iv_shroud.setVisibility(View.GONE);
//                }
                Glide.with(context.getApplicationContext())
                        .load(homeRecy.getImage()==null?"":homeRecy.getImage())
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.mipmap.errorimage)
                        .error(R.mipmap.errorimage)
                        .into(holderRecy.imageView);
                onItemEventClick(holderRecy);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return contentList == null?0:contentList.size();
    }

    class ViewHolderOne extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView titleTab,home_more;
        public ViewHolderOne(View itemView) {
            super(itemView);
            titleTab = (TextView) itemView.findViewById(R.id.home_title);
            home_more = (TextView) itemView.findViewById(R.id.home_titlemore);
            home_more.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.home_titlemore:
                    onItemClickListener.OnClickTabMore(getLayoutPosition());
                    break;
            }
        }
    }

    class ViewHolderTwo extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imageView,iv_shroud;
        TextView nameRecy;
        TextView moneyRecy, advertRecy, countRecy;
        TextView  button1,button2,button3;
        TextView  autotrophy,purchase,presell;
        public ViewHolderTwo(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_HomeMoreLeft);
//            iv_shroud = (ImageView) itemView.findViewById(R.id.iv_shroud);
//            Drawable background = iv_shroud.getBackground();
//            background.setAlpha(255);

            nameRecy = (TextView) itemView.findViewById(R.id.title);
//
            moneyRecy = (TextView) itemView.findViewById(R.id.home_money);
            moneyRecy.setOnClickListener(this);
            advertRecy = (TextView) itemView.findViewById(R.id.home_action);
            countRecy = (TextView) itemView.findViewById(R.id.home_count);

            button1 = (TextView) itemView.findViewById(R.id.lable_button1);
            button1.setOnClickListener(this);
            button2 = (TextView) itemView.findViewById(R.id.lable_button2);
            button2.setOnClickListener(this);
            button3 = (TextView) itemView.findViewById(R.id.lable_button3);
            button3.setOnClickListener(this);

            autotrophy = (TextView) itemView.findViewById(R.id.autotrophy);
            purchase = (TextView) itemView.findViewById(R.id.purchase);
            presell = (TextView) itemView.findViewById(R.id.presell);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.lable_button1:
                    onItemClickListener.OnClickRecyButton(1,getLayoutPosition());
                    break;
                case R.id.lable_button2:
                    onItemClickListener.OnClickRecyButton(2,getLayoutPosition());
                    break;
                case R.id.lable_button3:
                    onItemClickListener.OnClickRecyButton(3,getLayoutPosition());
                    break;
                case R.id.home_money:
                    String trim = moneyRecy.getText().toString().trim();
                    if ("登录看价格".equals(trim)){
                        ((MainActivity)context).startBaseActivity(context,LoginActivity.class);
                    }else if ("认证看价格".equals(trim)){
//                        startWebViewActivity(Config.ATTESTATION);
                        ((SimpleTopbarActivity)context).showStartAuthorDialog(AuthorActivity.class);
                    }
                    break;
            }
        }
    }
    public OnRcItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnRcItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    class ViewHolderFootView extends RecyclerView.ViewHolder{
        LinearLayout footview;
        public ViewHolderFootView(View itemView) {
            super(itemView);
            footview = (LinearLayout) itemView.findViewById(R.id.footview);
        }
    }

    private void onItemEventClick(RecyclerView.ViewHolder holder) {
        final int position = holder.getLayoutPosition();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.OnItemClick(v, position);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemClickListener.OnItemLongClick(v, position);
                return true;
            }
        });
    }
    private void startWebViewActivity(String url) {
        Intent intent = new Intent(context, WebViewH5Activity.class);
        intent.putExtra("webViewUrl", url);
        context.startActivity(intent);
    }
}

