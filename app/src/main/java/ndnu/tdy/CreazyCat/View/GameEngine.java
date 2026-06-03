package ndnu.tdy.CreazyCat.View;

import android.util.Log;

/**
 * 游戏状态管理：矩阵初始化、障碍物放置、猫移动、胜负判断。
 */
public class GameEngine {

    private static final String TAG = "GameEngine";

    private final int row;
    private final int col;
    private final int obstacleCount;

    private Point[][] matrix;
    private Point cat;
    private int steps;
    private boolean canMove;

    public GameEngine(int row, int col, int rand) {
        this.row = row;
        this.col = col;
        this.obstacleCount = row * col / rand;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    public int getSteps() { return steps; }
    public Point getCat() { return cat; }
    public boolean canMove() { return canMove; }

    /**
     * 获取指定坐标的网格点，越界返回 null。
     */
    public Point getDot(int x, int y) {
        if (x < 0 || x >= col || y < 0 || y >= row) {
            return null;
        }
        return matrix[y][x];
    }

    /**
     * 判断点是否在地图边缘。
     */
    public boolean inEdge(Point dot) {
        return dot.getX() == 0 || dot.getY() == 0
                || dot.getX() + 1 == col || dot.getY() + 1 == row;
    }

    /**
     * 初始化游戏：重置矩阵，放置猫和障碍物。
     */
    public void initGame() {
        Log.d(TAG, "initGame: " + row + "x" + col + ", obstacles=" + obstacleCount);
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

        int placed = 0;
        while (placed < obstacleCount) {
            int x = (int) (Math.random() * col);
            int y = (int) (Math.random() * row);
            if (getDot(x, y).getStatus() == Point.STATUS.STATUS_OFF) {
                getDot(x, y).setStatus(Point.STATUS.STATUS_ON);
                placed++;
            }
        }
        Log.d(TAG, "Cat at (" + cat.getX() + "," + cat.getY() + "), placed " + placed + " obstacles");
    }

    /**
     * 移动猫到目标点。
     */
    public void moveCatTo(Point target) {
        target.setStatus(Point.STATUS.STATUS_IN);
        getDot(cat.getX(), cat.getY()).setStatus(Point.STATUS.STATUS_OFF);
        cat.setXY(target.getX(), target.getY());
    }

    /**
     * 放置障碍物。返回 true 表示成功放置。
     */
    public boolean placeObstacle(int x, int y) {
        Point dot = getDot(x, y);
        if (dot != null && dot.getStatus() == Point.STATUS.STATUS_OFF) {
            dot.setStatus(Point.STATUS.STATUS_ON);
            steps++;
            return true;
        }
        return false;
    }

    /**
     * 标记游戏不可继续（猫被困住或到达边缘）。
     */
    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }
}
