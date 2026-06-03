package ndnu.tdy.CreazyCat.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatDelegate;

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

        // 设置按钮
        ImageView settingsButton = findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });

        // 应用主题设置
        GamePreferences prefs = new GamePreferences(this);
        applyTheme(prefs.getThemeMode());

        // 新手引导
        if (!prefs.isGuideShown()) {
            showGuide(prefs);
        }
    }

    private void applyTheme(int mode) {
        switch (mode) {
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case 2:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    private void showGuide(final GamePreferences prefs) {
        new AlertDialog.Builder(this, R.style.DialogTheme)
                .setTitle("🎮 游戏玩法")
                .setMessage("1. 点击空白格子放置障碍物\n\n" +
                        "2. 阻止小猫逃到地图边缘\n\n" +
                        "3. 在猫到达边缘前围住它即可获胜\n\n" +
                        "💡 提示：注意猫会自动选择最佳逃跑路线！")
                .setPositiveButton("知道了", null)
                .setOnDismissListener(new android.content.DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(android.content.DialogInterface dialog) {
                        prefs.setGuideShown();
                    }
                })
                .show();
    }
}
