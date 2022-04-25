package de.sfuhrm.genetic;

import java.util.Collection;
import java.util.Random;

/**
 * A definition of a genetic algorithm instance.
 * @param <T> the hypothesis class to use.
 * @since 3.0.0
 */
public interface AlgorithmDefinition<T> {

    /** Initializes an instance.
     * @param r the source of randomness that can be used.
     * */
    void initialize(Random r);

    /** Randomly create a hypothesis.
     * @return returns {@code this} hypothesis after a random initialization.
     */
    T newRandomHypothesis();

    /** Implements the mutation operator on a hypothesis.
     * The mutation operator flips one bit
     * of the genome of the hypothesis. Example: If a hypothesis consists
     * of the vector {@code (a,b,c)}, then a possible mutation result
     * could be {@code (a,d,c)} or {@code (d,b,c)}.
     * @param instance the hypothesis to mutate.
     * @see #crossOverHypothesis(Object, Object)  the
     * other genetic operator on this instance.
     */
    void mutateHypothesis(T instance);

    /** Calculates two-element crossover offspring of two parent
     * hypothesis. The two offsprings are a combination of
     * {@code first} and the {@code second} instances. Example:
     * if the parents are the vectors {@code (a,b)} and {@code (c,d)},
     * possible cross over children could be
     * {@code (a,d)} and {@code (c,b)}.
     * @param first the first hypothesis to cross-over with.
     * @param second the second hypothesis to cross-over with.
     * @return the collection of offsprings.
     * @see #mutateHypothesis(Object)
     * the other genetic operator on this instance.
     */
    Collection<T> crossOverHypothesis(T first, T second);

    /** Calculates the fitness of a hypothesis. A bigger result means a
     * better performance of this hypothesis. The winner hypothesis will
     * be the one with a high fitness function result.
     * If calculating the fitness function is expensive, consider
     * using the {@linkplain
     * GeneticAlgorithm} with an executor service
     * to have a higher throughput.
     * @param hypothesis the hypothesis to calculate the fitness for.
     * @return a fitness where a bigger number means more fitness. Please
     * be advised that negative numbers will be problematic for the algorithm.
     */
    double calculateFitness(T hypothesis);

    /** Checks whether we still need to continue the search.
     * Presents the best hypothesis to the definition.
     * @param hypothesis the current best hypothesis to consider when
     *                   choosing to continue searching or not.
     * @return {@code true} if the search will go on,
     * {@code false} to stop searching.
     */
    boolean loop(T hypothesis);
}
