package com.yasn.purchase.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.yasn.purchase.R;
import com.yasn.purchase.activity.IntegralActivity;
import com.yasn.purchase.adapter.IntegralFreezeAdapter;
import com.yasn.purchase.common.Config;
import com.yasn.purchase.model.IntegralFreezeModel;
import com.yasn.purchase.utils.ToastUtil;
import com.yasn.purchase.view.MultiSwipeRefreshLayout;
import com.yasn.purchase.view.RecyclerViewDecoration;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 冻结积分
 * Created by gs on 2018/1/8.
 */

public class IntegralFreezeFragment extends OrderFragment implements MultiSwipeRefreshLayout.OnLoadListener {

    IntegralFreezeAdapter adapter;
    private RecyclerView rcIntegral;
    private LinearLayoutManager mLinearLayoutManager;
    private int pagNo = 1;
    private MultiSwipeRefreshLayout swipe_layout;
    // 标志位，标志已经初始化完成。
    private boolean isPrepared;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_integal;
    }

    @Override
    protected void lazyLoad() {
        Log.e("TAG_onHiddenChanged", "积分=" + isPrepared + ";isVisible=" + isVisible);
        if (!isPrepared || !isVisible) {
            return;
        }
        //填充各控件的数据
        OkHttpDemand();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e("TAG_onHiddenChanged", "积分=" + hidden);
        if (!hidden) { //隐藏时所作的事情
            lazyLoad();
            isVisible = false;
        } else {
            isVisible = true;
        }
    }

    @Override
    protected void OkHttpDemand() {
        initData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
        }
    }

    @Override
    protected void initView(LayoutInflater inflater, View view) {
        super.initView(inflater, view);
        RelativeLayout title = (RelativeLayout) view.findViewById(R.id.topbat_parent);
        title.setVisibility(View.GONE);

        ivError.setImageResource(R.mipmap.jifen_black);
        tvErrorHint.setText("未搜索到冻结积分明细！");
        initSwipeRefreshLayout(view);
        initRecyclerView(view);
        //XXX初始化view的各控件
        isPrepared = true;
        lazyLoad();
    }

    private void initData() {
        Map<String, Object> params = new HashMap<String, Object>();
        if (token != null && !"".equals(token)) {
            params.put("access_token", token);
        } else if (resetToken != null && !"".equals(resetToken)) {
            params.put("access_token", resetToken);
        }
        okHttpGet(100, Config.SHOPINTEGALFREEZE + pagNo, params);
    }

    private void initSwipeRefreshLayout(View view) {
        //搜索列表
        swipe_layout = (MultiSwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        //下拉刷新监听
//        swipe_layout.setOnRefreshListener(this);
        //禁止下拉刷新
        swipe_layout.setEnabled(false);
//        swipe_layout.setMultiSwipeRefreshClickListener(this);
        //上拉加載监听
        swipe_layout.setOnLoadListener(this);
        //设置样式刷新显示的位置
        swipe_layout.setProgressViewOffset(true, -20, 100);
        swipe_layout.setColorSchemeResources(R.color.red, R.color.orange, R.color.blue, R.color.black);
    }


    private void initRecyclerView(View view) {


        rcIntegral = (RecyclerView) view.findViewById(R.id.rc_Integral);

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setAutoMeasureEnabled(true);
        rcIntegral.setLayoutManager(mLinearLayoutManager);
        adapter = new IntegralFreezeAdapter(getActivity(), mLinearLayoutManager);
        rcIntegral.setAdapter(adapter);
        RecyclerViewDecoration recyclerViewDecoration = new RecyclerViewDecoration(
                getActivity(), LinearLayoutManager.HORIZONTAL, 2, getResources().getColor(R.color.line_c3));
        rcIntegral.addItemDecoration(recyclerViewDecoration);
        rcIntegral.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean isTop = recyclerView.canScrollVertically(-1);//返回false表示不能往下滑动，即代表到顶部了；

                boolean isBottom = recyclerView.canScrollVertically(1);//返回false表示不能往上滑动，即代表到底部了；
                //屏幕中最后一个可见子项的position
//                int lastVisibleItemPosition = mLinearLayoutManager.findLastVisibleItemPosition();
                //当前屏幕所看到的子项个数
                int visibleItemCount = mLinearLayoutManager.getChildCount();
                //当前RecyclerView的所有子项个数
                int totalItemCount = mLinearLayoutManager.getItemCount();
                Log.e("TAG_底部Scrolled", "isBottom=" + isBottom + "visibleItemCount=" + visibleItemCount + ";totalItemCount=" + totalItemCount);
                if (isBottom) {
                    swipe_layout.setBottom(false);
                } else {
                    if (visibleItemCount == totalItemCount) {
                        swipe_layout.setBottom(false);
                        adapter.upFootText();
                    } else {
                        swipe_layout.setBottom(true);
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onSuccessResult(int requestCode, int returnCode, String returnMsg, String returnData, Map<String, Object> paramsMaps) {
        super.onSuccessResult(requestCode,returnCode,returnMsg,returnData,paramsMaps);
        switch (requestCode) {
            case 100:
                IntegralFreezeModel integralFreezeModel = JSON.parseObject(returnData, IntegralFreezeModel.class);
                IntegralFreezeModel.DataBean data = integralFreezeModel.getData();
                if (data != null) {
                    rcIntegral.setVisibility(View.VISIBLE);
                    llError.setVisibility(View.GONE);
                    List<IntegralFreezeModel.DataBean.FreezePointListBean> freezePointList = data.getFreezePointList();
                    if (pagNo > 1) {

                        if (freezePointList == null || freezePointList.size() == 0) {
                            adapter.upFootText();
                            swipe_layout.setBottom(false);
                            ToastUtil.showToast("冻结积分明细已全部显示！");
                        } else {
                            adapter.addData(freezePointList);
                        }
                    } else {
                        if (freezePointList == null || freezePointList.size() == 0) {
                            llError.setVisibility(View.VISIBLE);
                            rcIntegral.setVisibility(View.GONE);
//                            adapter.upFootText();
                            ToastUtil.showToast("未搜索到冻结积分明细！");
                        } else {
                            adapter.setData(freezePointList);
                        }
                    }
                    int point = data.getPoint();
                    ((IntegralActivity) getActivity()).getPoint(point);
                }
                swipe_layout.setLoading(false);
                break;
        }
    }

    @Override
    public void onCancelResult() {
    }

    @Override
    public void onErrorResult(int errorCode, IOException errorExcep) {

    }

    @Override
    public void onParseErrorResult(int errorCode) {
        rcIntegral.setVisibility(View.GONE);
        llError.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFinishResult() {
    }

    @Override
    public void onLoad() {
        if (rcIntegral != null && rcIntegral.getAdapter() != null) {
            swipe_layout.setLoading(true);
            pagNo++;
            initData();
        }
    }
}