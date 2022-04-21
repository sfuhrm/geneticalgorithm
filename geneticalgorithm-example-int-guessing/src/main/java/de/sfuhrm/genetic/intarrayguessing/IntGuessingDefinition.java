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
package de.sfuhrm.genetic.intarrayguessing;

import de.sfuhrm.genetic.AlgorithmDefinition;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.util.stream.IntStream;

/** Definition for a int guessing scenario.
 * @author Stephan Fuhrmann
 */
public final class IntGuessingDefinition implements
        AlgorithmDefinition<int[]> {

    /** The source of randomness. */
    private Random random;

    /** The length of the genome / array length. */
    private int genomeLength;

    IntGuessingDefinition(final int inLength) {
        this.genomeLength = inLength;
    }

    @Override
    public void initialize(final Random r) {
        this.random = r;
    }

    @Override
    public int[] newRandomHypothesis() {
        int[] result = new int[genomeLength];
        IntStream
                .range(0, result.length)
                .forEach(
                        i -> result[i] = random.nextInt(result.length));

        return result;
    }

    @Override
    public void mutateHypothesis(final int[] instance) {
        int point = random.nextInt(instance.length);
        instance[point] = random.nextInt(instance.length);
    }

    @Override
    public Collection<int[]> crossOverHypothesis(
            final int[] first, final int[] second) {
        int point = random.nextInt(first.length);
        int[] offspringOne = new int[first.length];
        int[] offspringTwo = new int[first.length];
        for (int i = 0; i < first.length; i++) {
            offspringOne[i] = i < point ? first[i] : second[i];
            offspringTwo[i] = i >= point ? second[i] : second[i];
        }
        return Arrays.asList(offspringOne, offspringTwo);
    }

    @Override
    public double calculateFitness(final int[] hypothesis) {
        double fitness = 0.;
        for (int i = 0; i < hypothesis.length; i++) {
            int genomeValue = hypothesis[i];
            if (genomeValue == i) {
                fitness += 1.;
            } else {
                fitness -= Math.abs(genomeValue - i);
            }
        }
        return Math.exp(fitness);
    }

    @Override
    public boolean loop(final int[] hypothesis) {
        GuessingExample.print(hypothesis);
        for (int i = 0; i < hypothesis.length; i++) {
            if (hypothesis[i] != i) {
                return true;
            }
        }
        return false;
    }
}
