package sonytv.remote.com.dk;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.connectsdk.core.MediaInfo;
import com.connectsdk.core.SubtitleInfo;
import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.device.ConnectableDeviceListener;
import com.connectsdk.device.DevicePicker;
import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.service.DeviceService;
import com.connectsdk.service.capability.ExternalInputControl;
import com.connectsdk.service.capability.KeyControl;
import com.connectsdk.service.capability.Launcher;
import com.connectsdk.service.capability.MediaControl;
import com.connectsdk.service.capability.MediaPlayer;
import com.connectsdk.service.capability.MouseControl;
import com.connectsdk.service.capability.PlaylistControl;
import com.connectsdk.service.capability.PowerControl;
import com.connectsdk.service.capability.TVControl;
import com.connectsdk.service.capability.TextInputControl;
import com.connectsdk.service.capability.ToastControl;
import com.connectsdk.service.capability.VolumeControl;
import com.connectsdk.service.capability.WebAppLauncher;
import com.connectsdk.service.command.ServiceCommandError;
import com.connectsdk.service.sessions.LaunchSession;

import java.util.List;
import java.util.Timer;

public class MainActivity extends FragmentActivity {
    private static final int NUM_PAGES = 2;
    private ViewPager mPager;
    private FragmentActivity myContext;
    AlertDialog dialog;
    AlertDialog pairingAlertDialog;
    AlertDialog pairingCodeDialog;
    DevicePicker dp;
    //   ConnectableDevice mTV;
    LinearLayout llMask;

    //   private ConnectableDevice mTv;
    private Launcher launcher;
    static MediaPlayer mediaPlayer;
    static MediaControl mediaControl;
    static TVControl tvControl;
    static VolumeControl volumeControl;
    private ToastControl toastControl;
    private MouseControl mouseControl;
    private TextInputControl textInputControl;
    static PowerControl powerControl;
    private ExternalInputControl externalInputControl;
    static KeyControl keyControl;
    private WebAppLauncher webAppLauncher;
    public Button[] buttons;
    Context mContext;
    public static ConnectableDevice mTV;
  //  public  TestResponseObject testResponse;

    private ConnectableDeviceListener deviceListener = new ConnectableDeviceListener() {

        @Override
        public void onPairingRequired(ConnectableDevice device, DeviceService service, DeviceService.PairingType pairingType) {
            Log.e("2ndScreenAPP", "Connected to " + mTV.getIpAddress());

            switch (pairingType) {
                case FIRST_SCREEN:
                    Log.e("2ndScreenAPP", "First Screen");
                    pairingAlertDialog.show();
                    break;

                case PIN_CODE:
                case MIXED:
                    Log.e("2ndScreenAPP", "Pin Code");
                    pairingCodeDialog.show();
                    break;

                case NONE:
                default:
                    break;
            }
        }

        @Override
        public void onConnectionFailed(ConnectableDevice device, ServiceCommandError error) {
            Log.e("2ndScreenAPP", "onConnectFailed");
            connectFailed(mTV);
        }

        @Override
        public void onDeviceReady(ConnectableDevice device) {
            Log.e("2ndScreenAPP", "onPairingSuccess");
            if (pairingAlertDialog.isShowing()) {
                pairingAlertDialog.dismiss();
            }
            if (pairingCodeDialog.isShowing()) {
                pairingCodeDialog.dismiss();
            }
            registerSuccess(mTV);
        }

        @Override
        public void onDeviceDisconnected(ConnectableDevice device) {
            Log.e("2ndScreenAPP", "Device Disconnected");
            connectEnded(mTV);
        }

        @Override
        public void onCapabilityUpdated(ConnectableDevice device, List<String> added, List<String> removed) {

        }
    };

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remote_new);
        DiscoveryManager.init(getApplicationContext());
        DiscoveryManager.getInstance().registerDefaultDeviceTypes();
        DiscoveryManager.getInstance().setPairingLevel(DiscoveryManager.PairingLevel.ON);
        DiscoveryManager.getInstance().start();
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        setupPicker();
//        DiscoveryManager.getInstance().registerDefaultDeviceTypes();
//        DiscoveryManager.getInstance().setPairingLevel(DiscoveryManager.PairingLevel.ON);
//        DiscoveryManager.getInstance().start();
   //     EPGApplication.testResponse = new TestResponseObject();


    }
    public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }



        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch(position) {
                case 0: return  new ControlFragment();
                case 1: return  new TVFragment();
                default: return  new ControlFragment();
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }





    /////////////////////////
    public static class TVFragment extends android.support.v4.app.Fragment {
        ImageButton ibPower;
        ImageButton ibVolUp;
        ImageButton ibVolDown;
        ImageButton ibChUp;
        ImageButton ibChDown;
        ImageButton ibNumber;
        Button btnNumber,btnNumber1, btnNumber2, btnNumber3, btnNumber4, btnNumber5, btnNumber6, btnNumber7, btnNumber8, btnNumber9, btnExit, btnOk;
        LinearLayout llNumber;
        LinearLayout llTVControl;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(
                    R.layout.tvfragment, container, false);
            ibPower = (ImageButton) rootView.findViewById(R.id.ibPowerN);
            ibVolUp = (ImageButton) rootView.findViewById(R.id.ibVolUp);
            ibVolDown = (ImageButton) rootView.findViewById(R.id.ibVolDown);
            ibChUp = (ImageButton) rootView.findViewById(R.id.ibChUp);
            ibChDown = (ImageButton) rootView.findViewById(R.id.ibChDown);
            ibNumber = (ImageButton) rootView.findViewById(R.id.ibNumber);
            btnNumber1 = (Button) rootView.findViewById(R.id.number1);
            btnNumber = (Button) rootView.findViewById(R.id.number0);
            btnNumber2 = (Button) rootView.findViewById(R.id.number2);
            btnNumber3 = (Button) rootView.findViewById(R.id.number3);
            btnNumber4 = (Button) rootView.findViewById(R.id.number4);
            btnNumber5 = (Button) rootView.findViewById(R.id.number5);
            btnNumber6 = (Button) rootView.findViewById(R.id.number6);
            btnNumber7 = (Button) rootView.findViewById(R.id.number7);
            btnNumber8 = (Button) rootView.findViewById(R.id.number8);
            btnNumber9 = (Button) rootView.findViewById(R.id.number9);
            btnExit = (Button) rootView.findViewById(R.id.audio);
            btnOk = (Button) rootView.findViewById(R.id.subt);
            llNumber = (LinearLayout) rootView.findViewById(R.id.llNumber);
            llTVControl = (LinearLayout) rootView.findViewById(R.id.llTVCOntrol);

            ibPower.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (powerControl != null) {
                //        testResponse = new TestResponseObject(true, TestResponseObject.SuccessCode, TestResponseObject.Power_OFF);
                        powerControl.powerOff(null);
                    }

                }
            });
            ibVolUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (volumeControl != null){
                        volumeControl.volumeUp(null);
                  //      testResponse =  new TestResponseObject(true, TestResponseObject.SuccessCode, TestResponseObject.VolumeUp);
                        }

                }
            });
            ibVolDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (volumeControl != null){
                        volumeControl.volumeDown(null);
                 //       testResponse =  new TestResponseObject(true, TestResponseObject.SuccessCode, TestResponseObject.VolumeDown);
                        }

                }
            });
            ibChUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tvControl != null)
                        tvControl.channelUp(null);

                }
            });
            ibChDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(tvControl != null) {
                        tvControl.channelDown(null);
                    }

                }
            });
            ibNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    llTVControl.setVisibility(View.GONE);
                    llNumber.setVisibility(View.VISIBLE);
                    Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slideinr);
                    llNumber.startAnimation(hyperspaceJumpAnimation);

                }
            });

            btnNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  //  if (keyControl != null)
                   //     keyControl.sendKeyCode(KeyControl.KeyCode.NUM_0, null);
                }
            });
            btnNumber1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                //    if (keyControl != null)
                  //      keyControl.sendKeyCode(KeyControl.KeyCode.NUM_1, null);
                }
            });
            btnNumber2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                //    if (keyControl != null)
                 //       keyControl.sendKeyCode(KeyControl.KeyCode.NUM_2, null);
                }
            });
            btnNumber3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                //    if (keyControl != null)
                //        keyControl.sendKeyCode(KeyControl.KeyCode.NUM_3, null);
                }
            });
            btnNumber4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                //    if (keyControl != null)
                //        keyControl.sendKeyCode(KeyControl.KeyCode.NUM_4, null);
                }
            });
            btnNumber5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
               //     if (keyControl != null)
                //        keyControl.sendKeyCode(KeyControl.KeyCode.NUM_5, null);
                }
            });
            btnNumber6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                //    if (keyControl != null)
                 //       keyControl.sendKeyCode(KeyControl.KeyCode.NUM_6, null);
                }
            });
            btnNumber7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                 //   if (keyControl != null)
                  //      keyControl.sendKeyCode(KeyControl.KeyCode.NUM_7, null);
                }
            });
            btnNumber8.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
               //     if (keyControl != null)
                //        keyControl.sendKeyCode(KeyControl.KeyCode.NUM_8, null);
                }
            });
            btnNumber9.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                //    if (keyControl != null)
                 //       keyControl.sendKeyCode(KeyControl.KeyCode.NUM_9, null);
                }
            });
            btnExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    llTVControl.setVisibility(View.VISIBLE);
                    llNumber.setVisibility(View.GONE);
                    Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slideinl);
                    llTVControl.startAnimation(hyperspaceJumpAnimation);
                }
            });
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (keyControl != null) {
                        keyControl.ok(null);
                     //   testResponse =  new TestResponseObject(true, TestResponseObject.SuccessCode, TestResponseObject.Clicked);
                    }
                }
            });


            return rootView;
        }
        public static TVFragment newInstance(String text) {

            TVFragment f = new TVFragment();
            Bundle b = new Bundle();
            b.putString("msg", text);

            f.setArguments(b);

            return f;
        }
    }
    ///////////////////
    public static class ControlFragment extends android.support.v4.app.Fragment {
        ImageButton ibNextControl;
        ImageButton ibPreControl;
        ImageButton ibUpControl;
        ImageButton ibDownControl;
        LinearLayout llControl;
        ImageButton ibOk;
        ImageButton ibMenu;
        ImageButton ibExit;
        ImageButton ibBack, ibVolumeMute;


        public static ControlFragment newInstance(String text) {

            ControlFragment f = new ControlFragment();
            Bundle b = new Bundle();
            b.putString("msg", text);

            f.setArguments(b);

            return f;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(
                    R.layout.controlfragment, container, false);
            ibNextControl = (ImageButton) rootView.findViewById(R.id.ibnextcontrol);
            ibDownControl = (ImageButton) rootView.findViewById(R.id.ibdowncontrol);
            ibPreControl = (ImageButton) rootView.findViewById(R.id.ibprecontrol);
            ibUpControl = (ImageButton) rootView.findViewById(R.id.ibupcontrol);
            ibBack = (ImageButton) rootView.findViewById(R.id.ibBackN);
            ibOk = (ImageButton) rootView.findViewById(R.id.ibOkN);
            ibMenu = (ImageButton) rootView.findViewById(R.id.ibMenuN);
            ibExit = (ImageButton) rootView.findViewById(R.id.ibExitN);
            llControl = (LinearLayout) rootView.findViewById(R.id.llControl);
            ibVolumeMute = (ImageButton) rootView.findViewById(R.id.ibVolumeN) ;

            ibNextControl.setOnTouchListener(new View.OnTouchListener(){
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    llControl.setBackgroundResource(R.drawable.oknextfocus);
                    if(event.getAction() == MotionEvent.ACTION_UP){
                        llControl.setBackgroundResource(R.drawable.ok);
                    }


                    return false;
                }
            });
            ibUpControl.setOnTouchListener(new View.OnTouchListener(){
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    llControl.setBackgroundResource(R.drawable.okupfocus);
                    if(event.getAction() == MotionEvent.ACTION_UP){
                        llControl.setBackgroundResource(R.drawable.ok);
                    }


                    return false;
                }
            });
            ibDownControl.setOnTouchListener(new View.OnTouchListener(){
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    llControl.setBackgroundResource(R.drawable.okdownfocus);
                    if(event.getAction() == MotionEvent.ACTION_UP){
                        llControl.setBackgroundResource(R.drawable.ok);
                    }


                    return false;
                }
            });
            ibPreControl.setOnTouchListener(new View.OnTouchListener(){
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    llControl.setBackgroundResource(R.drawable.okprefocus);
                    if(event.getAction() == MotionEvent.ACTION_UP){
                        llControl.setBackgroundResource(R.drawable.ok);
                    }


                    return false;
                }
            });

            ibVolumeMute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setSelected(!view.isSelected());
                    if(volumeControl != null)
                        volumeControl.setMute(view.isSelected(), null);
                    if (view.isSelected()) {
                      //  testResponse =  new TestResponseObject(true, TestResponseObject.SuccessCode, TestResponseObject.Muted_Media);
                    } else {
                    //    testResponse =  new TestResponseObject(true, TestResponseObject.SuccessCode, TestResponseObject.UnMuted_Media);
                    }
                }
            });

            ibUpControl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (keyControl != null) {
                        keyControl.up(null);
                   //     testResponse = new TestResponseObject(true, TestResponseObject.SuccessCode, TestResponseObject.UpClicked);
                    }

                }
            });
            ibDownControl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (keyControl != null) {
                        keyControl.down(null);
                     //   testResponse =  new TestResponseObject(true, TestResponseObject.SuccessCode, TestResponseObject.DownClicked);
                    }

                }
            });
            ibPreControl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (keyControl != null) {
                        keyControl.left(null);
                    //    testResponse =  new TestResponseObject(true, TestResponseObject.SuccessCode, TestResponseObject.LeftClicked);
                    }

                }
            });
            ibNextControl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (keyControl != null) {
                        keyControl.right(null);
                      //  testResponse =  new TestResponseObject(true, TestResponseObject.SuccessCode, TestResponseObject.RightClicked);
                    }

                }
            });

            ibBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (keyControl != null) {
                        keyControl.back(null);
                    }

                }
            });
            ibExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                //    if (keyControl != null) {
                 //       keyControl.exit(null);
                 //   }

                }
            });
            ibMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (keyControl != null) {
                        keyControl.home(null);
                     //   testResponse =  new TestResponseObject(true, TestResponseObject.SuccessCode, TestResponseObject.HomeClicked);
                    }

                }
            });
            ibOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (keyControl != null) {
                        keyControl.ok(null);
                     //   testResponse =  new TestResponseObject(true, TestResponseObject.SuccessCode, TestResponseObject.Clicked);
                    }

                }
            });

            return rootView;
        }
    }


    ////////////////////////Remote control
    void connectFailed(ConnectableDevice device) {
        if (device != null)
            Log.e("2ndScreenAPP", "Failed to connect to " + device.getIpAddress());

        if (mTV != null) {
            mTV.removeListener(deviceListener);
            mTV.disconnect();
            mTV = null;
        }
    }

    void connectEnded(ConnectableDevice device) {
        if (pairingAlertDialog.isShowing()) {
            pairingAlertDialog.dismiss();
        }
        if (pairingCodeDialog.isShowing()) {
            pairingCodeDialog.dismiss();
        }
        mTV.removeListener(deviceListener);
        mTV = null;
    }
    void registerSuccess(ConnectableDevice device) {
        Log.e("2ndScreenAPP", "successful register");
        setTv();
    }
    private void setupPicker() {
        dp = new DevicePicker(this);

        ///////////////////
//        final DevicePickerListView view = new DevicePickerListView(this);
//        ConnectableDevice c = new ConnectableDevice();
//        for (int i = 0; i <  view.pickerAdapter.getCount(); i++) {
//            ConnectableDevice d = view.pickerAdapter.getItem(i);
//        }
        //////////////////////

        dialog = dp.getPickerDialog("Device List", new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                mTV = (ConnectableDevice)arg0.getItemAtPosition(arg2);
                mTV.addListener(deviceListener);
                mTV.setPairingType(null);
                mTV.connect();
                //    connectItem.setTitle(mTV.getFriendlyName());
                dp.pickDevice(mTV);
            }
        });

        pairingAlertDialog = new AlertDialog.Builder(this)
                .setTitle("Pairing with TV")
                .setMessage("Please confirm the connection on your TV")
                .setPositiveButton("Okay", null)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dp.cancelPicker();

                        hConnectToggle();
                    }
                })
                .create();

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        pairingCodeDialog = new AlertDialog.Builder(this)
                .setTitle("Enter Pairing Code on TV")
                .setView(input)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (mTV != null) {
                            String value = input.getText().toString().trim();
                            mTV.sendPairingKey(value);
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dp.cancelPicker();
                        hConnectToggle();
                    }
                })
                .create();
        dialog.show();
    }
    public void hConnectToggle()
    {
        if (!isFinishing()) {
            if (mTV != null)
            {
                if (mTV.isConnected())
                    mTV.disconnect();
                mTV.removeListener(deviceListener);
                mTV = null;
            } else
            {
                dialog.show();
            }
        }
    }

    public void setTv()
    {
        //   = tv;

        if (mTV == null) {
            launcher = null;
            mediaPlayer = null;
            mediaControl = null;
            tvControl = null;
            volumeControl = null;
            toastControl = null;
            textInputControl = null;
            mouseControl = null;
            externalInputControl = null;
            powerControl = null;
            keyControl = null;
            webAppLauncher = null;

            //  disableButtons();
        }
        else {
            launcher = mTV.getCapability(Launcher.class);
            mediaPlayer = mTV.getCapability(MediaPlayer.class);
            mediaControl = mTV.getCapability(MediaControl.class);
            tvControl = mTV.getCapability(TVControl.class);
            volumeControl = mTV.getCapability(VolumeControl.class);
            toastControl = mTV.getCapability(ToastControl.class);
            textInputControl = mTV.getCapability(TextInputControl.class);
            mouseControl = mTV.getCapability(MouseControl.class);
            externalInputControl = mTV.getCapability(ExternalInputControl.class);
            powerControl = mTV.getCapability(PowerControl.class);
            keyControl = mTV.getCapability(KeyControl.class);
            webAppLauncher = mTV.getCapability(WebAppLauncher.class);

            // enableButtons();
        }
    }
    public LaunchSession launchSession;
    //private MediaControl mMediaControl = null;
    private PlaylistControl mPlaylistControl = null;
    public static final String URL_IMAGE_ICON =
            "http://ec2-54-201-108-205.us-west-2.compute.amazonaws.com/samples/media/videoIcon.jpg";
    public static final String URL_VIDEO_MP4 = "http://103.233.48.21/hls/vod/Hoang/ryan/index.m3u8";
    private void playVideo() {
        SubtitleInfo.Builder subtitleBuilder = null;
//        if (subtitlesButton.isChecked()) {
//            subtitleBuilder = new SubtitleInfo.Builder(
//                    getTv().hasCapability(MediaPlayer.Subtitle_WebVTT) ? URL_SUBTITLES_WEBVTT :
//                            URL_SUBTITLE_SRT);
//            subtitleBuilder.setLabel("English").setLanguage("en");
//        }

        MediaInfo mediaInfo = new MediaInfo.Builder(URL_VIDEO_MP4, "video/mp4")
                .setTitle("luongdt")
                .setDescription("ABCSDDSSD")
                .setIcon(URL_IMAGE_ICON)
             //   .setId(12345)
             //   .setAccountNumber("123")
           //     .setPosition(10000)
                .setSubtitleInfo(subtitleBuilder == null ? null : subtitleBuilder.build())
                .build();

//        mediaPlayer.playMedia(URL_VIDEO_MP4, "", "title", "sdas", "", true, new MediaPlayer.LaunchListener() {
//            @Override
//            public void onSuccess(MediaPlayer.MediaLaunchObject object) {
//
//            }
//
//            @Override
//            public void onError(ServiceCommandError error) {
//
//            }
//        });

        mediaPlayer.playMedia(mediaInfo, true, new MediaPlayer.LaunchListener() {

            @Override
            public void onError(ServiceCommandError error) {
                Log.e("Error", "Error playing video", error);
                //      stopMediaSession();
            }

            public void onSuccess(MediaPlayer.MediaLaunchObject object) {
                Log.e(""," sucesss");
                launchSession = object.launchSession;
            //    testResponse = new TestResponseObject(true, TestResponseObject.SuccessCode,
              //          TestResponseObject.Play_Video);
                mediaControl = object.mediaControl;
                mPlaylistControl = object.playlistControl;
                stopUpdating();
                //   enableMedia();
                //   isPlaying = true;
            }
        });
    }
    private Timer refreshTimer;
    private void stopUpdating() {
        if (refreshTimer == null)
            return;

        refreshTimer.cancel();
        refreshTimer = null;
    }
    public void removeTVListener(){
        if (mTV != null) {
            mTV.removeListener(deviceListener);
            mTV.disconnect();
            mTV = null;
        }
    }
    @Override
    public void onBackPressed() {
//		super.onBackPressed();
        removeTVListener();
        finish();

    }


}
