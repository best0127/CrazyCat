package ndnu.tdy.CreazyCat.View;

import org.junit.Test;
import static org.junit.Assert.*;

public class PointTest {

    @Test
    public void testConstructor() {
        Point p = new Point(3, 5);
        assertEquals(3, p.getX());
        assertEquals(5, p.getY());
        assertEquals(Point.STATUS.STATUS_OFF, p.getStatus());
    }

    @Test
    public void testSetStatus() {
        Point p = new Point(0, 0);
        p.setStatus(Point.STATUS.STATUS_ON);
        assertEquals(Point.STATUS.STATUS_ON, p.getStatus());

        p.setStatus(Point.STATUS.STATUS_IN);
        assertEquals(Point.STATUS.STATUS_IN, p.getStatus());

        p.setStatus(Point.STATUS.STATUS_OFF);
        assertEquals(Point.STATUS.STATUS_OFF, p.getStatus());
    }

    @Test
    public void testSetXY() {
        Point p = new Point(1, 2);
        p.setXY(10, 20);
        assertEquals(10, p.getX());
        assertEquals(20, p.getY());
    }
}
