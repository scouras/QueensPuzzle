package net.scouras;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/**
 * Attribute and data cache for each queen
 */
public class Queen {

  private static final Logger LOG = LogManager.getLogger();

  /** board size */
  public final int N;

  // Coordinates / Attack Vectors
  /** x-coordinate, or vertical column */
  public final int x;
  /** y-coordinate, or horizontal row */
  public final int y;
  /** y-intercept of positive diagonal vector (Y=+X+P) */
  public final int p;
  /** y-intercept of negative diagonal vector (Y=-X+N) */
  public final int n;

  /** +/- diagonal y coordinates for each x, while on board */
  public final Integer[] ps;
  public final Integer[] ns;

  ArrayList<Line> lines = new ArrayList<>();

  /**
   * Queens are parameterized exclusively by their x,y coordinates
   *
   * @param N size of board
   * @param x vertical column (0-indexed)
   * @param y horizontal row (0-indexed)
   */
  public Queen(int N, int x, int y) {
    this.N = N;
    this.x = x;
    this.y = y;
    this.p = this.y - this.x;  // y = +1 * x + p
    this.n = this.x + this.y;  // y = -1 * x + n

    this.ps = new Integer[N];
    this.ns = new Integer[N];
    for (int i = 0; i < N; i++) {
      int p = this.p + i;
      int n = this.n - i;
      if (p >= 0 && p < N) {
        this.ps[i] = p;
      }
      if (n >= 0 && n < N) {
        this.ns[i] = n;
      }
    }
  }
}




