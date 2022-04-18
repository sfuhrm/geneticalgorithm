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

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Generic genetic algorithm implementation.
 * After initializing the instance the main focus
 * is on {@linkplain #findMaximum(Function, Supplier) finding the maximum}
 * in the hypothesis space.
 * @param <H> The hypothesis class to use.
 * @author Stephan Fuhrmann
 **/
public class GeneticAlgorithm<H extends AbstractHypothesis<H>> {

    /** The random generator to use. */
    private final Random random;

    /** The fraction between 0 and 1 at which cross over operations are done.
     * The other part of the population will be filled with selected
     * hypothesis.
     */
    @Getter @Setter
    private double crossOverRate;

    /** The fraction between 0 and 1 at which mutations are done.
     * This is the fraction of hypothesis that the mutation operator
     * is applied to.
     */
    @Getter @Setter
    private double mutationRate;

    /** The size of each population generation as a count of individuals.
     */
    @Getter @Setter
    private int generationSize;

    /** The number of the current generation.
     */
    @Getter
    private int generationNumber;

    /**
     * Constructs a new genetic algorithm.
     * @param inCrossOverRate the fraction at which the cross over
     *                      operator is applied to the population,
     *                      between 0 and 1. A good value for the
     *                      cross over rate is {@code 0.3}.
     * @param inMutationRate the fraction at which the mutation operator
     *                     is applied to the population, between 0 and 1.
     *                     A good value for the mutation rate is {@code 0.05}.
     * @param inGenerationSize the number of individual hypothesis in the
     *                       population for each generation, greater than 1.
     *                       The generation size choice depends on the
     *                       complexity of your problem, but {@code 100} is
     *                       a good starting point.
     * @throws IllegalArgumentException if the parameters are illegal.
     */
    public GeneticAlgorithm(final double inCrossOverRate,
                            final double inMutationRate,
                            final int inGenerationSize) {
        this(inCrossOverRate, inMutationRate, inGenerationSize,
                new Random());
    }

    /**
     * Constructs a new genetic algorithm.
     * @param inCrossOverRate the fraction at which the cross over
     *                      operator is applied to the population,
     *                      between 0 and 1. A good value for the
     *                      cross over rate is {@code 0.3}.
     * @param inMutationRate the fraction at which the mutation operator
     *                     is applied to the population, between 0 and 1.
     *                     A good value for the mutation rate is {@code 0.05}.
     * @param inGenerationSize the number of individual hypothesis in the
     *                       population for each generation, greater than 1.
     *                       The generation size choice depends on the
     *                       complexity of your problem, but {@code 100} is
     *                       a good starting point.
     * @param inRandom the random number generator to use as source of
     *                 randomness.
     * @throws IllegalArgumentException if the parameters are illegal.
     */
    public GeneticAlgorithm(final double inCrossOverRate,
                            final double inMutationRate,
                            final int inGenerationSize,
                            final Random inRandom) {
        if (inCrossOverRate < 0. || inCrossOverRate > 1.) {
            throw new IllegalArgumentException(
                    "Cross over rate not between 0 and 1: " + inCrossOverRate);
        }
        if (inMutationRate < 0. || inMutationRate > 1.) {
            throw new IllegalArgumentException(
                    "Mutation rate not between 0 and 1: " + inMutationRate);
        }
        if (inGenerationSize < 2) {
            throw new IllegalArgumentException(
                    "Generation size is < 2: " + inMutationRate);
        }
        this.crossOverRate = inCrossOverRate;
        this.mutationRate = inMutationRate;
        this.generationSize = inGenerationSize;
        this.random = inRandom;
    }

    /** Perform the genetic operation.
     * @param loopCondition the abort condition that stays true while the
     *                      maximum is not yet reached. Gets presented
     *                      the best hypothesis as input.
     * @param hypothesisSupplier creation function for new hypothesis.
     * @param computeEngine the compute engine to use.
     * @return the maximum element, if any.
     */
    private Optional<H> innerFindMaximum(
            final Function<H, Boolean> loopCondition,
            final Supplier<H> hypothesisSupplier,
            final ComputeEngine<H> computeEngine) {
        List<H> currentGeneration = new ArrayList<>(generationSize);
        List<H> nextGeneration = new ArrayList<>(generationSize);
        Optional<H> max = Optional.empty();

        for (int i = 0; i < generationSize; i++) {
            currentGeneration.add(hypothesisSupplier.get().randomInit());
        }

        generationNumber = 0;
        do {
            double sumOfProbabilities =
                    computeEngine.updateFitnessAndGetSumOfProbabilities(
                            currentGeneration);

            Optional<H> curMax = computeEngine.max(currentGeneration);
            if (curMax.isPresent()) {
                if (max.isPresent()) {
                    if (curMax.get().getFitness() > max.get().getFitness()) {
                        max = curMax;
                    }
                } else {
                    max = curMax;
                }
            }

            nextGeneration.clear();

            computeEngine.select(currentGeneration,
                    sumOfProbabilities,
                    (int) ((1. - crossOverRate) * generationSize),
                    nextGeneration);

            computeEngine.crossover(currentGeneration, sumOfProbabilities,
                    (int) ((crossOverRate) * generationSize),
                    nextGeneration);

            currentGeneration.clear();
            currentGeneration.addAll(nextGeneration);

            computeEngine.mutate(currentGeneration,
                    (int) (mutationRate * generationSize));

            generationNumber++;
        } while (max.isPresent() && loopCondition.apply(max.get()));

        return max;
    }


    /** Perform the genetic optimization.
     * The algorithm will search for an optimal hypothesis until the
     * {@code loopCondition} returns {@code false}.
     * New hypothesis are created using the {@code hypothesisSupplier}.
     *
     * Good implementations for the {@code loopCondition} are:
     * <ul>
     *     <li>a maximum number of generations</li>
     *     <li>a maximum amount of time, for example
     *     10 seconds of optimization</li>
     *     <li>a check whether the supplied hypothesis solves the
     *     problem</li>
     * </ul>
     *
     * @param loopCondition the abort condition that stays true while the
     *                      maximum is not yet reached. Gets presented
     *                      the best hypothesis as input after every
     *                      generation of the genetic algorithm.
     * @param hypothesisSupplier creation function for new hypothesis.
     *     This will usually be {@code YourHypothesis::new}.
     * @param executorService the executor service to use when calculating the
     *     fitness. Can be obtained using a call like
     * {@code Executors.newFixedThreadPool(
     * Runtime.getRuntime().availableProcessors() * 2)}.
     * @return the maximum
     *     {@link AbstractHypothesis#calculateFitness() fitness} element,
     *     if any.
     * @throws NullPointerException
     * if one of the parameters is {@code null}.
     * @see java.util.concurrent.Executors
     */
    public Optional<H> findMaximum(
            @NonNull final Function<H, Boolean> loopCondition,
            @NonNull final Supplier<H> hypothesisSupplier,
            @NonNull final ExecutorService executorService) {

        return innerFindMaximum(
                loopCondition,
                hypothesisSupplier,
                new ExecutorServiceComputeEngine<>(
                        random,
                        executorService
                ));
    }

    /** Perform the genetic optimization.
     * For a discussion on the usage, see
     * {@link #findMaximum(Function, Supplier, ExecutorService)}.
     * @param loopCondition the abort condition that stays true while the
     *                      maximum is not yet reached. Gets presented
     *                      the best hypothesis as input.
     * @param hypothesisSupplier creation function for new hypothesis.
     * @return the maximum element, if any.
     * @throws NullPointerException
     * if one of the parameters is {@code null}.
     * @see #findMaximum(Function, Supplier, ExecutorService)
     */
    public Optional<H> findMaximum(
            @NonNull final Function<H, Boolean> loopCondition,
            @NonNull final Supplier<H> hypothesisSupplier) {
        return innerFindMaximum(loopCondition, hypothesisSupplier,
                new SimpleComputeEngine<>(random));
    }
}
