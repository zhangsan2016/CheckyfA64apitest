package smartcity.ldgd.com.checkyfa64apitest;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;
import smartcity.ldgd.com.checkyfa64apitest.util.HttpUtil;
import smartcity.ldgd.com.checkyfa64apitest.util.MyByteUtil;

/**
 * Created by ldgd on 2019/11/19.
 * 功能：
 * 说明：
 */

public class aa {
    private  int i;
    public static void main(String[] args) {

        System.out.println(MyByteUtil.bytesIntHL(new byte[]{5, -8}));

     /*   int lengIndex = 7;


        byte[] buff = {-18, 4, 5, 0, 0, 0, 0, 0, 14, 48, 24, 48, 24, 4, -49, 0, 4, -46, 0, 123, 0, 123, 31, 127, -115, -17};
      *//*  CopyOfcheckCRC.checkTheCrc(
                Arrays.copyOfRange(data, 5, 47),
                Arrays.copyOfRange(data, 47, 49));*//*

        // [48, 24, 48, 24, 4, -49, 0, 4, -46, 0, 123, 0, 123, 31]
        System.out.println(Arrays.toString(copyOfRange(buff, lengIndex, lengIndex + 2)));
        lengIndex = lengIndex + 2;

        byte[] dataCrc = copyOfRange(buff, 9, 23);

        int dataSize = MyByteUtil.bytesIntHL(copyOfRange(buff, 7, 9));
        if (dataSize > 0) {
            byte[] data = Arrays.copyOfRange(buff, 9, 9 + dataSize);
            byte[] crc = Arrays.copyOfRange(buff, 9 + dataSize, 9 + dataSize + 2);
            System.out.println("data = " + Arrays.toString(data));

            System.out.println("crc = " + Arrays.toString(Arrays.copyOfRange(buff, 9 + dataSize, 9 + dataSize + 2)));

            CheckCRC.checkTheCrc(data,crc);
            System.out.println("xxxx = " +  CheckCRC.checkTheCrc(crc,data));

        }*/


        HttpUtil.sendHttpRequest("http://134.175.135.19:8080/APP/getUpdate", new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("xxxx"  + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("response = " +  response.body().string());

            }
        });




 /*       //定期检查刷新数据... 	 开启一个线程，检查有效期...(过期自动删除缓存)
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);
        //定时周期任务(间隔时间重复执行)
        final int i = 0;
        scheduledThreadPool.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                //执行代码
                i++;
                System.out.println("定时执行 " + i);
            }
        }, 0, 1, TimeUnit.MINUTES);
        //参数第一次执行时间，间隔执行时间,执行时间单位*/
    }
}
