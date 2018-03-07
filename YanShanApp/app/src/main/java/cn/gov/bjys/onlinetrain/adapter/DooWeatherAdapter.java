package cn.gov.bjys.onlinetrain.adapter;

import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.gov.bjys.onlinetrain.R;
import cn.gov.bjys.onlinetrain.bean.WeatherInfoBean;

/**
 * Created by dodo on 2018/3/7.
 */

public class DooWeatherAdapter extends BaseQuickAdapter<WeatherInfoBean.detailWeatherInfo, BaseViewHolder> {


    public DooWeatherAdapter(int layoutResId, List<WeatherInfoBean.detailWeatherInfo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, WeatherInfoBean.detailWeatherInfo item) {
        ImageView todayIcon = helper.getView(R.id.today);
        if (helper.getAdapterPosition() != 0) {
            todayIcon.setVisibility(View.GONE);
        }

        helper.setText(R.id.xingqi, fixDate(item.getDate()));

        helper.setText(R.id.type, item.getType());

        helper.setText(R.id.wendu, getWenduAverage(item.getHigh(), item.getLow()));

        ImageView icon = helper.getView(R.id.icon);

        setWeatherIcon(icon, item.getType());
    }


    private String fixDate(String inputStr) {
        String outputStr = "";

        int index = inputStr.indexOf("星期");

        outputStr = inputStr.substring(index);

        return outputStr;
    }

    //获取平均温度
    private String getWenduAverage(String high, String low) {
        float hWendu = 0;
        int indexH = high.indexOf("温");
        String hStr = high.substring(indexH, high.length() - 2).trim();


        float lWendu = 0;
        int indexL = low.indexOf("温");
        String lStr = low.substring(indexL, low.length() - 2).trim();

        try {
            hWendu = Float.valueOf(hStr);
            lWendu = Float.valueOf(lStr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (hWendu + lWendu) / 2L + "°";
    }


    //给天气状况选择图标
    private void setWeatherIcon(ImageView icon, String weatherStr) {

        if (weatherStr.contains("晴")) {
            //晴天图片

        } else if (weatherStr.contains("多云")) {
            //多云图片

        } else if (weatherStr.contains("阴")) {
            //阴天图片

        } else if (weatherStr.contains("雨")) {
            if (weatherStr.contains("小")) {
                //小雨图片

            } else {
                //雨图片

            }
        }


    }


}