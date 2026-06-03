package ndnu.tdy.CreazyCat.View;

/**
 * 六边形网格中的一个单元格。
 */
public class Point {

    private int x;
    private int y;
    private STATUS status;

    public enum STATUS {
        STATUS_OFF,  // 空位
        STATUS_ON,   // 障碍物
        STATUS_IN    // 猫的位置
    }

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
        this.status = STATUS.STATUS_OFF;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
