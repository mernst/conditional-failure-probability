package cfp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

/**
 * Typically, a TestExecution represents the execution of a test suite. Each test in the suite may
 * succeed (Integer value = 0), fail (Integer value = 1), or not run (Integer value = null).
 *
 * <p>A TestExecution can also represent multiple executions, in which case the values can be
 * arbitrary nonnegative integers.
 */
public class TestExecution extends ArrayList<Integer> {

  public TestExecution(int capacity) {
    super(capacity);
  }

  /**
   * Returns a new TestExecution that has the same data as the given collection of integers, in the
   * same order.
   *
   * @param toCopy a collection of integers
   * @return a new TestExecution that has the same data as the given collection of integers
   */
  public TestExecution(Collection<Integer> c) {
    super(c);
  }

  /**
   * Returns a new TestExecution of the given size, containing all zeroes.
   *
   * @param size the size of the TestExecution
   * @return a new TestExecution of the given size, containing all zeroes
   */
  public static TestExecution allZeroes(int size) {
    return new TestExecution(Collections.nCopies(size, 0));
  }

  /**
   * Returns a new TestExecution of the given size, containing random data. The probability of a
   * datum being a failure is {@code failureProbability}.
   *
   * @param size the size of the TestExecution
   * @param failureProbability the probability of each test failing; between 0 and 1 inclusive
   * @return a new TestExecution of the given size, containing random data
   */
  public static TestExecution random(int size, double failureProbability, Random r) {
    ArrayList<Integer> data = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      double randomDouble = r.nextDouble();
      boolean isFailure = randomDouble < failureProbability;
      // System.out.printf("isFailure = %s, randomDouble = %f%n", isFailure, randomDouble);
      data.add(isFailure ? 1 : 0);
    }
    return new TestExecution(data);
  }

  /**
   * Creates a TestExecution from an array of strings, each of which is the empty string (the test
   * did not run), "pass" (the test passed), or "fail" (the test failed).
   *
   * @param passOrFail an array of strings as described above
   * @return a TestExecution created from the array of strings
   */
  public static TestExecution fromPassOrFailArray(String[] passOrFailArray) {
    TestExecution result = new TestExecution(passOrFailArray.length);
    for (String passOrFail : passOrFailArray) {
      Integer val =
          switch (passOrFail) {
            case "" -> null;
            case "pass" -> 0;
            case "fail" -> 1;
            default -> throw new Error("bad string \"" + passOrFail + "\" in " + passOrFailArray);
          };
      result.add(val);
    }
    return result;
  }

  /**
   * Updates this by adding or subtracting corresponding elements of {@code other}.
   *
   * @param other a TestExecution for a single test, whose size is the same as the size of this
   * @param increment if true, increment this; if false, decrement this
   */
  private void incrementBy(TestExecution other, boolean increment) {
    int size = this.size();
    assert size == other.size();
    for (int i = 0; i < size; i++) {
      Integer thisElt = this.get(i);
      if (thisElt == null) {
        thisElt = 0;
      }
      Integer otherElt = other.get(i);
      if (otherElt == null || otherElt.equals(0)) {
        continue;
      }
      if (increment) {
        thisElt = thisElt + otherElt;
      } else {
        thisElt = thisElt - otherElt;
      }
      this.set(i, thisElt);
    }
  }

  /**
   * Updates this by arithmetically adding corresponding elements of {@code other}.
   *
   * @param other a TestExecution for a single test, whose size is the same as the size of this
   */
  public void incrementBy(TestExecution other) {
    incrementBy(other, true);
  }

  /**
   * Updates this by subtracting corresponding elements of {@code other}.
   *
   * @param other a TestExecution for a single test, whose size is the same as the size of this
   */
  public void decrementBy(TestExecution other) {
    incrementBy(other, false);
  }
}
