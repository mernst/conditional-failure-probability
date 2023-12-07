package cfp;

import java.util.TreeSet;
import java.util.NavigableSet;
import java.util.Random;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** This program orders tests by conditional failure probability. */
public class OrderTests {

  /**
   * Order the given tests by conditional failure probability.
   *
   * @param args the command-line arguments: a single CSV file where each row records a test suite
   *     execution; each value is empty (the test was not run), pass (the test passed), or fail (the
   *     test failed)
   */
  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.printf("Wrong number of arguments %d, expected 1 CSV file.%n", args.length);
      System.exit(1); // failure status code
    }

    TestFailures tf = readCsv(args[0]);
    System.out.println(tf.toString());

    List<Integer> testOrdering = testIndexesInOrder(tf);

    System.out.println("Test ordering: " + testOrdering);
  }

  /**
   * Returns the order to run the tests in, based on conditional failure probability.
   *
   * @return the order to run the tests in, based on conditional failure probability
   */
  protected static List<Integer> testIndexesInOrder(TestFailures tf) {

    // This algorithm has complexity linear in the total number of tests, O(tf.size() *
    // tf.numTests()).
    // The reason is that each TestExecution is incremented into the summary once and each
    // TestExecution is decremented out of the summary once.

    int numTests = tf.getNumTests();

    // The output of the algorithm.  Each list element is the index of a test.
    // For marginally greater efficiency, this could be int[], since it does not need to contain null.
    List<Integer> result = new ArrayList<>(numTests);

    // Tests that have not yet bee added to `result`.
    NavigableSet<Integer> remaining = new TreeSet<>();
    for (int i = 0; i < numTests; i++) {
      remaining.add(i);
    }

    // This does not remove executions in which every test passed.
    for (int i = 0; i < numTests; i++) {
      int testIndex = tf.mostFailingTest();
      if (testIndex == -1) {
        break;
      }
      result.add(testIndex);
      remaining.remove(testIndex);
      tf.removeIfFailing(testIndex);
    }

    for (Integer i : remaining) {
      result.add(i);
    }

    return result;
  }

  /**
   * Reads a CSV file and produces a {@link TestFailures}.
   *
   * @param filename a CSV file name
   * @return a TestFailures corresponding to the CSV file contents
   */
  private static TestFailures readCsv(String filename) {

    List<TestExecution> testExecutions = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] values = line.split(","); // inefficient to repeatedly convert string to regex
        testExecutions.add(TestExecution.fromPassOrFailArray(values));
      }
    } catch (IOException e) {
      System.err.printf("Problem while reading " + filename + ": " + e.getMessage());
      System.exit(1); // failure
    }

    return new TestFailures(testExecutions);
  }
}
