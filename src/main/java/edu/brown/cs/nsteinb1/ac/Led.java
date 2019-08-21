package edu.brown.cs.nsteinb1.ac;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods to find Levenshtein edit distance between two strings.
 *
 * @author nicole
 *
 */
final class Led {

  private Led() {
  }

  /**
   * Takes into two strings and returns the LED between them.
   *
   * @param a
   *          string 1
   * @param b
   *          string 2
   * @return led
   */
  public static int getLed(String a, String b) {
    // If strings are equal, no edits are needed.
    if (a.equals(b)) {
      return 0;
    }

    int n = a.length();
    int m = b.length();

    // If comparing to the empty string, return the length of the non-empty
    // string - the led would be that many insertions.
    if (n == 0) {
      return m;
    } else if (m == 0) {
      return n;
    }

    // Construct a matrix containing m rows and n columns.
    List<List<Integer>> matrix = new ArrayList<>();

    // Add n columns, initializing the top row with 0...n.
    for (int i = 0; i <= n; i++) {
      List<Integer> col = new ArrayList<>();
      col.add(i);
      matrix.add(col);
    }

    // Initialize first column with 0...m.
    for (int j = 1; j <= m; j++) {
      matrix.get(0).add(j);
    }

    // Compare each character of a and b.
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < m; j++) {
        int cost = 0;
        // If characters are equal, cost is 0. If not, cost is 1.
        if (a.charAt(i) != b.charAt(j)) {
          cost = 1;
        }

        // Set matrix[i, j] to the minimum of its previous neighboring cells.
        matrix.get(i + 1).add(returnMinCell(i + 1, j + 1, cost, matrix));

      }
    }

    return matrix.get(n).get(m);
  }

  /**
   * Return the minimum of the cell above + 1, the cell to the left + 1, and the
   * cell diagonally above and to the left + cost.
   *
   * @param i
   *          row
   * @param j
   *          col
   * @param cost
   *          cost
   * @param matrix
   *          matrix for comparing strings
   * @return the minimum
   */
  private static int returnMinCell(int i, int j, int cost,
      List<List<Integer>> matrix) {

    // cell above
    int a = matrix.get(i - 1).get(j) + 1;

    // cell to the left
    int b = matrix.get(i).get(j - 1) + 1;

    // cell diagonally up and left
    int c = matrix.get(i - 1).get(j - 1) + cost;

    return Math.min(a, Math.min(b, c));
  }

}
