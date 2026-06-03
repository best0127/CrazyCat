package ndnu.tdy.CreazyCat.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import ndnu.tdy.CreazyCat.GamePreferences;
import ndnu.tdy.CreazyCat.R;

public class ChooseActivity extends BaseActivity {

    private static final String TAG = "ChooseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_choose);
        setFullScreen();
        setupListeners();
    }

    private void setupListeners() {
        GamePreferences prefs = new GamePreferences(this);

        int[] buttonIds = {R.id.button1, R.id.button2, R.id.button3, R.id.button4};
        int[] flags = {1, 2, 3, 4};
        String[] modeNames = {
                getString(R.string.mode_simple),
                getString(R.string.mode_normal),
                getString(R.string.mode_hard),
                getString(R.string.mode_timed)
        };

        for (int i = 0; i < buttonIds.length; i++) {
            Button button = findViewById(buttonIds[i]);
            final int flag = flags[i];

            // 显示最佳成绩
            int bestSteps = prefs.getBestSteps(flag);
            if (bestSteps > 0) {
                button.setText(modeNames[i] + " - 最佳: " + bestSteps + "步");
            }

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Mode button clicked, flag=" + flag);
                    Intent intent = new Intent(ChooseActivity.this, GameActivity.class);
                    intent.putExtra("flag", flag);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d(TAG, "Back key pressed");
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
