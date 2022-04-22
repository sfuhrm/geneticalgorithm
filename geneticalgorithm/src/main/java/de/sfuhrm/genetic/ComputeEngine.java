package de.sfuhrm.genetic;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/** An engine for computing several operations on
 * genetic hypothesis populations.
 * @param <H> the hypothesis class to compute for.
 * */
abstract class ComputeEngine<H> {

    /** The shared source of randomness. */
    @Getter(AccessLevel.PACKAGE)
    private final Random random;

    /** The algorithm definition to use for hypothesis
     * creation and manipulation.
     * */
    @Getter(AccessLevel.PACKAGE)
    private final AlgorithmDefinition<H> algorithmDefinition;

    /**
     * Creates a new instance.
     * @param inRandom the source of randomness to use.
     * @param inAlgorithmDefinition the algorithm definition to use.
     * */
    ComputeEngine(final Random inRandom,
                  final AlgorithmDefinition<H> inAlgorithmDefinition) {
        this.random = inRandom;
        this.algorithmDefinition = inAlgorithmDefinition;
    }

    /** Create a specific number of random hypothesis.
     * @param count the number of hypothesis to create.
     * @return a list of hypothesis handles just created.
     * */
    abstract List<Handle<H>> createRandomHypothesisHandles(int count);

    /**
     * Selects a fraction of {@code 1-crossOverRate} hypothesis
     * relative to their fitness. Adds the results to the
     * {@code targetCollection}.
     *
     * @param population         the population to select on.
     * @param sumOfProbabilities the
     *                           sum of probabilities of the population.
     * @param targetCount        the number of instances to add to
     *                           the {@code targetCollection}.
     * @param targetCollection   the target list to put selected elements to.
     */
    abstract void select(List<Handle<H>> population,
                double sumOfProbabilities,
                int targetCount,
                Collection<Handle<H>> targetCollection);

    /**
     * Cross-overs a fraction of {@code crossOverRate} hypothesis
     * relative to their fitness.
     *
     * @param population         the population to select on.
     * @param sumOfProbabilities the
     *                           sum of probabilities of the population.
     * @param targetCount        the number of instances to add to
     *                           the {@code targetCollection}.
     * @param targetCollection   the target set to put crossed over elements to.
     */
    abstract void crossover(
            List<Handle<H>> population,
            double sumOfProbabilities,
            int targetCount,
            Collection<Handle<H>> targetCollection);

    /**
     * Mutates a fraction of {@code mutationRate} hypothesis.
     *
     * @param selectedSet   the population to mutate on.
     * @param mutationCount the number of instances to mutate.
     */
    abstract void mutate(List<Handle<H>> selectedSet,
                int mutationCount);

    /**
     * Probabilistically selects one hypothesis relative to the selection
     * probability of it.
     *
     * @param population         the population to select from.
     * @param sumOfProbabilities the
     *                           sum of probabilities of the population.
     * @return the selected element.
     */
    Handle<H> probabilisticSelect(final List<Handle<H>> population,
                                  final double sumOfProbabilities
    ) {
        return innerProbabilisticSelectSimplifiedStochastic(
                population, sumOfProbabilities);
    }

    private Handle<H> innerProbabilisticSelectRealStochastic(
            final List<Handle<H>> population,
            final double sumOfProbabilities
    ) {
        Handle<H> result = population.get(0);
        for (int i = 0; i < population.size(); i++) {
            double randomPoint = getRandom().nextDouble(); // random number
            int index = getRandom().nextInt(population.size());
            result = population.get(index);
            if (randomPoint < result.getProbability()) {
                return result;
            }
        }
        return result;
    }

    private Handle<H> innerProbabilisticSelectSimplifiedStochastic(
            final List<Handle<H>> population,
            final double sumOfProbabilities
    ) {
        Handle<H> result = population.get(0);
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

    /**
     * Find the maximum fitness element of the given collection.
     *
     * @param in the population to find the maximum in.
     * @return the maximum element, if any.
     */
    Optional<Handle<H>> max(final List<Handle<H>> in) {
        Handle<H> result = null;
        double resultFitness = .0;
        for (int i = 0; i < in.size(); i++) {
            Handle<H> current = in.get(i);
            if (null != result) {
                if (current.getFitness() > resultFitness) {
                    result = current;
                    resultFitness = current.getFitness();
                }
            } else {
                result = current;
                resultFitness = current.getFitness();
            }
        }
        return Optional.ofNullable(result);
    }

    /**
     * Updates the fitness and probability of the population.
     * Returns the sum of the probabilities.
     *
     * @param population the population to work on.
     * @return the sum of probabilities, which should be 1.
     */
    abstract double updateFitnessAndGetSumOfProbabilities(
            List<Handle<H>> population);
}
