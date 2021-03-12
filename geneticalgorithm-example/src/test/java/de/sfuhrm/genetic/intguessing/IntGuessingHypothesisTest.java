/*
 * Copyright 2016 Stephan Fuhrmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.sfuhrm.genetic.intguessing;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for the IntGuessingHypothesis
 * @author Stephan Fuhrmann
 */
public class IntGuessingHypothesisTest {
    @Test
    public void testInit() {
        IntGuessingHypothesis guessingHypothesis = new IntGuessingHypothesis(4);
        assertNotNull(guessingHypothesis.getGenome());
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
        assertEquals(Math.exp(4.), a.calculateFitness(), 0.001);
    }

    @Test
    public void testFitnessWithSingleHit() {
        IntGuessingHypothesis a = new IntGuessingHypothesis(4);
        a.setGenome(new int[] {0,9,9,9});
        assertEquals(Math.exp(-20.), a.calculateFitness(), 0.001);
    }

    @Test
    public void testFitnessWithQuadMiss() {
        IntGuessingHypothesis a = new IntGuessingHypothesis(4);
        a.setGenome(new int[] {1,2,3,4});
        assertEquals(Math.exp(-4.), a.calculateFitness(), 0.001);
    }

    @Test
    public void testFitnessWithFullMiss() {
        IntGuessingHypothesis a = new IntGuessingHypothesis(4);
        a.setGenome(new int[] {3,2,1,0});
        assertEquals(Math.exp(-8.), a.calculateFitness(), 0.001);
    }
}