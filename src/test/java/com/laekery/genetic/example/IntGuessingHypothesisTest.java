package com.laekery.genetic.example;


import java.util.stream.IntStream;
import org.junit.Test;
import static org.junit.Assert.*;

public class IntGuessingHypothesisTest {
    @Test
    public void testInit() {
        new IntGuessingHypothesis(4);
    }
    
    @Test
    public void testEquals() {
        IntGuessingHypothesis a = new IntGuessingHypothesis(4);
        IntGuessingHypothesis b = new IntGuessingHypothesis(4);
        a.setGenome(new int[] {1,2,3,4});
        b.setGenome(new int[] {1,2,3,4});
        assertEquals(a, b);
    }
    
    @Test
    public void testFitnessWithGoal() {
        IntGuessingHypothesis a = new IntGuessingHypothesis(4);
        a.setGenome(new int[] {0,1,2,3});
        assertEquals(0, a.calculateFitness(), 0.001);
    }
    
    @Test
    public void testFitnessWithQuadMiss() {
        IntGuessingHypothesis a = new IntGuessingHypothesis(4);
        a.setGenome(new int[] {1,2,3,4});
        assertEquals(-4, a.calculateFitness(), 0.001);
    }
    
    @Test
    public void testFitnessWithFullMiss() {
        IntGuessingHypothesis a = new IntGuessingHypothesis(4);
        a.setGenome(new int[] {3,2,1,0});
        assertEquals(-(3+1+1+3), a.calculateFitness(), 0.001);
    }
}
