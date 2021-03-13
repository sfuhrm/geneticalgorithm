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
class ExecutorServiceComputeEngine<H extends AbstractHypothesis<H>>
        extends ComputeEngine<H> {

    /** The executor service to use for multi-threading.
     * */
    private final ExecutorService executorService;

    /**
     * Creates a new instance.
     * @param inRandom the source of randomness. Must not be {@code null}.
     * @param inExecutorService the executor service to use for multi threading.
     */
    ExecutorServiceComputeEngine(final Random inRandom,
                                 final ExecutorService inExecutorService) {
        super(inRandom);
        this.executorService = inExecutorService;
    }

    private void handleException(final Exception exception) {
        throw new RuntimeException(exception);
    }

    @Override
    void select(final List<H> population,
                       final double sumOfProbabilities,
                       final int targetCount,
                       final Collection<H> targetCollection) {
        List<Future<H>> futureList = new ArrayList<>(targetCount);
        for (int i = 0; i < targetCount; i++) {
            futureList.add(executorService.submit(() ->
                probabilisticSelect(
                        population,
                        sumOfProbabilities
                )
            ));
        }
        for (Future<H> future : futureList) {
            try {
                targetCollection.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                handleException(e);
            }
        }
    }

    @Override
    void crossover(
            final List<H> population,
            final double sumOfProbabilities,
            final int targetCount,
            final Collection<H> targetCollection) {
        List<Future<List<H>>> futureList = new ArrayList<>(targetCount);
        for (int i = 0; i < targetCount / 2; i++) {
            futureList.add(executorService.submit(() -> {
                H first = probabilisticSelect(population,
                        sumOfProbabilities);
                H second = probabilisticSelect(population,
                        sumOfProbabilities);
                return first.crossOver(second);
            }));
        }

        int childCount = 0;
        for (Future<List<H>> future : futureList) {
            List<H> children;
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
    void mutate(final List<H> selectedSet,
                       final int mutationCount) {
        List<Future<?>> futureList = new ArrayList<>(mutationCount);
        for (int i = 0; i < mutationCount; i++) {
            futureList.add(executorService.submit(() -> {
                int index = getRandom().nextInt(selectedSet.size());
                H current = selectedSet.get(index);
                current.mutate();
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
            final List<H> population) {
        double sumFitness = 0.;

        List<Future<H>> futureList = new ArrayList<>(population.size());
        for (H hypothesis : population) {
            futureList.add(executorService.submit(() -> {
                hypothesis.setFitness(hypothesis.calculateFitness());
                return hypothesis;
            }));
        }

        for (Future<H> future : futureList) {
            try {
                sumFitness += future.get().getFitness();
            } catch (InterruptedException | ExecutionException e) {
                handleException(e);
            }
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
