package com.bkstek.stour;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bkstek.stour.util.CommonDefine;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener;
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubeActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    YouTubePlayerView youtube;
    TextView txtBack;
    String VIDEO_ID = "";
    int locationID;
    String TAG = "";
    Context context;
    TextView txtTitle;
    String title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);

        youtube = (YouTubePlayerView) findViewById(R.id.youtube);
        txtBack = (TextView) findViewById(R.id.txtBack);
        txtTitle = findViewById(R.id.txtTitle);
        context = YoutubeActivity.this;

        Intent intent = getIntent();
        VIDEO_ID = intent.getStringExtra("VIDEO_ID");
        locationID = intent.getIntExtra("locationID", 0);
        TAG = intent.getStringExtra("TAG");
        title = intent.getStringExtra(CommonDefine.TITLE);

        txtTitle.setText(title);


        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iYoutube = new Intent();
                if (TAG.equals("HIS") || TAG.equals("CUL") || TAG.equals("FOOD") ) {
                    iYoutube = new Intent(context, NoneServiceActivity.class);
                } else if (TAG.equals("RES") || TAG.equals("HOTEL")){
                    iYoutube = new Intent(context, ServiceActivity.class);
                } else if (TAG.equals(CommonDefine.SMART)){
                    iYoutube = new Intent(context, MapsActivity.class);
                    iYoutube.putExtra(CommonDefine.FUNC, CommonDefine.SMART);
                }

                iYoutube.putExtra("PlaceID", locationID);
                iYoutube.putExtra("TAG", TAG);
                startActivity(iYoutube);
                finish();
            }
        });

        youtube.initialize(CommonDefine.KEY_YOUTUBE, this);
    }

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean b) {
        /** add listeners to YouTubePlayer instance **/
        player.setPlayerStateChangeListener(playerStateChangeListener);
        player.setPlaybackEventListener(playbackEventListener);
        /** Start buffering **/
        if (!b) {
            player.cueVideo(getYoutubeID(VIDEO_ID));
            System.out.println("Video id  " + getYoutubeID(VIDEO_ID));
        }
    }

    public static String getYoutubeID(String youtubeUrl) {

        if (TextUtils.isEmpty(youtubeUrl)) {
            return "";
        }
        String video_id = "";

        String expression = "^.*((youtu.be" + "\\/)" + "|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*"; // var regExp = /^.*((youtu.be\/)|(v\/)|(\/u\/\w\/)|(embed\/)|(watch\?))\??v?=?([^#\&\?]*).*/;
        CharSequence input = youtubeUrl;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(input);
        if (matcher.matches()) {
            String groupIndex1 = matcher.group(7);
            if (groupIndex1 != null && groupIndex1.length() == 11)
                video_id = groupIndex1;
        }
        if (TextUtils.isEmpty(video_id)) {
            if (youtubeUrl.contains("youtu.be/")  ) {
                String spl = youtubeUrl.split("youtu.be/")[1];
                if (spl.contains("\\?")) {
                    video_id = spl.split("\\?")[0];
                }else {
                    video_id =spl;
                }

            }
        }

        return video_id;
    }

    private PlaybackEventListener playbackEventListener = new PlaybackEventListener() {
        @Override
        public void onBuffering(boolean arg0) {
        }

        @Override
        public void onPaused() {
        }

        @Override
        public void onPlaying() {
        }

        @Override
        public void onSeekTo(int arg0) {
        }

        @Override
        public void onStopped() {
        }
    };

    private PlayerStateChangeListener playerStateChangeListener = new PlayerStateChangeListener() {
        @Override
        public void onAdStarted() {
        }

        @Override
        public void onError(ErrorReason arg0) {
        }

        @Override
        public void onLoaded(String arg0) {
        }

        @Override
        public void onLoading() {
        }

        @Override
        public void onVideoEnded() {
        }

        @Override
        public void onVideoStarted() {
        }
    };


    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this, "Failured to Initialize!", Toast.LENGTH_LONG).show();
    }
}
