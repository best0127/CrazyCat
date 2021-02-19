package ndnu.tdy.CreazyCat.Activity;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

import ndnu.tdy.CreazyCat.Music.MusicServer2;
import ndnu.tdy.CreazyCat.Music.MusicServer3;
import ndnu.tdy.CreazyCat.R;
import ndnu.tdy.CreazyCat.View.GameView;

public class GameActivity extends AppCompatActivity {

    //定义行列，随机数
    private int COL;
    private int ROW;
    private int rand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //启动背景音乐服务
        Intent intent1 = new Intent(GameActivity.this, MusicServer3.class);
        startService(intent1);

        //游戏全屏
        setStatusBar();

        //接收ChooseActivity的值并绘制游戏界面
        Chooser();

    }

    /**
     * 模式选择
     */
    public void Chooser (){

        Intent intent = getIntent();
        int flag;

        flag = intent.getIntExtra("flag",1);

        switch (flag){

            case 1:
                ROW=12;
                COL=12;
                rand=5;
                break;

            case 2:
                ROW=9;
                COL=9;
                rand=6;
                break;

            case 3:
                ROW=8;
                COL=8;
                rand=7;
                break;

            case 4:
                ROW=8;
                COL=8;
                rand=4;
                Display display = getWindowManager().getDefaultDisplay();
                // 获取屏幕高度
                int height = display.getHeight();
                Toast toast = Toast.makeText(GameActivity.this, "你将有10秒时间完成游戏", Toast.LENGTH_LONG);
                // 这里给了一个1/4屏幕高度的y轴偏移量
                toast.setGravity(Gravity.TOP, 0, height / 4);
                toast.show();
                break;
        }

        GameView gameView = new GameView(GameActivity.this,ROW,COL,rand);
        setContentView(gameView);

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

    /**
     * 停止背景音乐服务
     */
    protected void onStop(){
        Intent intent = new Intent(GameActivity.this, MusicServer3.class);
        stopService(intent);
        super.onStop();
    }

    /**
     * 返回键返回模式选择界面并停止背景音乐服务，结束进程
     * @param keyCode
     * @param event
     * @return
     */
        @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode== KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(GameActivity.this,ChooseActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            onStop();
//            System.exit(0);
            finish();
        }
        return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件
    }

}