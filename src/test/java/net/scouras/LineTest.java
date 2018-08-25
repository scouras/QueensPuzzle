package net.scouras;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class LineTest {

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
  public void testDiagonal() {

    System.out.println();
    Line line = new Line(N, queens[0], queens[1]);

    assertEquals(1,   line.dx);
    assertEquals(1,   line.dy);
    assertEquals(0,   line.minX);
    assertEquals(N-1, line.maxX);
    assertEquals(0,   line.minY);
    assertEquals(N-1, line.maxY);

    assertEquals(line.a, 1.0, delta);
    assertEquals(line.b, 0.0, delta);

    assertTrue(line.isColinear(queens[1]));
    assertTrue(line.isColinear(queens[2]));
    assertFalse(line.isColinear(queens[3]));
    assertTrue(line.isColinear(queens[4]));

  }

  @Test
  public void testPositive() {
    System.out.println();

    Line line = new Line(N, queens[2], queens[3]);

    assertEquals(line.minX, 5);
    assertEquals(line.minY, 0);
    assertEquals(line.maxX, 54);
    assertEquals(line.maxY, 98);

    assertFalse(line.isColinear(queens[1]));
    assertTrue (line.isColinear(queens[2]));
    assertTrue (line.isColinear(queens[3]));
    assertFalse(line.isColinear(queens[4]));
  }

  @Test
  public void testNegative() {
    System.out.println();

    Line line = new Line(N, queens[5], queens[6]);

    assertEquals(line.minX, 0);
    assertEquals(line.minY, 0);
    assertEquals(line.maxX, 30);
    assertEquals(line.maxY, 30);
    assertEquals(line.startX, 0);
    assertEquals(line.startY, 30);

    assertFalse(line.isColinear(queens[1]));
    assertFalse(line.isColinear(queens[2]));
    assertFalse(line.isColinear(queens[3]));
    assertTrue (line.isColinear(queens[4]));
  }

  @Test
  public void testNullPointer() {
    System.out.println();

    Queen q1 = queens[7];
    Queen q2 = queens[8];
    System.out.format("q1 [%d,%d]  q2 [%d,%d]\n",
        q1.x, q1.y, q2.x, q2.y);

    Line line = new Line(N, q1, q2);

    assertEquals(line.minX, 0);
  }
}
