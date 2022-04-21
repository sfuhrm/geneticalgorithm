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
class SimpleComputeEngine<H> extends ComputeEngine<H> {

    /**
     * Creates a new instance.
     * @param inRandom the source of randomness. Must not be {@code null}.
     * @param inAlgorithmDefinition the definition of the algorithm to use.
     */
    SimpleComputeEngine(final Random inRandom,
                        final AlgorithmDefinition<H> inAlgorithmDefinition) {
        super(inRandom, inAlgorithmDefinition);
    }

    @Override
    void select(final List<Handle<H>> population,
                       final double sumOfProbabilities,
                       final int targetCount,
                       final Collection<Handle<H>> targetCollection) {
        for (int i = 0; i < targetCount; i++) {
            Handle<H> selected = probabilisticSelect(
                    population,
                    sumOfProbabilities
            );
            targetCollection.add(selected);
        }
    }

    @Override
    void crossover(
            final List<Handle<H>> population,
            final double sumOfProbabilities,
            final int targetCount,
            final Collection<Handle<H>> targetCollection) {

        for (int i = 0; i < targetCount;) {
            Handle<H> first = probabilisticSelect(population,
                    sumOfProbabilities
            );
            Handle<H> second = probabilisticSelect(population,
                    sumOfProbabilities
            );

            Collection<H> offsprings =
                    getAlgorithmDefinition().crossOverHypothesis(
                        first.getHypothesis(),
                        second.getHypothesis());

            i += offsprings.size();

            for (H offspring : offsprings) {
                targetCollection.add(new Handle<>(offspring));
            }
        }
    }

    @Override
    void mutate(final List<Handle<H>> selectedSet,
                       final int mutationCount) {
        for (int i = 0; i < mutationCount; i++) {
            int index = getRandom().nextInt(selectedSet.size());
            Handle<H> current = selectedSet.get(index);
            getAlgorithmDefinition().mutateHypothesis(current.getHypothesis());
            current.setHasFitness(false);
        }
    }

    @Override
    double updateFitnessAndGetSumOfProbabilities(
            final List<Handle<H>> population) {
        double sumFitness = 0.;
        for (Handle<H> current : population) {
            if (!current.isHasFitness()) {
                current.setFitness(
                        getAlgorithmDefinition()
                                .calculateFitness(current.getHypothesis()));
                current.setHasFitness(true);
            }
            sumFitness += current.getFitness();
        }
        double sumOfProbabilities = 0.;
        for (Handle<H> current : population) {
            double probability = current.getFitness() / sumFitness;
            current.setProbability(probability);
            sumOfProbabilities += probability;
        }
        return sumOfProbabilities;
    }
}
