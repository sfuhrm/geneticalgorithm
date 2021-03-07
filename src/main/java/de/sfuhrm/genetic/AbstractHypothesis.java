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

import java.util.List;
import java.util.Random;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/** Hypothesis base class.
 * Your implementation can rely on that the {@linkplain #randomInit()} method
 * is used after calling the constructor.
 *
 * @param <T> the parameter needs to have the concrete type of the
 *           hypothesis class for the methods to return the
 *           correct subtype.
 * @author Stephan Fuhrmann
 */
public abstract class AbstractHypothesis<T extends AbstractHypothesis<?>> {

    /** Fitness of this hypothesis.
     * Used by {@linkplain GeneticAlgorithm} internally.
     * */
    @Getter(AccessLevel.PACKAGE) @Setter(AccessLevel.PACKAGE)
    private double fitness;

    /** Selection probability based on energy of this hypothesis.
     * Used by {@linkplain GeneticAlgorithm} internally.
     * */
    @Getter(AccessLevel.PACKAGE) @Setter(AccessLevel.PACKAGE)
    private double probability;

    /** The source of randomness that can be used in the hypothesis.
     */
    @Getter(AccessLevel.PROTECTED)
    private static final Random RANDOM = new Random();

    /** Randomly initialize the hypothesis.
     * @return returns {@code this} hypothesis after a random initialization.
     */
    protected abstract T randomInit();

    /** Returns a two-element crossover offspring of this and the
     * other hypothesis. The two offsprings are a combination of
     * {@code this} and the {@code other} instances. Example:
     * if the parents are the vectors {@code (a,b)} and {@code (c,d)},
     * possible cross over children could be
     * {@code (a,d)} and {@code (c,b)}.
     * @param other the other hypothesis to cross-over with.
     * @return a two-element List of the offsprings.
     * @see #mutate() the other genetic operator on this instance.
     */
    protected abstract List<T> crossOver(T other);

    /** Implements the mutation operator on this hypothesis.
     * The mutation operator flips one bit
     * of the genome of the hypothesis. Example: If a hypothesis consists
     * of the vector {@code (a,b,c)}, then a possible mutation result
     * could be {@code (a,d,c)} or {@code (d,b,c)}.
     * @see #crossOver(AbstractHypothesis) the
     * other genetic operator on this instance.
     */
    protected abstract void mutate();

    /** Calculates the fitness of this hypothesis. A bigger result means a
     * better performance of this hypothesis. The winner hypothesis will
     * be the one with a high fitness function result.
     * If calculating the fitness function is expensive, consider
     * using he {@linkplain
     * GeneticAlgorithm#findMaximum(
     * java.util.function.Function,
     * java.util.function.Supplier,
     * java.util.concurrent.ExecutorService)}
     * parallel approach to have a higher throughput.
     * @return a fitness where a bigger number means more fitness. Please
     * be advised that negative numbers will be problematic for the algorithm.
     */
    protected abstract double calculateFitness();
}
