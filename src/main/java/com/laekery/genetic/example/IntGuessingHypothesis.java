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
package com.laekery.genetic.example;

import com.laekery.genetic.AbstractHypothesis;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/** Hypothesis for counting array. */
class IntGuessingHypothesis extends AbstractHypothesis<IntGuessingHypothesis> {

    /** Zone allocation per drone. */
    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    private int[] genome;
    
    IntGuessingHypothesis(int totalNumbers) {
        genome = new int[totalNumbers];
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof IntGuessingHypothesis)) {
            return false;
        }
        IntGuessingHypothesis other = (IntGuessingHypothesis)obj;
        return Arrays.equals(genome, other.genome);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(genome);
    }

    @Override
    protected IntGuessingHypothesis randomInit() {
        IntStream.range(0, genome.length).forEach(i -> genome[i] = RANDOM.nextInt(genome.length));
        return this;
    }

    @Override
    protected List<IntGuessingHypothesis> crossOver(IntGuessingHypothesis other) {
        int point = RANDOM.nextInt(genome.length);
        IntGuessingHypothesis one = new IntGuessingHypothesis(genome.length);
        IntGuessingHypothesis two = new IntGuessingHypothesis(genome.length);
        for (int i = 0; i < genome.length; i++) {
            one.genome[i] = i < point ? genome[i] : other.genome[i];
            two.genome[i] = i >= point ? genome[i] : other.genome[i];
        }
        return Arrays.asList(one, two);
    }

    @Override
    protected double calculateFitness() {
        return IntStream.range(0, genome.length).map(i -> genome[i] == i ? 1 : 0).sum();
    }

    @Override
    protected void mutate() {
        int point = RANDOM.nextInt(genome.length);
        genome[point] = RANDOM.nextInt(genome.length);
    }

    @Override
    public String toString() {
        return Arrays.toString(genome);
    }
}
