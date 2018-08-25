package net.scouras;

import org.junit.Test;
import static org.junit.Assert.*;

public class PuzzleTest {

    @Test
    void testSolve() {
        System.out.println("Creating net.scouras.Puzzle");
        Puzzle P = new Puzzle();
        System.out.println("Creating net.scouras.Board");
        Board B = new Board(25);
        System.out.println("Solving!");
        P.solve(B);
        B.display(true);
    }
}

// Attacks Matrix
// N: 30 - 37.5 s
// N: 25 - 904 ms
// N; 50 - >12 hours



