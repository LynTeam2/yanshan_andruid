package cn.gov.bjys.onlinetrain.fragment.ClassFragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ycl.framework.base.FrameFragment;

import java.util.List;

import butterknife.Bind;
import cn.gov.bjys.onlinetrain.R;
import cn.gov.bjys.onlinetrain.act.HomeClassStudyThirdActivity;
import cn.gov.bjys.onlinetrain.adapter.DooHomeClassStudySecondAdapter;
import cn.gov.bjys.onlinetrain.api.ZipCallBackListener;
import cn.gov.bjys.onlinetrain.bean.CategoriesBean;
import cn.gov.bjys.onlinetrain.task.HomeClassStudySencondTask;


/**
 * Created by Administrator on 2018/1/27 0027.
 */
public class HomeClassStudySecondFragment extends FrameFragment  implements SwipeRefreshLayout.OnRefreshListener ,ZipCallBackListener{

    HomeClassStudySencondTask mHomeClassStudySencondTask;
    public static HomeClassStudySecondFragment newInstance() {
        Bundle args = new Bundle();
        HomeClassStudySecondFragment fragment = new HomeClassStudySecondFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        return inflater.inflate(R.layout.fragment_linear_layout, container, false);
    }


    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;


    @Bind(R.id.rv)
    RecyclerView mRecyclerView;
    DooHomeClassStudySecondAdapter mItemAdapter;

    @Override
    protected void initViews() {
        super.initViews();
        mItemAdapter = new DooHomeClassStudySecondAdapter(R.layout.item_linear_class_layout,null);
        mItemAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                //TODO 去课程界面
                CategoriesBean bean = (CategoriesBean) adapter.getData().get(position);
                String jn = bean.getJsonName();
                Bundle mBundle = new Bundle();
                mBundle.putString("jsonName",jn);
                mBundle.putString("title",bean.getCategoryName());
                startAct(HomeClassStudyThirdActivity.class,mBundle);
            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mItemAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        gainZipDatas();
    }

    @Override
    public void onRefresh() {
        gainZipDatas();
    }

    public void gainZipDatas(){
        mHomeClassStudySencondTask = new HomeClassStudySencondTask(this);
        mHomeClassStudySencondTask.execute();
    }

    @Override
    public void zipCallBackListener(List datas) {
        mItemAdapter.setNewData(datas);
    }

    @Override
    public void zipCallBackSuccess() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void zipCallBackFail() {
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
