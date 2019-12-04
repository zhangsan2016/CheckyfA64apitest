package smartcity.ldgd.com.checkyfa64apitest;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by ldgd on 2019/11/19.
 * 功能：
 * 说明：
 */

public class aa {
    private int i;

    public static void main(String[] args) {

        //定期检查刷新数据... 	 开启一个线程，检查有效期...(过期自动删除缓存)
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);
        //定时周期任务(间隔时间重复执行)
        final int i = 0;
        scheduledThreadPool.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                System.out.println("定时更新执行");

            }
        }, 0, 1, TimeUnit.SECONDS);
        //参数第一次执行时间，间隔执行时间,执行时间单位


        //定期检查刷新数据... 	 开启一个线程，检查有效期...(过期自动删除缓存)
        ScheduledExecutorService scheduledThreadPoo2 = Executors.newScheduledThreadPool(5);
        //定时周期任务(间隔时间重复执行)
        scheduledThreadPoo2.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                System.out.println("定时更新执行222222222");

            }
        }, 0, 1, TimeUnit.SECONDS);
        //参数第一次执行时间，间隔执行时间,执行时间单位

    }



}
