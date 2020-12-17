package smartcity.ldgd.com.checkyfa64apitest.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import com.aill.androidserialport.SerialPort;
import com.aill.androidserialport.SerialPortFinder;
import com.example.yf_a64_api.YF_A64_API_Manager;

import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.core.AccountCreator;
import org.linphone.core.Address;
import org.linphone.core.Call;
import org.linphone.core.CallParams;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;
import org.linphone.core.MediaDirection;
import org.linphone.core.ProxyConfig;
import org.linphone.core.RegistrationState;
import org.linphone.core.TransportType;
import org.linphone.core.Transports;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import smartcity.ldgd.com.checkyfa64apitest.R;
import smartcity.ldgd.com.checkyfa64apitest.camera.CameraManager;
import smartcity.ldgd.com.checkyfa64apitest.entity.LdDevice;
import smartcity.ldgd.com.checkyfa64apitest.services.LinphoneService;
import smartcity.ldgd.com.checkyfa64apitest.util.AlarmClickHelper;
import smartcity.ldgd.com.checkyfa64apitest.util.FaceRecoUtil;
import smartcity.ldgd.com.checkyfa64apitest.util.FtpManager;
import smartcity.ldgd.com.checkyfa64apitest.util.LogUtil;
import smartcity.ldgd.com.checkyfa64apitest.util.MyByteUtil;
import smartcity.ldgd.com.checkyfa64apitest.util.UpdateAppManager;
import smartcity.ldgd.com.checkyfa64apitest.view.CircularProgressView;

import static smartcity.ldgd.com.checkyfa64apitest.util.MyByteUtil.bytesIntHL;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 26;
    private static final int STOP_FINGERPRINTVIEW = 11;
    private static final int START_FINGERPRINTVIEW = 12;
    private static final int STOP_DEVICE_AND_CAMERA = 13;
    private static final int START_DEVICE_AND_CAMERA = 14;
    private static final int UP_PARAMETER = 15;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};
    // 人脸识别存放目录
    private String ficeFile = Environment.getExternalStorageDirectory() + "/magic";
    // 广告配置文件地址
    private String adConfigFile = Environment.getExternalStorageDirectory() + "/AdConfig";
    private static final String TAG = "MainActivity";


    // 要切换的照片，放在drawable文件夹下
    //  int[] images = {R.drawable.img55, R.drawable.img4, R.drawable.img5};
    //  int[] images = {R.drawable.img57, R.drawable.img57, R.drawable.img58};
    int[] images = {R.drawable.img57, R.drawable.img58};

    // Message传递标志
    int SIGN = 17;
    // 照片索引
    int num = 0;
    // 设备参数信息类
    private LdDevice ldDevice = new LdDevice();
    // 电参信息
    private TextView tv_voltage, tv_electricity, tv_power, tv_energy, tv_power_factor, tv_leak_curt, tv_alarm_status, tv_wind_speed;
    // 温度、湿度、光照度
    private TextView tv_temperature, tv_humidity, tv_illuminance;
    // 人脸识别
    private ImageView iv_face1, iv_face2, iv_face3, iv_face4;
    private GridView gridview;

    // 指纹视图
    private RelativeLayout fingerprintView;
    // 设备与相机视图
    private LinearLayout deviceAndCameraView;
    // 指纹扫描线
    private ImageView scanLine;
    // 指纹扫描的动画
    private TranslateAnimation animation;
    // 串口状态
    private boolean openSerialPort = false;

    // 串口
    private SerialPort mSerialPort;
    private CircularProgressView cpv_wendu, cpv_shidu;

    // 相机显示
    private SurfaceView scanPreview;
    private VideoView videoView;
    private SurfaceHolder mHolder;
    private CameraManager mCameraManager;
    private Camera mCamera;

    // 人脸识别图片
    private ImageView img1, img2, img3, img4, img5, img6, img7;
    private List<ImageView> imgList;

    // 更新电参的线程池
    private ScheduledExecutorService scheduledThreadPool;

    private UpdateAppManager updateAppManager;

    // 一键报警
    private AccountCreator mAccountCreator ;
    private Button bt_alarm;
    private CoreListenerStub mCoreListener;


 /*   private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return false;
        }
    });*/
    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case STOP_FINGERPRINTVIEW:
                    if (fingerprintView.getVisibility() == View.VISIBLE) {
                        fingerprintView.setVisibility(View.GONE);
                        scanLine.clearAnimation();
                    }
                    break;
                case START_FINGERPRINTVIEW:
                    if (fingerprintView.getVisibility() == View.GONE) {
                        fingerprintView.setVisibility(View.VISIBLE);
                        scanLine.startAnimation(animation);
                        // 隐藏其他界面
                        deviceAndCameraView.setVisibility(View.GONE);

                        // 一键报警功能修改如下
                        myHandler.postDelayed(new Runnable(){

                            public void run() {
                                // 关闭指纹扫描
                                myHandler.sendEmptyMessage(STOP_FINGERPRINTVIEW);
                                // 启动视频界面
                                myHandler.removeMessages(STOP_DEVICE_AND_CAMERA);
                                myHandler.sendEmptyMessage(START_DEVICE_AND_CAMERA);
                                //   myHandler.removeCallbacksAndMessages(null);
                                myHandler.sendEmptyMessageDelayed(STOP_DEVICE_AND_CAMERA, 60000);

                                // 报警
                                Alarm();
                            }
                        }, 2600);


                    }
                    break;
                case STOP_DEVICE_AND_CAMERA:
                    if (deviceAndCameraView.getVisibility() == View.VISIBLE) {
                        deviceAndCameraView.setVisibility(View.GONE);
                        // 摄像头关闭预览
                        //   videoView.stopPlayback();

                        // releaseCamera();
                        //        mCamera.stopPreview();
                    }
                    break;
                case START_DEVICE_AND_CAMERA:


                    if (deviceAndCameraView.getVisibility() == View.GONE) {
                        // 显示设备与相机界面
                        deviceAndCameraView.setVisibility(View.VISIBLE);
                        // 隐藏其他界面
                        fingerprintView.setVisibility(View.GONE);

                        // 重新开启摄像头
                        if (!videoView.isPlaying()){
                            startFaceTheCamera();
                        }



                        // 更新界面电参
                        tv_temperature.setText("温度：" + (ldDevice.getTemperature() / 10) + " ℃");
                        tv_humidity.setText("湿度：" + (ldDevice.getHumidity() / 10) + " %");
                        tv_illuminance.setText("光照度：" + (ldDevice.getIlluminance()) + " lux");
                        tv_voltage.setText("电压：" + (ldDevice.getVoltage() / 100) + " V");
                        tv_electricity.setText("电流：" + (ldDevice.getElectricity() / 100) + " A");
                        tv_power.setText("功率：" + (ldDevice.getPower() / 10) + " W");
                        tv_energy.setText("电能：" + (int) ldDevice.getElectricalEnergy() + " Kw.h");
                        tv_power_factor.setText("功率因数：" + (ldDevice.getPowerFactor() / 1000) + "");
                        tv_leak_curt.setText("漏电电流：" + (int) (ldDevice.getLeakCurrent()) + " mA");


                        StringBuffer sb = new StringBuffer();
                        if (MyByteUtil.bitget(ldDevice.getAlarmStatus(), 0) == 1) {
                            sb.append("[过流报警]");
                        }
                        if (MyByteUtil.bitget(ldDevice.getAlarmStatus(), 1) == 1) {
                            sb.append("[漏电报警]");
                        }
                        if (MyByteUtil.bitget(ldDevice.getAlarmStatus(), 2) == 1) {
                            sb.append("[漏电报警]");
                        }
                        if (MyByteUtil.bitget(ldDevice.getAlarmStatus(), 3) == 1) {
                            sb.append("[欠压报警]");
                        }
                        if (MyByteUtil.bitget(ldDevice.getAlarmStatus(), 4) == 1) {
                            sb.append("[欠流报警]");
                        }
                        if (sb.toString().equals("")) {
                            sb.append("正常");
                        }
                        tv_alarm_status.setText("报警状态：" + sb.toString());

                        //   mCamera.startPreview();// 开启预览
                    }
                    break;
                case UP_PARAMETER:

                    // 更新界面电参
                    if (deviceAndCameraView.getVisibility() == View.VISIBLE) {

                        tv_temperature.setText("温度：" + (ldDevice.getTemperature() / 10) + " ℃");
                        tv_humidity.setText("湿度：" + (ldDevice.getHumidity() / 10) + " %");
                        tv_illuminance.setText("光照度：" + nubTransition((ldDevice.getIlluminance() + MyRandom(30, 70)), 2) + " lux");
                        tv_voltage.setText("电压：" + (ldDevice.getVoltage() / 100) + " V");
                        tv_electricity.setText("电流：" + (ldDevice.getElectricity() / 100) + " A");
                        tv_power.setText("功率：" + (ldDevice.getPower() / 10) + " W");
                        tv_energy.setText("电能：" + (int) ldDevice.getElectricalEnergy() + " Kw.h");
                        tv_power_factor.setText("功率因数：" + (ldDevice.getPowerFactor() / 1000) + "");
                        tv_leak_curt.setText("漏电电流：" + (int) (ldDevice.getLeakCurrent()) + " mA");
                        tv_wind_speed.setText(" 风速：" + nubTransition(MyRandom(15, 23), 2) + " m/s");
                        cpv_shidu.setProgress((int) (ldDevice.getHumidity() / 10));
                        cpv_wendu.setProgress((int) (ldDevice.getTemperature() / 10));
                    }

                    break;
            }

        }
    };


    private double nubTransition(double nub, int bit) {
        return new BigDecimal(nub).setScale(bit, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 去掉窗口标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏顶部的状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);

      //  mAccountCreator = LinphoneService.getCore().createAccountCreator(null);

    /*   int height =  Resources.getSystem().getDisplayMetrics().heightPixels;
       int width =  Resources.getSystem().getDisplayMetrics().widthPixels;
        float density =  Resources.getSystem().getDisplayMetrics().density;
        LogUtil.e("height = " + height + "   width = " + width + "    density = " + density );*/


        //AudioManager实例对象调节当前音量
        //获取最大音量和当前音量，参数：STREAM_VOICE_CALL（通话）、STREAM_SYSTEM（系统声音）、STREAM_RING（铃声）、STREAM_MUSIC（音乐）和STREAM_ALARM（闹铃）
        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
        int current = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        AudioDeviceInfo[] aa = audioManager.getDevices(audioManager.GET_DEVICES_OUTPUTS);
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);

        if(current != 5){
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, 5, AudioManager.FLAG_SHOW_UI);
        }
        System.out.println(">>>>>>>>>>>>>>>>> max : current = " + max + " : " + current);


        // 初始化View
        initView();

        // 初始化广告
        initAdvertising();

        // 初始化摄像头
        //  initCamera();

        // 初始化串口
        initPort2();

        // 初始化人脸识别
        initFaceRecognition();

        // 开启 Ftp 服务器
        startFtpService();

        // 开启视频通话服务
        startLinphoneService();

        // 一键报警
        aKeyAlarm();

        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                      //  parseBytes(new byte[]{5,2,2,4,6,7,0,0,0,0,0});
                    }
                }).start();
           //     parseBytes(new byte[]{5,2,2,4,6,7,0,0,0,0,0});

            }
        }, 3000);



   /*     // 开启 Ftp 服务器
        startFtpService();
        // 一键报警
        aKeyAlarm();
        // 开启视频通话服务
        startLinphoneService();*/





        /*  String path = Environment.getExternalStorageDirectory()+ "/" + "app-debug.apk";
        openAPKFile(MainActivity.this, path);*/

   /*     File updateDir = new File(Environment.getExternalStorageDirectory(),
                "app-debug.apk");
        try {
            updateDir.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }

    private void aKeyAlarm() {
        // 一键报警测试
        bt_alarm = this.findViewById(R.id.bt_AKeyAlarm);
        bt_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Alarm();

            }
        });

        mCoreListener = new CoreListenerStub() {
            @Override
            public void onRegistrationStateChanged(Core core, ProxyConfig cfg, RegistrationState state, String message) {
                upLinphoneStart(state);
            }

            @Override
            public void onCallStateChanged(Core core, Call call, Call.State state, String message) {
                // Toast.makeText(LinphoneService.this, message + " "+ state, Toast.LENGTH_SHORT).show();

                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>> onCallStateChanged " + state);

                if (state == Call.State.IncomingReceived) {
                    // 接收电话，自动接听
                    CallParams params = LinphoneService.getCore().createCallParams(call);
                    params.enableVideo(false);
                    call.acceptWithParams(params);
                }else if (state == Call.State.OutgoingInit) {
                    // 拨出电话
                    bt_alarm.setText(  "与服务中心通连接中...");
                    bt_alarm.setTextColor(getResources().getColor(R.color.colorAccent));
                }else if (state == Call.State.OutgoingProgress ) {
                    // 电话拨出中
                    bt_alarm.setText(  "正在连接应急中心请等待...");
                }else if (state == Call.State.StreamsRunning ) {
                    // 接通中
                    bt_alarm.setText(  "与应急中心通话中...");
                    bt_alarm.setTextColor(getResources().getColor(R.color.colorAccent));
                }else if (state == Call.State.End ) {
                    // 通话结束
                    ProxyConfig proxyConfig = LinphoneService.getCore().getDefaultProxyConfig();
                    if (proxyConfig != null) {
                        upLinphoneStart(proxyConfig.getState());
                    }
                }

              /*  else if (state == Call.State.Connected) {
                    // 与服务中心连接中
                    bt_alarm.setText(state + "与服务中心通话中...");
                }else if (state == Call.State.End) {
                    //   bt_alarm.setText("服务中心连接状态：已连接");
                }else if (state == Call.State.Error) {
                    // bt_alarm.setText("服务中心连接状态：未连接");
                }else if (state == Call.State.OutgoingInit) {
                    bt_alarm.setText(state + "正在拨打客户中心号码，请稍后...");
                }*/
            }
        };


    }

    private  void Alarm(){
        ProxyConfig proxyConfig = LinphoneService.getCore().getDefaultProxyConfig();
        if (proxyConfig == null || proxyConfig.getState() == RegistrationState.None) {

            Toast.makeText(MainActivity.this, "客户中心连接失败，请联系管理员！", Toast.LENGTH_SHORT).show();

            return;
        }

        if (AlarmClickHelper.isFastDoubleClick()) {//连续点击
            return;
        }
        Core core = LinphoneService.getCore();
        if(core != null){
        //    Address addressToCall = core.interpretUrl("1012");
            Address addressToCall = core.interpretUrl("1000");
            CallParams params = core.createCallParams(null);

            params.enableVideo(false);

            MediaDirection md = params.getAudioDirection();

            if (addressToCall != null) {
                core.inviteAddressWithParams(addressToCall, params);
            }
        }else{
            Toast.makeText(MainActivity.this, "客户中心连接失败，请联系管理员！", Toast.LENGTH_SHORT).show();
        }
    }

    private void upLinphoneStart(final RegistrationState state) {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> upLinphoneStart " + state);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (state == RegistrationState.Ok) {
                    bt_alarm.setText("服务中心连接状态：已连接");
                } else if (state == RegistrationState.Failed) {
                    bt_alarm.setText("服务中心连接状态：未连接");
                }else if (state == RegistrationState.None) {
                    bt_alarm.setText("服务中心连接状态：连接失败");
                }else{
                    bt_alarm.setText("服务中心连接状态：未连接");
                }
                bt_alarm.setTextColor(getResources().getColor(R.color.colorProgressBg));
            }
        });

    }


    public String getAdConfig(File filePath) {

        StringBuilder text = new StringBuilder();
        BufferedReader br = null;
        try {
            if (!filePath.exists() ) {
                return null;
            }

            br = new BufferedReader(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line;

        try {
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return text.toString();

    }

    private void startFtpService() {
        String config = getString(R.string.users);
        FtpManager ftpManager = FtpManager.getInstance(config);
        ftpManager.startServer();
    }


    private void initView() {

        fingerprintView = (RelativeLayout) this.findViewById(R.id.view_fingerprint_capture);
        deviceAndCameraView = (LinearLayout) this.findViewById(R.id.view_device_camera);
        scanPreview = (SurfaceView) findViewById(R.id.capture_preview);
        tv_voltage = (TextView) this.findViewById(R.id.tv_voltage);
        tv_electricity = (TextView) this.findViewById(R.id.tv_electricity);
        tv_power = (TextView) this.findViewById(R.id.tv_power);
        tv_energy = (TextView) this.findViewById(R.id.tv_energy);
        tv_power_factor = (TextView) this.findViewById(R.id.tv_power_factor);
        tv_leak_curt = (TextView) this.findViewById(R.id.tv_leak_curt);
        tv_alarm_status = (TextView) this.findViewById(R.id.tv_alarm_status);
        videoView = (VideoView) this.findViewById(R.id.video_view);
        tv_temperature = (TextView) this.findViewById(R.id.tv_temperature);
        tv_humidity = (TextView) this.findViewById(R.id.tv_humidity);
        tv_illuminance = (TextView) this.findViewById(R.id.tv_illuminance);
        tv_wind_speed = (TextView) this.findViewById(R.id.tv_wind_speed);
        gridview = (GridView) this.findViewById(R.id.gridview);
        cpv_wendu = (CircularProgressView) this.findViewById(R.id.cpv_wendu);
        cpv_shidu = (CircularProgressView) this.findViewById(R.id.cpv_shidu);

        // 人脸识别图片
        img1 = (ImageView) this.findViewById(R.id.img1);
        img2 = (ImageView) this.findViewById(R.id.img2);
        img3 = (ImageView) this.findViewById(R.id.img3);
        img4 = (ImageView) this.findViewById(R.id.img4);
        img5 = (ImageView) this.findViewById(R.id.img5);
        img6 = (ImageView) this.findViewById(R.id.img6);
        img7 = (ImageView) this.findViewById(R.id.img7);
        imgList = new ArrayList<>();
        imgList.add(img1);
        imgList.add(img2);
        imgList.add(img3);
        imgList.add(img4);
        imgList.add(img5);
        imgList.add(img6);
        imgList.add(img7);


       /* provinceAdapter = new ProvinceAdapter(this);
        gridview.setAdapter(provinceAdapter);*/

        // 初始化动画
        scanLine = (ImageView) findViewById(R.id.capture_scan_line);
        animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.9f);
        animation.setDuration(3000);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);

        //  checkPermissionCamera();



    }

    private void checkAndRequestCallPermissions() {
        ArrayList<String> permissionsList = new ArrayList<>();

        // Some required permissions needs to be validated manually by the user
        // Here we ask for record audio and camera to be able to make video calls with sound
        // Once granted we don't have to ask them again, but if denied we can

        //检测是否有写的权限
        int permission = getPackageManager().checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getPackageName());

        // 音频权限
        int recordAudio = getPackageManager().checkPermission(Manifest.permission.RECORD_AUDIO, getPackageName());

        // 相机权限
        int camera = getPackageManager().checkPermission(Manifest.permission.CAMERA, getPackageName());


        if (permission != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (recordAudio != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.RECORD_AUDIO);
        }

        if (camera != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.CAMERA);
        }

        if (permissionsList.size() > 0) {
            String[] permissions = new String[permissionsList.size()];
            permissions = permissionsList.toArray(permissions);
            ActivityCompat.requestPermissions(this, permissions, 99);
        }
    }



    @Override
    protected void onStart() {
        super.onStart();


        // 验证权限
       // verifyStoragePermissions(this);
        checkAndRequestCallPermissions();




    }


    // This thread will periodically check if the Service is ready, and then call onServiceReady
    private class ServiceWaitThread extends Thread {
        public void run() {

            while (!LinphoneService.isReady()) {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException("waiting thread sleep() has been interrupted");
                }
            }

            // 添加监听
            LinphoneService.getCore().removeListener(mCoreListener);
            LinphoneService.getCore().addListener(mCoreListener);


            // 登录linphone帐号
            ProxyConfig proxyConfig = LinphoneService.getCore().getDefaultProxyConfig();
            if (proxyConfig == null) {

                // 设置端口号
                  /*  Transports transports = core.getTransports();
                    transports.setUdpPort(5060);
                    transports.setTcpPort(5060);
                    transports.setTlsPort(-1);
                    core.setTransports(transports);*/
                mAccountCreator = LinphoneService.getCore().createAccountCreator(null);
                mAccountCreator.setUsername("1001");
                mAccountCreator.setPassword("1867668");
                mAccountCreator.setDomain("120.26.216.74:16384");
                mAccountCreator.setTransport(TransportType.Udp);
                // This will automatically create the proxy config and auth info and add them to the Core
                ProxyConfig cfg = mAccountCreator.createProxyConfig();
                // Make sure the newly created one is the default
                LinphoneService.getCore().setDefaultProxyConfig(cfg);
                // 添加监听
            }else {
                upLinphoneStart(proxyConfig.getState());
            }


        }
    }

    private List<String> imgs;
    private FaceRecoUtil faceRecoUtil = new FaceRecoUtil();
    //  private ProvinceAdapter provinceAdapter;
    // 需要使用图片个数
    private int numberUsed = 15;
    // list截取的位置
    private int toIndex;

    private void initFaceRecognition() {

        //定期检查刷新数据... 	 开启一个线程，检查有效期...(过期自动删除缓存)
        scheduledThreadPool = Executors.newScheduledThreadPool(2);
        scheduledThreadPool.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {

             //   LogUtil.e("initFaceRecognition 线程运行");

                // 判断人脸识别界面是否在显示状态，不在显示状态不处理
                if (deviceAndCameraView.getVisibility() == View.GONE) {
               //     LogUtil.e("initFaceRecognition 当前界面不显示");
                    return;
                }

                // 显示人脸头像
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        showFaceImage();
                    }
                }).start();



            }
        }, 0, 1, TimeUnit.SECONDS);
        //参数第一次执行时间，间隔执行时间,执行时间单位

    }

    private synchronized void showFaceImage() {
        toIndex = 0;
        // 清空集合缓存
        if (imgs != null && imgs.size() > 0) {
            imgs.clear();
        }

        // 获取指定目录中所有图片
        imgs = faceRecoUtil.getFilesAllName(ficeFile);
        LogUtil.e("initFaceRecognition Size xxx = " + imgs.size());
        for (String img : imgs) {
            LogUtil.e("initFaceRecognition xxx = " + img);
        }

        if (imgs == null && imgs.size() == 0) {
          //   System.out.println("provinceAdapter.notifyDataSetChanged()");
            //   provinceAdapter.notifyDataSetChanged();
            return;

        } else {
            // 如果当前图片大于需要显示的图片个数，显示图片数为需要显示图片，如果当前图片小于需要显示图片数，显示图片数为当前文件夹图片数
            if (imgs.size() < numberUsed) {
                toIndex = imgs.size();
            } else {
                toIndex = numberUsed;
                // 删除多余的图片
                for (int i = numberUsed; i < imgs.size(); i++) {
                    File file = new File(imgs.get(i));
                    MainActivity.this.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "=?", new String[]{imgs.get(i)});//删除系统缩略图
                    file.delete();//删除SD中图片
                }
            }

        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // provinceAdapter.setImgs(imgs.subList(0, toIndex));
                int size;
                if (imgs.size() > 7) {
                    size = 7;
                } else {
                    size = imgs.size();
                }
                for (int i = 0; i < size; i++) {
                    imgList.get(i).setImageURI(Uri.fromFile(new File(imgs.get(i))));
                }

            }
        });
    }

    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


        // 启动人脸镜头
        startFaceTheCamera();
        // 初始化摄像头
       //   initCamera();

        // 检测更新
        CheckForUpdates();

        // 删除人脸识别文件夹下的所有文件
        deleteDirWihtFile(new File(ficeFile));

        // 更新一键报警连接状态
        if(LinphoneService.isReady()){

            // 添加监听
               LinphoneService.getCore().removeListener(mCoreListener);
               LinphoneService.getCore().addListener(mCoreListener);

            ProxyConfig proxyConfig = LinphoneService.getCore().getDefaultProxyConfig();
            if (proxyConfig != null) {
                upLinphoneStart(proxyConfig.getState());
            }
        }else{
            bt_alarm.setText("服务中心连接状态：未连接");
        }


    }

    // String uri = "rtsp://192.168.1.75:554/user=admin_password=tlJwpbo6_channel=1_stream=0.sdp?real_stream";
    String uri;
    private void startFaceTheCamera() {

        if(uri == null || uri.isEmpty()){
            // 从配置文件中获取摄像头地址
            String adConfig = getAdConfig(new File(adConfigFile, "ad_config.properties"));
            if (adConfig != null && adConfig.length() > 0) {
                try {
                    JSONObject deviceObj = new JSONObject(adConfig);
                    String rtspUrl = (String) deviceObj.opt("face_camera");
                    if (rtspUrl != null) {
                        uri = rtspUrl;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        // 摄像头开启预览
        if(uri != null){
            videoView.setVideoURI(Uri.parse(uri));
            videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    //  LogUtil.e("xxxxxxxxxxxxx setOnErrorListener");
                    return true;
                }
            });
            videoView.start();
            videoView.requestFocus();
        }



    }

    /**
     * 删除文件夹和文件夹里面的文件
     *
     * @param dir 删除的文件夹
     */
    public static void deleteDirWihtFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWihtFile(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }


    /**
     * 检测是否需要更新
     */
    private void CheckForUpdates() {
        // 定时检测更新app
        updateAppManager = new UpdateAppManager(this);
        //定期检查刷新数据... 	 开启一个线程，检查有效期...(过期自动删除缓存)
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);
        //定时周期任务(间隔时间重复执行)
        final int i = 0;
        scheduledThreadPool.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                LogUtil.e("定时更新执行");
                // 定时检测更新
                updateAppManager.checkUpdateInfo();

            }
        }, 0, 20, TimeUnit.MINUTES);
        //参数第一次执行时间，间隔执行时间,执行时间单位
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 移除一键报警监听
        LinphoneService.getCore().removeListener(mCoreListener);
        this.stopService( new Intent(this,LinphoneService.class));

        openSerialPort = false;
        // 关闭跟新程序
        if (updateAppManager != null) {
            updateAppManager.setCancel(true);
        }
        // 关闭更新电参界面的线程池
        if (scheduledThreadPool != null) {
            scheduledThreadPool.shutdown();
        }
        // 关闭ftp
        FtpManager.getInstance(getString(R.string.users)).stopFtpServer();
    }

    private void initPortListening() {

        if (mSerialPort == null) {
            Toast.makeText(this, "串口为空", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    /**
                     * @param 1 串口路径
                     * @param 2 波特率
                     * @param 3 flags 给0就好
                     */
                    //从串口对象中获取输入流
                    InputStream inputStream = mSerialPort.getInputStream();
                    //使用循环读取数据，建议放到子线程去
                    openSerialPort = true;
                    while (openSerialPort) {
                        if (inputStream.available() > 0) {
                            //当接收到数据时，sleep 500毫秒（sleep时间自己把握）
                            Thread.sleep(100);
                            //sleep过后，再读取数据，基本上都是完整的数据
                            byte[] buffer = new byte[inputStream.available()];
                            int size = inputStream.read(buffer);
                            LogUtil.e(" buffer = " + Arrays.toString(buffer));

                            if (buffer == null || buffer.length == 0) {
                                continue;
                            }
//                            LogUtil.e(" buffer = " + new String(buffer, "utf-8"));

                          /*  if (buffer[0] == 1) {
                                myHandler.sendEmptyMessage(START_FINGERPRINTVIEW);
                                //   myHandler.removeCallbacksAndMessages(null);
                                myHandler.sendEmptyMessageDelayed(STOP_FINGERPRINTVIEW, 2000);
                            } else if (buffer[0] == 2) {
                                myHandler.sendEmptyMessage(START_DEVICE_AND_CAMERA);
                                myHandler.sendEmptyMessageDelayed(STOP_DEVICE_AND_CAMERA, 5000);
                            }*/

                            parseBytes(buffer);

                            // write();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void parseBytes(final byte[] buffer) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (buffer.length <= 2 || buffer.length <= 8) {
                    return;
                }

                // 校验CRC
              /*  boolean checkCrc = CheckCRC.checkTheCrc(
                        Arrays.copyOfRange(buffer, 5, 47),
                        Arrays.copyOfRange(buffer, 47, 49));
                if(!checkCrc){
                    return;
                }*/
                //    Toast.makeText(MainActivity.this,Arrays.toString(buffer),Toast.LENGTH_SHORT).show();


                // 解析指令(判断功能吗)
                if (buffer[2] == 5) {

                    LogUtil.e(" 获取电参 = " + Arrays.toString(buffer));


                    //  获取电参
                    ldDevice.setVoltage((MyByteUtil.bytesIntHL(new byte[]{buffer[9], buffer[10]})));
                    ldDevice.setElectricity(MyByteUtil.bytesIntHL(new byte[]{buffer[11], buffer[12]}));
                    ldDevice.setPower(MyByteUtil.bytesIntHL(new byte[]{buffer[13], buffer[14]}));
                    ldDevice.setElectricalEnergy(MyByteUtil.bytesIntHL(new byte[]{buffer[15], buffer[16], buffer[17]}));
                    ldDevice.setPowerFactor(MyByteUtil.bytesIntHL(new byte[]{buffer[18], buffer[19]}));
                    ldDevice.setLeakCurrent(MyByteUtil.bytesIntHL(new byte[]{buffer[20], buffer[21]}));
                    ldDevice.setAlarmStatus(buffer[22]);


                    LogUtil.e("ldDevice = " + ldDevice.toString());

                    //   myHandler.sendEmptyMessage(START_DEVICE_AND_CAMERA);

                } else if (buffer[2] == 6) {
                    LogUtil.e(" 获取温湿度 = " + Arrays.toString(buffer));
                    ldDevice.setTemperature(bytesIntHL(new byte[]{buffer[9], buffer[10]}));
                    ldDevice.setHumidity(bytesIntHL(new byte[]{buffer[11], buffer[12]}));
                    ldDevice.setIlluminance(bytesIntHL(new byte[]{buffer[13], buffer[14], buffer[15]}));

                    myHandler.removeMessages(UP_PARAMETER);
                    myHandler.sendEmptyMessage(UP_PARAMETER);

                } else if (buffer[2] == 1) {
                    LogUtil.e(" 红外启动 = " + Arrays.toString(buffer));
                    myHandler.removeMessages(STOP_DEVICE_AND_CAMERA);
                    myHandler.sendEmptyMessage(START_DEVICE_AND_CAMERA);
                    //   myHandler.removeCallbacksAndMessages(null);
                    myHandler.sendEmptyMessageDelayed(STOP_DEVICE_AND_CAMERA, 20000);

                } else if (buffer[2] == 2) {
                    LogUtil.e(" 指纹采集 = " + Arrays.toString(buffer));
                    myHandler.removeMessages(STOP_FINGERPRINTVIEW);
                    myHandler.sendEmptyMessage(START_FINGERPRINTVIEW);
                    //   myHandler.removeCallbacksAndMessages(null);



                   /* LogUtil.e(" 指纹采集 = " + Arrays.toString(buffer));
                    myHandler.removeMessages(STOP_FINGERPRINTVIEW);
                    myHandler.sendEmptyMessage(START_FINGERPRINTVIEW);
                    //   myHandler.removeCallbacksAndMessages(null);
                    myHandler.sendEmptyMessageDelayed(STOP_FINGERPRINTVIEW, 2800);*/
                }
            }
        }).start();
    }


    private void initPort2() {

        try {
            // 打开/dev/ttyUSB0路径设备的串口
            mSerialPort = new SerialPort(new File("/dev/ttyS3"), 115200, 0);

            // 获取所有串口
            SerialPortFinder serialPortFinder = new SerialPortFinder();
            String[] devices = serialPortFinder.getAllDevicesPath();
            LogUtil.e("String[] = " + "长度：" + devices.length + " " + Arrays.toString(devices));

        } catch (Exception e) {

        }

        // 监听串口
        initPortListening();
    }

    private void initPort() {

        YF_A64_API_Manager yfapi = new YF_A64_API_Manager(this);
        String nettyp0 = yfapi.yfgetUartPath("uart0");
        String nettyp1 = yfapi.yfgetUartPath("uart1");
        String nettyp2 = yfapi.yfgetUartPath("uart2");
        String nettyp3 = yfapi.yfgetUartPath("uart3");

        LogUtil.e("nettyp0 = " + nettyp0 + "    nettyp1 = " + nettyp1 + "   nettyp2 = " + nettyp2 + "    nettyp3 = " + nettyp3);

        //https://github.com/AIlll/AndroidSerialPort

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    /**
                     * @param 1 串口路径
                     * @param 2 波特率
                     * @param 3 flags 给0就好
                     */
                    SerialPort serialPort = new SerialPort(new File("/dev/ttyS3"), 115200, 0);
                    //从串口对象中获取输入流
                    InputStream inputStream = serialPort.getInputStream();

                    //使用循环读取数据，建议放到子线程去
                    while (true) {
                        if (inputStream.available() > 0) {
                            //当接收到数据时，sleep 500毫秒（sleep时间自己把握）
                            Thread.sleep(500);
                            //sleep过后，再读取数据，基本上都是完整的数据
                            byte[] buffer = new byte[inputStream.available()];
                            int size = inputStream.read(buffer);
                            LogUtil.e(" buffer = " + Arrays.toString(buffer));
                            LogUtil.e(" buffer = " + new String(buffer));

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void write() {

        try {
            SerialPort serialPort = new SerialPort(new File("/dev/ttyS3"), 115200, 0);
            //从串口对象中获取输出流
            OutputStream outputStream = serialPort.getOutputStream();
            //需要写入的数据
            byte[] data = new byte[]{-18, 4, 5, 0, 0, 0, 0, 0, 14, 86, -105, 0, 17, 1, 123, 0, 0, 0, 3, -55, 0, 0, 0, -18, 46, -17};
            //写入数据
            // 写入数据
            try {
                outputStream.write(data);
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void initAdvertising() {

        // 定时更新广告
        TimedRefreshAD();


    }

    // 定时刷新广告
    private void TimedRefreshAD() {
        final ImageView image = (ImageView) findViewById(R.id.image);
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                if (msg.what == SIGN) {
                    image.setImageResource(images[num++]);
                    //  image.setBackgroundResource(images[num++]);
                    if (num >= images.length) {
                        num = 0;
                    }
                }
            }
        };
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Message msg = new Message();
                msg.what = SIGN;
                handler.sendMessage(msg);
            }
        }, 50, 15000);
    }

    //region 初始化和回收相关资源
    private void initCamera() {


        // 初始化相机
        mHolder = scanPreview.getHolder();
        mCameraManager = new CameraManager(getApplication());
        mHolder.addCallback(this);
        //    mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        //mCamera = Camera.open();
      //  mCamera = Camera.open(0);
   /*     Camera.Parameters p = mCamera.getParameters();
        //  p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        p.setPictureSize(352, 288);
        mCamera.setParameters(p);*/

        try {
            mCamera = Camera.open(0);
            mCamera.setPreviewDisplay(mHolder);

            Thread.sleep(2000);
            if (mCamera != null) {
                // 开始预览
                mCamera.startPreview();
                Log.d(TAG, "startPreview() called");
            }


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }




    private void releaseCamera() {

        //关闭相机
        if (mCameraManager != null) {
            mCameraManager.closeDriver();
            mCameraManager = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 99) {

            // 开启 Ftp 服务器
          //  startFtpService();

            // 开启视频通话服务
            startLinphoneService();



            for (int i = 0; i < permissions.length; i++) {
                Log.e("MainActivity", "申请的权限为：" + permissions[i] + ",申请结果：" + grantResults[i]);
            }
        }
    }

    private void startLinphoneService() {
        // 开启视频通话服务（一键报警功能）
        if (!LinphoneService.isReady()) {
            // If it's not, let's start it
            startService(new Intent().setClass(this, LinphoneService.class));
            // And wait for it to be ready, so we can safely use it afterwards
            new ServiceWaitThread().start();

        }

    }

    //region 检查权限
    private boolean isOpenCamera;

    private void checkPermissionCamera() {
        int checkPermission = 0;
        if (Build.VERSION.SDK_INT >= 23) {
            // checkPermission =ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA);
            checkPermission = PermissionChecker.checkSelfPermission(this, Manifest.permission.CAMERA);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            } else {
                isOpenCamera = true;
            }

        } else {
            checkPermission = checkPermission(26);
            if (checkPermission == AppOpsManager.MODE_ALLOWED) {
                isOpenCamera = true;
            } else if (checkPermission == AppOpsManager.MODE_IGNORED) {
                isOpenCamera = false;
            }
        }
    }


    /**
     * 反射调用系统权限,判断权限是否打开
     *
     * @param permissionCode 相应的权限所对应的code
     * @see {@link AppOpsManager }
     */
    private int checkPermission(int permissionCode) {
        int checkPermission = 0;
        if (Build.VERSION.SDK_INT >= 19) {
            AppOpsManager _manager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            try {
                Class<?>[] types = new Class[]{int.class, int.class, String.class};
                Object[] args = new Object[]{permissionCode, Binder.getCallingUid(), getPackageName()};
                Method method = _manager.getClass().getDeclaredMethod("noteOp", types);
                method.setAccessible(true);
                Object _o = method.invoke(_manager, args);
                if ((_o instanceof Integer)) {
                    checkPermission = (Integer) _o;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            checkPermission = 0;
        }
        return checkPermission;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        try {
          //  mCameraManager.openDriver(holder);

            //1 为前置摄像头
            //0 为后置摄像头
            mCamera = Camera.open(0);
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview(); // 预览

        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //SurfaceView的尺寸发生改变
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //SurfaceView开始销毁
    }

    private double MyRandom(double max, double min) {
        return (min + (Math.random() * (max - min + 1))) * 0.1;
    }


}
