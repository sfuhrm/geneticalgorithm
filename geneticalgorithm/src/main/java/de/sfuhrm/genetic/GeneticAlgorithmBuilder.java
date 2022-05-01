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

import lombok.NonNull;

import java.util.Random;
import java.util.concurrent.ExecutorService;

/** A builder for {@linkplain GeneticAlgorithm} instances.
 * The builder has defaults that can be seen in the
 * default static final fields:
 * <ul>
 * <li> {@linkplain #CROSS_OVER_RATE_DEFAULT},
 * <li> {@linkplain #MUTATION_RATE_DEFAULT},
 * <li> {@linkplain #GENERATION_SIZE_DEFAULT},
 * <li> no {@linkplain #withExecutorService(ExecutorService)
 * executor service}
 * (only single threaded execution),
 * <li> and a generic pseudo {@linkplain #withRandom(Random)
 * random number} generator.
 * </ul>
 * @param <H> the class the hypothesis data will be of.
 * @author Stephan Fuhrmann
 * */
public class GeneticAlgorithmBuilder<H> {
    /** The algorithm definition to work with. */
    private final AlgorithmDefinition<H> algorithmDefinition;

    /** The random generator to use. */
    private Random random;

    /** The default cross-over rate ({@value #CROSS_OVER_RATE_DEFAULT}).
     * */
    public static final double CROSS_OVER_RATE_DEFAULT = 0.3;

    /** The default mutation-rate ({@value #MUTATION_RATE_DEFAULT}). */
    public static final double MUTATION_RATE_DEFAULT = 0.05;

    /** The default generation size ({@value #GENERATION_SIZE_DEFAULT}).
     * */
    public static final int GENERATION_SIZE_DEFAULT = 100;

    /** The fraction between 0 and 1 at which cross over operations are done.
     * The other part of the population will be filled with selected
     * hypothesis.
     */
    private double crossOverRate = CROSS_OVER_RATE_DEFAULT;

    /** The fraction between 0 and 1 at which mutations are done.
     * This is the fraction of hypothesis that the mutation operator
     * is applied to.
     */
    private double mutationRate = MUTATION_RATE_DEFAULT;

    /** The size of each population generation as a count of individuals.
     */
    private int generationSize = GENERATION_SIZE_DEFAULT;

    /** The executor service to use for parallel execution.
     * This a way to speed up calculation when the generation is
     * large.
     * */
    private ExecutorService executorService;

    /** Creates a new builder.
     * @param inAlgorithmDefinition the non-null
     *                              algorithm definition to generate
     *                              a genetic algorithm with.
     * */
    public GeneticAlgorithmBuilder(
            @NonNull final AlgorithmDefinition<H> inAlgorithmDefinition) {
        this.algorithmDefinition = inAlgorithmDefinition;
    }

    /**
     * Defines the cross-over rate to use.
     * @param inRate the cross-over rate between 0 and 1.
     * @return this instance.
     * @throws IllegalArgumentException if the valid parameter range
     * is exceeded.
     * */
    public GeneticAlgorithmBuilder<H> withCrossOverRate(
            final double inRate) {
        if (inRate < 0. || inRate > 1.) {
            throw new IllegalArgumentException(
                    "Input must be x >= 0 && x >= 1, but is " + inRate);
        }
        this.crossOverRate = inRate;
        return this;
    }

    /**
     * Defines the random generator to use.
     * @param inRandom the random generator to use.
     * @return this instance.
     * @throws IllegalArgumentException if the valid parameter range
     * is exceeded.
     * */
    public GeneticAlgorithmBuilder<H> withRandom(
            @NonNull final Random inRandom) {
        this.random = inRandom;
        return this;
    }

    /**
     * Defines the executor service to use.
     * @param inExecutorService the executor service.
     * @return this instance.
     * @throws NullPointerException if the input
     * reference is {@code null}.
     * */
    public GeneticAlgorithmBuilder<H> withExecutorService(
            @NonNull final ExecutorService inExecutorService) {
        this.executorService = inExecutorService;
        return this;
    }

    /**
     * Defines the mutation rate to use.
     * @param rate the mutation rate between 0 and 1.
     * @return this instance.
     * @throws IllegalArgumentException if the valid parameter range
     * is exceeded.
     * */
    public GeneticAlgorithmBuilder<H> withMutationRate(
            final double rate) {
        if (rate < 0. || rate > 1.) {
            throw new IllegalArgumentException(
                    "Input must be x >= 0 && x >= 1, but is " + rate);
        }
        this.mutationRate = rate;
        return this;
    }

    /**
     * Defines the generation size to use.
     * @param size the generation size greater or equal to 2.
     * @return this instance.
     * @throws IllegalArgumentException if the valid parameter range
     * is exceeded.
     * */
    public GeneticAlgorithmBuilder<H> withGenerationSize(
            final int size) {
        if (size < 2) {
            throw new IllegalArgumentException(
                    "Input must be x >= 2, but is " + size);
        }
        this.generationSize = size;
        return this;
    }

    /** Creates a new instance of a
     * {@link  GeneticAlgorithm} with the
     * defined parameters.
     * @return a new GeneticAlgorithm instance.
     * */
    public GeneticAlgorithm<H> build() {
        Random myRandom = random != null ? random : new Random();

        ComputeEngine<H> myComputeEngine;
        if (executorService != null) {
            myComputeEngine = new ExecutorServiceComputeEngine<>(
                    myRandom,
                    algorithmDefinition,
                    executorService
            );
        } else {
            myComputeEngine = new SimpleComputeEngine<>(
                    myRandom,
                    algorithmDefinition
            );
        }

        return new GeneticAlgorithm(
            crossOverRate,
            mutationRate,
            generationSize,
            algorithmDefinition,
            myComputeEngine,
            myRandom);
    }
}
