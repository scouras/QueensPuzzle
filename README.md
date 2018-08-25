Introduction
============

Since this is a public git repo for a semi-private project, I'll avoid saying too much here. Hopefully helps avoid having the challenge problem show up with Google search.


Execution
=========

The project is managed in [Gradle](https://gradle.org/). The following command *should* work magic:
* download Gradle
* detect your Java installation
* download dependencies
* build the project
* execute Puzzle with a standard board size of 8x8

`./gradlew run --args 8`

If you prefer your IDE, the board size can be configured by setting `Puzzle.N = 10` in `Puzzle.java`.

At current levels of optimization, N=50 is reliably solved within seconds to minutes. N=100 has yet to complete. [Somewhere in between](https://en.wikipedia.org/wiki/Argument_to_moderation) lies [exponentiation](https://en.wikipedia.org/wiki/Exponentiation#Power_functions).


Output
======

The current board is printed to the console periodically (every 5 seconds), or when interesting events happen (i.e. new max depth achieved).  The status line includes a timestamp, elapsed time, count of board positions evaluated, and current/maximum number of queens placed.

The diagram shows the placement of each Queen (QXX) and the approximate level of attack on each empty square (increasing as `. + * X #` up to 5 attacks). To avoid spamming your terminal board display is disabled when `N > MAX_DISPLAY_BOARD` (default 100).

When (if) the program finishes, a complete reevaluation is performed, and the status is printed. Only board sizes 2 and 3 have no solution, at least for the standard problem. N=5 and 6, and likely others, have no solution in the extended problem (according to this program, anyway).


```
# In Progress...

[06:38:58.589] (1.000s)  |  4 raw  |  Queens: 4 / 4


      |  0  1  2  3  4  5  6  7
  ____|_________________________
    0 | Q00 .  .  .  .  +  .  .
    1 |  .  .        .        .
    2 |     .  .  .        .  .
    3 |  +  . Q02 +  .  +  +  .
    4 |     +     .  +  .     .
    5 |  .     .  .  +  +
    6 |  +  .  + Q03 +  +  +  .
    7 |  . Q01 +  .  +  .  +  +
 ```

```
# Solved!

[06:38:58.641] (1.000s)  |  139 raw  |  Queens: 8 / 8


      |  0  1  2  3  4  5  6  7
  ____|_________________________
    0 |  +  +  *  +  + Q05 *  +
    1 |  .  *  + Q03 *  *  *  +
    2 | Q00 .  *  *  *  *  .  *
    3 |  .  *  +  * Q04 +  *  .
    4 |  *  +  *  +  +  *  + Q07
    5 |  * Q01 +  +  +  *  *  *
    6 |  +  *  +  +  +  + Q06 +
    7 |  +  . Q02 +  +  *  +  +
 ```

```
# Interesting patterns emerge with larger boards

[06:58:54.022] (1.000s)  |  10 raw  |  Queens: 10 / 10


      |  0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
  ____|____________________________________________________________________________
    0 |  +  .  .  +  +  . Q06 +  .  .  .  .  +  *  .  *  .  .  +  .  .  *  +  +  +
    1 |     .     .  .  .     .           .  .     .              .  .     .  .  .
    2 |  .  . Q02 .  +  +  +  .  +  .  *  *  +  +  .  *  +  +  .  +  .  +  +  +  .
    3 |  .  .     +        .        +  .     .        .        .     .  .  .     .
    4 |  .  .  .     .     .  .  .  .  +  +  .  +           .     .  .  .     +
    5 |     .  .        .     .  +     +  +     .        .     .  .  .     .
    6 |  +  .  .  +  .  .  *  +  . Q09 +  .  +  .  .  +  .  +  +  +  .  +  .  .  .
    7 |  .           .  .  .  .  .     .        .  .     .  .  .     .
    8 |  +  +  .  .  + Q05 .  +  +  *  +  *  .  +  +  *  +  +  +  +  .  .  .  .  .
    9 |     .  .  .  .     +        .  .     *     .  +  .     .
   10 |        +  +     .  .  .  .     .  +     +  .  .  +  .
   11 |     .  .  .  +           +  .  .  .  .  .  +     .  .
   12 | Q00 +  .  +  +  +  .  *  .  *  .  +  X  *  .  *  .  .  +  .  .  .  .  .  .
   13 |  .  .  .        .  .     .     +  .  .  .  .     .        .
   14 |  .  +  +  .  .  .  * Q07 +  +  +  *  .  *  +  .  .  +  .  .  +  .  .  +  .
   15 |  .        .        .  .  +  .  .     +        .        .        .
   16 |  .  .  .  .  +  *  .  + Q08 X  .  +  .  +  *  .  +  .  .  +  .  .  +  .  .
   17 |  .           .  .  .  .  +  .  *           .        .        .     .  .
   18 |     .     .  .  .  +  .     +  +  .  .        +        .  .     +        .
   19 |  .     +     .  .  .  .  .        .  .     .     +        .        .
   20 |  .  *  . Q03 +  +  .  +  +  +  +  +  +  *  .  .  *  +  .  .  +  .  .  +  .
   21 |  .     +  .  +     .        .  .        .  .           +        .        .
   22 |     .  .  +     +     .        .  .  .     .  .     .     .  .     .
   23 |  *  +  +  . Q04 .  +  .  .  .  +  *  .  .  .  +  +  .  .  .  +  .  +  +  .
   24 |  + Q01 .  +  .  +  .  +  .  +  .  +  *  +  .  .  +  +  +  .  .  +  .  .  *
```


Implementation Notes
====================

Overall architecture is one of [Constraint Satisfaction](https://en.wikipedia.org/wiki/Constraint_satisfaction_problem) with aggressive [dead-end elimination](https://en.wikipedia.org/wiki/Dead-end_elimination). Queens are placed one at a time, starting in the left hand column and progressing to the right. As each queen is placed, attack vectors are calculated and added to the board. For the next column, only rows with 0 attacks are evaluated as options. If there are no options (or all have failed), the last queen retreats and her attacks are subtracted.

Smart Queen placement is critical, so there are 4 strategies implemented to prioritize which position to try first. NAIVE (enumerated order), HERMIT (furthest from the previous Queen), STALKER (closest...), and RANDOM. It turned out that, for any significant N, RANDOM was the fastest by orders of magnitude. Even if the others could optimize local placement, they soon fell into very deep traps. Detecting and escaping these traps, perhaps by switching strategies at the right time, is difficult and beyond the scope of the project.

A small test suite is included, mostly created while debugging. It is by no means comprehensive at this time.


