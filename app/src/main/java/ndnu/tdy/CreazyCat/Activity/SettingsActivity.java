package ndnu.tdy.CreazyCat.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;

import ndnu.tdy.CreazyCat.GamePreferences;
import ndnu.tdy.CreazyCat.R;

public class SettingsActivity extends BaseActivity {

    private static final String TAG = "SettingsActivity";
    private GamePreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setFullScreen();
        prefs = new GamePreferences(this);

        // 返回按钮
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("设置");
        }

        setupAnimSpeed();
        setupCountdown();
        setupTheme();
    }

    private void setupAnimSpeed() {
        SeekBar seekBar = findViewById(R.id.seekbar_anim_speed);
        TextView label = findViewById(R.id.label_anim_speed);

        int speed = prefs.getAnimSpeed();
        seekBar.setProgress(speed);
        label.setText("猫动画速度: " + getSpeedText(speed));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                label.setText("猫动画速度: " + getSpeedText(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                prefs.setAnimSpeed(seekBar.getProgress());
            }
        });
    }

    private String getSpeedText(int speed) {
        switch (speed) {
            case 0: return "慢";
            case 2: return "快";
            default: return "正常";
        }
    }

    private void setupCountdown() {
        RadioGroup radioGroup = findViewById(R.id.rg_countdown);
        int seconds = prefs.getCountdownSeconds();

        if (seconds == 20) radioGroup.check(R.id.rb_20s);
        else if (seconds == 30) radioGroup.check(R.id.rb_30s);
        else radioGroup.check(R.id.rb_10s);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_20s) prefs.setCountdownSeconds(20);
                else if (checkedId == R.id.rb_30s) prefs.setCountdownSeconds(30);
                else prefs.setCountdownSeconds(10);
            }
        });
    }

    private void setupTheme() {
        RadioGroup radioGroup = findViewById(R.id.rg_theme);
        int theme = prefs.getThemeMode();

        if (theme == 1) radioGroup.check(R.id.rb_light);
        else if (theme == 2) radioGroup.check(R.id.rb_dark);
        else radioGroup.check(R.id.rb_system);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int mode;
                if (checkedId == R.id.rb_light) {
                    mode = 1;
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else if (checkedId == R.id.rb_dark) {
                    mode = 2;
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    mode = 0;
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                }
                prefs.setThemeMode(mode);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
