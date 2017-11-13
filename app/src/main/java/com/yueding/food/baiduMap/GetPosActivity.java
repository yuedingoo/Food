package com.yueding.food.baiduMap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.yueding.food.R;
import com.yueding.food.db.Restaurant;

import java.util.ArrayList;
import java.util.List;

public class GetPosActivity extends AppCompatActivity {

    private TextureMapView mMapView;
    private BaiduMap mBaiduMap;
    LocationClient mLocClient;
    public MyLocationListener myListener = new MyLocationListener();
    private String addr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_get_pos);
        mMapView = findViewById(R.id.mTexturemap);
        mBaiduMap = mMapView.getMap();

        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(GetPosActivity.this, Manifest.permission.
                ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(GetPosActivity.this, Manifest.permission.
                READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(GetPosActivity.this, Manifest.permission.
                WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(GetPosActivity.this, permissions, 1);
        } else {
            requestLocation();
        }

    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            addr = bdLocation.getAddrStr();    //获取详细地址信息
//            String country = bdLocation.getCountry();    //获取国家
//            String province = bdLocation.getProvince();    //获取省份
//            String city = bdLocation.getCity();    //获取城市
//            String district = bdLocation.getDistrict();    //获取区县
//            String street = bdLocation.getStreet();    //获取街道信息

            Toast.makeText(GetPosActivity.this, addr, Toast.LENGTH_SHORT).show();

            LatLng ll = new LatLng(bdLocation.getLatitude(),
                    bdLocation.getLongitude());

            // 构造定位数据
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(bdLocation.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude()).build();
            // 设置定位数据
            mBaiduMap.setMyLocationData(locData);

            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(ll).zoom(18.5f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

        }

    }

    private void requestLocation() {
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }


    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLocation();
                } else {
                    Toast.makeText(this, "必须同意权限才能使用", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ok_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ok_menu:
                if (addr != null) {
                    int id = getIntent().getIntExtra("id", 0);
                    Restaurant restaurantUpdate = new Restaurant();
                    restaurantUpdate.setAddress(addr);
                    restaurantUpdate.update(id);
                }
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
