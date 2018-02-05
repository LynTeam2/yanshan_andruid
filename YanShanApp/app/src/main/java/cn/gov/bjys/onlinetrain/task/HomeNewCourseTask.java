package cn.gov.bjys.onlinetrain.task;

import com.ycl.framework.utils.util.FastJSONParser;

import java.io.File;
import java.io.IOException;

import cn.gov.bjys.onlinetrain.adapter.DooHomeClassStudyAdapter;
import cn.gov.bjys.onlinetrain.bean.NewestCourseBean;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/2/3 0003.
 */
public class HomeNewCourseTask extends BaseAsyncTask {

    public final static String RELATIVE_PATH = "course" + File.separator+"latest.json";//相对路径
    DooHomeClassStudyAdapter mAdapter;
    public HomeNewCourseTask(DooHomeClassStudyAdapter adapter){
        mAdapter = adapter;
    }

    @Override
    protected Void doInBackground(Void... params) {
        File queFile = new File(rootDir+File.separator+rootName+File.separator+RELATIVE_PATH);
        if(queFile == null){
            return null;
        }
        Observable.just(queFile)
                .filter(new Func1<File, Boolean>() {
                    @Override
                    public Boolean call(File file) {
                        return file.getName().endsWith(".json");
                    }
                })
                .map(new Func1<File, String>() {
                    @Override
                    public String call(File file) {
                        String str = "";
                        try {
                            str = ReaderJsonFile(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return str;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        NewestCourseBean bean = FastJSONParser.getBean(s, NewestCourseBean.class);
                        if(bean != null){
                            mAdapter.setNewData(bean.getCourses());
                        }
                    }
                });
        return super.doInBackground(params);
    }
}
