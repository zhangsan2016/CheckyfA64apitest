package smartcity.ldgd.com.checkyfa64apitest.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import smartcity.ldgd.com.checkyfa64apitest.MainActivity;

/**
 * Created by ldgd on 2019/11/12.
 * 功能：
 * 说明：开机启动
 */

public class MyBroadcastReceiver extends BroadcastReceiver {
    private final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";


    /**
     * 接收广播消息后都会进入 onReceive 方法，然后要做的就是对相应的消息做出相应的处理
     * @param context 表示广播接收器所运行的上下文
     * @param intent  表示广播接收器收到的Intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Wmx logs::", intent.getAction());
        Toast.makeText(context, intent.getAction(), Toast.LENGTH_LONG).show();

        /**
         * 如果 系统 启动的消息，则启动 APP 主页活动
         */
        if (ACTION_BOOT.equals(intent.getAction())) {
            Intent intentMainActivity = new Intent(context, MainActivity.class);
            intentMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentMainActivity);
            Toast.makeText(context, "开机完毕~", Toast.LENGTH_LONG).show();
        }

    }
}
