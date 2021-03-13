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
package de.sfuhrm.genetic;

import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Generic genetic algorithm implementation.
 * @param <H> The hypothesis class to use.
 * @author Stephan Fuhrmann
 **/
class SimpleComputeEngine<H extends AbstractHypothesis<H>>
        extends ComputeEngine<H> {

    /**
     * Creates a new instance.
     * @param inRandom the source of randomness. Must not be {@code null}.
     */
    SimpleComputeEngine(final Random inRandom) {
        super(inRandom);
    }

    @Override
    void select(final List<H> population,
                       final double sumOfProbabilities,
                       final int targetCount,
                       final Collection<H> targetCollection) {
        for (int i = 0; i < targetCount; i++) {
            H selected = probabilisticSelect(
                    population,
                    sumOfProbabilities
            );
            targetCollection.add(selected);
        }
    }

    @Override
    void crossover(
            final List<H> population,
            final double sumOfProbabilities,
            final int targetCount,
            final Collection<H> targetCollection) {
        for (int i = 0; i < targetCount;) {
            H first = probabilisticSelect(population,
                    sumOfProbabilities
            );
            H second = probabilisticSelect(population,
                    sumOfProbabilities
            );
            List<H> children = first.crossOver(second);
            targetCollection.addAll(children);
            i += children.size();
        }
    }

    @Override
    void mutate(final List<H> selectedSet,
                       final int mutationCount) {
        for (int i = 0; i < mutationCount; i++) {
            int index = getRandom().nextInt(selectedSet.size());
            H current = selectedSet.get(index);
            current.mutate();
        }
    }

    @Override
    double updateFitnessAndGetSumOfProbabilities(
            final List<H> population) {
        double sumFitness = 0.;
        for (H current : population) {
            current.setFitness(current.calculateFitness());
            sumFitness += current.getFitness();
        }
        double sumOfProbabilities = 0.;
        for (H current : population) {
            double probability = current.getFitness() / sumFitness;
            current.setProbability(probability);
            sumOfProbabilities += probability;
        }
        return sumOfProbabilities;
    }
}
