package net.scouras;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static java.lang.System.out;
import static java.util.Objects.isNull;
import static net.scouras.Puzzle.getElapsedSeconds;


/**
 * NxN scalable "chess" board upon which the Queens are positioned.
 */
public class Board {

  private static final Logger LOG = LogManager.getLogger();

  /** Size of board in both x- and y- dimensions */
  int N;
  long processedRaw = 0;
  float processedPercent = 0.0f;


  /** First free column on the board */
  int x;

  /** Cache all cells attacked by each Queen */
  int[][] attacks;

  /** Rows with slots available to place queens */
  int[] availability;

  /** Queens placed on the board */
  Stack<Queen> queens = new Stack<>();

  /**
   * Create a square NxN board.
   *
   * @param N Number of rows and columns
   */
  Board(int N) {
    this.N = N;
    this.x = 0;
    this.attacks = new int[N][N];

    this.availability = new int[N];
    for (int i = 0; i < N; i++) {
      availability[i] = N;
    }
  }

  /** For tracking the current column */
  int getCurrentX() {
    return x;
  }

  /**
   * Get list of all playable rows (y) for the current column.
   *
   * @return list of rows
   */
  List<Integer> getFreeY() {
    ArrayList<Integer> rows = new ArrayList<>();
    for (int i = 0; i < N; i++) {
      if (attacks[x][i] == 0) {
        rows.add(i);
      }
    }
    return rows;
  }


  public enum QueenStatus {COMPLETE, GOOD, UNKNOWN, BAD}

  /**
   * Add a new queen and cache her attack vectors.
   */
  void push(Queen queen) {
    LOG.trace("++ Pushing Queen [{},{}]", queen.x, queen.y);

    // Standard chess attacks
    for (int _x = 0; _x < N; _x++) {
      attacks[_x][queen.y]++;
      if (!isNull(queen.ps[_x])) {
        attacks[_x][queen.ps[_x]]++;
      }
      if (!isNull(queen.ns[_x])) {
        attacks[_x][queen.ns[_x]]++;
      }
    }

    // Colinearity attacks
    for (Queen q : queens) {
      Line line = new Line(N, q, queen);
      queen.lines.add(line);

      int _x = x;
      int _y = line.getY(_x);

      while (_x <= line.maxX && _y <= line.maxY) {
        attacks[_x][_y]++;
        _x += line.dx;
        _y += line.dy;
      }
    }

    // Advance to next column.
    queens.push(queen);
    x++;
    processedRaw++;
    display();
  }

  /** Remove a failed position and clear attack vectors. */
  void pop() {
    x--;
    LOG.trace("-- popping {}", x);
    Queen queen = queens.pop();
    for (int _x = 0; _x < N; _x++) {
      attacks[_x][queen.y]--;
      if (!isNull(queen.ps[_x])) {
        attacks[_x][queen.ps[_x]]--;
      }
      if (!isNull(queen.ns[_x])) {
        attacks[_x][queen.ns[_x]]--;
      }
    }

    // Colinearity attacks
    for (Line line : queen.lines) {
      int _x = x;
      int _y = line.getY(_x);

      while (_x <= line.maxX && _y <= line.maxY) {
        attacks[_x][_y]--;
        _x += line.dx;
        _y += line.dy;
      }
    }
  }

  /**
   * Display an ascii current state of the board.
   * <p>
   * This method is "intelligently" rate limited (see DISPLAY_INTERVAL), so can
   * be spammed in an inner loop. It incorporates triggers to detect when
   * something interesting happened, and warrants a display. Additionally, it
   * can be forced to display if necessary. Neither delays the next rate-limited
   * display.
   *
   * @param force Override the DISPLAY_INTERVAL rate limit
   */


  int MAX_DISPLAY_BOARD = 100;
  Duration DISPLAY_INTERVAL = Duration.ofSeconds(5);
  LocalDateTime displayNext = LocalDateTime.now();
  /** Track the most Queens placed and displayed to the user */
  int mostQueensSeen = 0;

  public void display(Boolean force) {

    LocalDateTime now = LocalDateTime.now();
    if (queens.size() > mostQueensSeen) {
      mostQueensSeen = queens.size();
      force = true;
    }

    // Determine if rate limits apply
    Boolean limit = true;
    if (force) {
      limit = false;
    } else if (now.isAfter(displayNext)) {
      displayNext = now.plus(DISPLAY_INTERVAL);
      limit = false;
    }
    if (limit) return;

    // Output!
    String str = toString();
    out.print(str);
  }

  /** Normally used display */
  public void display() {
    display(false);
  }


  public String toString() {

    LocalDateTime now = LocalDateTime.now();
    double elapsed = getElapsedSeconds();

    StringBuilder sb = new StringBuilder();
    Formatter F = new Formatter(sb, Locale.US);

    // Start with some progress stats
    F.format("[%1$tT.%1$tL] (%2$.3fs)", now, elapsed);
    F.format("  |  %,d raw  |  %.2f%% ", processedRaw, processedPercent);
    F.format("  |  Queens: %d / %d\n", queens.size(), mostQueensSeen);
    if (N > MAX_DISPLAY_BOARD) {
      return sb.toString();
    }

    // Top Header
    String[] attackStrings = {" ", "", "+", "*", "X", "#"};

    F.format("\n\n%5s | ", "");
    for (int j = 0; j < N; j++) {
      F.format("%2d ", j);
    }
    F.format("\n");
    F.format("  ____|_");
    for (int j = 0; j < N; j++) {
      F.format("___");
    }
    F.format("\n");

    String c;
    int atk;
    for (int _y = 0; _y < N; _y++) {
      F.format("%5d | ", _y);
      for (int _x = 0; _x < N; _x++) {
        if (queens.size() > _x && queens.get(_x).y == _y) {
          c = String.format("Q%02d", _x);
        } else {
          atk = attacks[_x][_y];
          if (atk > 5) {
            atk = 5;
          }
          c = " " + attackStrings[atk] + " ";
        }
        F.format("%3s", c);
      }
      F.format("\n");
    }
    F.format("\n");

    return sb.toString();
  }

  /** Is the board full of queens */
  Boolean isFull() { return queens.size() == N; }

  /**
   * Check if the last queen broke constraint or heuristic restrictions
   *
   * Validation Tests
   * ----------------
   * 1 queen per column
   * 1 queen per row
   * 1 queen per +diagonal
   * 1 queen per -diagonal
   * no queens colinear
   */

  QueenStatus validate() { return validate(false); }

  QueenStatus validate(Boolean complete) {

    if (complete) {
      LOG.info("Performing COMPLETE Validation");
      Queen qi, qj;
      for (int i = 0; i < queens.size(); i++) {
        qi = queens.get(i);

        ///// Queens in correct columns
        if (qi.x != i) {
          LOG.warn("Queen %d in wrong column %d", i, qi.x);
          return QueenStatus.BAD;
        }

        for (int j = i + 1; j < queens.size(); j++) {
          qj = queens.get(j);

          ///// Every column filled
          if ((j - i) == 1) {
            if (qj.x - qi.x != 1) {
              LOG.warn("Queens %d and %d are not adjacent, skipping column %d",
                  i, j, qi.x);
              return QueenStatus.BAD;
            }
          }

          ///// Check x & y
          if (qi.x == qj.x) {
            LOG.warn("Queens %d and %d in the same column %d", i, j, qi.x);
            return QueenStatus.BAD;
          }
          if (qi.y == qj.y) {
            LOG.warn("Queens %d and %d in the same row %d", i, j, qi.y);
            return QueenStatus.BAD;
          }

          ///// Check Diagonals
          if (qi.p == qj.p) {
            LOG.warn("Queens %d and %d along positive diagonal %d", i, j, qi.p);
            return QueenStatus.BAD;
          }
          if (qi.n == qj.n) {
            LOG.warn("Queens %d and %d along negative diagonal %d", i, j, qi.n);
            return QueenStatus.BAD;
          }
        }
      }
    }

    if (isFull()) {
      LOG.info("-- Validated COMPLETE Board Sucessfully! :)");
      return QueenStatus.COMPLETE;
    } else {
      LOG.info("-- Board is GOOD");
      return QueenStatus.GOOD;
    }
  }
}
