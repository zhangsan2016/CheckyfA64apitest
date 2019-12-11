package smartcity.ldgd.com.checkyfa64apitest.util;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by ldgd on 2019/11/28.
 * 功能：
 * 说明：
 */

public class UpdateAppManager {

    private static final String FILE_SEPARATOR = "/";
    private static final String FILE_PATH = Environment.getExternalStorageDirectory()  + FILE_SEPARATOR;
    private static final String FILE_NAME = FILE_PATH + "app-debug.apk";
    private static final int UPDATE_TOKEN = 0x29;
    private static final int INSTALL_TOKEN = 0x31;
    private Context context;
    private String message = "检测到本程序有新版本发布，建议您更新！";
    private String spec = "";
    private String json1 = "http://192.168.1.158:8089/APP/getUpdate";
    private String result;
    private String VersionName;//当前版本名
    private int VersionCode;//当前版本号
    //  http://192.168.0.107:8080/app-debug.apk
    // 下载应用的对话框
    private Dialog dialog;
    // 下载应用的进度条
    private ProgressBar progressBar;
    private int curProgress;
    private boolean isCancel;

    public UpdateAppManager(Context context) {
        this.context = context;
    }

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TOKEN:
              //      progressBar.setProgress(curProgress);
                    break;

                case INSTALL_TOKEN:
                    installApp();
                    break;
            }
        }
    };


    /**
     * 下载新版本应用
     */
    private void downloadApp() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                // 在非主线程的情况下需要自己开启线程中的 Looper 否则使用 Handle 会报错
                Looper.prepare();

                URL url = null;
                InputStream in = null;
                FileOutputStream out = null;
                HttpURLConnection conn = null;
                try {
                    url = new URL(spec);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    long fileLength = conn.getContentLength();
                    in = conn.getInputStream();
                    File filePath = new File(FILE_PATH);
                    if (!filePath.exists()) {
                        filePath.mkdirs();
                    }
                    out = new FileOutputStream(FILE_NAME);
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    long readedLength = 0l;
                    while ((len = in.read(buffer)) != -1 && !isCancel) {
                        // 用户点击“取消”按钮，下载中断
                        if (isCancel) {
                            break;
                        }
                        out.write(buffer, 0, len);
                        readedLength += len;
                        curProgress = (int) (((float) readedLength / fileLength) * 100);
                      //  handler.sendEmptyMessage(UPDATE_TOKEN);
                        LogUtil.e("curProgress = " + curProgress);
                        if (readedLength >= fileLength) {
                         //   dialog.dismiss();
                            // 下载完毕，通知安装
                            handler.sendEmptyMessage(INSTALL_TOKEN);
                            break;
                        }
                    }
                    out.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
                Looper.loop();
            }
        }).start();
    }

    /**
     * 安装新版本应用
     */
    private void installApp() {
        File appFile = new File(FILE_NAME);


        if (!appFile.exists()) {
            return;
        }
        // 跳转到新版本应用安装页面
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + appFile.toString()), "application/vnd.android.package-archive");
        context.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());

    }


    public void setCancel(boolean cancel) {
        isCancel = cancel;
    }

    public void checkUpdateInfo() {
        gethttpresult(json1);
    }


    public void gethttpresult(final String urlStr) {
        new Thread(new Runnable() {

            @Override
            public void run() {

                try {


                    HttpUtil.sendHttpRequest("http://134.175.135.19:8089/APP/getUpdate", new okhttp3.Callback() {

                        @Override
                        public void onFailure(Call call, IOException e) {
                            LogUtil.e("网络连接失败 = " + e.getMessage());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {

                            result = response.body().string();

                            try {
                                int IntversionCode;
                                JSONObject jsonObject = new JSONObject(result);
                                String downloadUrl = jsonObject.optString("updatedir");
                                String versionCode = jsonObject.optString("versionCode");
                                String versionName = jsonObject.optString("versionName");
                                spec = downloadUrl;

                                LogUtil.e("downloadUrl = " + downloadUrl + "versionCode = " + versionCode + "   versionName  = "+ versionName);



                                try {
                                    //获取软件版本号，对应AndroidManifest.xml下android:versionCode
                                    VersionCode = context.getPackageManager().
                                            getPackageInfo(context.getPackageName(), 0).versionCode;
                                } catch (PackageManager.NameNotFoundException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    VersionName = context.getPackageManager().
                                            getPackageInfo(context.getPackageName(), 0).versionName;
                                } catch (PackageManager.NameNotFoundException e) {
                                    e.printStackTrace();
                                }
                                LogUtil.e( "当前版本为：" + VersionName);

                                try {
                                    IntversionCode = Integer.parseInt(versionCode.trim());
                                    if (IntversionCode > VersionCode) {
                                        LogUtil.e("app 需要更新版本");
                                        downloadApp();
                                    } else {
                                     LogUtil.e("app 不需要更新版本");
                                    }
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }//版本号转为int,再比较
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                } catch (Exception e) {

                }
            }

        }).start();
    }


}
