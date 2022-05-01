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

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Central entry class for generic algorithm implementations.
 * There are two approaches you can take:
 *
 * <ol>
 *     <li>High-level approach: Call the of the {@link #findMaximum()}
 *     method. It will iterate multiple generations until the
 *     {@link AlgorithmDefinition#loop(Object) finish-condition}
 *     is met.</li>
 *     <li>Low-Level approach: Iterate yourself and calculate
 *     each generation yourself using the
 *     {@link #calculateNextGeneration(List)}
 *     method. This way you can do your own analysis on the state of the
 *     population.</li>
 * </ol>
 *
 * @param <H> The hypothesis class to use.
 * @author Stephan Fuhrmann
 **/
public class GeneticAlgorithm<H> {

    /** The compute engine to use. */
    private final ComputeEngine<H> computeEngine;

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
     *                       population for each generation,
     *                       greater than 1.
     *                       The generation size choice depends on the
     *                       complexity of your problem, but {@code 100} is
     *                       a good starting point.
     * @param inAlgorithmDefinition the algorithm definition to work with.
     * @param inComputeEngine the compute engine to use.
     * @param inRandom the random number generator to use as source of
     *                 randomness.
     * @throws IllegalArgumentException if the parameters are illegal.
     */
    GeneticAlgorithm(final double inCrossOverRate,
                final double inMutationRate,
                final int inGenerationSize,
                @NonNull final AlgorithmDefinition<H> inAlgorithmDefinition,
                @NonNull final ComputeEngine<H> inComputeEngine,
                @NonNull final Random inRandom) {
        this.crossOverRate = inCrossOverRate;
        this.mutationRate = inMutationRate;
        this.generationSize = inGenerationSize;
        this.algorithmDefinition = Objects.requireNonNull(inAlgorithmDefinition,
                "inAlgorithmDefinition is null");

        inAlgorithmDefinition.initialize(inRandom);

        computeEngine = inComputeEngine;
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
     * @return the population of the next generation. This will contain some
     * individuals from the {@code currentGeneration} input.
     * @since 3.0.0
     * */
    public List<H> calculateNextGeneration(
            final List<H> currentGeneration) {

        List<Handle<H>> nextGeneration =
                innerCalculateNextGeneration(
                currentGeneration
                        .stream()
                        .map(Handle::new)
                        .collect(Collectors.toList()));

        return nextGeneration
                .stream()
                .map(Handle::getHypothesis)
                .collect(Collectors.toList());
    }

    /** Calculate one generation step.
     * Takes a current generation and calculates the
     * next generation out of it.
     * @param currentGeneration the current generation of hypothesis handles.
     * @return the population of the next generation. This will contain some
     * individuals from the {@code currentGeneration} input.
     * @since 3.0.0
     * */
    private List<Handle<H>> innerCalculateNextGeneration(
            final List<Handle<H>> currentGeneration) {

        List<Handle<H>> nextGeneration = computeEngine.calculateNextGeneration(
                currentGeneration,
                generationSize,
                crossOverRate,
                mutationRate);

        generationNumber++;

        return nextGeneration;
    }

    /** Perform the genetic operation.
     * @return the maximum element, if any.
     */
    private Optional<H> innerFindMaximum() {
        List<Handle<H>> currentGeneration =
                computeEngine.createRandomHypothesisHandles(
                    generationSize);
        Optional<Handle<H>> allTimeMax = Optional.empty();

        generationNumber = 0;
        do {

            List<Handle<H>> nextGeneration =
                    innerCalculateNextGeneration(currentGeneration);

            Optional<Handle<H>> curMax = computeEngine.max(nextGeneration);
            allTimeMax = max(curMax.orElse(null), allTimeMax.orElse(null));

            currentGeneration = nextGeneration;
        } while (allTimeMax.isPresent()
                && algorithmDefinition.loop(allTimeMax.get().getHypothesis()));

        return allTimeMax.map(Handle::getHypothesis);
    }


    /** Perform the genetic optimization.
     * The algorithm will search for an optimal hypothesis until
     * {@link AlgorithmDefinition#loop(Object)} returns {@code false}.
     * New hypothesis are created using the {@code hypothesisSupplier}.
     *
     * @return the maximum
     *     {@link AlgorithmDefinition#calculateFitness(Object)} element,
     *     if any.
     * @see AlgorithmDefinition#loop(Object)
     */
    public Optional<H> findMaximum() {
        return innerFindMaximum();
    }
}
