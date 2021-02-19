package ndnu.tdy.CreazyCat.Music;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import ndnu.tdy.CreazyCat.R;

public class MusicServer2 extends Service {

    private MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        if (mediaPlayer == null) {

            // R.raw.bg2是资源文件，MP3格式的
            mediaPlayer = MediaPlayer.create(this, R.raw.bg2);
            mediaPlayer.setLooping(true);
//            mediaPlayer.isLooping();
            mediaPlayer.start();

        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mediaPlayer.stop();
    }
}
