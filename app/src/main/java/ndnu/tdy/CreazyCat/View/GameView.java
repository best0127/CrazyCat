package ndnu.tdy.CreazyCat.View;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ndnu.tdy.CreazyCat.Activity.ChooseActivity;
import ndnu.tdy.CreazyCat.Activity.MainActivity;
import ndnu.tdy.CreazyCat.R;

/**
 * 游戏主视图，协调 Engine、AI、Renderer，处理触摸和生命周期。
 */
@SuppressLint("ViewConstructor")
public class GameView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {

    private static final String TAG = "GameView";

    private static final int ANIM_FRAME_DELAY_MS = 40;
    private static final int ANIM_INITIAL_DELAY_MS = 50;
    private static final int COUNTDOWN_MS = 10_000;

    private final GameEngine engine;
    private final CatAI ai;
    private final GameRenderer renderer;

    private Timer animTimer;
    private Timer countdownTimer;

    private volatile boolean surfaceValid = false;
    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    private final Object canvasLock = new Object();

    private OnGameOverListener onGameOverListener;
    private long countdownEndTime;

    public interface OnGameOverListener {
        void onGameOver();
        void onRetry();
        void onWin(int steps);
    }

    public void setOnGameOverListener(OnGameOverListener listener) {
        this.onGameOverListener = listener;
    }

    public GameView(Context context, int row, int col, int rand) {
        super(context);
        engine = new GameEngine(row, col, rand);
        ai = new CatAI(engine);
        renderer = new GameRenderer(context, engine);

        Log.d(TAG, "GameView created: " + row + "x" + col + ", rand=" + rand);
        renderer.preloadDrawables(context);
        engine.initGame();
        getHolder().addCallback(this);
        setOnTouchListener(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    /**
     * 获取当前模式的 rand 值，用于判断是否限时模式。
     */
    public int getRand() {
        return engine.getCol() == 10 ? 4 : (engine.getRow() == 8 ? 4 : (engine.getRow() == 12 ? 6 : 5));
    }

    // ==================== 重新开始 ====================

    public void restartGame() {
        Log.d(TAG, "restartGame");
        engine.initGame();
        redraw();
    }

    // ==================== SurfaceHolder.Callback ====================

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated");
        surfaceValid = true;
        redraw();
        startAnimTimer();
        startCountdown();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        renderer.updateDimensions(width, height);
        Log.d(TAG, "surfaceChanged: " + width + "x" + height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed");
        surfaceValid = false;
        stopAnimTimer();
        stopCountdown();
    }

    // ==================== 定时器 ====================

    private void startAnimTimer() {
        animTimer = new Timer();
        animTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                renderer.advanceFrame();
                redraw();
            }
        }, ANIM_INITIAL_DELAY_MS, ANIM_FRAME_DELAY_MS);
    }

    private void stopAnimTimer() {
        if (animTimer != null) {
            animTimer.cancel();
            animTimer.purge();
            animTimer = null;
        }
    }

    private void startCountdown() {
        // 限时模式由 GameActivity 调用 setTimedMode(true) 启动
    }

    /**
     * 由 GameActivity 调用，标记为限时模式并启动倒计时。
     */
    public void setTimedMode(boolean timed) {
        if (timed) {
            countdownEndTime = System.currentTimeMillis() + COUNTDOWN_MS;
            countdownTimer = new Timer();
            countdownTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            showGameOverDialog(R.string.dialog_title_timeout, R.string.dialog_msg_timeout);
                        }
                    });
                }
            }, COUNTDOWN_MS);
        }
    }

    /**
     * 获取剩余倒计时秒数（限时模式专用）。
     */
    public int getRemainingSeconds() {
        if (countdownEndTime == 0) return -1;
        long remaining = countdownEndTime - System.currentTimeMillis();
        return (int) Math.max(0, remaining / 1000);
    }

    public boolean isTimedMode() {
        return countdownEndTime > 0;
    }

    private void stopCountdown() {
        if (countdownTimer != null) {
            countdownTimer.cancel();
            countdownTimer.purge();
            countdownTimer = null;
        }
    }

    // ==================== 渲染 ====================

    private void redraw() {
        if (!surfaceValid) return;
        synchronized (canvasLock) {
            if (!surfaceValid) return;
            Canvas canvas = null;
            try {
                canvas = getHolder().lockCanvas();
                if (canvas == null || !surfaceValid) return;
                renderer.draw(canvas);

                // 限时模式绘制倒计时
                if (isTimedMode()) {
                    int seconds = getRemainingSeconds();
                    if (seconds >= 0) {
                        android.graphics.Paint textPaint = new android.graphics.Paint();
                        textPaint.setAntiAlias(true);
                        textPaint.setTextSize(renderer.getCellWidth() * 0.8f);
                        textPaint.setTextAlign(android.graphics.Paint.Align.CENTER);
                        textPaint.setColor(seconds <= 3 ? 0xFFFF0000 : 0xFFFFFFFF);
                        textPaint.setShadowLayer(4f, 2f, 2f, 0x80000000);
                        canvas.drawText(String.valueOf(seconds),
                                renderer.getScreenWidth() / 2f,
                                renderer.getTopOffset() / 2f + renderer.getCellWidth() * 0.3f,
                                textPaint);
                    }
                }
            } catch (IllegalStateException e) {
                return;
            } finally {
                if (canvas != null && surfaceValid) {
                    try {
                        getHolder().unlockCanvasAndPost(canvas);
                    } catch (IllegalStateException e) {
                        // ignore
                    }
                }
            }
        }
    }

    // ==================== 游戏结果 ====================

    private void showGameOverDialog(int titleResId, int messageResId, Object... formatArgs) {
        Log.d(TAG, "showGameOverDialog: " + titleResId);
        stopAnimTimer();
        stopCountdown();

        Context ctx = getContext();
        View dialogView = ((android.app.Activity) ctx).getLayoutInflater()
                .inflate(R.layout.dialog_game_result, null);

        TextView titleView = dialogView.findViewById(R.id.dialog_title);
        TextView messageView = dialogView.findViewById(R.id.dialog_message);
        Button btnRetry = dialogView.findViewById(R.id.dialog_btn_retry);
        Button btnMode = dialogView.findViewById(R.id.dialog_btn_mode);
        Button btnHome = dialogView.findViewById(R.id.dialog_btn_home);

        titleView.setText(titleResId);
        if (formatArgs.length > 0) {
            messageView.setText(ctx.getString(messageResId, formatArgs));
        } else {
            messageView.setText(messageResId);
        }

        AlertDialog dialog = new AlertDialog.Builder(ctx, R.style.DialogTheme)
                .setView(dialogView)
                .create();

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                engine.initGame();
                countdownEndTime = 0;
                redraw();
                startAnimTimer();
                // 重新启动倒计时（如果是限时模式需要由外部设置）
                if (onGameOverListener != null) {
                    onGameOverListener.onRetry();
                }
            }
        });

        btnMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ctx.startActivity(new Intent(ctx, ChooseActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                if (onGameOverListener != null) {
                    onGameOverListener.onGameOver();
                }
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ctx.startActivity(new Intent(ctx, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                if (onGameOverListener != null) {
                    onGameOverListener.onGameOver();
                }
            }
        });

        dialog.show();
    }

    private void failure() {
        showGameOverDialog(R.string.dialog_title_lose, R.string.dialog_msg_lose);
    }

    private void win() {
        int steps = engine.getSteps() + 1;
        showGameOverDialog(R.string.dialog_title_win, R.string.dialog_msg_win, steps);
        if (onGameOverListener != null) {
            onGameOverListener.onWin(steps);
        }
    }

    // ==================== 触摸事件 ====================

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_UP) return true;
        if (event.getY() <= renderer.getTopOffset()) return true;

        int touchY = (int) ((event.getY() - renderer.getTopOffset()) / renderer.getCellWidth());
        int touchX;
        int col = engine.getCol();

        if (touchY % 2 == 0) {
            if (event.getX() <= renderer.getLeftPadding()
                    || event.getX() >= renderer.getLeftPadding() + renderer.getCellWidth() * col) {
                return true;
            }
            touchX = (int) ((event.getX() - renderer.getLeftPadding()) / renderer.getCellWidth());
        } else {
            if (event.getX() <= (renderer.getLeftPadding() + renderer.getCellWidth() / 2)
                    || event.getX() > (renderer.getLeftPadding() + renderer.getCellWidth() / 2 + renderer.getCellWidth() * col)) {
                return true;
            }
            touchX = (int) ((event.getX() - renderer.getCellWidth() / 2 - renderer.getLeftPadding()) / renderer.getCellWidth());
        }

        if (touchX + 1 > col || touchY + 1 > engine.getRow()) return true;

        // 游戏已结束，触摸重新开始
        if (engine.inEdge(engine.getCat()) || !engine.canMove()) {
            Log.d(TAG, "Game over, restarting");
            engine.initGame();
            countdownEndTime = 0;
            redraw();
            startAnimTimer();
            return true;
        }

        // 放置障碍物
        if (engine.placeObstacle(touchX, touchY)) {
            // 猫移动
            Point nextMove = ai.evaluateMove(engine.getCat());
            if (nextMove == null) {
                // 猫被困住，玩家获胜
                engine.setCanMove(false);
                win();
            } else {
                engine.moveCatTo(nextMove);
                if (engine.inEdge(engine.getCat())) {
                    failure();
                }
            }
            redraw();
            Log.d(TAG, "Touch at (" + touchX + "," + touchY + "), steps=" + engine.getSteps());
        }

        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
            stopAnimTimer();
            stopCountdown();
        }
        return super.onKeyDown(keyCode, event);
    }

}
