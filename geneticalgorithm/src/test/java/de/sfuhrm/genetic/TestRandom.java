package de.sfuhrm.genetic;

import java.util.Random;

/** A {@link Random} subclass for testing that records
 * and controls the values returned by {@link #nextDouble()}
 * and {@link #nextInt(int)}.
 */
class TestRandom extends Random {

    private double nextDoubleValue;
    private int nextIntValue;
    private boolean nextDoubleCalled;
    private boolean nextIntCalled;

    @Override
    public double nextDouble() {
        nextDoubleCalled = true;
        return nextDoubleValue;
    }

    @Override
    public int nextInt(int bound) {
        nextIntCalled = true;
        return nextIntValue;
    }

    void whenNextDouble(double value) {
        this.nextDoubleValue = value;
    }

    void whenNextInt(int value) {
        this.nextIntValue = value;
    }

    boolean isNextDoubleCalled() {
        return nextDoubleCalled;
    }

    boolean isNextIntCalled() {
        return nextIntCalled;
    }

    void reset() {
        nextDoubleCalled = false;
        nextIntCalled = false;
        nextDoubleValue = 0;
        nextIntValue = 0;
    }
}
