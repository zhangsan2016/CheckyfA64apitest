package smartcity.ldgd.com.checkyfa64apitest.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.VideoView;

import smartcity.ldgd.com.checkyfa64apitest.R;

/**
 * Created by ldgd on 2019/11/18.
 * 功能：
 * 说明：
 */

public class rtspActivity extends Activity {

    Button playButton ;
    VideoView videoView ;
    String rtspUrl = "" ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viedo);

        videoView = (VideoView)this.findViewById(R.id.rtsp_player);
        PlayRtspStream(rtspUrl);


    }

    //play rtsp stream
    private void PlayRtspStream(String rtspUrl){
        videoView.setVideoURI(Uri.parse(rtspUrl));
        videoView.requestFocus();
        videoView.start();
    }
}
