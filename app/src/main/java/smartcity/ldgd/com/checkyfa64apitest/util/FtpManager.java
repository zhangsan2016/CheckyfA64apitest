package smartcity.ldgd.com.checkyfa64apitest.util;

import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ldgd on 2020/1/3.
 * 功能：
 * 说明：Ftp服务器管理器
 */

public class FtpManager {

    private static FtpManager sInstance = null;
    private FtpManager(){};

    private Button mButton, btn_stop;
    private EditText mEditText;
    private static final String TAG = "FtpServerService";
    private static String hostip = ""; // 本机IP
 //   private static final int PORT = 29106;
    private static final int PORT = 2221;
    private TextView tv_ip;
    private static final String dirname = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ftp";
    // ftp服务器配置文件路径
    private static final String filename = dirname + "/users.properties";
    private FtpServer mFtpServer = null;


    private FtpManager(String configuration) {
        //创建服务器配置文件
        try {
            creatDirsFiles(configuration);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FtpManager getInstance(String configuration){
        if(sInstance == null){
            synchronized (FtpManager.class) {
                if(sInstance == null){
                    sInstance = new FtpManager(configuration);
                }
            }
        }
        return sInstance;
    }




    /**
     * 创建服务器配置文件
     */
    private void creatDirsFiles(String configuration) throws IOException {
        File dir = new File(dirname);
        if (!dir.exists()) {
            dir.mkdir();
        }
        FileOutputStream fos = null;
        // String tmp = getString(R.string.users);
        String tmp = configuration;
        File sourceFile = new File(dirname + "/users.properties");
        fos = new FileOutputStream(sourceFile);
        fos.write(tmp.getBytes());
        if (fos != null) {
            fos.close();
        }
    }

    /**
     * 开启FTP服务器
     * @param hostip 本机ip
     */
    private void startFtpServer(String hostip) {
        FtpServerFactory serverFactory = new FtpServerFactory();
        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
        File files = new File(filename);
        if (!files.exists() || files.length() == 0) {
            //   Toast.makeText(this,"文件为空！",Toast.LENGTH_SHORT).show();
        }
        //设置配置文件
        userManagerFactory.setFile(files);


       /* try {
            // 账户信息（也可以新增用户）
            UserManager userManager = userManagerFactory.createUserManager();
            BaseUser user = null;
            user = (BaseUser) userManager.getUserByName("admin");
            user.setPassword("admin123456");
            userManager.save(user); // 修改账户信息
        } catch (FtpException e) {
            e.printStackTrace();
        }*/

       // 添加 ftp 用户
        try {




            // 账户信息（也可以新增用户）
            UserManager userManager = userManagerFactory.createUserManager();
            BaseUser  francis  = new BaseUser(userManager.getUserByName("admin"));
            francis.setPassword("francis");
            francis.setName("francis");
            francis.setEnabled(true);
            /* francis.setMaxIdleTime(300000);*/
            francis.setHomeDirectory( Environment.getExternalStorageDirectory().getAbsolutePath());
            // 添加权限
            List<Authority> authorities = new ArrayList<Authority>();
            authorities.add(new WritePermission());
            francis.setAuthorities(authorities);


            userManager.save(francis); // 修改账户信息
        } catch (FtpException e) {
            e.printStackTrace();
        }

        serverFactory.setUserManager(userManagerFactory.createUserManager());
        // 设置监听IP和端口号
        ListenerFactory factory = new ListenerFactory();
        factory.setPort(PORT);
        factory.setServerAddress(hostip);
        // start the server
        serverFactory.addListener("default", factory.createListener());
        mFtpServer = serverFactory.createServer();

        try {

            mFtpServer.start();
            //      mHandler.sendEmptyMessage(0x0001);
            LogUtil.e( "开启了FTP服务器  ip = " + hostip);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("xxx e.getMessage().toString() = " + e.getMessage().toString());
        }
    }

    /**
     * 关闭FTP服务器
     */
    public void stopFtpServer() {
        if (mFtpServer != null) {
            mFtpServer.stop();
            mFtpServer = null;
            // mHandler.sendEmptyMessage(0x0002);
            LogUtil.e( "关闭了FTP服务器 ip = " + hostip);
        } else {
            // mHandler.sendEmptyMessage(0x0004);
        }
    }

    /**
     * 获取本机ip
     */
    private String getLocalIpAddress() {
        try {
            List<NetworkInterface> interfaces = Collections
                    .list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf
                        .getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = Isipv4(sAddr);
                        if (isIPv4) {
                            return sAddr;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }




    public void startServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //hostip = mEditText.getText().toString().trim();
                hostip = getLocalIpAddress();
                if (mFtpServer == null) {
                    startFtpServer(hostip);
                } else {
                    // 当前FTP服务已开启
                }
            }
        }).start();

    }

    public static boolean Isipv4(String ipv4) {
        if (ipv4 == null || ipv4.length() == 0) {
            return false;//字符串为空或者空串
        }
        String[] parts = ipv4.split("\\.");//因为java doc里已经说明, split的参数是reg, 即正则表达式, 如果用"|"分割, 则需使用"\\|"
        if (parts.length != 4) {
            return false;//分割开的数组根本就不是4个数字
        }
        for (int i = 0; i < parts.length; i++) {
            try {
                int n = Integer.parseInt(parts[i]);
                if (n < 0 || n > 255) {
                    return false;//数字不在正确范围内
                }
            } catch (NumberFormatException e) {
                return false;//转换数字不正确
            }
        }
        return true;
    }
}
