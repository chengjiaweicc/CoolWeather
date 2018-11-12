package com.example.coolweather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coolweather.db.City;
import com.example.coolweather.db.County;
import com.example.coolweather.db.Province;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {//遍历省市县数据的碎片  碎片不能直接显示在界面上，还需要把它添加到活动里

    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTY=2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList=new ArrayList<>();

    /**
     * 省列表
     */
    private List<Province> provinceList;

    /**
     * 市列表
     */
    private  List<City> cityList;

    /**
     * 县列表
     */
    private List<County> countyList;

    /**
     * 选中的省份
     */
    private Province selectedProvince;

    /**
     * 选中的城市
     */
    private City selectedCity;

    /**
     * 当前选中的级别
     */
    private int currentLevel;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.choose_area,container,false);
        titleText=(TextView) view.findViewById(R.id.title_text);//获取控件实例
        backButton=(Button) view.findViewById(R.id.back_button);
        listView=(ListView) view.findViewById(R.id.list_view);
        adapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);//初始化ArrayAdapter
        listView.setAdapter(adapter);//设置listView的适配器
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {//碎片的用法
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//listView的点击事件
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {//判断当前级别是否为省级别
                    selectedProvince=provinceList.get(position);//获取省份
                    queryCities();//查询市，加载市级数据
                }else if (currentLevel == LEVEL_CITY) {//判断当前级别是否为市
                    selectedCity=cityList.get(position);//获取市
                    queryCounties();//查询县，加载县级数据
                }//从省市县列表界面跳转到天气界面
                 else if (currentLevel==LEVEL_COUNTY) {//判断如果当前级别是LEVEL_COUNTY，就启动WeatherActivity，并把当前选中县的天气id传送过去
                    String weatherId=countyList.get(position).getWeatherId();
                    //调用getActivity()方法，配合instanceof关键字，就可以判断出该碎片在MainActivity还是WeatherActivity中
                    if (getActivity() instanceof MainActivity) {//在MainActivity中，处理逻辑不变
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    }else if (getActivity() instanceof WeatherActivity) {//在WeatherActivity中，就关闭滑动菜单，显示下拉刷新进度条，然后请求新城市的天气信息
                        WeatherActivity activity=(WeatherActivity) getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefresh.setRefreshing(true);
                        activity.requestWeather(weatherId);
                    }
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {//backButton的点击事件
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY) {//对当前级别进行判断
                    queryCities();
                }else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();//调用queryProvinces方法，开始加载省级数据
                }
            }
        });
        queryProvinces();
    }

    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryProvinces() {
        titleText.setText("中国");//将头布局的标题设置成中国
        backButton.setVisibility(View.GONE);//隐藏返回按钮，到了省级别就不能再返回
        provinceList= DataSupport.findAll(Province.class);//调用LitePal的查询接口来从数据库中读取省级数据
        if (provinceList.size()>0) {//判断是否从数据库中读取到数据
            dataList.clear();
            for (Province province:provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);//将数据显示到界面上
            currentLevel=LEVEL_PROVINCE;
        }else{
            String address="http://guolin.tech/api/china";//组装一个请求地址
            queryFromServer(address,"province");//调用queryFromServer方法从服务器上查询数据
        }
    }

    /**
     * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceName());//将头布局设置成省的民称
        backButton.setVisibility(View.VISIBLE);//设置返回按钮可见
        //从数据库中读取市级数据
        cityList=DataSupport.where("provinceid = ?", String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size()>0) {//判断是否读取到数据
            dataList.clear();
            for (City city:cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);//将数据显示到界面上
            currentLevel=LEVEL_CITY;
        }else{
            int provinceCode=selectedProvince.getProvinceCode();
            String address="http://guolin.tech/api/china/"+provinceCode;
            queryFromServer(address,"city");//调用queryFromServer方法从服务器上查询数据
        }
    }

    /**
     * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryCounties() {
        titleText.setText(selectedCity.getCityName());//将头布局设置成市的名称
        backButton.setVisibility(View.VISIBLE);//返回按钮可见
        countyList=DataSupport.where("cityid=?",String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size()>0) {//判断是否读到数据
            dataList.clear();
            for (County county:countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_COUNTY;
        }else{
            int provnceCode=selectedProvince.getProvinceCode();
            int cityCode=selectedCity.getCityCode();
            String adddress="http://guolin.tech/api/china/"+provnceCode+"/"+cityCode;
            queryFromServer(adddress,"county");
        }
    }

    /**
     * 根据传入的地址和类型从服务器上查询省市县数据
     */
    private void queryFromServer(String address, final String type) {
        showProgressDialog();//调用显示进度对话框方法
        HttpUtil.sendOkHttpRequest(address, new Callback() {//调用sendOkHttpRequest方法向服务器发送请求，响应的数据会回调到onResponse方法中
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText=response.body().string();
                boolean result =false;
                if ("province".equals(type)) {
                    result= Utility.handleProvinceResponse(responseText);//调用handleProvinceResponse方法来解析和处理服务器返回的数据，并存储到数据库中
                }else if ("city".equals(type)) {
                    result=Utility.handleCityResponse(responseText,selectedProvince.getId());
                }else if ("county".equals(type)) {
                    result=Utility.handleCountyResponse(responseText,selectedCity.getId());
                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                /**
                                 * 再次调用queryProvinces方法来重新加载省级数据，由于queryProvinces方法牵扯到了UI操作，因此必须在主线程中调用
                                 * 这里借助runOnUiThread()方法来实现从子线程切换到主线程，现在数据库中已经存在了数据，因此调用queryProvinces就会直接将数据显示到界面上
                                 */
                                queryProvinces();
                            }else if ("city".equals(type)) {
                                queryCities();
                            }else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }

            }

            @Override
            public void onFailure(Call call, IOException e) {
                //通过runOnUiThread()方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}

