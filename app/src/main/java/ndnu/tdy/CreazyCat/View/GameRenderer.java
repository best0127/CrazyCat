package ndnu.tdy.CreazyCat.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.core.content.ContextCompat;

import ndnu.tdy.CreazyCat.R;

/**
 * 游戏渲染器：绘制六边形网格、猫动画、背景。
 */
public class GameRenderer {

    private static final String TAG = "GameRenderer";

    private final GameEngine engine;
    private final Context context;
    private final Drawable[] catFrames = new Drawable[16];
    private Drawable background;
    private int animFrameIndex;

    private int screenWidth;
    private int cellWidth;
    private int topOffset;
    private int leftPadding;

    public GameRenderer(Context context, GameEngine engine) {
        this.context = context;
        this.engine = engine;
    }

    public int getCellWidth() { return cellWidth; }
    public int getTopOffset() { return topOffset; }
    public int getLeftPadding() { return leftPadding; }
    public int getScreenWidth() { return screenWidth; }

    /**
     * 预加载猫动画帧和背景图片。
     */
    public void preloadDrawables(Context context) {
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
        Log.d(TAG, "Preloaded " + catFrames.length + " cat frames");
    }

    /**
     * 更新 surface 尺寸参数。
     */
    public void updateDimensions(int width, int height) {
        int col = engine.getCol();
        int row = engine.getRow();
        cellWidth = width / (col + 1);
        topOffset = height - cellWidth * row - 2 * cellWidth;
        leftPadding = cellWidth / 3;
        screenWidth = width;
        Log.d(TAG, "Dimensions: " + width + "x" + height + ", cellWidth=" + cellWidth);
    }

    /**
     * 推进动画帧。
     */
    public void advanceFrame() {
        animFrameIndex = (animFrameIndex + 1) % catFrames.length;
    }

    /**
     * 绘制游戏画面到 Canvas。
     */
    public void draw(Canvas canvas) {
        int row = engine.getRow();
        int col = engine.getCol();
        Point cat = engine.getCat();

        int bgColor = ContextCompat.getColor(context, R.color.game_background);
        int emptyColor = ContextCompat.getColor(context, R.color.cell_empty);
        int obstacleColor = ContextCompat.getColor(context, R.color.cell_obstacle);
        int catColor = ContextCompat.getColor(context, R.color.cell_cat);

        canvas.drawColor(bgColor);
        Paint paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);

        RectF rect = new RectF();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                int offset = (i % 2 != 0) ? cellWidth / 2 : 0;
                Point dot = engine.getDot(j, i);
                switch (dot.getStatus()) {
                    case STATUS_IN:
                        paint.setColor(catColor);
                        break;
                    case STATUS_ON:
                        paint.setColor(obstacleColor);
                        break;
                    case STATUS_OFF:
                        paint.setColor(emptyColor);
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

        // 绘制猫
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

        // 绘制顶部背景
        if (background != null) {
            background.setBounds(0, 0, screenWidth, topOffset);
            background.draw(canvas);
        }
    }
}
