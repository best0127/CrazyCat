package ndnu.tdy.CreazyCat.View;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class CatAITest {

    private GameEngine engine;
    private CatAI ai;

    @Before
    public void setUp() {
        // 8x8 网格，rand=4，16个障碍物
        engine = new GameEngine(8, 8, 4);
        ai = new CatAI(engine);
    }

    @Test
    public void testGetNeighbour_left() {
        engine.initGame();
        Point cat = engine.getCat();
        // 猫在 (3,3)，左边应该是 (2,3)
        Point left = ai.getNeighbour(cat, CatAI.DIR_LEFT);
        assertNotNull(left);
        assertEquals(2, left.getX());
        assertEquals(3, left.getY());
    }

    @Test
    public void testGetNeighbour_right() {
        engine.initGame();
        Point cat = engine.getCat();
        Point right = ai.getNeighbour(cat, CatAI.DIR_RIGHT);
        assertNotNull(right);
        assertEquals(4, right.getX());
        assertEquals(3, right.getY());
    }

    @Test
    public void testGetNeighbour_outOfBounds() {
        engine.initGame();
        // 猫在 (3,3)，尝试获取 (3,3) 左边的左边的左边的左边
        Point cat = engine.getCat();
        // 创建一个在边缘的点
        Point edgePoint = engine.getDot(0, 3);
        Point left = ai.getNeighbour(edgePoint, CatAI.DIR_LEFT);
        assertNull(left); // 越界应返回 null
    }

    @Test
    public void testGetDistance_atEdge() {
        engine.initGame();
        Point edgePoint = engine.getDot(0, 0);
        int dist = ai.getDistance(edgePoint, CatAI.DIR_LEFT);
        assertEquals(1, dist); // 在边缘返回 1
    }

    @Test
    public void testGetDistance_blocked() {
        // 创建一个简单的测试场景：5x5 网格
        GameEngine testEngine = new GameEngine(5, 5, 5);
        CatAI testAi = new CatAI(testEngine);
        testEngine.initGame();

        // 手动设置猫在中心 (2,2)
        Point cat = testEngine.getDot(2, 2);
        cat.setStatus(Point.STATUS.STATUS_IN);

        // 在 (3,2) 放障碍物
        Point obstacle = testEngine.getDot(3, 2);
        obstacle.setStatus(Point.STATUS.STATUS_ON);

        // 从 (2,2) 向右应该被阻挡（距离为 0 或负值）
        int dist = testAi.getDistance(cat, CatAI.DIR_RIGHT);
        assertTrue("Distance should be <= 0 when blocked, got: " + dist, dist <= 0);
    }

    @Test
    public void testEvaluateMove_catCanMove() {
        engine.initGame();
        Point cat = engine.getCat();
        Point nextMove = ai.evaluateMove(cat);
        // 猫应该能移动到某个位置
        assertNotNull(nextMove);
    }

    @Test
    public void testEvaluateMove_catTrapped() {
        engine.initGame();
        Point cat = engine.getCat();
        // 围住猫的所有邻居
        for (int i = 1; i <= 6; i++) {
            Point n = ai.getNeighbour(cat, i);
            if (n != null) {
                n.setStatus(Point.STATUS.STATUS_ON);
            }
        }
        Point nextMove = ai.evaluateMove(cat);
        assertNull(nextMove); // 猫被困住应返回 null
    }

    @Test
    public void testInEdge() {
        engine.initGame();
        assertTrue(engine.inEdge(engine.getDot(0, 0)));   // 左上角
        assertTrue(engine.inEdge(engine.getDot(7, 7)));   // 右下角
        assertTrue(engine.inEdge(engine.getDot(0, 4)));   // 左边缘
        assertFalse(engine.inEdge(engine.getDot(3, 3)));  // 中间
        assertFalse(engine.inEdge(engine.getDot(4, 4)));  // 中间
    }
}
