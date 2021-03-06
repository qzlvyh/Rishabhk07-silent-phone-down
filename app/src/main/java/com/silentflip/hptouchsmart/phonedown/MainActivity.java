package com.silentflip.hptouchsmart.phonedown;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "TAG";

    boolean DEBUG = false;
    public static final String MUSIC_ = "music";
    public static final String RINGTONE_ = "ringtone";
    public static final String ALARM_ = "alarm";

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;

    Toolbar toolbar;

    //FragmentTransaction fragmentTransaction;
    NavigationView navigationView;
    SettingFragment settingFragment;
    HomeFragment homeFragment;
    AboutFragment aboutFragment;
    android.app.FragmentTransaction fragmentTransaction;
    int state = 0;
    public static final int REQ_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setBackgroundColor(Color.parseColor("#323232"));
        Window window = this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.parseColor("#212121"));
        }
        int isGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY);
        Log.d(TAG, "onCreate: " + isGranted);
        NotificationManager n = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(n.isNotificationPolicyAccessGranted()) {
                AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            }else{
                // Ask the user to grant access
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivityForResult(intent,REQ_CODE);
            }
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                actionBarDrawerToggle = new ActionBarDrawerToggle(this , drawerLayout  , toolbar , R.string.drawer_open , R.string.drawer_close);
                actionBarDrawerToggle.getDrawerArrowDrawable().setColor(Color.WHITE);
                navigationView = (NavigationView) findViewById(R.id.navgation_view);



                drawerLayout.setDrawerListener(actionBarDrawerToggle);


                settingFragment = new SettingFragment();
                homeFragment = new HomeFragment();
                aboutFragment = new AboutFragment();

                fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.FragementFrame , homeFragment , null);
                fragmentTransaction.commit();





            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {

                    switch (item.getItemId()){


                        case R.id.home:

                            fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.FragementFrame , homeFragment , null);
                            fragmentTransaction.commit();
                            if(DEBUG)Log.d("TAG"  , "home Called");
                            getSupportActionBar().setTitle("Silent Flip");
                            item.setChecked(true);
                            drawerLayout.closeDrawers();
                            state = 0;
                            break;

                        case R.id.setting:

                            fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.FragementFrame , settingFragment , null);
                            fragmentTransaction.commit();
                            getSupportActionBar().setTitle("Settings");
                            item.setChecked(true);
                            drawerLayout.closeDrawers();
                            state = 1;
                            break;

                        case R.id.about:

                            fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.FragementFrame , aboutFragment, null);
                            fragmentTransaction.commit();
                            getSupportActionBar().setTitle("About");
                            item.setChecked(true);
                            drawerLayout.closeDrawers();
                            state = 2;
                            break;


                    }

                    return false;
                }
            });





    }


    @Override
    protected void onResume() {
        super.onResume();
      if (DEBUG) Log.d("TAG" , "onResume called !! ");
        fragmentTransaction = getFragmentManager().beginTransaction();
        if(state == 0) {
            fragmentTransaction.replace(R.id.FragementFrame, homeFragment, null);
        }
        if(state == 1) {
            fragmentTransaction.replace(R.id.FragementFrame, settingFragment, null);
        }
        if(state == 2) {
            fragmentTransaction.replace(R.id.FragementFrame, aboutFragment, null);
        }
        fragmentTransaction.commit();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
