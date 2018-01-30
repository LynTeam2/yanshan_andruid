
package cn.gov.bjys.onlinetrain.act;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ycl.framework.base.FrameActivity;
import com.ycl.framework.db.entity.ExamBean;
import com.ycl.framework.utils.sp.SavePreference;
import com.ycl.framework.utils.util.ToastUtil;
import com.ycl.framework.view.TitleHeaderView;
import com.zhy.autolayout.utils.AutoUtils;
import com.zls.www.statusbarutil.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import cn.gov.bjys.onlinetrain.R;
import cn.gov.bjys.onlinetrain.act.dialog.EndPracticeDialog;
import cn.gov.bjys.onlinetrain.act.pop.EndExamPop;
import cn.gov.bjys.onlinetrain.act.view.ExamBottomLayout;
import cn.gov.bjys.onlinetrain.adapter.DooExamBottomAdapter;
import cn.gov.bjys.onlinetrain.adapter.DooExamStateFragmentAdapter;
import cn.gov.bjys.onlinetrain.bean.ExamXqBean;
import cn.gov.bjys.onlinetrain.utils.PracticeHelper;
import cn.gov.bjys.onlinetrain.utils.YSConst;
import cn.gov.bjys.onlinetrain.utils.YSUserInfoManager;


/**
 * Created by dodozhou on 2017/9/27.
 */
public class PracticeActivity extends FrameActivity implements View.OnClickListener {
    public final static String TAG = PracticeActivity.class.getSimpleName();

    public final static int KESHI = 1;//课时练习
    public final static int CUOTI = 2;//错题练习
    public final static int TIXING = 3;//题目类型练习

    private int mType = KESHI;//默认为课时练习

    @Bind(R.id.viewpager)
    ViewPager mViewPager;
    @Bind(R.id.exam_bottom_layout)
    ExamBottomLayout mExamBottomLayout;

    @Bind(R.id.header)
    TitleHeaderView mHeader;

    BottomSheetBehavior mBehavior;
    DooExamStateFragmentAdapter mExamAdapter;
    DooExamBottomAdapter mDooExamBottomAdapter;



    @Override
    protected void setRootView() {
        setContentView(R.layout.activity_examination_layout);
        StatusBarUtil.addStatusForFragment(this, findViewById(R.id.status_bar_layout));
    }

    @Override
    protected void initStatusBar() {
        StatusBarUtil.setTranslucentForImageViewInFragment(this, null);
    }

    public void initPracticeData(){
        Intent recIntent = getIntent();
        Bundle recBundle = recIntent.getExtras();
        mType = recBundle.getInt(TAG);
        switch (mType){
            case KESHI:
                PracticeHelper.getInstance().getmCourseBean();
                //TODO 根据CourseBean里面的题目去题库选择题目
                mHeader.setTitleText("课程练习");
                break;
            case CUOTI:
                mHeader.setTitleText("错题练习");
                break;
            case TIXING:
                mHeader.setTitleText("专项练习");
                break;
            default:
                break;
        }

        initUserCollections();

        //初始化错误
        mErrorQuestionsList = new ArrayList<>();
        //初始化正确
        mRightQuestionsList = new ArrayList<>();
    }
    List<ExamBean> mQuestionsList;//所有试题数据
    private int mPosition = 0;//当前试题在显示页面的位置 数组下标

    List<ExamBean> mErrorQuestionsList;
    List<ExamBean> mRightQuestionsList;

    EndPracticeDialog mEndPracticeDialog;


    private List<Long> mUserCollecions = new ArrayList<>();
    private void initUserCollections(){
        String userCollections = SavePreference.getStr(this, YSConst.UserInfo.USER_COLLECTION_IDS + YSUserInfoManager.getsInstance().getUserId());
        String[] ids = userCollections.split(",");
        mUserCollecions.clear();
        for(String id:ids){
                mUserCollecions.add(Long.valueOf(id));
            }
    }

    public void CalAnswer(ExamBean bean, boolean isRight){
            if(isRight){
                mRightQuestionsList.add(bean);
            }else{
                mErrorQuestionsList.add(bean);
            }
        if(mRightQuestionsList.size() + mErrorQuestionsList.size() >= mQuestionsList.size()){
            //结束弹框
         handOfPractice();
        }
    }



    @Override
    public void initData() {
        super.initData();
        mExamAdapter = new DooExamStateFragmentAdapter(getSupportFragmentManager(), mQuestionsList);
        mViewPager.setAdapter(mExamAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setViewPagerAndExamBottom(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setCurrentItem(0, false);
    }



    private String getTimesStr(long time) {
        String ret = "";
        ret = ret + (time / 3600 == 0 ? "" : time / 3600 + ":");//小时数
        long fen = time % 3600;
        ret = ret + (fen / 60 > 9 ? fen / 60 : "0" + fen / 60) + ":";
        long miao = fen % 60;
        ret = ret + (miao % 60 > 9 ? miao % 60 : "0" + miao % 60);
        return ret;
    }


    //页面切换统一处理
    private void setViewPagerAndExamBottom(int positions) {
        //记录下标
        mPosition = positions;

        if (positions > mQuestionsList.size() -1) {
            return;
        }

        initCollectionView();//每次切换页面的时候改变底部收藏状态

        mExamBottomLayout.setNowQuesitonContent(positions + 1, mQuestionsList.size());
        if (mViewPager != null) {
            mViewPager.setCurrentItem(positions, true);
        }
        if (mDooExamBottomAdapter != null) {
            List<ExamXqBean> datas = mDooExamBottomAdapter.getData();
            if (datas != null && !datas.isEmpty()) {
                ExamXqBean bean = datas.get(positions);
                for (ExamXqBean examXqBean : datas) {
                    if (examXqBean.getmType() == ExamXqBean.CHOICE)
                        examXqBean.setmType(ExamXqBean.NOMAL);
                }
                if (bean.getmType() == ExamXqBean.NOMAL) {
                    bean.setmType(ExamXqBean.CHOICE);
                }
            }
            mDooExamBottomAdapter.notifyDataSetChanged();
        }
    }

    private List<ExamXqBean> mDatas = new ArrayList<>();


    public void createBottomDatas() {
        mDatas.clear();
        for (int i = 0; i < mQuestionsList.size(); i++) {
            ExamXqBean bean = new ExamXqBean();
            bean.setmPosition(i);
            bean.setmSpanSize(1);
            bean.setmType(ExamXqBean.NOMAL);
            mDatas.add(bean);
        }
    }


    @Override
    public void initViews() {
        super.initViews();
//        prepareAnimator();

        //初始化数据
        initPracticeData();
        mQuestionsList = prepareDatas();

        mHeader.hideLeftImg();
        createBottomDatas();//创造底下的选择按钮个数
        ViewGroup.LayoutParams blp = mExamBottomLayout.getLayoutParams();
        blp.height = AutoUtils.getPercentHeightSize(1120);//重置高度
        mExamBottomLayout.setLayoutParams(blp);
        mExamBottomLayout.setmDatas(mDatas);
        mExamBottomLayout.getView(R.id.hand_of_paper).setOnClickListener(this);

        initCollectionView();

        mExamBottomLayout.getView(R.id.show_all_layout).setOnClickListener(this);
        mDooExamBottomAdapter = mExamBottomLayout.getmDooExamBottomAdapter();
        mDooExamBottomAdapter.getData().get(0).setmType(ExamXqBean.CHOICE);//初始化
        mDooExamBottomAdapter.notifyDataSetChanged();
        mDooExamBottomAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                setViewPagerAndExamBottom(position);
            }
        });


        mBehavior = BottomSheetBehavior.from(mExamBottomLayout);
        mBehavior.setPeekHeight(AutoUtils.getPercentHeightSize(120));
        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    private void initCollectionView(){
        ExamBean tempBean = mQuestionsList.get(mPosition);
        if(mUserCollecions.contains(tempBean.getId())){
            mExamBottomLayout.getView(R.id.hand_of_paper).setTag(true);
            ((ImageView)mExamBottomLayout.getView(R.id.handofpagerstart)).setImageResource(R.drawable.collection_icon);//收藏按钮
        }else {
            mExamBottomLayout.getView(R.id.hand_of_paper).setTag(false);
            ((ImageView)mExamBottomLayout.getView(R.id.handofpagerstart)).setImageResource(R.drawable.collection_icon);//未收藏按钮
        }
        ((TextView)mExamBottomLayout.getView(R.id.handofpager)).setText("收藏");
    }

    private List<ExamBean> prepareDatas() {
        List<ExamBean> list = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            int rInt = random.nextInt(3);
            ExamBean bean = new ExamBean();
            switch (rInt % 3) {
                case ExamBean.TEXT_JUDGMENT_EXAM:
                    bean.setExamBeanType(ExamBean.TEXT_JUDGMENT_EXAM);
                    break;
                case ExamBean.TEXT_SINGLE_EXAM:
                    bean.setExamBeanType(ExamBean.TEXT_SINGLE_EXAM);
                    break;
                case ExamBean.TEXT_MULTIPLE_EXAM:
                    bean.setExamBeanType(ExamBean.TEXT_MULTIPLE_EXAM);
                    break;
                case ExamBean.VIDEO_EXAM:
                    bean.setExamBeanType(ExamBean.VIDEO_EXAM);
                    break;
            }
            list.add(bean);
        }
        return list;
    }


    EndExamPop mEndExamPop;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hand_of_paper:
                ExamBean tempBean = mQuestionsList.get(mPosition);
                boolean tag = (boolean) mExamBottomLayout.getView(R.id.hand_of_paper).getTag();
                if(tag){//已经收藏 ---->未收藏
                    //TODO 未收藏状态图片资源
                    mUserCollecions.remove(tempBean.getId());
                    mExamBottomLayout.getView(R.id.hand_of_paper).setTag(!tag);
                    ((ImageView)mExamBottomLayout.getView(R.id.handofpagerstart)).setImageResource(R.drawable.collection_icon);
                }else {//未收藏------->收藏
                    //TODO 收藏状态图片资源
                    mUserCollecions.add(tempBean.getId());
                    mExamBottomLayout.getView(R.id.hand_of_paper).setTag(!tag);
                    ((ImageView)mExamBottomLayout.getView(R.id.handofpagerstart)).setImageResource(R.drawable.collection_icon);
                }
                ToastUtil.showToast("收藏");
                //todo 交卷
//                handOfPaper();
                break;

            case R.id.show_all_layout:
                //todo 显示整个layout
                mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED == mBehavior.getState() ?
                        BottomSheetBehavior.STATE_EXPANDED : BottomSheetBehavior.STATE_COLLAPSED);
                break;
        }
    }

    private void handOfPractice() {
        //todo 结束练习
        if(mEndPracticeDialog == null){
            mEndPracticeDialog = new EndPracticeDialog(this);
        }
        mEndPracticeDialog.bindDatas(mRightQuestionsList.size(),mErrorQuestionsList.size(),mQuestionsList.size());
        mEndPracticeDialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(mEndPracticeDialog.isShowing())
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    public List<ExamBean> getDatas() {
        return mExamAdapter.getmDatas();
    }


    public void userChoiceResult(ExamBean bean, int position) {
        int c = bean.getmChoose().get(0);
        String answer = bean.getAnswer();
        int i;
        switch (bean.getExamBeanType()) {
            case ExamBean.TEXT_JUDGMENT_EXAM:
//                i = SingleExamBean.Judgment.isTrue;
                if(answer.equals("true")){
                    i = 0;
                }else {
                    i = 0;
                }
                if (c != i) {
                    //错
                    errorGo(position);
                    bean.setDoRight(ExamBean.ERROR);
                    CalAnswer(bean,false);
                    refreshBottomCircleUI(position, false);//更新圆圈UI
                    mExamBottomLayout.setErrorNum();//错题目数量自增一
                } else {
                    //对
                    rightGo(position);
                    bean.setDoRight(ExamBean.RIGHT);
                    CalAnswer(bean,true);
                    refreshBottomCircleUI(position, true);
                    mExamBottomLayout.setRightNums();//对题目数量自增一
                }
                break;
            case ExamBean.TEXT_SINGLE_EXAM:
//                i = SingleExamBean.SingleChoose.isTrue;
                if(answer.equals("choiceA")){
                    i = 0;
                }else if (answer.equals("choiceB")) {
                    i = 1;
                }else if(answer.equals("choiceC")){
                    i = 2;
                }else if(answer.equals("choiceD")){
                    i = 3;
                }else {
                    i = 0;
                }


                if (c != i) {
                    //错
                    bean.setDoRight(ExamBean.ERROR);
                    CalAnswer(bean,false);
                    refreshBottomCircleUI(position, false);//更新圆圈UI
                    mExamBottomLayout.setErrorNum();//错题目数量自增一
                    errorGo(position);
                } else {
                    //对

                    bean.setDoRight(ExamBean.RIGHT);
                    CalAnswer(bean,true);

                    refreshBottomCircleUI(position, true);
                    mExamBottomLayout.setRightNums();//对题目数量自增一
                    rightGo(position);
                }
                break;
            case ExamBean.TEXT_MULTIPLE_EXAM:
                List<Integer> lists = bean.getmChoose();
//                int[] cc = cn.gov.bjys.onlinetrain.bean.SingleExamBean.MultiChoose.isTrue;
                List<Integer> cc = new ArrayList<>();
                String[] res = answer.split(",");
                for(String s : res){
                    int k;
                    if(s.equals("choiceA")){
                        k = 0;
                    }else if (s.equals("choiceB")) {
                        k = 1;
                    }else if(s.equals("choiceC")){
                        k = 2;
                    }else if(s.equals("choiceD")){
                        k = 3;
                    }else{
                        k=0;
                    }
                    cc.add(k);
                }

                if (lists.size() == cc.size()) {
                    boolean isEq = false;
                    boolean isRealNotEq = false;
                    int count = 0;
                    for (int tempc : cc) {
                        isEq = false;
                        for (int k = 0; k < lists.size(); k++) {
                            int tempUserC = lists.get(k);
                            if (tempUserC == tempc) {
                                isEq = true;
                                break;
                            }
                            if (k == lists.size() - 1) {
                                if (!isEq) {
                                    isRealNotEq = true;
                                }
                            }
                        }
                    }
                    if (!isRealNotEq) {
                        //全对
                        bean.setDoRight(ExamBean.RIGHT);
                        CalAnswer(bean,true);
                        refreshBottomCircleUI(position, true);
                        mExamBottomLayout.setRightNums();//对题目数量自增一
                        rightGo(position);
                    } else {
                        errorGo(position);
                        bean.setDoRight(ExamBean.ERROR);
                        CalAnswer(bean,false);
                        refreshBottomCircleUI(position, false);//更新圆圈UI
                        mExamBottomLayout.setErrorNum();//错题目数量自增一
                    }
                } else {
                    errorGo(position);
                    bean.setDoRight(ExamBean.ERROR);
                    CalAnswer(bean,false);
                    refreshBottomCircleUI(position, false);//更新圆圈UI
                    mExamBottomLayout.setErrorNum();//错题目数量自增一
                }

                break;
        }

    }

    private void errorGo(int position){
//        setViewPagerAndExamBottom(position);
    }

    private void rightGo(int position){
        setViewPagerAndExamBottom(position+1);
    }

    public void refreshBottomCircleUI(int positions, boolean isRight) {
        if (mDooExamBottomAdapter != null) {
            List<ExamXqBean> datas = mDooExamBottomAdapter.getData();
            if (datas != null && !datas.isEmpty()) {
                ExamXqBean bean = datas.get(positions);
                bean.setmType(isRight ? ExamXqBean.RIGHT : ExamXqBean.FAIL);
            }
            mDooExamBottomAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void finish() {
        super.finish();
    }

    public void finishPractice(){
        //TODO 保存数据 退出activity

    }
}
