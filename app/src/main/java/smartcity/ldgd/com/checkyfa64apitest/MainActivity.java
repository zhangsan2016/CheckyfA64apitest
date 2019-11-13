package smartcity.ldgd.com.checkyfa64apitest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.aill.androidserialport.SerialPort;
import com.aill.androidserialport.SerialPortFinder;
import com.example.yf_a64_api.YF_A64_API_Manager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import smartcity.ldgd.com.checkyfa64apitest.util.LogUtil;

public class MainActivity extends AppCompatActivity {

    // 要切换的照片，放在drawable文件夹下
    int[] images = {R.drawable.img1, R.drawable.img2, R.drawable.img3,};
    // Message传递标志
    int SIGN = 17;
    // 照片索引
    int num = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去掉窗口标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏顶部的状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        // 初始化广告
        initAdvertising();

        // 初始化串口
       // initPort();

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

    private void initPort() {
    /*  try {
          // 打开/dev/ttyUSB0路径设备的串口
          SerialPort mSerialPort = new SerialPort(new File("/dev/ttyUSB0"), 9600, 0);

      } catch (IOException e) {
          System.out.println("找不到该设备文件");
      }*/

        SerialPortFinder serialPortFinder = new SerialPortFinder();
        String[] devices = serialPortFinder.getAllDevicesPath();
        LogUtil.e("String[] = " + "长度：" + devices.length + " " + Arrays.toString(devices));


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
                /**
                 * @param 1 串口路径
                 * @param 2 波特率
                 * @param 3 flags 给0就好
                 */
                try {
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
            byte[] data = new byte[10];
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
}
