package de.sfuhrm.genetic;

import lombok.Getter;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/** An engine for computing several operations on
 * genetic hypothesis populations.
 * @param <H> the hypothesis class to compute for.
 * */
abstract class ComputeEngine<H extends AbstractHypothesis<H>> {

    /** The shared source of randomness. */
    @Getter
    private final Random random;

    /**
     * Creates a new instance.
     * @param inRandom the source of randomness to use.
     * */
    ComputeEngine(final Random inRandom) {
        this.random = inRandom;
    }

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
    abstract void select(List<H> population,
                double sumOfProbabilities,
                int targetCount,
                Collection<H> targetCollection);

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
            List<H> population,
            double sumOfProbabilities,
            int targetCount,
            Collection<H> targetCollection);

    /**
     * Mutates a fraction of {@code mutationRate} hypothesis.
     *
     * @param selectedSet   the population to mutate on.
     * @param mutationCount the number of instances to mutate.
     */
    abstract void mutate(List<H> selectedSet,
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
    H probabilisticSelect(final List<H> population,
                          final double sumOfProbabilities
    ) {
        H result = population.get(0);
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
    Optional<H> max(final List<H> in) {
        Optional<H> result = Optional.empty();
        for (int i = 0; i < in.size(); i++) {
            H current = in.get(i);
            if (result.isPresent()) {
                if (current.getFitness() > result.get().getFitness()) {
                    result = Optional.of(current);
                }
            } else {
                result = Optional.of(current);
            }
        }
        return result;
    }

    /**
     * Updates the fitness and probability of the population.
     * Returns the sum of the probabilities.
     *
     * @param population the population to work on.
     * @return the sum of probabilities, which should be 1.
     */
    abstract double updateFitnessAndGetSumOfProbabilities(
            List<H> population);
}
