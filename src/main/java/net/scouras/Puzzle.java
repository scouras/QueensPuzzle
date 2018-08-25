package net.scouras;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import static net.scouras.Board.QueenStatus;

public class Puzzle {

  public static long NANO_START = System.nanoTime();
  public static long getElapsedNanos() {
    return System.nanoTime() - NANO_START;
  }
  public static long getElapsedMillis() {
    return getElapsedNanos() / 1000 / 1000;
  }
  static double getElapsedSeconds() {
    return (double) (getElapsedNanos() / 1000 / 1000 / 1000);
  }

  private static final Logger LOG = LogManager.getLogger();




  /**
   * Size of board to solve
   */
  public static final int N = 50;

  /**
   * Strategies for optimizing Queen placement.
   *
   * NAIVE: brute-force ordered approach
   * HERMIT: value isolation, distance from last Queen
   * STALKER: value the last Queen
   * RANDOM: Monte Carlo can't save you now
   */
  public enum Strategy {NAIVE, HERMIT, STALKER, RANDOM}

  /** Preferred global strategy */
  Strategy strategy = Strategy.RANDOM;

  public static void main(String[] args) {
    LOG.info("Creating New Board");
    Puzzle P = new Puzzle();
    Board B = new Board(N);
    LOG.info("Solving!");
    QueenStatus status = P.solve(B);

    B.display(true);
    if (status == QueenStatus.COMPLETE) {
      status = B.validate(true);
      if (status == QueenStatus.COMPLETE) {
        LOG.info("!! COMPLETED !!");
      } else {
        throw new RuntimeException("Re-validation Failed");
      }
    } else {
      LOG.info("!! FAILED TO FIND ANY SOLUTION!!");
    }
  }

  /** Reorder rows to optimize the given strategy */
  public void prioritize(Strategy strategy, Board B, List<Integer> ys) {
    LOG.trace("prioritize {}", strategy);

    final int sign = strategy == Strategy.HERMIT ? -1 : 1;
    switch(strategy) {
      case NAIVE:
        break;
      case RANDOM:
        Collections.shuffle(ys);
        break;
      case HERMIT:
      case STALKER:
        // For first queen, improvise
        if (B.queens.size() == 0) {
          prioritize(Strategy.RANDOM, B, ys);
          break;
        }
        Queen lastQueen = B.queens.peek();
        int lastY = lastQueen.y;
        ys.sort(Comparator.comparingInt(y -> sign * Math.abs(y - lastY)));
        break;
    }
  }

  public QueenStatus solve(Board B) {
    int x = B.getCurrentX();
    LOG.trace("solve {}", x);

    QueenStatus status = QueenStatus.UNKNOWN;

    // generate candidate placements and evaluate
    List<Integer> ys = B.getFreeY();
    if (ys.size() == 0) {
      LOG.debug(".. No valid positions, backtracking");
      return QueenStatus.BAD;
    }
    prioritize(strategy, B, ys);

    for (Integer y1 : ys) {
      int y = y1;
      Queen q = new Queen(B.N, x, y);
      B.push(q);
      if (B.isFull()) { return QueenStatus.COMPLETE; }
      status = solve(B);

      switch (status) {
        case COMPLETE:
          return status;
        case BAD:
          B.pop();
          continue;
      }

      B.display(true);
      throw new RuntimeException(
          String.format("Bad status? %s", status));

    }
    LOG.debug(" -- {} - Surfacing w/ status {}", x, status);
    return status;
  }
}

