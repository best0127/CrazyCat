package ndnu.tdy.CreazyCat.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import ndnu.tdy.CreazyCat.GamePreferences;
import ndnu.tdy.CreazyCat.R;
import ndnu.tdy.CreazyCat.View.GameView;

public class GameActivity extends BaseActivity {

    private static final String TAG = "GameActivity";

    private int col;
    private int row;
    private int rand;
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setFullScreen();
        startGame();
    }

    private void startGame() {
        int flag = getIntent().getIntExtra("flag", 1);
        Log.d(TAG, "startGame: flag=" + flag);

        switch (flag) {
            case 1: row = 8;  col = 8;  rand = 4; break;  // 简单
            case 2: row = 10; col = 10; rand = 5; break;  // 普通
            case 3: row = 12; col = 12; rand = 6; break;  // 困难
            case 4: row = 10; col = 10; rand = 4; break;  // 限时
        }
        Log.d(TAG, "row=" + row + ", col=" + col + ", rand=" + rand);

        FrameLayout container = new FrameLayout(this);

        gameView = new GameView(this, row, col, rand);
        gameView.setOnGameOverListener(new GameView.OnGameOverListener() {
            @Override
            public void onGameOver() {
                finish();
            }

            @Override
            public void onRetry() {
                // 重新开始时，如果是限时模式需要重新启动倒计时
                if (rand == 4) {
                    gameView.setTimedMode(true);
                }
            }

            @Override
            public void onWin(int steps) {
                // 保存最佳成绩
                int mode = getIntent().getIntExtra("flag", 1);
                new GamePreferences(GameActivity.this).updateBestSteps(mode, steps);
            }
        });
        container.addView(gameView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        // 退出按钮
        ImageView exitButton = new ImageView(this);
        exitButton.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        exitButton.setBackgroundResource(R.drawable.bg_back_button);
        exitButton.setPadding(24, 24, 24, 24);
        exitButton.setColorFilter(0xFFFFFFFF);
        FrameLayout.LayoutParams exitParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        exitParams.gravity = android.view.Gravity.TOP | android.view.Gravity.START;
        exitParams.setMargins(32, 48, 0, 0);
        container.addView(exitButton, exitParams);

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Exit button clicked");
                showExitDialog();
            }
        });

        setContentView(container);

        // 限时模式启动倒计时
        if (rand == 4) {
            gameView.setTimedMode(true);
        }
    }

    private void showExitDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_exit_confirm, null);

        AlertDialog dialog = new AlertDialog.Builder(this, R.style.DialogTheme)
                .setView(dialogView)
                .create();

        dialogView.findViewById(R.id.btn_exit_to_mode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                navigateToModeSelect();
            }
        });

        dialogView.findViewById(R.id.btn_exit_to_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                navigateToHome();
            }
        });

        dialogView.findViewById(R.id.btn_exit_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void navigateToModeSelect() {
        Intent intent = new Intent(this, ChooseActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d(TAG, "Back key pressed");
            showExitDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
