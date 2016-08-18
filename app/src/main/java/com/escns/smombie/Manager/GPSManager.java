package com.escns.smombie.Manager;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by hyo99 on 2016-08-09.
 */

public class GPSManager implements LocationListener
{
    Context mContext;

    Location location; // 위치정보
    protected LocationManager locationManager;

    double mCurLon = 0.0; // 현재 경도
    double mCurLat = 0.0; // 현재 위도
    double mLastLon = 0.0; // 이전 경도
    double mLastLat = 0.0; // 이전 위도
    double mDistance = 0.0; // 사이 거리

    long lastTime = System.currentTimeMillis(); // GPS가 갱신된 전시간
    long curTime = System.currentTimeMillis(); // GPS가 갱신된 현재시간
    long betweenTime; // GPS가 갱신되기까지 걸린 시간
    double speed; // 이동 속도

    // 현재 GPS 사용유무
    boolean isGPSEnabled = false;

    // 접근 권한 유무
    boolean isAccessEnabled = false;

    // 최소 GPS 정보 업데이트 거리 10미터
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 3;//10;
    // 최소 GPS 정보 업데이트 시간 밀리세컨이므로 1분
    private static final long MIN_TIME_BW_UPDATES = 1000;//1000 * 60 * 1;

    /**
     * 생성자
     * @param con MainActivity의 Context
     //* @param act MainActivity의 Activity
     */
    public GPSManager (Context con)
    {
        mContext = con;
    }

    /**
     * 객체화 시 이전경도, 이전위도, 이전시간을 초기화시키주는 함수
     * @param a 이전 경도
     * @param b 이전 시간
     */
    public void init(double a, double b)
    {
        mLastLon = a;
        mLastLat = b;

        lastTime = System.currentTimeMillis();
    }

    /**
     * GPS로부터 현재위치의 경도,위도를 받아오는 함수
     * @return GPS가 정상적으로 동작하면 1을 아니면 0을 반환
     */
    public boolean getLocation() {

        if (ContextCompat.checkSelfPermission( mContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
        }

        // 연결
        locationManager = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);

        // GPS 유무정보 가져오기
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(isGPSEnabled) {
            Log.d("tag", "값 도출!!! isGPSEnabled : " + isGPSEnabled);
            location = null;

            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Log.d("tag", "location : " + location);
            }
            catch (Exception e) {
                Log.d("tag", "error / location : " + location);
            }

            if(location != null) {
                mLastLon = mCurLon;
                mLastLat = mCurLat;
                mCurLon = location.getLongitude();
                mCurLat = location.getLatitude();
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    /**
     * GPS 정보를 통해 경도,위도,
     * @return 0:GPS 사용불가 / 1:제자리 / 2:걷기
     */
    public int getData() {

        // getLocation() : 경도, 위도 구하기

        //걸린시간 구하기
        curTime = System.currentTimeMillis();
        betweenTime = (curTime - lastTime) / 1000; // 걸린 시간
        lastTime = curTime;

        if( getLocation() ) {

            mDistance = calculateDistance(mLastLon, mLastLat, mCurLon, mCurLat);

            //distance = distance / 10;      // 오차 보정 --> 사용자설정

            Log.d("tag", "이동거리(변환전) : " + mDistance);
            // distance = cutPoint(distance);
            Log.d("tag", "이동거리(변환후) : " + mDistance);

            // 이동속도 구하기
            if(mDistance == 0. || (mLastLon == mCurLon)&&(mLastLat == mCurLat)) {
                speed = 0.;
            }
            else {
                speed = (mDistance) / (double) betweenTime; // m/s
                Log.d("tag", "이동속도(변환전) : " + speed);
                // speed = cutPoint(speed);
                Log.d("tag", "이동속도(변환후) : " + speed);
            }

            Log.d("tag", "걸린시간 : " + betweenTime);

            // 제자리일 때는 1, 걸을 때는 2를 반환
            if (speed < 0.5) {
                Toast.makeText(mContext, "lon: " + mCurLon + " / lat: " + mCurLat + "\n제자리입니다 " + betweenTime, Toast.LENGTH_SHORT).show();
                return 2;
            } else {
                Toast.makeText(mContext, "lon: " + mCurLon + " / lat: " + mCurLat + "\n" + speed + "m/s입니다 " + betweenTime, Toast.LENGTH_SHORT).show();
                return 2;
            }
        }
        else { // GPS 정보를 받아오지 못할 때 0을 반환
            Toast.makeText(mContext, "GPS가 작동하지않습니다 " + betweenTime, Toast.LENGTH_SHORT).show();
            return 2;
        }
    }

    public double calculateDistance(double lastLon, double lastLat, double curLon, double curLat) {
        //이동거리 구하기
        double theta = lastLon - curLon;
        double distance = Math.sin(deg2rad(lastLat)) * Math.sin(deg2rad(curLat)) + Math.cos(deg2rad(lastLat))
                * Math.cos(deg2rad(curLat)) * Math.cos(deg2rad(theta));
        distance = Math.acos(distance);
        distance = rad2deg(distance);

        distance = distance * 60 * 1.1515;
        distance = distance * 1.609344;    // 단위 mile 에서 km 변환.
        distance = distance * 1000.0;      // 단위  km 에서 m 로 변환

        return distance;
    }

    /**
     * 주어진 도(degree) 값을 라디언으로 변환
     * @param deg 도 값
     * @return 계산값 반환
     */
    private double deg2rad(double deg)
    {
        return (double)(deg * Math.PI / (double)180d);
    }

    /**
     * 주어진 라디언(radian) 값을 도(degree) 값으로 변환
     * @param rad 라디언 값
     * @return 계산값 반환
     */
    private double rad2deg(double rad)
    {
        return (double)(rad * (double)180d / Math.PI);
    }

    /**
     * 소숫점을 자르는 함수
     * @param d 소숫점을 가진 실수
     * @return 자른 소숫점 반환
     */
    public double cutPoint(double d)
    {
        String str = String.format("%.5f", d); // 소숫점 자르기
        return Double.parseDouble(str);
    }


    /**
     * 위치정보가 갱신 될 때마다 호출되는 오버라이딩 함수
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.d("tag", "위치가 갱신되었습니다!!!");
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle)
    {
    }

    @Override
    public void onProviderEnabled(String s)
    {
    }

    @Override
    public void onProviderDisabled(String s)
    {
    }

    public double getmCurLon() {
        return mCurLon;
    }

    public void setmCurLon(double mCurLon) {
        this.mCurLon = mCurLon;
    }

    public double getmCurLat() {
        return mCurLat;
    }

    public void setmCurLat(double mCurLat) {
        this.mCurLat = mCurLat;
    }
}
