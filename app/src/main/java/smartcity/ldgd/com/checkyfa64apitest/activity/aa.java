package smartcity.ldgd.com.checkyfa64apitest.activity;

import android.app.Activity;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraDevice.StateCallback;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

/**
 * Created by ldgd on 2019/11/14.
 * 功能：
 * 说明：
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class aa extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    private CameraManager mCameraManager;
    private int mCameraID;
    public final int BACK_CAMERA = 0; //后置摄像头的CameraId，默认的mCameraID

    /**
     * 摄像头创建监听，获得摄像头状态stateCallback
     */
    private StateCallback stateCallback = new StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice camera) {

        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {

        }

        @Override
        public void onError(CameraDevice camera, int error) {

        }
    };




  /*  //获取摄像头管理
    mCameraManager =(CameraManager)getSystemService(Context.CAMERA_SERVICE);
//打开摄像头

mCameraManager.openCamera(mCameraID + "", stateCallback, mainHandler);*/


}
