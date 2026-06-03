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
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_guide, null);

        final AlertDialog dialog = new AlertDialog.Builder(this, R.style.DialogTheme)
                .setView(dialogView)
                .create();

        dialogView.findViewById(R.id.btn_guide_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
