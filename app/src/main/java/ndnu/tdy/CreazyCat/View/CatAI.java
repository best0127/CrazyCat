package ndnu.tdy.CreazyCat.View;

import java.util.HashMap;
import java.util.Vector;

/**
 * 猫的 AI 移动逻辑：评估所有可走方向，选择最优路径逃离。
 */
public class CatAI {

    // 六边形方向常量
    public static final int DIR_LEFT = 1;
    public static final int DIR_TOP_LEFT = 2;
    public static final int DIR_TOP_RIGHT = 3;
    public static final int DIR_RIGHT = 4;
    public static final int DIR_BOTTOM_RIGHT = 5;
    public static final int DIR_BOTTOM_LEFT = 6;

    private static final int ESCAPE_DISTANCE_THRESHOLD = 20;

    private final GameEngine engine;

    public CatAI(GameEngine engine) {
        this.engine = engine;
    }

    /**
     * 获取 dot 在六边形方向 dir 上的相邻点。
     */
    public Point getNeighbour(Point dot, int dir) {
        int x = dot.getX();
        int y = dot.getY();
        boolean isOddRow = (y % 2 != 0);

        switch (dir) {
            case DIR_LEFT:
                return engine.getDot(x - 1, y);
            case DIR_TOP_LEFT:
                return isOddRow ? engine.getDot(x, y - 1) : engine.getDot(x - 1, y - 1);
            case DIR_TOP_RIGHT:
                return isOddRow ? engine.getDot(x + 1, y - 1) : engine.getDot(x, y - 1);
            case DIR_RIGHT:
                return engine.getDot(x + 1, y);
            case DIR_BOTTOM_RIGHT:
                return isOddRow ? engine.getDot(x, y + 1) : engine.getDot(x + 1, y + 1);
            case DIR_BOTTOM_LEFT:
                return isOddRow ? engine.getDot(x - 1, y + 1) : engine.getDot(x, y + 1);
            default:
                return null;
        }
    }

    /**
     * 获取 one 在方向 dir 上的可达距离。
     * 正值表示可达边缘，负值表示被阻挡。
     */
    public int getDistance(Point one, int dir) {
        if (engine.inEdge(one)) {
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
            if (engine.inEdge(next)) {
                return distance + 1;
            }
            distance++;
            current = next;
        }
    }

    /**
     * 评估猫的下一步移动，返回目标点。返回 null 表示猫被困住（玩家获胜）。
     */
    public Point evaluateMove(Point cat) {
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
            return null; // 玩家获胜
        }

        if (available.size() == 1) {
            return available.get(0);
        }

        Point best = null;
        if (!direct.isEmpty()) {
            // 有通向边缘的路径，选距离最短的
            int min = ESCAPE_DISTANCE_THRESHOLD;
            for (Point p : direct) {
                if (engine.inEdge(p)) {
                    return p;
                }
                int dist = getDistance(p, directionMap.get(p));
                if (dist < min) {
                    min = dist;
                    best = p;
                }
            }
        } else {
            // 没有通向边缘的路径，选被阻挡距离最短的
            int max = 1;
            for (Point p : available) {
                int dist = getDistance(p, directionMap.get(p));
                if (dist < max) {
                    max = dist;
                    best = p;
                }
            }
        }
        return best;
    }
}
