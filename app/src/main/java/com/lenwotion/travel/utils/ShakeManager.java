package com.lenwotion.travel.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by fq on 2017/12/11.
 */
public class ShakeManager implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensor;
    /**
     * 上一次检测的时间
     */
    private long mLastUpdateTime;

    private onShakeListener onShakeListener;

    public ShakeManager(Context context) {
        context = context.getApplicationContext();
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        registerListener();
    }

    /**
     * 传感器更新速率：
     * SensorManager.SENSOR_DELAY_FASTEST:
     * 指定可能最快的传感器更新速率
     * SensorManager.SENSOR_DELAY_GAME:
     * 指定适合在游戏中使用的更新速率
     * SensorManager.SENSOR_DELAY_NORMAL:
     * 指定默认的更新速率
     * SensorManager.SENSOR_DELAY_UI:
     * 指定适合于更新UI的更新速率
     */
    public void registerListener() {
        if (sensor != null && sensorManager != null)
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    public void unregisterListener() {
        if (sensor != null && sensorManager != null)
            sensorManager.unregisterListener(this, sensor);
    }

    //传感器的值改变调用此方法
    @Override
    public void onSensorChanged(SensorEvent event) {
        int miniValue = 15;
        int UPDATE_INTERVAL = 2 * 1000;
        float x = event.values[0];//x轴变化的值
        float y = event.values[1];//y轴变化的值
        float z = event.values[2];//z轴变化的值,记得减去9.8重力加速度
        if (Math.abs(x) > miniValue || Math.abs(y) > miniValue || Math.abs(z - 9.8) > miniValue) {
            long currentTime = System.currentTimeMillis();
            long diffTime = currentTime - mLastUpdateTime;
            if (diffTime < UPDATE_INTERVAL) {
                return;
            }
            //传感器太灵敏，防止摇一摇触发多次,UPDATE_INTERVAL效果执行多久就设置多久
            mLastUpdateTime = currentTime;
            if (onShakeListener != null) {
                onShakeListener.shake();
            }
        }

    }

    //传感器的精确度改变调用此方法
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void setonShakeListener(onShakeListener onShakeListener) {
        this.onShakeListener = onShakeListener;
    }

    public interface onShakeListener {
        void shake();
    }

}
