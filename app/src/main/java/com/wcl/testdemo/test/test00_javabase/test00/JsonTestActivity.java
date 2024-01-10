package com.wcl.testdemo.test.test00_javabase.test00;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.wcl.testdemo.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Author WCL
 * @Date 2023/11/30 17:06
 * @Version
 * @Description Json测试界面.
 */
public class JsonTestActivity extends AppCompatActivity {

    /**
     * Comment: 用来输出测试结果的控制台.
     */
    @BindView(R.id.tv)
    TextView mTvConsole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json_test);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_0, R.id.tv_1, R.id.tv_2, R.id.tv_3, R.id.tv_4, R.id.tv_5, R.id.tv_6, R.id.tv_7, R.id.tv_8, R.id.tv_9, R.id.tv_10, R.id.tv_11, R.id.tv_12, R.id.tv_13, R.id.tv_14, R.id.tv_15, R.id.tv_16, R.id.tv_17, R.id.tv_18, R.id.tv_19, R.id.tv_20})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_0://Json转[普通对象].
                test0();
                break;
            case R.id.tv_1://Json转[单层多泛型对象].
                test1();
                break;
            case R.id.tv_2://Json转[多层多泛型对象].
                test2();
                break;
            case R.id.tv_3://
                break;
            case R.id.tv_4://
                break;
            case R.id.tv_5://
                break;
            case R.id.tv_6://
                break;
            case R.id.tv_7://
                break;
            case R.id.tv_8://
                break;
            case R.id.tv_9://
                break;
            case R.id.tv_10://
                break;
            case R.id.tv_11://
                break;
            case R.id.tv_12://
                break;
            case R.id.tv_13://
                break;
            case R.id.tv_14://
                break;
            case R.id.tv_15://
                break;
            case R.id.tv_16://
                break;
            case R.id.tv_17://
                break;
            case R.id.tv_18://
                break;
            case R.id.tv_19://
                break;
            case R.id.tv_20://
                break;
        }
    }

    //Json转[多层多泛型对象].
    private void test2() {
        //List:
        List<Person> personList = new ArrayList<>();
        personList.add(new Person("List1"));
        personList.add(new Person("List2"));
        //Map:
        Map<String, Person> personMap = new HashMap<>();
        personMap.put("key1", new Person("Map1"));
        personMap.put("key2", new Person("Map2"));
        //数组:
        Person[] personArr = new Person[]{new Person("Array1"), new Person("Array2")};
        //构建Json字符串:
        String json = GsonUtils.toJson(new Result<List<Person>, Map<String, Person>, Person[]>(personList, personMap, personArr));//Json字符串.
        //Json字符串转对象(方式1):
        Result<List<Person>, Map<String, Person>, Person[]> result1 = GsonUtils.fromJson(json, Result.class);
        //Json字符串转对象(方式2):
        Result<Integer, String, Person> result2 = GsonUtils.fromJson(
                json,
                GsonUtils.getType(
                        Result.class, //参数1:总Type.
                        GsonUtils.getListType(Person.class), //可变参数2:泛型1Type.
                        GsonUtils.getMapType(String.class, Person.class), //可变参数3:泛型2Type.
                        GsonUtils.getArrayType(Person.class) //可变参数4:泛型3Type.
                )
        );
        //打印.
        String msg = "Json转[多层多泛型对象]:\n" + json + "\n方式一:\n" + result1 + "\n方式二:\n" + result2;
        print(msg);
    }

    //Json转[单层多泛型对象].
    private void test1() {
        //构建Json字符串:
        String json = GsonUtils.toJson(new Result<Integer, String, Person>(29, "WCL", new Person("WCL")));//Json字符串.
        //Json字符串转对象(方式1):
        Result<Integer, String, Person> result1 = GsonUtils.fromJson(json, Result.class);
        //Json字符串转对象(方式2):
        Result<Integer, String, Person> result2 = GsonUtils.fromJson(
                json,
                GsonUtils.getType(
                        Result.class, //参数1:总Type.
                        Integer.class, //可变参数2:泛型1Type.
                        String.class, //可变参数3:泛型2Type.
                        Person.class //可变参数4:泛型3Type.
                )
        );
        //打印.
        String msg = "Json转[单层多泛型对象]:\n" + json + "\n方式一:\n" + result1 + "\n方式二:\n" + result2;
        print(msg);
    }

    //Json转[普通对象].
    private void test0() {
        //构建Json字符串:
        String json = GsonUtils.toJson(new Person("WCL"));//Json字符串.
        //Json字符串转对象(方式1):
        Person person1 = GsonUtils.fromJson(json, Person.class);
        //Json字符串转对象(方式2):
        Person person2 = GsonUtils.fromJson(json, GsonUtils.getType(Person.class));
        //打印.
        String msg = "Json转[普通对象]:\n" + json + "\n方式一:\n" + person1 + "\n方式二:\n" + person2;
        print(msg);
    }

    //一键三连,在三个地方输出打印结果.
    private void print(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            LogUtils.d(msg);
            ToastUtils.showShort(msg);
            mTvConsole.setText(msg);
        }
    }

    /**
     * @Author WCL
     * @Date 2023/11/30 17:14
     * @Version
     * @Description
     */
    static class Result<K, T, V> {
        int code;
        String message;
        K kData;
        T tData;
        V vData;

        Result(K kData, T tData, V vData) {
            this.code = 200;
            this.message = "success";
            this.kData = kData;
            this.tData = tData;
            this.vData = vData;
        }
    }

    /**
     * @Author WCL
     * @Date 2023/11/30 17:14
     * @Version
     * @Description
     */
    static class Person {

        String name;
        int age;
        String address;

        Person(String name) {
            this.name = name;
        }
    }

}