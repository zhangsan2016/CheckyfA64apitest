package smartcity.ldgd.com.checkyfa64apitest.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.VideoView;

import androidx.annotation.Nullable;

import smartcity.ldgd.com.checkyfa64apitest.R;

/**
 * Created by ldgd on 2019/11/18.
 * 功能：
 * 说明：
 */

public class rtspActivity extends Activity {

    Button playButton ;
    VideoView videoView ;
    String rtspUrl = "rtsp://192.168.1.75:554/user=admin_password=tlJwpbo6_channel=1_stream=0.sdp?real_stream" ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aa);

       // videoView = (VideoView)this.findViewById(R.id.rtsp_player);
     //   PlayRtspStream(rtspUrl);

    }

    //play rtsp stream
    private void PlayRtspStream(String rtspUrl){
        videoView.setVideoURI(Uri.parse(rtspUrl));
        videoView.requestFocus();
        videoView.start();

    }
}
