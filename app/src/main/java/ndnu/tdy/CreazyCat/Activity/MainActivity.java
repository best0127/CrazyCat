package ndnu.tdy.CreazyCat.Activity;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import ndnu.tdy.CreazyCat.Music.MusicServer;
import ndnu.tdy.CreazyCat.Music.MusicServer2;
import ndnu.tdy.CreazyCat.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //启动背景音乐服务
        Intent intent = new Intent(MainActivity.this, MusicServer.class);
        startService(intent);

        setContentView(R.layout.activity_main);

        //游戏全屏
        setStatusBar();

    }

    /**
     * 停止背景音乐服务
     */
    protected void onStop(){
        Intent intent = new Intent(MainActivity.this,MusicServer.class);
        stopService(intent);
        super.onStop();
    }

    /**
     * 开始游戏跳转到模式选择
     * @param view
     */
    public void onClick(View view) {
        Intent intent = new Intent(MainActivity.this, ChooseActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 游戏全屏设置
     */
    protected void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//隐藏状态栏但不隐藏状态栏字体
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏状态栏，并且不显示字体
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//实现状态栏文字颜色为暗色

        }
    }

/*    private static final int TIME_EXIT=2000;
    private long mBackPressed;

    @Override
    public void onBackPressed(){                                    //onBackPressed()   捕获后退键按钮back的信息
        if(mBackPressed+TIME_EXIT>System.currentTimeMillis()){      //currentTimeMillis,返回毫秒级别的系统时间
            super.onBackPressed();
        }else{
            Toast.makeText(this,"再点击一次返回退出游戏", Toast.LENGTH_SHORT).show();
            mBackPressed=System.currentTimeMillis();
        }
    }*/

    /**
     * 返回键结束游戏
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onStop();
        finish();
//        System.exit(0);
    }

}
