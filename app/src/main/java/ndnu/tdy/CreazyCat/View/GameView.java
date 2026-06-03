package ndnu.tdy.CreazyCat.View;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
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

@SuppressLint("ViewConstructor")
public class GameView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {

    private static final String TAG = "GameView";

    // 六边形方向常量
    private static final int DIR_LEFT = 1;
    private static final int DIR_TOP_LEFT = 2;
    private static final int DIR_TOP_RIGHT = 3;
    private static final int DIR_RIGHT = 4;
    private static final int DIR_BOTTOM_RIGHT = 5;
    private static final int DIR_BOTTOM_LEFT = 6;

    // 动画参数
    private static final int ANIM_FRAME_DELAY_MS = 65;
    private static final int ANIM_INITIAL_DELAY_MS = 50;
    private static final int COUNTDOWN_MS = 10_000;

    // 猫逃跑距离阈值
    private static final int ESCAPE_DISTANCE_THRESHOLD = 20;

    private final int row;
    private final int col;
    private final int rand;
    private final int obstacleCount;

    private int screenWidth;
    private int cellWidth;
    private int topOffset;
    private int leftPadding;

    private final Drawable[] catFrames = new Drawable[16];
    private Drawable background;
    private int animFrameIndex = 0;

    private Timer animTimer;
    private TimerTask animTimerTask;
    private Timer countdownTimer;

    private Point[][] matrix;
    private Point cat;

    private int steps;
    private boolean canMove = true;
    private volatile boolean surfaceValid = false;

    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    private final Object canvasLock = new Object();

    public interface OnGameOverListener {
        void onGameOver();
    }

    private OnGameOverListener onGameOverListener;

    public void setOnGameOverListener(OnGameOverListener listener) {
        this.onGameOverListener = listener;
    }

    public GameView(Context context, int row, int col, int rand) {
        super(context);
        this.row = row;
        this.col = col;
        this.rand = rand;
        this.obstacleCount = row * col / rand;
        Log.d(TAG, "GameView created: row=" + row + ", col=" + col + ", rand=" + rand + ", obstacles=" + obstacleCount);

        preloadDrawables(context);
        initGame();
        getHolder().addCallback(this);
        setOnTouchListener(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    private void preloadDrawables(Context context) {
        int[] frameResIds = {
                R.drawable.cat1, R.drawable.cat2, R.drawable.cat3, R.drawable.cat4,
                R.drawable.cat5, R.drawable.cat6, R.drawable.cat7, R.drawable.cat8,
                R.drawable.cat9, R.drawable.cat10, R.drawable.cat11, R.drawable.cat12,
                R.drawable.cat13, R.drawable.cat14, R.drawable.cat15, R.drawable.cat16
        };
        for (int i = 0; i < frameResIds.length; i++) {
            catFrames[i] = context.getDrawable(frameResIds[i]);
        }
        background = context.getDrawable(R.drawable.bg);
        Log.d(TAG, "preloaded " + catFrames.length + " cat frames");
    }

    private void startCountdown() {
        if (rand == 4) {
            Log.d(TAG, "Starting countdown timer: " + COUNTDOWN_MS + "ms");
            countdownTimer = new Timer();
            countdownTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.d(TAG, "Countdown finished, showing timeout dialog");
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

    private void initGame() {
        Log.d(TAG, "initGame");
        steps = 0;
        canMove = true;
        matrix = new Point[row][col];

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                matrix[i][j] = new Point(j, i);
                matrix[i][j].setStatus(Point.STATUS.STATUS_OFF);
            }
        }

        cat = new Point(col / 2 - 1, row / 2 - 1);
        getDot(cat.getX(), cat.getY()).setStatus(Point.STATUS.STATUS_IN);
        Log.d(TAG, "Cat initial position: (" + cat.getX() + ", " + cat.getY() + ")");

        int placed = 0;
        while (placed < obstacleCount) {
            int x = (int) (Math.random() * col);
            int y = (int) (Math.random() * row);
            if (getDot(x, y).getStatus() == Point.STATUS.STATUS_OFF) {
                getDot(x, y).setStatus(Point.STATUS.STATUS_ON);
                placed++;
            }
        }
        Log.d(TAG, "Placed " + placed + " obstacles");
    }

    // ==================== 绘图 ====================

    private void redraw() {
        if (!surfaceValid) return;
        synchronized (canvasLock) {
            if (!surfaceValid) return;
            Canvas canvas = null;
            try {
                canvas = getHolder().lockCanvas();
                if (canvas == null || !surfaceValid) return;

                canvas.drawColor(Color.rgb(0, 0x8c, 0xd7));
                Paint paint = new Paint();
                paint.setFlags(Paint.ANTI_ALIAS_FLAG);

                RectF rect = new RectF();
                for (int i = 0; i < row; i++) {
                    for (int j = 0; j < col; j++) {
                        int offset = (i % 2 != 0) ? cellWidth / 2 : 0;
                        Point dot = getDot(j, i);
                        switch (dot.getStatus()) {
                            case STATUS_IN:
                                paint.setColor(0xFFEEEEEE);
                                break;
                            case STATUS_ON:
                                paint.setColor(0xFFFFAA00);
                                break;
                            case STATUS_OFF:
                                paint.setColor(0x74000000);
                                break;
                        }
                        rect.set(
                                dot.getX() * cellWidth + offset + leftPadding,
                                dot.getY() * cellWidth + topOffset,
                                (dot.getX() + 1) * cellWidth + offset + leftPadding,
                                (dot.getY() + 1) * cellWidth + topOffset
                        );
                        canvas.drawOval(rect, paint);
                    }
                }

                int catLeft;
                int catTop;
                if (cat.getY() % 2 == 0) {
                    catLeft = cat.getX() * cellWidth;
                } else {
                    catLeft = (cellWidth / 2) + cat.getX() * cellWidth;
                }
                catTop = cat.getY() * cellWidth;

                Drawable catDrawable = catFrames[animFrameIndex];
                if (catDrawable != null) {
                    catDrawable.setBounds(
                            catLeft - cellWidth / 6 + leftPadding,
                            catTop - cellWidth / 2 + topOffset,
                            catLeft + cellWidth + leftPadding,
                            catTop + cellWidth + topOffset
                    );
                    catDrawable.draw(canvas);
                }

                if (background != null) {
                    background.setBounds(0, 0, screenWidth, topOffset);
                    background.draw(canvas);
                }
            } catch (IllegalStateException e) {
                // Surface 已释放，忽略
                return;
            } finally {
                if (canvas != null && surfaceValid) {
                    try {
                        getHolder().unlockCanvasAndPost(canvas);
                    } catch (IllegalStateException e) {
                        // Surface 已释放，忽略
                    }
                }
            }
        }
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
        cellWidth = width / (col + 1);
        topOffset = height - cellWidth * row - 2 * cellWidth;
        leftPadding = cellWidth / 3;
        screenWidth = width;
        Log.d(TAG, "surfaceChanged: " + width + "x" + height + ", cellWidth=" + cellWidth);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed");
        surfaceValid = false;
        stopAnimTimer();
        stopCountdown();
    }

    // ==================== 动画定时器 ====================

    private void startAnimTimer() {
        animTimer = new Timer();
        animTimerTask = new TimerTask() {
            @Override
            public void run() {
                animFrameIndex = (animFrameIndex + 1) % catFrames.length;
                redraw();
            }
        };
        animTimer.schedule(animTimerTask, ANIM_INITIAL_DELAY_MS, ANIM_FRAME_DELAY_MS);
        Log.d(TAG, "Animation timer started");
    }

    private void stopAnimTimer() {
        if (animTimer != null) {
            animTimer.cancel();
            animTimer.purge();
            animTimer = null;
            Log.d(TAG, "Animation timer stopped");
        }
    }

    private void stopCountdown() {
        if (countdownTimer != null) {
            countdownTimer.cancel();
            countdownTimer.purge();
            countdownTimer = null;
            Log.d(TAG, "Countdown timer stopped");
        }
    }

    // ==================== 游戏逻辑 ====================

    private Point getDot(int x, int y) {
        if (x < 0 || x >= col || y < 0 || y >= row) {
            return null;
        }
        return matrix[y][x];
    }

    private boolean inEdge(Point dot) {
        return dot.getX() == 0 || dot.getY() == 0
                || dot.getX() + 1 == col || dot.getY() + 1 == row;
    }

    private void moveTo(Point dot) {
        dot.setStatus(Point.STATUS.STATUS_IN);
        getDot(cat.getX(), cat.getY()).setStatus(Point.STATUS.STATUS_OFF);
        cat.setXY(dot.getX(), dot.getY());
    }

    private int getDistance(Point one, int dir) {
        if (inEdge(one)) {
            return 1;
        }
        int distance = 0;
        Point current = one;
        while (true) {
            Point next = getNeighbour(current, dir);
            if (next == null) return distance * -1;
            if (next.getStatus() == Point.STATUS.STATUS_ON) {
                return distance * -1;
            }
            if (inEdge(next)) {
                return distance + 1;
            }
            distance++;
            current = next;
        }
    }

    private Point getNeighbour(Point dot, int dir) {
        int x = dot.getX();
        int y = dot.getY();
        boolean isOddRow = (y % 2 != 0);

        switch (dir) {
            case DIR_LEFT:
                return getDot(x - 1, y);
            case DIR_TOP_LEFT:
                return isOddRow ? getDot(x, y - 1) : getDot(x - 1, y - 1);
            case DIR_TOP_RIGHT:
                return isOddRow ? getDot(x + 1, y - 1) : getDot(x, y - 1);
            case DIR_RIGHT:
                return getDot(x + 1, y);
            case DIR_BOTTOM_RIGHT:
                return isOddRow ? getDot(x, y + 1) : getDot(x + 1, y + 1);
            case DIR_BOTTOM_LEFT:
                return isOddRow ? getDot(x - 1, y + 1) : getDot(x, y + 1);
            default:
                return null;
        }
    }

    private void move() {
        if (inEdge(cat)) {
            Log.d(TAG, "Cat is at edge, game over");
            failure();
            return;
        }

        Vector<Point> available = new Vector<>();
        Vector<Point> direct = new Vector<>();
        HashMap<Point, Integer> directionMap = new HashMap<>();

        for (int i = 1; i <= 6; i++) {
            Point n = getNeighbour(cat, i);
            if (n != null && n.getStatus() == Point.STATUS.STATUS_OFF) {
                available.add(n);
                directionMap.put(n, i);
                if (getDistance(n, i) > 0) {
                    direct.add(n);
                }
            }
        }

        if (available.isEmpty()) {
            Log.d(TAG, "Cat has no available moves, win!");
            win();
            canMove = false;
        } else if (available.size() == 1) {
            moveTo(available.get(0));
        } else {
            Point best = null;
            if (!direct.isEmpty()) {
                int min = ESCAPE_DISTANCE_THRESHOLD;
                for (Point p : direct) {
                    if (inEdge(p)) {
                        best = p;
                        break;
                    }
                    int dist = getDistance(p, directionMap.get(p));
                    if (dist < min) {
                        min = dist;
                        best = p;
                    }
                }
            } else {
                int max = 1;
                for (Point p : available) {
                    int dist = getDistance(p, directionMap.get(p));
                    if (dist < max) {
                        max = dist;
                        best = p;
                    }
                }
            }
            if (best != null) {
                moveTo(best);
            }
        }

        if (inEdge(cat)) {
            Log.d(TAG, "Cat reached edge after move, game over");
            failure();
        }
    }

    // ==================== 游戏结果对话框 ====================

    private void showGameOverDialog(int titleResId, int messageResId, Object... formatArgs) {
        Log.d(TAG, "showGameOverDialog: title=" + titleResId);
        stopAnimTimer();
        stopCountdown();

        Context ctx = getContext();
        View dialogView = ((android.app.Activity) ctx).getLayoutInflater()
                .inflate(R.layout.dialog_game_result, null);

        TextView titleView = dialogView.findViewById(R.id.dialog_title);
        TextView messageView = dialogView.findViewById(R.id.dialog_message);
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

        btnMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Game over dialog: navigate to mode select");
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
                Log.d(TAG, "Game over dialog: navigate to home");
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
        showGameOverDialog(R.string.dialog_title_win, R.string.dialog_msg_win, steps + 1);
    }

    // ==================== 触摸事件 ====================

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_UP) {
            return true;
        }

        if (event.getY() <= topOffset) {
            return true;
        }

        int touchY = (int) ((event.getY() - topOffset) / cellWidth);
        int touchX;

        if (touchY % 2 == 0) {
            if (event.getX() <= leftPadding
                    || event.getX() >= leftPadding + cellWidth * col) {
                return true;
            }
            touchX = (int) ((event.getX() - leftPadding) / cellWidth);
        } else {
            if (event.getX() <= (leftPadding + cellWidth / 2)
                    || event.getX() > (leftPadding + cellWidth / 2 + cellWidth * col)) {
                return true;
            }
            touchX = (int) ((event.getX() - cellWidth / 2 - leftPadding) / cellWidth);
        }

        if (touchX + 1 > col || touchY + 1 > row) {
            return true;
        }

        if (inEdge(cat) || !canMove) {
            Log.d(TAG, "Touch on edge/cat stuck, restarting game");
            initGame();
            canMove = true;
            return true;
        }

        Point touched = getDot(touchX, touchY);
        if (touched != null && touched.getStatus() == Point.STATUS.STATUS_OFF) {
            touched.setStatus(Point.STATUS.STATUS_ON);
            move();
            steps++;
            Log.d(TAG, "Touch at (" + touchX + "," + touchY + "), steps=" + steps);
        }

        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
            Log.d(TAG, "Back key in GameView");
            stopAnimTimer();
            stopCountdown();
        }
        return super.onKeyDown(keyCode, event);
    }
}
