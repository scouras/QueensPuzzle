package net.scouras;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * This class is mostly a dumping ground for code I'll probably eliminate.
 */
public class Utils {




  /** OLD SOLVER */

  /*
  public Board.QueenStatus solve(Board B) {
    int x = B.getCurrentX();
    LOG.trace("solve {}", x);

    Board.QueenStatus status = Board.QueenStatus.UNKNOWN;

    // generate candidate placements and evaluate
    List<Integer> ys = B.getFreeY();
    if (ys.size() == 0) {
      LOG.debug(".. No valid positions, backtracking");
      return Board.QueenStatus.BAD;
    }
    prioritize(strategy, B, ys);

    for (Integer y1 : ys) {
      int y = y1;
      Queen q = new Queen(B.N, x, y);
      B.push(q);
      status = B.validate();
      LOG.debug(" .. validation was {}", status);
      if (status == Board.QueenStatus.BAD) {
        LOG.error("Got bad status");
        System.exit(255);
      }

      // promising so far, next queen
      if (status == Board.QueenStatus.GOOD) {
        status = solve(B);
      }

      // bad push, undo and move on
      if (status == Board.QueenStatus.BAD) {
        B.pop();
        continue;
      }

      // double check with complete validation
      if (status == Board.QueenStatus.COMPLETE) { return status; }

      B.display(true);
      throw new RuntimeException(
          String.format("Bad status? %s", status));

    }
    LOG.debug(" -- {} - Exiting w/ status {}", x, status);
    return status;
  }
  */


  /**
   * Check all constraints from Queens to find playable rows in the current
   * column.
   */

  /*
  void computeFreeY(int boo) {
    badY = new BitSet(N);

    // Check Attack Vectors From Each Queen
    for (Queen q : queens) {

      badY.set(q.y);  // horizontal

      Integer p = q.ps[x]; // diagonal up
      if (!isNull(p)) {
        badY.set(p);
      }

      Integer n = q.ns[x]; // diagonal down
      if (!isNull(n)) {
        badY.set(n);
      }
    }

    // TODO: Check Colinearity


    // Reproduce as just the available placements
    freeY = new ArrayList<>();
    for (int i = 0; i < N; i++) {
      if (!badY.get(i)) {
        freeY.add(i);
      }
    }
  }
  */


  /**
   * Natural log of the factorial function, to estimate number of permutations
   * <p>
   * This goes to Infinity around fac(442), so could probably be shrunk more.
   * Returns a window (size) of the last few counts, for estimating progress as
   * the leftmost columns progress.
   *
   * @param N
   * @param size
   * @return

  /* TODO: Implement progress estimation if it becomes needed again
  double positions;
  double[] subpositions;
  int WINDOW = 5;

  double[] logPermutations(int N, int size) {

  double[] p = new double[size];

  double v = 1.0;
  double w = 1.0;
  for (int i = 2; i <= N; i++) {
  w = Math.log(i);
  v *= w;
  //out.format("i: %8d   w: %8.4f   v: %20.4f\n", i, w, v);
  }

  p[0] = v;
  for (int i = 1; i < size; i++) {
  v /= Math.log(N - i);
  p[i] = v;
  }
  return p;

  }
   */

  /*
  @Test
  void testFactorial() {

    int N = 1000;
    int size = 10;
    Board b = new Board(N);
    double[] p = b.logPermutations(N, size);

    System.out.format("Permutations N: %d  Size: %d    %s\n",
        N, size, Arrays.toString(p));
  }
  */



  /**
   * Get list of all playable rows (y) for the current column.
   *
   * @return list of rows
   */

  /*
  List<Integer> getAllFreeY() {
    ArrayList<Integer> rows = new ArrayList<>();
    for (int i = x + 1; i < N; i++) {
      for (int j = 0; j < N; j++) {
        if (attacks[i][j] == 0) {
          if (!rows.contains(i)) {
            rows.add(i);
          }
        }
      }
    }
    return rows;
  }
  */


  /** Deprecated until improved
   * O(n^2) implementation is much slower than brute force,
   * especially as N grows past 100.
   */

  /* Originally part of Board.validate()
    if (false) {
    List<Integer> rows = getAllFreeY();
    if (rows.size() < (N - x - 1)) {
      LOG.debug("Insufficient rows ({}) for queens ({})",
          rows.size(), x);
      return Board.QueenStatus.BAD;
    }
  }
  */




}
