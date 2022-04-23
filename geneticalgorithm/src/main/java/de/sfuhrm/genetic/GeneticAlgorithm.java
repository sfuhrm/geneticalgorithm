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

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutorService;

/**
 * Central entry class for generic algorithm implementations.
 * There are two approaches you can take:
 *
 * <ol>
 *     <li>High-level approach: Call one of the {@link #findMaximum()} or
 *     {@link #findMaximum(ExecutorService)} methods. They
 *     will iterate multiple generations until the
 *     {@link AlgorithmDefinition#loop(Object) finish-condition}
 *     is met.</li>
 *     <li>Low-Level approach: Iterate yourself and calculate
 *     each generation yourself using the
 *     {@link #calculateNextGeneration(List, ComputeEngine)}
 *     method. This way you can do your own analysis on the state of the
 *     population.</li>
 * </ol>
 *
 * @param <H> The hypothesis class to use.
 * @author Stephan Fuhrmann
 **/
public class GeneticAlgorithm<H> {

    /** The random generator to use. */
    private final Random random;

    /** The algorithm definition to work with. */
    private final AlgorithmDefinition<H> algorithmDefinition;

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
     * @param inCrossOverRate the fraction at which the cross-over
     *                      operator is applied to the population,
     *                      between 0 and 1. A good value for the
     *                      cross-over rate is {@code 0.3}.
     * @param inMutationRate the fraction at which the mutation operator
     *                     is applied to the population, between 0 and 1.
     *                     A good value for the mutation rate is {@code 0.05}.
     * @param inGenerationSize the number of individual hypothesis in the
     *                       population for each generation, greater than 1.
     *                       The generation size choice depends on the
     *                       complexity of your problem, but {@code 100} is
     *                       a good starting point.
     * @param inAlgorithmDefinition the algorithm definition to work with.
     * @throws IllegalArgumentException if the parameters are illegal.
     */
    public GeneticAlgorithm(final double inCrossOverRate,
                            final double inMutationRate,
                            final int inGenerationSize,
                            final AlgorithmDefinition<H> inAlgorithmDefinition
                            ) {
        this(inCrossOverRate, inMutationRate, inGenerationSize,
                inAlgorithmDefinition,
                new Random());
    }

    /**
     * Constructs a new genetic algorithm.
     * @param inCrossOverRate the fraction at which the cross-over
     *                      operator is applied to the population,
     *                      between 0 and 1. A good value for the
     *                      cross-over rate is {@code 0.3}.
     * @param inMutationRate the fraction at which the mutation operator
     *                     is applied to the population, between 0 and 1.
     *                     A good value for the mutation rate is {@code 0.05}.
     * @param inGenerationSize the number of individual hypothesis in the
     *                       population for each generation, greater than 1.
     *                       The generation size choice depends on the
     *                       complexity of your problem, but {@code 100} is
     *                       a good starting point.
     * @param inAlgorithmDefinition the algorithm definition to work with.
     * @param inRandom the random number generator to use as source of
     *                 randomness.
     * @throws IllegalArgumentException if the parameters are illegal.
     */
    public GeneticAlgorithm(final double inCrossOverRate,
                            final double inMutationRate,
                            final int inGenerationSize,
                            final AlgorithmDefinition<H> inAlgorithmDefinition,
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
        this.algorithmDefinition = inAlgorithmDefinition;

        inAlgorithmDefinition.initialize(random);
    }

    private static <H> Optional<Handle<H>> max(
            final Handle<H> a,
            final Handle<H> b) {
        if (a == null) {
            return Optional.ofNullable(b);
        }
        if (b == null) {
            return Optional.of(a);
        }

        if (a.getFitness() > b.getFitness()) {
            return Optional.of(a);
        } else {
            return Optional.of(b);
        }
    }

    /** Calculate one generation step.
     * Takes a current generation and calculates the
     * next generation out of it.
     * @param currentGeneration the current generation of hypothesis handles.
     * @param computeEngine the compute engine to use for computation.
     * @return the population of the next generation. This will contain some
     * individuals from the {@code currentGeneration} input.
     * @since 3.0.0
     * */
    public List<Handle<H>> calculateNextGeneration(
            final List<Handle<H>> currentGeneration,
            final ComputeEngine<H> computeEngine) {

        List<Handle<H>> nextGeneration = computeEngine.calculateNextGeneration(
                currentGeneration,
                generationSize,
                crossOverRate,
                mutationRate);

        generationNumber++;

        return nextGeneration;
    }

    /** Perform the genetic operation.
     * @param computeEngine the compute engine to use.
     * @return the maximum element, if any.
     */
    private Optional<Handle<H>> innerFindMaximum(
            final ComputeEngine<H> computeEngine) {
        List<Handle<H>> currentGeneration =
                computeEngine.createRandomHypothesisHandles(
                    generationSize);
        Optional<Handle<H>> allTimeMax = Optional.empty();

        generationNumber = 0;
        do {

            List<Handle<H>> nextGeneration =
                    calculateNextGeneration(currentGeneration,
                            computeEngine);

            Optional<Handle<H>> curMax = computeEngine.max(nextGeneration);
            allTimeMax = max(curMax.orElse(null), allTimeMax.orElse(null));

            currentGeneration = nextGeneration;
        } while (allTimeMax.isPresent()
                && algorithmDefinition.loop(allTimeMax.get().getHypothesis()));

        return allTimeMax;
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
     * @param executorService the executor service to use when calculating the
     *     fitness. Can be obtained using a call like
     * {@code Executors.newFixedThreadPool(
     * Runtime.getRuntime().availableProcessors() * 2)}.
     * @return the maximum
     *     {@link AlgorithmDefinition#calculateFitness(Object)} element,
     *     if any.
     * @throws NullPointerException
     * if one of the parameters is {@code null}.
     * @see java.util.concurrent.Executors
     */
    public Optional<Handle<H>> findMaximum(
            @NonNull final ExecutorService executorService) {

        return innerFindMaximum(
                new ExecutorServiceComputeEngine<>(
                        random,
                        algorithmDefinition,
                        executorService
                ));
    }

    /** Perform the genetic optimization.
     * For a discussion on the usage, see
     * {@link #findMaximum(ExecutorService)} .
     * @return the maximum element, if any.
     * @throws NullPointerException
     * if one of the parameters is {@code null}.
     * @see #findMaximum(ExecutorService)
     */
    public Optional<Handle<H>> findMaximum() {
        return innerFindMaximum(
                new SimpleComputeEngine<>(random, algorithmDefinition));
    }
}
