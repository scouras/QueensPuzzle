package net.scouras;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;
//import net.scouras.Queen;

public class QueenTest {

    @Test
    void newQueenData() {
        Queen q = new Queen(3, 1, 2);
        assertEquals(q.N, 3);
        assertEquals(q.x, 1);
        assertEquals(q.y, 2);
        assertEquals(q.p, 1);
        assertEquals(q.n, 3);

        assertArrayEquals(q.ps, new Integer[]{1, 2, null});
        assertArrayEquals(q.ns, new Integer[]{null, 2, 1});

        System.out.println(Arrays.toString(q.ps));

        System.out.println(Arrays.toString(q.ns));
    }
}
