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

import java.util.Collection;
import java.util.Random;

/**
 * Hypothesis creation, manipulation and
 * evaluation callbacks for the genetic algorithm.
 * An instance of this interface is needed
 * to initialize a {@link GeneticAlgorithm} instance
 * using the
 * {@link GeneticAlgorithmBuilder#GeneticAlgorithmBuilder(AlgorithmDefinition)}
 * constructor.
 *
 * The hypothesis should be implemented as an
 * immutable classes since the package caches
 * the fitness of the hypothesis.
 *
 * @param <T> the hypothesis class to use, can be any Java class.
 *           The class should be immutable.
 * @since 3.0.0
 */
public interface AlgorithmDefinition<T> {

    /** Initializes an instance of the algorithm definition.
     * This method will be called only once.
     * The source of randomness is passed in to use resources
     * thoughtfully. Besides that, using the same
     * random number generator will create more predictable
     * results for unit tests.
     * @param r the non-null source of randomness that can be used.
     * */
    void initialize(Random r);

    /** Randomly create a hypothesis. The created
     * hypothesis will be required to be able to
     * be passed in to each of the other
     * methods in this implementation afterwards.
     * @return a new random hypothesis after initialization.
     */
    T newRandomHypothesis();

    /** Implements the mutation operator on a hypothesis.
     * The mutation operator flips one bit
     * of the genome of the hypothesis. Example: If a hypothesis consists
     * of the vector {@code (a,b,c)}, then a possible mutation result
     * could be {@code (a,d,c)} or {@code (d,b,c)}.
     *
     * Implementations will need to create a copy of the
     * input and flip one bit.
     * @param instance the hypothesis to mutate. The instance needs
     *                 to stay unmodified.
     * @return the mutated version of the hypothesis.
     * @see #crossOverHypothesis(Object, Object)  the
     * other genetic operator on this instance.
     */
    T mutateHypothesis(T instance);

    /** Calculates two-element crossover offspring of two parent
     * hypothesis. The two offsprings are a combination of
     * {@code first} and the {@code second} instances. Example:
     * if the parents are the vectors {@code (a,b)} and {@code (c,d)},
     * possible cross over children could be
     * {@code (a,d)} and {@code (c,b)}.
     *
     * Implementations will need to find a random cross-over point,
     * and then create one or more offsprings by recombining
     * the parents.
     *
     * @param first the first hypothesis to cross-over with. Needs
     *                 to stay unmodified.
     * @param second the second hypothesis to cross-over with. Needs
     *                 to stay unmodified.
     * @return the collection of offsprings.
     * @see #mutateHypothesis(Object)
     * the other genetic operator on this instance.
     */
    Collection<T> crossOverHypothesis(T first, T second);

    /** Calculates the fitness of a hypothesis. A bigger fitness means a
     * better performance of this hypothesis. The winner hypothesis will
     * be the one with the highest fitness function result.
     * If calculating the fitness function is expensive, consider
     * using the {@linkplain
     * GeneticAlgorithmBuilder#withExecutorService(ExecutorService)}
     * to have a higher throughput.
     * @param hypothesis the hypothesis to calculate the fitness for.
     * @return a fitness where a bigger number means more fitness. Please
     * be advised that negative numbers will be problematic for the algorithm.
     */
    double calculateFitness(T hypothesis);

    /** Checks whether we still need to continue the search.
     * Helps in the decision whether {@linkplain GeneticAlgorithm#findMaximum()}
     * should continue searching or not.
     * Good implementations for the {@code loopCondition} are:
     * <ul>
     *     <li>a maximum number of generations</li>
     *     <li>a maximum amount of time, for example
     *     10 seconds of optimization</li>
     *     <li>a check whether the supplied hypothesis solves the
     *     problem</li>
     * </ul>
     * @param hypothesis the current best hypothesis to consider when
     *                   choosing to continue searching or not.
     *                   Needs to stay unmofified.
     * @return {@code true} if the search should go on,
     * {@code false} to stop searching.
     * @see GeneticAlgorithm#findMaximum()
     */
    boolean loop(T hypothesis);
}
