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

import de.sfuhrm.genetic.AbstractHypothesis;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/** Hypothesis for a fixed-size int array content.
 * @author Stephan Fuhrmann
 */
public final class IntGuessingHypothesis extends
        AbstractHypothesis<IntGuessingHypothesis> {

    /** Slot allocation per number. */
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private int[] genome;

    /** Constructor.
     * @param totalNumbers the length of the number array to guess.
     *                     Example: 3 would mean that an int array with
     *                     3 int elements will need to be guessed.
     * */
    public IntGuessingHypothesis(final int totalNumbers) {
        genome = new int[totalNumbers];
    }

    @Override
    protected IntGuessingHypothesis randomInit() {
        IntStream
                .range(0, genome.length)
                .forEach(
                        i -> genome[i] = getRANDOM().nextInt(genome.length));
        return this;
    }

    @Override
    protected List<IntGuessingHypothesis> crossOver(
            final IntGuessingHypothesis other) {
        int point = getRANDOM().nextInt(genome.length);
        IntGuessingHypothesis one = new IntGuessingHypothesis(genome.length);
        IntGuessingHypothesis two = new IntGuessingHypothesis(genome.length);
        for (int i = 0; i < genome.length; i++) {
            one.genome[i] = i < point ? genome[i] : other.genome[i];
            two.genome[i] = i >= point ? genome[i] : other.genome[i];
        }
        return Arrays.asList(one, two);
    }

    protected double maximumFitness() {
        return Math.exp(genome.length);
    }

    @Override
    protected double calculateFitness() {
        double fitness = 0.;
        for (int i = 0; i < genome.length; i++) {
            int genomeValue = genome[i];
            if (genomeValue == i) {
                fitness += 1.;
            } else {
                fitness -= Math.abs(genomeValue - i);
            }
        }
        return Math.exp(fitness);
    }

    @Override
    protected void mutate() {
        int point = getRANDOM().nextInt(genome.length);
        genome[point] = getRANDOM().nextInt(genome.length);
    }

    @Override
    public String toString() {
        return Arrays.toString(genome);
    }
}
