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

/*-
 * #%L
 * Genetic Algorithm library
 * %%
 * Copyright (C) 2022 Stephan Fuhrmann
 * %%
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
 * #L%
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Generic genetic algorithm implementation.
 * This implementation uses just the main thread.
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
    List<Handle<H>> calculateNextGeneration(
            final List<Handle<H>> currentGeneration,
            final int generationSize,
            final double crossOverRate,
            final double mutationRate) {
        List<Handle<H>> nextGeneration = new ArrayList<>(generationSize);

        updateFitness(
                currentGeneration);

        select(currentGeneration,
                (int) ((1. - crossOverRate) * generationSize),
                nextGeneration);

        crossover(currentGeneration,
                (int) ((crossOverRate) * generationSize),
                nextGeneration);

        mutate(nextGeneration,
                (int) (mutationRate * generationSize));

        return nextGeneration;
    }

    @Override
    List<Handle<H>> createRandomHypothesisHandles(final int count) {
        List<Handle<H>> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            result.add(
                    new Handle<>(
                            getAlgorithmDefinition().newRandomHypothesis()));
        }
        return result;
    }

    @Override
    void select(final List<Handle<H>> population,
                       final int targetCount,
                       final Collection<Handle<H>> targetCollection) {
        for (int i = 0; i < targetCount; i++) {
            Handle<H> selected = probabilisticSelect(
                    population
            );
            targetCollection.add(selected);
        }
    }

    @Override
    void crossover(
            final List<Handle<H>> population,
            final int targetCount,
            final Collection<Handle<H>> targetCollection) {

        for (int i = 0; i < targetCount;) {
            Handle<H> first = probabilisticSelect(population);
            Handle<H> second = probabilisticSelect(population);

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
    void updateFitness(
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
        for (Handle<H> current : population) {
            double probability = current.getFitness() / sumFitness;
            current.setProbability(probability);
        }
    }
}
