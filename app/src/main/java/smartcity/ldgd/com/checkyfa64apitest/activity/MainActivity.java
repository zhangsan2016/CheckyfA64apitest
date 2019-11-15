package smartcity.ldgd.com.checkyfa64apitest.activity;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Toast;

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
import smartcity.ldgd.com.checkyfa64apitest.util.LogUtil;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 26;
    private static final int STOP_FINGERPRINTVIEW = 11;
    private static final int START_FINGERPRINTVIEW = 12;
    private static final int STOP_DEVICE_AND_CAMERA = 13;
    private static final int START_DEVICE_AND_CAMERA = 14;
    private static final String TAG = "MainActivity";

    // 要切换的照片，放在drawable文件夹下
    int[] images = {R.drawable.img4, R.drawable.img5};
    // int[] images = {R.drawable.img1,R.drawable.img2,R.drawable.img3,R.drawable.img4, R.drawable.img5};

    // Message传递标志
    int SIGN = 17;
    // 照片索引
    int num = 0;

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
                    }
                    break;
                case STOP_DEVICE_AND_CAMERA:
                    if (deviceAndCameraView.getVisibility() == View.VISIBLE) {
                        deviceAndCameraView.setVisibility(View.GONE);
                        // releaseCamera();
                        mCamera.stopPreview();

                    }

                    break;
                case START_DEVICE_AND_CAMERA:
                    if (deviceAndCameraView.getVisibility() == View.GONE) {
                        deviceAndCameraView.setVisibility(View.VISIBLE);
                        mCamera.startPreview();// 开启预览

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
        initCamera();

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


        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        } else {
            //扫码
        }

        Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
        startActivityForResult(intent, 0);

    }

    private void initView() {

        fingerprintView = (RelativeLayout) this.findViewById(R.id.view_fingerprint_capture);
        deviceAndCameraView = (LinearLayout) this.findViewById(R.id.view_device_camera);
        scanPreview = (SurfaceView) findViewById(R.id.capture_preview);


        // 初始化动画
        scanLine = (ImageView) findViewById(R.id.capture_scan_line);
        animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.9f);
        animation.setDuration(3000);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);

        checkPermissionCamera();


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
                            Thread.sleep(500);
                            //sleep过后，再读取数据，基本上都是完整的数据
                            byte[] buffer = new byte[inputStream.available()];
                            int size = inputStream.read(buffer);
                            LogUtil.e(" buffer = " + Arrays.toString(buffer));
                            LogUtil.e(" buffer = " + new String(buffer, "utf-8"));
                            if (buffer[0] == 1) {
                                myHandler.sendEmptyMessage(START_FINGERPRINTVIEW);
                                //   myHandler.removeCallbacksAndMessages(null);
                                myHandler.sendEmptyMessageDelayed(STOP_FINGERPRINTVIEW, 2000);
                            } else if (buffer[0] == 2) {
                                myHandler.sendEmptyMessage(START_DEVICE_AND_CAMERA);
                                myHandler.sendEmptyMessageDelayed(STOP_DEVICE_AND_CAMERA, 5000);
                            }


                            write();
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
        }, 0, 5000);
    }

    //region 初始化和回收相关资源
    private void initCamera() {

        // 初始化相机
        mHolder = scanPreview.getHolder();
        mCameraManager = new CameraManager(getApplication());
        mHolder.addCallback(this);
        //    mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mCamera = Camera.open(0);
     /*   Camera.Parameters p = mCamera.getParameters();
        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        p.setPictureSize(358, 1920);
        mCamera.setParameters(p);*/

        try {
            mCamera.setPreviewDisplay(mHolder);
            // 开始预览
            mCamera.startPreview();
        } catch (IOException e) {
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
            if (mCamera != null) {
                mCamera.setPreviewDisplay(holder);

            }
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
