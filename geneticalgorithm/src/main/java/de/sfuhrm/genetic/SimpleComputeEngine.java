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
import java.util.Optional;
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

    /** Cross-overs a fraction of {@code crossOverRate} hypothesis
     * relative to their fitness.
     * @param population the population to select on.
     * @param sumOfProbabilities the
     * sum of probabilities of the population.
     * @param targetCount the number of instances to add to
     *                    the {@code targetCollection}.
     * @param targetCollection the target set to put crossed over elements to.
     */
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

    /** Mutates a fraction of {@code mutationRate} hypothesis.
     * @param selectedSet the population to mutate on.
     * @param mutationCount the number of instances to mutate.
     */
    void mutate(final List<H> selectedSet,
                       final int mutationCount) {
        for (int i = 0; i < mutationCount; i++) {
            int index = getRandom().nextInt(selectedSet.size());
            H current = selectedSet.get(index);
            current.mutate();
        }
    }

    /** Probabilistically selects one hypothesis relative to the selection
     * probability of it.
     * @param population the population to select from.
     * @param sumOfProbabilities the
     * sum of probabilities of the population.
     * @return the selected element.
     */
    H probabilisticSelect(final List<H> population,
                                 final double sumOfProbabilities
    ) {
        H result = population.get(0);
        double randomPoint = getRandom().nextDouble(); // random number
        // a random point in the sum of probabilities
        double inflatedPoint = randomPoint * sumOfProbabilities;

        double soFar = 0;
        for (int i = 0; i < population.size() && soFar < inflatedPoint; i++) {
            result = population.get(i);
            soFar += result.getProbability();
        }
        return result;
    }

    /** Find the maximum fitness element of the given collection.
     * @param in the population to find the maximum in.
     * @return the maximum element, if any.
     */
    Optional<H> max(final List<H> in) {
        Optional<H> result = Optional.empty();
        for (int i = 0; i < in.size(); i++) {
            H current = in.get(i);
            if (result.isPresent()) {
                if (current.getFitness() > result.get().getFitness()) {
                    result = Optional.of(current);
                }
            } else {
                result = Optional.of(current);
            }
        }
        return result;
    }

    /**
     * Updates the fitness and probability of the population.
     * Returns the sum of the probabilities.
     * @param population the population to work on.
     * @return the sum of probabilities, which should be 1.
     */
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
