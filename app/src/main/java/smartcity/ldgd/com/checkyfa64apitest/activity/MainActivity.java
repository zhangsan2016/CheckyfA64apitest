package smartcity.ldgd.com.checkyfa64apitest.activity;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.aill.androidserialport.SerialPort;
import com.aill.androidserialport.SerialPortFinder;
import com.example.yf_a64_api.YF_A64_API_Manager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import smartcity.ldgd.com.checkyfa64apitest.R;
import smartcity.ldgd.com.checkyfa64apitest.camera.CameraManager;
import smartcity.ldgd.com.checkyfa64apitest.entity.LdDevice;
import smartcity.ldgd.com.checkyfa64apitest.util.LogUtil;
import smartcity.ldgd.com.checkyfa64apitest.util.MyByteUtil;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 26;
    private static final int STOP_FINGERPRINTVIEW = 11;
    private static final int START_FINGERPRINTVIEW = 12;
    private static final int STOP_DEVICE_AND_CAMERA = 13;
    private static final int START_DEVICE_AND_CAMERA = 14;
    private static final String TAG = "MainActivity";

    // 要切换的照片，放在drawable文件夹下
    int[] images = {R.drawable.img55, R.drawable.img4, R.drawable.img5};
    // int[] images = {R.drawable.img1,R.drawable.img2,R.drawable.img3,R.drawable.img4, R.drawable.img5};

    // Message传递标志
    int SIGN = 17;
    // 照片索引
    int num = 0;
    // 设备参数信息类
    private LdDevice ldDevice = new LdDevice();
    // 电参信息
    private TextView tv_voltage, tv_electricity, tv_power, tv_energy, tv_power_factor, tv_leak_curt, tv_alarm_status;

    // 指纹视图
    private RelativeLayout fingerprintView;
    // 设备与相机视图
    private LinearLayout deviceAndCameraView;
    // 指纹扫描线
    private ImageView scanLine;
    // 指纹扫描的动画
    private TranslateAnimation animation;

    // 串口
    private SerialPort mSerialPort;

    // 相机显示
    private SurfaceView scanPreview;
    private VideoView videoView;
    private SurfaceHolder mHolder;
    private CameraManager mCameraManager;
    private Camera mCamera;

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

                    if (fingerprintView.getVisibility() == View.VISIBLE) {
                        return;
                    }


                    if (deviceAndCameraView.getVisibility() == View.GONE) {
                        // 显示设备与相机界面
                        deviceAndCameraView.setVisibility(View.VISIBLE);
                        // 隐藏其他界面
                        fingerprintView.setVisibility(View.GONE);


                        // 更新界面电参
                        tv_voltage.setText((ldDevice.getVoltage() / 100) + " V");
                        tv_electricity.setText((ldDevice.getElectricity() / 100) + " A");
                        tv_power.setText((ldDevice.getPower() / 10) + " W");
                        tv_energy.setText((int) ldDevice.getElectricalEnergy() + " Kw.h");
                        tv_power_factor.setText((ldDevice.getPowerFactor() / 1000) + "");
                        tv_leak_curt.setText((int) (ldDevice.getLeakCurrent()) + " mA");

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
                        tv_alarm_status.setText(sb.toString());
                        // tv_alarm_status.setTextColor();

                        //   mCamera.startPreview();// 开启预览

                    }
                    break;
            }

        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去掉窗口标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏顶部的状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

    /*   int height =  Resources.getSystem().getDisplayMetrics().heightPixels;
       int width =  Resources.getSystem().getDisplayMetrics().widthPixels;
        float density =  Resources.getSystem().getDisplayMetrics().density;
        LogUtil.e("height = " + height + "   width = " + width + "    density = " + density );*/


        // 初始化View
        initView();

        // 初始化广告
        initAdvertising();

        // 初始化摄像头
        //  initCamera();

        // 初始化串口
        initPort2();

        // 监听串口
        initPortListening();


      /*  RelativeLayout   scanCropView = (RelativeLayout) findViewById(R.id.capture_crop_view);
        ImageView  scanLine = (ImageView) findViewById(R.id.capture_scan_line);
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation
                .RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                0.9f);
        animation.setDuration(3000);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);
        scanLine.startAnimation(animation);*/


       /* if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        } else {
            //扫码
        }*/

      /*  List<Camera.Size> previewSizes = mCamera.getParameters().getSupportedPreviewSizes();
        for (int i = 0; i < previewSizes.size(); i++) {
            Camera.Size psize = previewSizes.get(i);
            Log.e(TAG + "initCamera", "PreviewSize,width: " + psize.width + " height: " + psize.height);
        }*/

/*     Intent intent = new Intent(this, rtspActivity.class);
        startActivity(intent);*/

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

        // 初始化动画
        scanLine = (ImageView) findViewById(R.id.capture_scan_line);
        animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.9f);
        animation.setDuration(3000);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);

        checkPermissionCamera();

    }

    @Override
    protected void onResume() {
        super.onResume();
        // 摄像头开启预览
      //  videoView.setVideoURI(Uri.parse("rtsp://192.168.1.72:554/user=admin_password=tlJwpbo6_channel=1_stream=0.sdp?real_stream"));
        videoView.setVideoURI(Uri.parse("rtsp://192.168.1.75:554/user=admin_password=tlJwpbo6_channel=1_stream=0.sdp?real_stream"));


        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return true;
            }
        });
        videoView.start();
        videoView.requestFocus();
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
                    while (true) {
                        if (inputStream.available() > 0) {
                            //当接收到数据时，sleep 500毫秒（sleep时间自己把握）
                            Thread.sleep(100);
                            //sleep过后，再读取数据，基本上都是完整的数据
                            byte[] buffer = new byte[inputStream.available()];
                            int size = inputStream.read(buffer);
                            LogUtil.e(" buffer = " + Arrays.toString(buffer));
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
                if (buffer.length <= 2) {
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
                    myHandler.sendEmptyMessageDelayed(STOP_FINGERPRINTVIEW, 2800);
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
        }, 50, 5000);
    }

    //region 初始化和回收相关资源
    private void initCamera() {

        // 初始化相机
        mHolder = scanPreview.getHolder();
        mCameraManager = new CameraManager(getApplication());
        mHolder.addCallback(this);
        //    mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mCamera = Camera.open();
   /*     Camera.Parameters p = mCamera.getParameters();
        //  p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        p.setPictureSize(352, 288);
        mCamera.setParameters(p);*/

        try {
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
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    finish();

                } else {
                    finish();
                }
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
            mCameraManager.openDriver(holder);
        } catch (IOException e) {
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
}
