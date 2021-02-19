package ndnu.tdy.CreazyCat.Activity;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import ndnu.tdy.CreazyCat.Music.MusicServer;
import ndnu.tdy.CreazyCat.Music.MusicServer2;
import ndnu.tdy.CreazyCat.R;
import ndnu.tdy.CreazyCat.View.GameView;

public class ChooseActivity extends AppCompatActivity {

    Intent intent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        //启动背景音乐服务
        Intent intent = new Intent(ChooseActivity.this, MusicServer2.class);
        startService(intent);

        //游戏全屏
        setStatusBar();

        //调用按钮监听
        Click();

    }

    /**
     * 停止背景音乐服务
     */
    protected void onStop(){
        Intent intent = new Intent(ChooseActivity.this, MusicServer2.class);
        stopService(intent);
        super.onStop();
    }

    /**
     * 绑定按钮控件
     */
    public void Click(){

        Button button1 = (Button)findViewById(R.id.button1);
        Button button2 = (Button)findViewById(R.id.button2);
        Button button3 = (Button) findViewById(R.id.button3);
        Button button4 = (Button)findViewById(R.id.button4);

        button1.setOnClickListener(new Listener());
        button2.setOnClickListener(new Listener());
        button3.setOnClickListener(new Listener());
        button4.setOnClickListener(new Listener());

    }

    /**
     * 监听按钮，并跳转到Gameactivity进行传值
     */
    public  class Listener implements View.OnClickListener {

        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View view) {


            switch (view.getId()){
                case R.id.button1:
                    intent = new Intent(ChooseActivity.this,GameActivity.class);
                    intent.putExtra("flag",1);
                    break;

                case R.id.button2:
                    intent = new Intent(ChooseActivity.this,GameActivity.class);
                    intent.putExtra("flag",2);
                    break;

                case R.id.button3:
                    intent = new Intent(ChooseActivity.this,GameActivity.class);
                    intent.putExtra("flag",3);
                    break;

                case R.id.button4:
                    intent = new Intent(ChooseActivity.this,GameActivity.class);
                    intent.putExtra("flag",4);
                    break;

            }

            //清除栈顶活动
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

            startActivity(intent);

        }
    }

    /**
     * 返回键返回主界面并停止背景音乐服务，结束进程
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(ChooseActivity.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            onStop();
//            System.exit(0);
            finish();
        }
        return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件
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

}