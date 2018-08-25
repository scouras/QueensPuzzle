package net.scouras;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

public class BoardTest {

    double delta = 1e-10;
    int N = 100;

    int[][] points = new int[][]{
        { 0,  0}, // 0
        { 5,  5}, // 1
        {10, 10}, // 2
        {15, 20}, // 3
        {15, 15}, // 4
        {10, 20}, // 5
        {20, 10}, // 6
        { 0,  0}, // 7
        { 1,  3}, // 8

    };

    Queen[] queens;

    @Before
    public void setup() {
        List<Queen> _queens = new ArrayList<>();
        for (int[] p : points) {
            Queen q = new Queen(N, p[0], p[1]);
            _queens.add(q);
        }

        queens = _queens.toArray(new Queen[0]);
    }

    @Test
    void newEmptyBoard() {
        Board b = new Board(5);

        assertEquals(b.N, 5);
        assertEquals(b.getCurrentX(), 0);

        List<Integer> rows = b.getFreeY();
        assertEquals(rows.size(), 5);
        System.out.format("Free Rows: %s", rows.toString());
    }





}
