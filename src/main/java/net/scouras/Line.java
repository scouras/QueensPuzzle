package net.scouras;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigInteger;

/**
 * Line defined by a pair of queens
 */
public class Line {

  private static final Logger LOG = LogManager.getLogger();

  int N;
  Queen q1;
  Queen q2;

  /**
   * Floating point representation
   * y = ax + b
   */
  float a;
  float b;

   /**
    * Integer representation
    * dx & dy are rise/run implementations
    */
  int dx, dy;
  int remX, remY; // x-remainder, offset from x=0 for iteration

  /** [x,y] limits of the line segment as it crosses the board */
  int minX, minY;
  int maxX, maxY;

  /** [x,y] coordinates of the first and last points on the line segment */
  int startX, startY;
  int endX, endY;

  public Line(int N, Queen q1, Queen q2) {
    LOG.trace("Line N: {} Q1: <{},{}>  Q2:<{},{}>", N, q1.x, q1.y, q2.x, q2.y);
    this.N = N;
    this.q1 = q1;
    this.q2 = q2;

    // initial reference point will be modified later
    startX = q1.x;
    startY = q1.y;

    ///// Integer Representation
    // reduce by gcd so we can iterate by dx/dy without skipping columns
    // using BigInteger strictly for lossless GCD
    BigInteger _dx = BigInteger.valueOf(q2.x - q1.x);
    BigInteger _dy = BigInteger.valueOf(q2.y - q1.y);
    BigInteger gcd = _dx.gcd(_dy);
    LOG.debug("BIG dx {}   dy {}   gcd {}", _dx, _dy, gcd);
    dx = _dx.divide(gcd).intValueExact();
    dy = _dy.divide(gcd).intValueExact();

    if (dx == 0) throw new RuntimeException("Queens in the same column");
    if (dy == 0) throw new RuntimeException("Queens in the same row");
    remX = q1.x % dx;
    remY = q1.y % dy;
    LOG.debug("DX {} ({})  DY {} ({})", dx, remX, dy, remY);

    ///// Floating Point Representations
    a = (float)dy / (float)dx;
    b = (float)q1.y - (a * (float)q1.x);
    LOG.debug("y = {} x + {}", a, b);

    ///// Find segment of line that crosses the board
    // Start with maximal X boundaries
    minX = remX;
    minY = remY;
    maxX = (N - 1) - ((N - 1 - remX) % dx);
    maxY = (N - 1) - ((N - 1 - remY) % dy);
    //maxY = N - (Math.abs(dy)-remY);
    LOG.debug("vY <{},{}>  -  <{},{}>", minX, getY(minX), maxX, getY(maxX));
    LOG.debug("vX <{},{}>  -  <{},{}>", getX(minY), minY, getX(maxY), maxY);
    LOG.debug("steps {} {}", getStepsX(minX, maxX), getStepsY(minY, maxY));

    // Constrain X limits if line crosses off board
    if (a > 0) {
      if (minY > getY(minX)) { minX = getX(minY); }
      else                   { minY = getY(minX); }
      if (maxY < getY(maxX)) { maxX = getX(maxY); }
      else                   { maxY = getY(maxX); }
      startX = minX;
      startY = minY;
      endX = maxX;
      endY = maxY;
    } else {
      if (minY > getY(maxX)) { maxX = getX(minY); }
      else                   { minY = getY(maxX); }
      if (maxY < getY(minX)) { minX = getX(maxY); }
      else                   { maxY = getY(minX); }
      startX = minX;
      startY = maxY;
    }
    LOG.debug("Base <{},{}>  Min <{},{}>   Max <{},{}>",
        startX, startY, minX, minY, maxX, maxY);

    /*
    if (a < 0) { int _y=minY; minY = maxY; maxY = _y; }

    int minXsteps = Math.abs(getStepsX(q1.x, minX));
    int maxXsteps = Math.abs(getStepsX(q1.x, maxX));
    int minYsteps = Math.abs(getStepsY(q1.y, minY));
    int maxYsteps = Math.abs(getStepsY(q1.y, maxY));
    LOG.debug("Start Steps <{}.{}>   End Steps <{},{}>",
        minXsteps, minYsteps, maxXsteps, maxYsteps);

    if (minXsteps < minYsteps) { minY = getY(minX); }
    else                       { minX = getX(minY); }
    if (maxXsteps < maxYsteps) { maxY = getY(maxX); }
    else                       { maxX = getX(maxY); }

    LOG.debug("Start <{}.{}>   End <{},{}>", minX, minY, maxX, maxY);
    */
  }


  public Boolean isColinear(Queen queen) {
    return isColinear(queen.x, queen.y);
  }

  public Boolean isColinear(int x, int y) {
    return (a*x + b) == y;
  }

  /**
   * Solve for x/y, ignoring board constraints.
   * @param y column
   * @return row
   */
  float getX(float y) { return (y-b)/a; }
  float getY(float x) { return a*x + b; }


  /**
   * Solve for x/y, but only for valid board positions.
   * If x,y are off the board, or don't have an integer solution, return null.
   *
   * @param y 0-indexed column
   * @retun 0-index row, or null if no valid solution
   */

  Integer getX(int y) {
    LOG.trace("Line.getX({})    dy {}  y%dy {}  remY {}",
        y, dy, (y % dy), remY);
    if (y < minY) return null;
    if (y > maxY) return null;
    if ((y % dy) != remY) { LOG.debug(" -- not int"); return null; }

    Integer steps = getStepsY(startY, y);
    Integer x = startX + dx * steps;
    LOG.debug(" -- Start <{},{}>:  Steps: {}  X/Y: <{},{}>  ",
        startX, startY, steps, x, y);
    return x;
  }

  Integer getY(int x) {
    LOG.trace("Line.getY({})", x);
    if (x < minX) return null;
    if (x > maxX) return null;
    if ((x % dx) != remX) { LOG.debug(" -- not int"); return null; }

    Integer steps = getStepsX(startX, x);
    Integer y = startY + dy * steps;
    LOG.debug(" -- Start <{},{}>:  Steps: {}  X/Y: <{},{}>  ",
        startX, startY, steps, x, y);
    return y;
    //return startY + dy * getStepsX(startX, x);
  }

  /**
   * Number of steps between 2 locations
   * @param x1 column 1
   * @param x2 column 2
   * @return steps
   */
  int getStepsX(int x1, int x2) { return (x2-x1)/dx; }
  int getStepsY(int y1, int y2) { return (y2-y1)/dy; }

}
