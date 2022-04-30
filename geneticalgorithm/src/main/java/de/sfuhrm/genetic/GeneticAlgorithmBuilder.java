package de.sfuhrm.genetic;

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

    /** The default cross-over rate (0.3).
     * */
    public static final double CROSS_OVER_RATE_DEFAULT = 0.3;

    /** The default mutation-rate (0.05). */
    public static final double MUTATION_RATE_DEFAULT = 0.05;

    /** The default generation size (100).
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
     * @param set the cross-over rate between 0 and 1.
     * @return this instance.
     * @throws IllegalArgumentException if the valid parameter range
     * is exceeded.
     * */
    public GeneticAlgorithmBuilder<H> withCrossOverRate(
            final double set) {
        if (set < 0. || set > 1.) {
            throw new IllegalArgumentException(
                    "Input must be x >= 0 && x >= 1, but is " + set);
        }
        this.crossOverRate = set;
        return this;
    }

    /**
     * Defines the random generator to use.
     * @param set the random generator to use.
     * @return this instance.
     * @throws IllegalArgumentException if the valid parameter range
     * is exceeded.
     * */
    public GeneticAlgorithmBuilder<H> withRandom(
            @NonNull final Random set) {
        this.random = set;
        return this;
    }

    /**
     * Defines the executor service to use.
     * @param set the executor service.
     * @return this instance.
     * @throws IllegalArgumentException if the valid parameter range
     * is exceeded.
     * */
    public GeneticAlgorithmBuilder<H> withExecutorService(
            @NonNull final ExecutorService set) {
        this.executorService = set;
        return this;
    }

    /**
     * Defines the mutation rate to use.
     * @param set the mutation rate between 0 and 1.
     * @return this instance.
     * @throws IllegalArgumentException if the valid parameter range
     * is exceeded.
     * */
    public GeneticAlgorithmBuilder<H> withMutationRate(
            final double set) {
        if (set < 0. || set > 1.) {
            throw new IllegalArgumentException(
                    "Input must be x >= 0 && x >= 1, but is " + set);
        }
        this.mutationRate = set;
        return this;
    }

    /**
     * Defines the generation size to use.
     * @param set the generation size greater or equal to 2.
     * @return this instance.
     * @throws IllegalArgumentException if the valid parameter range
     * is exceeded.
     * */
    public GeneticAlgorithmBuilder<H> withGenerationSize(
            final int set) {
        if (set < 2) {
            throw new IllegalArgumentException(
                    "Input must be x >= 2, but is " + set);
        }
        this.generationSize = set;
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