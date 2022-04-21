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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Compute engine that uses an {@linkplain ExecutorService} for multi-threaded
 * execution.
 * @param <H> The hypothesis class to use.
 * @author Stephan Fuhrmann
 **/
class ExecutorServiceComputeEngine<H>
        extends ComputeEngine<H> {

    /** The executor service to use for multi-threading.
     * */
    private final ExecutorService executorService;

    /**
     * Creates a new instance.
     * @param inRandom the source of randomness. Must not be {@code null}.
     * @param inDefinition the algorithm definition to use.
     * @param inExecutorService the executor service to use for multi threading.
     */
    ExecutorServiceComputeEngine(final Random inRandom,
                                 final AlgorithmDefinition inDefinition,
                                 final ExecutorService inExecutorService) {
        super(inRandom, inDefinition);
        this.executorService = inExecutorService;
    }

    private void handleException(final Exception exception) {
        throw new RuntimeException(exception);
    }

    @Override
    void select(final List<Handle<H>> population,
                       final double sumOfProbabilities,
                       final int targetCount,
                       final Collection<Handle<H>> targetCollection) {
        List<Future<Handle<H>>> futureList = new ArrayList<>(targetCount);
        for (int i = 0; i < targetCount; i++) {
            futureList.add(executorService.submit(() ->
                probabilisticSelect(
                        population,
                        sumOfProbabilities
                )
            ));
        }
        for (Future<Handle<H>> future : futureList) {
            try {
                targetCollection.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                handleException(e);
            }
        }
    }

    @Override
    void crossover(
            final List<Handle<H>> population,
            final double sumOfProbabilities,
            final int targetCount,
            final Collection<Handle<H>> targetCollection) {
        List<Future<List<Handle<H>>>> futureList = new ArrayList<>(targetCount);
        for (int i = 0; i < targetCount / 2; i++) {
            futureList.add(executorService.submit(() -> {
                Handle<H> first = probabilisticSelect(population,
                        sumOfProbabilities);
                Handle<H> second = probabilisticSelect(population,
                        sumOfProbabilities);
                Collection<H> offsprings = getAlgorithmDefinition()
                        .crossOverHypothesis(
                            first.getHypothesis(),
                            second.getHypothesis());
                List<Handle<H>> offspringHandles
                        = new ArrayList<>(offsprings.size());
                for (H offspring : offsprings) {
                    offspringHandles.add(new Handle<>(offspring));
                }
                return offspringHandles;
            }));
        }

        int childCount = 0;
        for (Future<List<Handle<H>>> future : futureList) {
            List<Handle<H>> children;
            try {
                children = future.get();
                childCount += children.size();
                targetCollection.addAll(children);
                if (childCount >= targetCount) {
                    break;
                }
            } catch (InterruptedException | ExecutionException e) {
                handleException(e);
            }
        }
    }

    @Override
    void mutate(final List<Handle<H>> selectedSet,
                       final int mutationCount) {
        List<Future<?>> futureList = new ArrayList<>(mutationCount);
        for (int i = 0; i < mutationCount; i++) {
            futureList.add(executorService.submit(() -> {
                int index = getRandom().nextInt(selectedSet.size());
                Handle<H> current = selectedSet.get(index);
                getAlgorithmDefinition()
                        .mutateHypothesis(current.getHypothesis());
                current.setHasFitness(false);
            }
            ));
        }

        for (Future<?> future : futureList) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                handleException(e);
            }
        }
    }

    @Override
    double updateFitnessAndGetSumOfProbabilities(
            final List<Handle<H>> population) {
        double sumFitness = 0.;

        List<Future<Handle<H>>> futureList = new ArrayList<>(population.size());
        for (Handle<H> handle : population) {
            if (handle.isHasFitness()) {
                sumFitness += handle.getFitness();
            } else {
                futureList.add(executorService.submit(() -> {
                    handle.setFitness(
                            getAlgorithmDefinition().calculateFitness(
                                    handle.getHypothesis()));
                    handle.setHasFitness(true);
                    return handle;
                }));
            }
        }

        for (Future<Handle<H>> future : futureList) {
            try {
                sumFitness += future.get().getFitness();
            } catch (InterruptedException | ExecutionException e) {
                handleException(e);
            }
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
