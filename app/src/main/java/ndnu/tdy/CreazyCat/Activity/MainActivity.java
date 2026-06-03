package ndnu.tdy.CreazyCat.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import ndnu.tdy.CreazyCat.GamePreferences;
import ndnu.tdy.CreazyCat.R;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        setFullScreen();

        // 开始游戏
        ImageView startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "start_button clicked");
                startActivity(new Intent(MainActivity.this, ChooseActivity.class));
            }
        });

        // 每次启动都显示玩法提示
        showGuide();
    }

    private void showGuide() {
        new AlertDialog.Builder(this)
                .setTitle("🎮 游戏玩法")
                .setMessage("1. 点击空白格子放置障碍物\n\n" +
                        "2. 阻止小猫逃到地图边缘\n\n" +
                        "3. 在猫到达边缘前围住它即可获胜\n\n" +
                        "💡 提示：注意猫会自动选择最佳逃跑路线！")
                .setPositiveButton("知道了", null)
                .show();
    }
}
