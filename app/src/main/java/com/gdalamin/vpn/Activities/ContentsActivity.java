 package com.gdalamin.vpn.Activities;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

//import com.airbnb.lottie.LottieAnimationView;

import com.airbnb.lottie.LottieAnimationView;
import com.anchorfree.partner.api.callback.Callback;
import com.anchorfree.partner.api.data.Country;
import com.anchorfree.partner.api.response.RemainingTraffic;
import com.anchorfree.partner.exceptions.PartnerRequestException;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.anchorfree.vpnsdk.vpnservice.VPNState;
import com.gdalamin.vpn.BuildConfig;
import com.gdalamin.vpn.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.material.navigation.NavigationView;
import com.gdalamin.vpn.Config;
import com.gdalamin.vpn.Utils.LocalFormatter;
import com.gdalamin.vpn.speed.Speed;
//import com.pixplicity.easyprefs.library.Prefs;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

 public abstract class ContentsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    LottieAnimationView lottieAnimationView;
    boolean vpn_toast_check = true;
    private Handler mHandler = new Handler();
    private long mStartTXtRX = 0;
    private long mStartTX = 0;
    Handler handlerTrafic = null;

    protected static final String TAG = MainActivity.class.getSimpleName();

    private int adCount = 0;
    VPNState state;
    int progressBarValue = 0;
    Handler handler = new Handler();
    private Handler customHandler = new Handler();
    private long startTime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    @BindView(R.id.testShowMeterDownload)
    TextView textDownloading;

    @BindView(R.id.testShowMeterupload)
    TextView textUploading;

    @BindView(R.id.vpn_details)
    ImageView vpn_detail_image;

    @BindView(R.id.tv_timer)
    TextView timerTextView;

    @BindView(R.id.connect_btn)
    ImageView connectBtnTextView;

    @BindView(R.id.connection_state)
    TextView connectionStateTextView;

    @BindView(R.id.flag_image)
    ImageView imgFlag;

    @BindView(R.id.flag_name)
    TextView flagName;


    //admob native advance)
    private UnifiedNativeAd nativeAd;
    public InterstitialAd mInterstitialAd;
    private String STATUS;
    private DrawerLayout drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        lottieAnimationView = findViewById(R.id.animation_view);


        ButterKnife.bind(this);



        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        vpn_detail_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showServerList();
            }
        });

        TextView next = (TextView) findViewById(R.id.in);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textAcitivity();
            }
        });



    }


    @Override
    protected void onStart() {
        super.onStart();
        if(STATUS == null) return;
        if (STATUS.equals("Connect")) {
            updateUI();
            connectToVpn();
//            loadAdAgain();
        }
        else if (STATUS.equals("Disconnect")) {
            disconnectAlert();
//            loadAdAgain();
        }
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



                      @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_upgrade) {
//            upgrade application is available...
        }
        else if (id == R.id.nav_helpus) {
//            find help about the application
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"emon79605@gmail.com.com"}); //  Support Email Here
            intent.putExtra(Intent.EXTRA_SUBJECT, "Send Bug Report");
            intent.putExtra(Intent.EXTRA_TEXT, "Please Give Your Feedback ");

            try {
                startActivity(Intent.createChooser(intent, "send mail"));
            } catch (ActivityNotFoundException ex) {
                Toast.makeText(this, "No mail app found!!!", Toast.LENGTH_SHORT);
            } catch (Exception ex) {
                Toast.makeText(this, "Unexpected Error!!!", Toast.LENGTH_SHORT);
            }
        } else if (id == R.id.nav_rate) {
//            rate application...
            rateUs();
        } else if (id == R.id.nav_share) {
//            share the application...
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "share app");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "I'm using this Free VPN App, it's provide all servers free https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
            } catch (Exception e) {
            }

        }
        else if (id == R.id.nav_policy) {
            Uri uri = Uri.parse(getResources().getString(R.string.privacy_policy_link)); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private Handler mUIHandler = new Handler(Looper.getMainLooper());
    final Runnable mUIUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            updateUI();
            checkRemainingTraffic();
            mUIHandler.postDelayed(mUIUpdateRunnable, 10000);
        }
    };

    @Override
    protected void onResume() {
//        if the application again available from background state...
        super.onResume();
        isConnected(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    startUIUpdateTask();
                }
            }

            @Override
            public void failure(PartnerRequestException error) {

            }


        });
    }

    @Override
    protected void onPause() {
//        application in the background state...
        super.onPause();
        stopUIUpdateTask();
    }

    @Override
    protected void onDestroy() {
        if (nativeAd != null) {
            nativeAd.destroy();
        }
        super.onDestroy();
    }

    protected abstract void loginToVpn();

    @OnClick(R.id.connect_btn)
    public void onConnectBtnClick(View v) {

        isConnected(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    STATUS = "Disconnect";


                        disconnectAlert();

                } else {

                    STATUS = "Connect";


                        updateUI();
                        connectToVpn();

                }
            }

            @Override
            public void failure(PartnerRequestException error) {
                Toast.makeText(getApplicationContext(), "" + error.getMessage(), Toast.LENGTH_LONG).show();

            }


        });

    }

    /*  Different functions defining the state of the
     *  */
    protected abstract void isConnected(Callback<Boolean> callback);

    protected abstract void connectToVpn();

    protected abstract void disconnectFromVnp();


     protected abstract void chooseServer();

     protected abstract void getCurrentServer(Callback<String> callback);

    protected void startUIUpdateTask() {
        stopUIUpdateTask();
        mUIHandler.post(mUIUpdateRunnable);
    }

    protected void stopUIUpdateTask() {
        mUIHandler.removeCallbacks(mUIUpdateRunnable);
        updateUI();
    }

    protected abstract void checkRemainingTraffic();

    protected void updateUI() {

//        To find svpn_vpn state...

        UnifiedSDK.getVpnState(new com.anchorfree.vpnsdk.callbacks.Callback<VPNState>() {
            @Override
            public void success(@NonNull VPNState vpnState) {
                state = vpnState;
                switch (vpnState) {
                    case IDLE: {

                        loadIcon();
                        connectBtnTextView.setEnabled(true);
                        connectionStateTextView.setText(R.string.disconnected);
                        timerTextView.setVisibility(View.GONE);
                        hideConnectProgress();
                        break;
                    }
                    case CONNECTED: {

                        //TODO ui elemante
                        loadIcon();
                        textDownloading.setVisibility(View.VISIBLE);
                        textUploading.setVisibility(View.VISIBLE);

                        connectBtnTextView.setEnabled(true);
                        connectionStateTextView.setText(R.string.connected);
                        timer();
                        timerTextView.setVisibility(View.VISIBLE);
                        hideConnectProgress();
                        break;
                    }
                    case CONNECTING_VPN:
                    case CONNECTING_CREDENTIALS:
                    case CONNECTING_PERMISSIONS: {
                        loadIcon();
                        connectionStateTextView.setText(R.string.connecting);
                        connectBtnTextView.setEnabled(true);
                        timerTextView.setVisibility(View.GONE);
                        showConnectProgress();
                        break;
                    }
                    case PAUSED: {
//                        svpn_vpn paused...
                        connectBtnTextView.setImageResource(R.drawable.ic_power);
//                        t_connection_status.setText("Not Selected");
                        connectionStateTextView.setText(R.string.paused);
//                        i_connection_status_image.setImageResource(R.drawable.ic_dot);
                        break;
                    }
                    default: {
//
                    }

                }
            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });

        getCurrentServer(new Callback<String>() {
            //            try to connect to current svpn_vpn server...
            @Override
            public void success(@NonNull final String currentServer) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }

            @Override
            public void failure(PartnerRequestException error) {

            }


        });
    }

    protected void updateTrafficStats(long outBytes, long inBytes) {
//        try to update the traffic state of the svpn_vpn...
        String outString = LocalFormatter.easyRead(outBytes, false);
        String inString = LocalFormatter.easyRead(inBytes, false);

    }

    protected void showConnectProgress() {
//        Updating progressbar
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                while (state == VPNState.CONNECTING_VPN || state == VPNState.CONNECTING_CREDENTIALS) {
                    progressBarValue++;

                    handler.post(new Runnable() {

                        @Override
                        public void run() {

                        }
                    });
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    protected void hideConnectProgress() {
        connectionStateTextView.setVisibility(View.VISIBLE);
    }

    protected void showMessage(String msg) {
        Toast.makeText(ContentsActivity.this, msg, Toast.LENGTH_SHORT).show();

    }
//TODO I need to change This
    protected void rateUs() {
        Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flag to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + this.getPackageName())));
        }
    }

    protected void timer() {
        if (adCount == 0) {
            startTime = SystemClock.uptimeMillis();
            customHandler.postDelayed(updateTimerThread, 0);
            timeSwapBuff += timeInMilliseconds;

        }
    }

    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            int hrs = mins / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            timerTextView.setText(String.format("%02d", hrs) + ":"
                    + String.format("%02d", mins) + ":"
                    + String.format("%02d", secs));
            customHandler.postDelayed(this, 0);
        }

    };

    //Todo work on this
     protected void loadIcon() {
         if (state == VPNState.IDLE) {
             connectBtnTextView.setImageResource(R.drawable.cnt);


         } else if (state == VPNState.CONNECTING_VPN || state == VPNState.CONNECTING_CREDENTIALS) {
             connectBtnTextView.setVisibility(View.VISIBLE);/*INVISIBLE IS CHEANGED TO VISIBLE*/
             lottieAnimationView.setVisibility(View.VISIBLE);
         } else if (state == VPNState.CONNECTED) {

             connectBtnTextView.setVisibility(View.VISIBLE);
             connectBtnTextView.setImageResource(R.drawable.ideal);

//             t_connection_status.setText("Selected");
             lottieAnimationView.setVisibility(View.GONE);
             if (vpn_toast_check == true) {
                 Toasty.success(ContentsActivity.this, "Server Connected", Toast.LENGTH_SHORT).show();
                 vpn_toast_check = false;
             }

         }

     }



     protected void disconnectAlert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you want to disconnect?");
        builder.setPositiveButton("Disconnect",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        disconnectFromVnp();
                        vpn_toast_check = true;
                        Toasty.success(ContentsActivity.this, "Server Disconnected", Toast.LENGTH_SHORT).show();
                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toasty.success(ContentsActivity.this, "VPN Remains Connected", Toast.LENGTH_SHORT).show();
                    }
                });
        builder.show();
    }




    /**
     * Creates a request for a new native ad based on the boolean parameters and calls the
     * corresponding "populate" method when one is successfully returned.
     */



    void showServerList() {
        startActivity(new Intent(this, Servers.class));
    }

    void textAcitivity(){
    }


    @OnClick(R.id.category)
    void openSideNavigation() {
        drawer.openDrawer(GravityCompat.START, true);
    }

    private long mLastRxBytes = 0;
    private long mLastTxBytes = 0;
    private long mLastTime = 0;

    private Speed mSpeed;


    private void handleTraficData() {

        if (handlerTrafic == null)
            return;


        handlerTrafic.postDelayed(this::setTraficData, 1000);


    }

    private void setTraficData() {
        long currentRxBytes = TrafficStats.getTotalRxBytes();

        long currentTxBytes = TrafficStats.getTotalTxBytes();

        long usedRxBytes = currentRxBytes - mLastRxBytes;
        long usedTxBytes = currentTxBytes - mLastTxBytes;
        long currentTime = System.currentTimeMillis();
        long usedTime = currentTime - mLastTime;

        mLastRxBytes = currentRxBytes;
        mLastTxBytes = currentTxBytes;
        mLastTime = currentTime;

        mSpeed = new Speed(this);
        mSpeed.calcSpeed(usedTime, usedRxBytes, usedTxBytes);

//            mIndicatorNotification.updateNotification(mSpeed);
        Log.e("speed-->>", "down-->>" + mSpeed.down.speedValue + "    upload-->>" + mSpeed.up.speedValue);


        if (state != null &&mSpeed != null && mSpeed.up != null && mSpeed.down != null && state.equals(VPNState.CONNECTED)) {


            textDownloading.setText(mSpeed.down.speedValue + " " + mSpeed.down.speedUnit);
            textUploading.setText(mSpeed.up.speedValue + " " + mSpeed.up.speedUnit);

//            sendBroadcast(traffic);
        } else {
            textDownloading.setText("0  " + mSpeed.down.speedUnit);
            textUploading.setText("0  " + " " + mSpeed.up.speedUnit);
        }

        handleTraficData();
    }


     public abstract void onRegionSelected(Country item);
 }
