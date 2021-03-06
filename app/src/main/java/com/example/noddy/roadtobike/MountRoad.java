package com.example.noddy.roadtobike;
/*산악도로 페이지*/
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapItemizedOverlay;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGPoint;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPathData;
import com.nhn.android.maps.overlay.NMapPathLineStyle;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.mapviewer.overlay.NMapPathDataOverlay;

public class MountRoad extends NMapActivity implements View.OnClickListener,
        NMapView.OnMapStateChangeListener, NMapView.OnMapViewTouchEventListener, NMapOverlayManager.OnCalloutOverlayListener {

    private static final String CLIENT_ID = "QYEl8bJjw912O8_ZRLJ9";
    private NMapView mMapView = null;
    private NMapController mMapController;
    private NMapMyLocationOverlay mMyLocationOverlay;
    private NMapLocationManager mMapLocationManager;
    private NMapCompassManager mMapCompassManager;
    NMapOverlayManager mOverlayManager;
    NMapPOIdataOverlay.OnStateChangeListener onPOIdataStateChangeListener;
    NMapViewerResourceProvider mMapViewerResourceProvider = null;
    MountRoute mMountRoute;

    private ImageButton Wifi_Btn;
    private ImageButton Toilet_Btn;
    private ImageButton Bike_Btn;
    private ImageButton Gps_Btn;
    private ImageButton Back_Btn;
    private ImageButton Camera_Btn;
    private ImageView imageView;
    private ImageView imageView2;
    private ImageView imageView3;
    private ImageView imageView8;

    private int flagForLocation = 0;
    //마커 경로 테스트용도
    private double disMylocationFromMarker = 0;
    private NGeoPoint myLocation, testFromDistance;
    LinearLayout Mapcontainer;
    public NMapPOIdata tpoiData;
    public NMapPOIdataOverlay poiDataOverlay;
    public int markerId;
    MarkerByCategory mMountMarker;
    //마커 이미지 테스트
    Bitmap markerBitmap,bitmapWifi, bitmapToilet, bitmapBike,bitmapNormal;
    Drawable markerTest,markerWifi, markerToilet, markerBike,markernormal;
    Intent intentMount, intent;
    private TextView textview;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mount_road);
        /*for Map APi*/
        Mapcontainer = (LinearLayout) findViewById(R.id.mountRoad_Btn);
        mMapView = new NMapView(this);
        mMapController = mMapView.getMapController();
        mMapView.setClientId(CLIENT_ID);
        Mapcontainer.addView(mMapView);
        mMapView.setClickable(true);
        mMapViewerResourceProvider = new NMapViewerResourceProvider(this);
        mOverlayManager = new NMapOverlayManager(this, mMapView,
                mMapViewerResourceProvider);
        mOverlayManager.setOnCalloutOverlayListener(this);
        mMapView.setOnMapStateChangeListener(this);
        /*
        * poiData.addPOIitem(127.0630205, 37.5091300, "위치1", markerTest, 0);
        * markerTest이 변수가 들어가는 부분인 아마 마커관련된거 같음
        * 아래보이는 두줄이용해서 markerTest에 getResources(),R.drawable.ic_net_wif를 이용해서
        * 변수에 내가 원하는 이미지를 넣음
        */
        markerBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_pin_06_r);
        markerTest = new BitmapDrawable(markerBitmap);
        bitmapNormal = BitmapFactory.decodeResource(getResources(),R.drawable.ic_pin_06_real);
        markernormal = new BitmapDrawable(bitmapNormal);
        bitmapWifi = BitmapFactory.decodeResource(getResources(),R.drawable.ic_wifi_real);
        markerWifi = new BitmapDrawable(bitmapWifi);
        bitmapToilet = BitmapFactory.decodeResource(getResources(),R.drawable.ic_toilet_real);
        markerToilet = new BitmapDrawable(bitmapToilet);
        bitmapBike = BitmapFactory.decodeResource(getResources(),R.drawable.ic_bike_real);
        markerBike = new BitmapDrawable(bitmapBike);
        markerId = NMapPOIflagType.PIN;
        /*my location*/
        mMapLocationManager = new NMapLocationManager(this);
        mMapLocationManager.enableMyLocation(false);
        mMapLocationManager.setOnLocationChangeListener(onMyLocationChangeListener);
        mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(mMapLocationManager, mMapCompassManager);
        /*MountRoute에서 경로를 위해 불러온 함수*/
        mMountRoute = new MountRoute();
        mMountRoute.ExcuteMountRoute(mOverlayManager,mMapView);
        //현재 나의 위치 받아오기
        myLocation = new NGeoPoint();
        myLocation = mMapLocationManager.getMyLocation();
        //카테고리별 마커 설정위한 클래스 호출
        mMountMarker = new MarkerByCategory();
        /*MountRoute에서 마커를 위해 불러온 함수*/
        mMountRoute.ExcuteMountPoint(mOverlayManager,mMapViewerResourceProvider,markernormal);
        /*Button MountRoadpage*/
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        imageView3 = (ImageView) findViewById(R.id.imageView3);
        imageView8 = (ImageView) findViewById(R.id.imageView8);
        Wifi_Btn = (ImageButton) findViewById(R.id.wifi_btn);
        Toilet_Btn = (ImageButton) findViewById(R.id.toilet_btn);
        Bike_Btn = (ImageButton) findViewById(R.id.bike_btn);
        Gps_Btn = (ImageButton) findViewById(R.id.gps_btn);
        Back_Btn = (ImageButton) findViewById(R.id.back_btn);
        Camera_Btn = (ImageButton) findViewById(R.id.camera_btn);
        Wifi_Btn.setOnClickListener(this);
        Toilet_Btn.setOnClickListener(this);
        Bike_Btn.setOnClickListener(this);
        Gps_Btn.setOnClickListener(this);
        Back_Btn.setOnClickListener(this);
        Camera_Btn.setOnClickListener(this);
        imageView.setVisibility(View.GONE);
        imageView2.setVisibility(View.GONE);
        imageView3.setVisibility(View.GONE);
        imageView8.setVisibility(View.GONE);

    }

    public void onMapInitHandler(NMapView mapView, NMapError errorInfo) {
        if (errorInfo == null) { // success
            //Toast.makeText(MountRoad.this, "(테스트)지도 초기화 성공.", Toast.LENGTH_SHORT).show();
            mMapController.setMapCenter(new NGeoPoint(128.2599500, 37.8741320), 11);
        } else { // fail
            // Toast.makeText(MountRoad.this, "(테스트)지도 초기화 실패.", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onMapCenterChange(NMapView nMapView, NGeoPoint nGeoPoint) {
    }

    @Override
    public void onMapCenterChangeFine(NMapView nMapView) {
    }

    @Override
    public void onZoomLevelChange(NMapView nMapView, int i) {
    }

    @Override
    public void onAnimationStateChange(NMapView nMapView, int i, int i1) {
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.wifi_btn:

                imageView2.setVisibility(View.GONE);
                imageView3.setVisibility(View.GONE);
                imageView8.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);                //여기다 버튼 이벤트 코딩
                mMountMarker.ExcutWifiPoint(mOverlayManager,mMapViewerResourceProvider,markerWifi);
                break;
            case R.id.toilet_btn:
                ////여기다 버튼 이벤트 코딩
                imageView.setVisibility(View.GONE);
                imageView2.setVisibility(View.GONE);
                imageView8.setVisibility(View.GONE);
                imageView3.setVisibility(View.VISIBLE);
                mMountMarker.ExcutToiletPoint(mOverlayManager,mMapViewerResourceProvider,markerToilet);
                break;
            case R.id.bike_btn:
                //여기다 버튼 이벤트 코딩
                imageView.setVisibility(View.GONE);
                imageView3.setVisibility(View.GONE);
                imageView8.setVisibility(View.GONE);
                imageView2.setVisibility(View.VISIBLE);
                mMountMarker.ExcutBikePoint(mOverlayManager,mMapViewerResourceProvider,markerBike);
                break;
            case R.id.gps_btn:
                //여기다 버튼 이벤트 코딩
                imageView.setVisibility(View.GONE);
                imageView2.setVisibility(View.GONE);
                imageView3.setVisibility(View.GONE);
                imageView8.setVisibility(View.VISIBLE);
                if(flagForLocation == 0)
                {
                    stopMyLocation();
                    flagForLocation = 1;
                }else if(flagForLocation == 1){
                    startMyLocation();
                    flagForLocation = 0;
                }
                break;
            case R.id.back_btn:
                //여기다 버튼 이벤트 코딩
                //intentMount =new Intent(this, MainActivity.class);
                //startActivity(intentMount);
                this.onBackPressed();
                break;
            case R.id.camera_btn:
                //여기다 버튼 이벤트 코딩
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
                break;
        }
    }
    public double distanceFromMarker(){

        if(myLocation != null)
        {
            disMylocationFromMarker = myLocation.getDistance(mMountRoute.forCheckedPoin[mMountRoute.FlagForMarker],myLocation);
        }
        else
        {
        }
        return disMylocationFromMarker;
    }

    /*현재 위치 시작*/
    private void startMyLocation() {

        if (mMyLocationOverlay != null) {
            if (!mOverlayManager.hasOverlay(mMyLocationOverlay)) {
                mOverlayManager.addOverlay(mMyLocationOverlay);
            }

            if (mMapLocationManager.isMyLocationEnabled()) {

                if (!mMapView.isAutoRotateEnabled()) {
                    mMyLocationOverlay.setCompassHeadingVisible(true);
                    mMapCompassManager.enableCompass();
                    mMapView.setAutoRotateEnabled(true, false);
                    Mapcontainer.requestLayout();
                } else {
                    stopMyLocation();
                }

                mMapView.postInvalidate();
            } else {
                boolean isMyLocationEnabled = mMapLocationManager.enableMyLocation(true);
                if (!isMyLocationEnabled) {
                    Toast.makeText(MountRoad.this, "Please enable a My Location source in system settings",
                            Toast.LENGTH_LONG).show();

                    Intent goToSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(goToSettings);

                    return;
                }
            }
        }
    }

    /*현재 위치 종료*/
    private void stopMyLocation() {
        if (mMyLocationOverlay != null) {
            mMapLocationManager.disableMyLocation();

            if (mMapView.isAutoRotateEnabled()) {
                mMyLocationOverlay.setCompassHeadingVisible(false);

                mMapCompassManager.disableCompass();

                mMapView.setAutoRotateEnabled(false, false);

                Mapcontainer.requestLayout();
            }
        }
    }
    public void showLoginDialog(NMapOverlayItem messageForMarker) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("위치정보");
        builder.setMessage(messageForMarker.getTitle());
        builder.setCancelable(false);

        builder.setNeutralButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.create();
        builder.show();
    }
    @Override
    public void onLongPress(NMapView nMapView, MotionEvent motionEvent) {

    }
    @Override
    public void onLongPressCanceled(NMapView nMapView) {

    }
    @Override
    public void onTouchDown(NMapView nMapView, MotionEvent motionEvent) {

    }
    @Override
    public void onTouchUp(NMapView nMapView, MotionEvent motionEvent) {

    }
    @Override
    public void onScroll(NMapView nMapView, MotionEvent motionEvent, MotionEvent motionEvent1) {

    }
    @Override
    public void onSingleTapUp(NMapView nMapView, MotionEvent motionEvent) {

    }
    /* MyLocation Listener */
    private final NMapLocationManager.OnLocationChangeListener onMyLocationChangeListener = new NMapLocationManager.OnLocationChangeListener() {
        @Override
        public boolean onLocationChanged(NMapLocationManager locationManager, NGeoPoint myLocation) {

            /*위치변경될 때 이벤트 발생*/
            if (mMapController != null) {
                mMapController.animateTo(myLocation);
                mMountRoute.CheckMylocationFromMarker(mOverlayManager,mMapViewerResourceProvider,markerTest, distanceFromMarker());
            }
            return true;
        }
        @Override
        public void onLocationUpdateTimeout(NMapLocationManager locationManager) {
            Toast.makeText(MountRoad.this, "Your current location is temporarily unavailable.", Toast.LENGTH_LONG).show();
        }
        @Override
        public void onLocationUnavailableArea(NMapLocationManager locationManager, NGeoPoint myLocation) {
            Toast.makeText(MountRoad.this, "Your current location is unavailable area.", Toast.LENGTH_LONG).show();
            stopMyLocation();
        }
    };
    /*오버레이가 클릭되었을 때의 이벤트 */
    @Override
    public NMapCalloutOverlay onCreateCalloutOverlay(NMapOverlay arg0,
                                                     NMapOverlayItem arg1, Rect arg2) {
        showLoginDialog(arg1);
        return null;
    }

}