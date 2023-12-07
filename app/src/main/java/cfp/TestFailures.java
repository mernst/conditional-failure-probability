package cfp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;
import java.util.StringJoiner;

/**
 * TestFailures represents a historical sequence of test executions. Each test may succeed, fail, or
 * not run.
 */
public class TestFailures {

  // TODO: Add a duration for each test.

  /**
   * The number of tests in each TestExecution. This is not the number of test executions in this
   * TestFailures object.
   */
  int numTests;

  /** The test executions. Each TestExecution has the same size: {@link #numTests}. */
  private LinkedList<TestExecution> testExecutions;

  /**
   * For each test, this contains the total number of times it failed in {@link #testExecutions}.
   */
  private TestExecution summary;

  /** All the inner collections have the same size. */
  public TestFailures(Collection<? extends Collection<Integer>> testExecutionsData) {
    assert !testExecutionsData.isEmpty();
    this.testExecutions = new LinkedList<>();
    numTests = 0;
    for (Collection<Integer> testExecutionData : testExecutionsData) {
      if (numTests == 0) {
        numTests = testExecutionData.size();
      } else {
        assert numTests == testExecutionData.size();
      }
      this.testExecutions.add(new TestExecution(testExecutionData));
    }
    summary = computeSummary(testExecutions);
  }

  private static TestExecution computeSummary(LinkedList<TestExecution> testExecutions) {
    assert !testExecutions.isEmpty();
    TestExecution result = null;
    for (TestExecution te : testExecutions) {
      if (result == null) {
        result = (TestExecution) te.clone();
      } else {
        result.incrementBy(te);
      }
    }
    return result;
  }

  /**
   * Returns a new TestFailures of the given size containing random data.
   *
   * @param numExecutions the number of test suite executions
   * @param numTests the size of each TestExecution
   * @param failureProbability the probability of each test failing; between 0 and 1 inclusive
   * @return a new TestFailures of the given size, containing random data
   */
  public static TestFailures random(
      int numExecutions, int numTests, double failureProbability, Random r) {
    ArrayList<TestExecution> testData = new ArrayList<>();
    for (int i = 0; i < numExecutions; i++) {
      testData.add(TestExecution.random(numTests, failureProbability, r));
    }
    return new TestFailures(testData);
  }

  /**
   * Returns the number of tests in each TestExecution.
   *
   * @return the number of tests in each TestExecution
   */
  public int getNumTests() {
    return numTests;
  }

  /**
   * Returns the number of times the test suite was executed.
   *
   * @return the number of times the test suite was executed
   */
  public int getNumExecutions() {
    return testExecutions.size();
  }

  /**
   * Returns the number of times each test in the test suite failed.
   *
   * @return the number of times each test in the test suite failed
   */
  public TestExecution getSummary() {
    return new TestExecution(summary);  // defensive copy
  }

  /**
   * Returns the index of the test that fails most often -- that is, the index of the maximum value
   * in {@link #summary}. In case of ties, chooses the lowest index among the ties.
   *
   * @return the index of the test that fails most often, or -1 if all tests pass
   */
  public int mostFailingTest() {
    int numExecutions = getNumExecutions();

    int maxIndex = -1;
    int maxVal = 0;
    for (int i = 0; i < numTests; i++) {
      Integer val = summary.get(i);
      if (val != null && val > maxVal) {
        maxIndex = i;
        maxVal = val;
      }
    }
    return maxIndex;
  }

  /**
   * Remove all test executions that fail the test at the given index. This method side-effects this
   * TestFailures object.
   *
   * @param testIndex the index of a test
   */
  public void removeIfFailing(int testIndex) {
    ListIterator<TestExecution> iter = testExecutions.listIterator();
    while (iter.hasNext()) {
      TestExecution te = iter.next();
      Integer val = te.get(testIndex);
      if (val != null && val != 0) {
        iter.remove();
        summary.decrementBy(te);
      }
    }
  }

  @Override
  public String toString() {
    StringJoiner sj = new StringJoiner(System.lineSeparator());
    sj.add(
        String.format(
            "TestFailures(%d executions of %d tests):", getNumExecutions(), getNumTests()));
    for (TestExecution te : testExecutions) {
      sj.add(te.toString());
    }
    sj.add("Summary: " + summary);
    return sj.toString();
  }
}
