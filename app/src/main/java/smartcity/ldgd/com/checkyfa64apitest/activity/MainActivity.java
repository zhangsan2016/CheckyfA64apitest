package smartcity.ldgd.com.checkyfa64apitest.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.Toast;

import com.aill.androidserialport.SerialPort;
import com.example.yf_a64_api.YF_A64_API_Manager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import smartcity.ldgd.com.checkyfa64apitest.R;
import smartcity.ldgd.com.checkyfa64apitest.camera.CameraManager;
import smartcity.ldgd.com.checkyfa64apitest.util.LogUtil;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback{

    private static final int STOP_FINGERPRINTVIEW = 11;
    private static final int START_FINGERPRINTVIEW = 12;
    private static final int STOP_DEVICE_AND_CAMERA = 13;
    private static final int START_DEVICE_AND_CAMERA = 14;
    private static final String TAG = "MainActivity";

    // 要切换的照片，放在drawable文件夹下
    int[] images = {R.drawable.img1, R.drawable.img2, R.drawable.img3,};
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
    private SurfaceView scanPreview;
    private SurfaceHolder mHolder;
    private CameraManager mCameraManager;

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
                        releaseCamera();

                    }

                    break;
                case START_DEVICE_AND_CAMERA:
                    if (deviceAndCameraView.getVisibility() == View.GONE) {
                        deviceAndCameraView.setVisibility(View.VISIBLE);
                        mHolder = scanPreview.getHolder();
                        initCamera(mHolder);
                        mHolder.addCallback(MainActivity.this);
                        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
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

        // 初始化View
        initView();

        // 初始化广告
        initAdvertising();

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

        // 初始化相机
        mHolder = scanPreview.getHolder();
        mCameraManager = new CameraManager(getApplication());

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

       /*      // 获取所有串口
            SerialPortFinder serialPortFinder = new SerialPortFinder();
            String[] devices = serialPortFinder.getAllDevicesPath();
            LogUtil.e("String[] = " + "长度：" + devices.length + " " + Arrays.toString(devices));*/

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
                    //    image.setImageResource(images[num++]);
                    image.setBackgroundResource(images[num++]);
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
        }, 0, 3000);
    }

    //region 初始化和回收相关资源
    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            mCameraManager.openDriver(surfaceHolder);
        } catch (IOException ioe) {
            Log.e(TAG, "相机被占用", ioe);
        } catch (RuntimeException e) {
            e.printStackTrace();
            Log.e(TAG, "Unexpected error initializing camera");
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
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
