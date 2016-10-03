package com.silentflip.hptouchsmart.phonedown;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

public class screenOff extends Service implements SensorEventListener {

    boolean DEBUG = true;
    boolean music;
    boolean ringtone;
    boolean alarm;
    boolean flag = true;
    boolean alarm_recovery = true;
    boolean music_recover ;
    SharedPreferences sharedPreferences;
    public  int ringVolume ;
    public int alarmVolume;
    public int musicVolume;
    AudioManager audioManager;
    boolean alarmFlipActive = false;
    boolean musicFlipActive = false;
    boolean ringFlipActive = false;
    long duration;
    long current;
    long alarmCurrent;
    long snoozeDur;


    public static final String TAG = "TAG";
    BroadcastReceiver broadcastReceiver;
    SensorManager sensorManager;

    long currentTime ;
    public screenOff() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");

    }

    @Override
    public void onCreate() {
        super.onCreate();



       if(DEBUG) Log.d(TAG , "Service oncreate called !! ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(DEBUG)Log.d(TAG ,"Service fired");
        //Toast.makeText(screenOff.this, "Silent Flip ON", Toast.LENGTH_SHORT).show();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        music = sharedPreferences.getBoolean("MUSIC" , false);
        ringtone = sharedPreferences.getBoolean("RINGTONE" , false);
        alarm = sharedPreferences.getBoolean("ALARM" , false);
        alarm_recovery = sharedPreferences.getBoolean("ALARM_RECOVERY" , false);
        music_recover = sharedPreferences.getBoolean("MUSIC_REC" , false);

        if(DEBUG){
        Log.d("MUSIC" , "value : " + music );
        Log.d("RINGTONE" , "value : " + ringtone );
        Log.d("ALARM" , "value : " + alarm );
        }

        flag = true;


        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this , sensor, SensorManager.SENSOR_DELAY_NORMAL);

        AudioManager audioManager;

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        alarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        ringVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
        musicVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("RING_VOLUME" , ringVolume);
        editor.commit();

        currentTime = SystemClock.uptimeMillis();


        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

//
//        Log.d("MUSIC" , "value : " + music );
//        Log.d("RINGTONE" , "value : " + ringtone );
//        Log.d("ALARM" , "value : " + alarm );

        if((SystemClock.uptimeMillis() - currentTime) > 1000){
            currentTime = SystemClock.uptimeMillis();
            if(true) {
                Log.d("x : ", "" + event.values[0]);
                Log.d("y : ", "" + event.values[1]);
                Log.d("z : ", "" + event.values[2]);
            }
        }


        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean check = sharedPreferences.getBoolean("CHECK" , false);



        if(check){
            music = sharedPreferences.getBoolean("MUSIC" , false);
            ringtone = sharedPreferences.getBoolean("RINGTONE" , false);
            alarm = sharedPreferences.getBoolean("ALARM" , false);
            alarm_recovery = sharedPreferences.getBoolean("ALARM_RECOVERY" , false);
            music_recover = sharedPreferences.getBoolean("MUSIC_REC", false);
            duration = Long.parseLong(sharedPreferences.getString("RING_REC", "300000"));
            snoozeDur = Long.parseLong(sharedPreferences.getString("SLEEP_MODE" , "300000"));
        }


        if(event.values[2] < -9 && flag && (event.values[1] > -6) ){
            //Log.d(TAG," on sensor changed z less than -8");

            if(music == true) {
                if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) > 0) {
                    musicVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                }
                musicFlipActive = true;
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            }
            if(ringtone == true){

//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putBoolean("RING_FLIPPED"  , true);
//                editor.commit();
                if(audioManager.getStreamVolume(AudioManager.STREAM_RING) > 0 ){
                    ringVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
                }
                current = SystemClock.uptimeMillis();
                audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                ringFlipActive = true;

            }
            if(alarm == true && alarmFlipActive == false){

                if(audioManager.getStreamVolume(AudioManager.STREAM_ALARM) > 0){
                    alarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
                }




                alarmFlipActive = true;
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM , 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);


            }
        }

        if(event.values[2] > 9 && flag && alarmFlipActive && alarm_recovery){
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM , alarmVolume, 0);
            alarmFlipActive = false;
            alarmCurrent  =  SystemClock.uptimeMillis();
        }
        if(event.values[2] > 7 && flag && musicFlipActive && music_recover){
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC , musicVolume , 0);
            musicFlipActive = false;
        }
        if(((SystemClock.uptimeMillis() - current) > duration) && ringFlipActive && flag ){

            audioManager.setStreamVolume(AudioManager.STREAM_RING , ringVolume , 0);
            ringFlipActive = false;

        }

        if(event.values[2] < -9 && alarm ==true && flag && (SystemClock.uptimeMillis() - alarmCurrent ) > snoozeDur && alarmFlipActive == true){


            audioManager.setStreamVolume(AudioManager.STREAM_ALARM , alarmVolume , 0);

        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onDestroy() {
       if(DEBUG) Log.d(TAG , "Service Destroyed");
       // Toast.makeText(screenOff.this, "Silent Flip OFF ", Toast.LENGTH_SHORT).show();
        flag = false;
        super.onDestroy();

    }
}
