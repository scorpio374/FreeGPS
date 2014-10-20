package cn.sz.free.gps;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.ArcOptions;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.DotOptions;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

public class MainActivity extends Activity {

	private MapView mMapView = null;
	public LocationClient mLocationClient;
	public MyLocationListener mMyLocationListener;
	private TextView resultText;
	private BaiduMap mBaiduMap;
	private LocationMode mCurrentMode;
	boolean isFirstLoc = true;// 是否首次定位
	
	private Button locationButton;
	private Button routeButton;
	private Button offlineButton;
	private Button geometryButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//在使用SDK各组件之前初始化context信息，传入ApplicationContext  
        //注意该方法要再setContentView方法之前实现  
        SDKInitializer.initialize(getApplicationContext());  
        setContentView(R.layout.activity_main);  
        resultText = (TextView)findViewById(R.id.tv_test);
        locationButton = (Button)findViewById(R.id.locationButton);
        routeButton = (Button)findViewById(R.id.routeButton);
        offlineButton = (Button)findViewById(R.id.offlineButton);
        geometryButton = (Button)findViewById(R.id.geometryButton);
        
        locationButton.setOnClickListener(mOnClickListener);
        routeButton.setOnClickListener(mOnClickListener);
        offlineButton.setOnClickListener(mOnClickListener);
        geometryButton.setOnClickListener(mOnClickListener);
        
        //获取地图控件引用 
        mMapView = (MapView) findViewById(R.id.bmapView); 
		
        //location
//        initLocationParams();
//        startLocation();
	}

	@Override  
    protected void onDestroy() {  
        super.onDestroy();  
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理  
        mMapView.onDestroy();  
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
    
    private void startLocation(){
    	mCurrentMode = LocationMode.NORMAL;
        mBaiduMap = mMapView.getMap();
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				mCurrentMode, true, null));
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocationClient = new LocationClient(this);
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(3000);
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
		mLocationClient.start();
    }
    
    /**
     * 计算距离
     * @param
     */
    private void startRoute(){
    	double distance = getDistanceFromXtoY(22.540231,113.950054,22.540231,114.950054);
    	Log.d("Debug","distance:"+distance);
    }
    
    private void startOffline(){
    	Intent intent = new Intent(MainActivity.this,OfflineDemo.class);
    	startActivity(intent);
    }
    
    /**
     * 绘图操作
     *  time : 2014-10-20 11:22:47
    	error code : 161
    	latitude : 22.540231
    	lontitude : 113.950054
    	radius : 65.11307
    	广东省深圳市南山区科技南二路
    	operationers : 1
     */
    private void startGeometry(){
    	// add point
    	LatLng llDot = new LatLng(22.540231, 113.970054);
		OverlayOptions ooDot = new DotOptions().center(llDot).radius(6)
				.color(0xFF0000FF);
		mBaiduMap.addOverlay(ooDot);
		
		LatLng llDot1 = new LatLng(22.560231, 113.990054);
		OverlayOptions ooDot1 = new DotOptions().center(llDot1).radius(6)
				.color(0xFF0000FF);
		mBaiduMap.addOverlay(ooDot1);
		
		LatLng llDot2 = new LatLng(22.540231, 114.000054);
		OverlayOptions ooDot2 = new DotOptions().center(llDot2).radius(6)
				.color(0xFF0000FF);
		mBaiduMap.addOverlay(ooDot2);
		
		//add line
		LatLng p1 = new LatLng(22.540231, 113.970054);
		LatLng p2 = new LatLng(22.560231, 113.990054);
		LatLng p3 = new LatLng(22.540231, 114.000054);
		List<LatLng> points = new ArrayList<LatLng>();
		points.add(p1);
		points.add(p2);
		points.add(p3);
		OverlayOptions ooPolyline = new PolylineOptions().width(5)
				.color(0xAA0000FF).points(points);
		mBaiduMap.addOverlay(ooPolyline);
		
		// 添加弧线
		OverlayOptions ooArc = new ArcOptions().color(0xAA00FF00).width(4)
				.points(p1, p2, p3);
		mBaiduMap.addOverlay(ooArc);
		
		// 添加圆
		LatLng llCircle = new LatLng(22.540231, 114.010054);
		OverlayOptions ooCircle = new CircleOptions().fillColor(0x000000FF)
				.center(llCircle).stroke(new Stroke(5, 0xAA000000))
				.radius(1400);
		mBaiduMap.addOverlay(ooCircle);
		
		// 添加多边形
		LatLng pt1 = new LatLng(22.550231, 113.940054);
		LatLng pt2 = new LatLng(22.550231, 113.960054);
		LatLng pt3 = new LatLng(22.530231, 113.960054);
		LatLng pt4 = new LatLng(22.535231, 113.950054);
		LatLng pt5 = new LatLng(22.530231, 113.940054);
		List<LatLng> pts = new ArrayList<LatLng>();
		pts.add(pt1);
		pts.add(pt2);
		pts.add(pt3);
		pts.add(pt4);
		pts.add(pt5);
		OverlayOptions ooPolygon = new PolygonOptions().points(pts)
				.stroke(new Stroke(5, 0xAA00FF00)).fillColor(0xAAFFFF00);
		mBaiduMap.addOverlay(ooPolygon);
		
		// 添加文字
		LatLng llText = new LatLng(22.520231, 116.397428);
		OverlayOptions ooText = new TextOptions().bgColor(0xAAFFFF00)
				.fontSize(24).fontColor(0xFFFF00FF).text("Free GPS").rotate(-30)
				.position(llText);
		mBaiduMap.addOverlay(ooText);
    }
    


    public static double getDistanceFromXtoY(double lat_a, double lng_a,double lat_b, double lng_b)
    {
	    double pk = (double) (180 / 3.14169);
	
	    double a1 = lat_a / pk;
	    double a2 = lng_a / pk;
	    double b1 = lat_b / pk;
	    double b2 = lng_b / pk;
	
	    double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
	    double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
	    double t3 = Math.sin(a1) * Math.sin(b1);
	    double tt = Math.acos(t1 + t2 + t3);
	
	    return 6366000 * tt;
     }

    
    private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch (arg0.getId()) {
			case R.id.locationButton:
				startLocation();
				break;
				
			case R.id.routeButton:
				startRoute();
				break;
				
			case R.id.offlineButton:
				startOffline();
				break;
				
			case R.id.geometryButton:
				startGeometry();
				break;

			default:
				break;
			}
		}
	};
    
	/**
	 * 实现实位回调监听
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			//Receive Location 
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation){
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
				sb.append("\ndirection : ");
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				sb.append(location.getDirection());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				//运营商信息
				sb.append("\noperationers : ");
				sb.append(location.getOperators());
			}
			Log.d("Debug", ""+sb.toString());
			resultText.setText(sb.toString());
			
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			}
		}
	}
	
}


