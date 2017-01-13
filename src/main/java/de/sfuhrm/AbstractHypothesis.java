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
package de.sfuhrm;

import java.util.List;
import java.util.Random;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/** Hypothesis base class. 
 * @param <T> the parameter needs to have the concrete type of the hypothesis class
 * for the methods to return the correct subtype.
 * @author Stephan Fuhrmann
 */
public abstract class AbstractHypothesis<T extends AbstractHypothesis> {

    /** Fitness of this hypothesis. */
    @Getter(AccessLevel.PACKAGE) @Setter(AccessLevel.PACKAGE)
    private double fitness;
    
    /** Selection probability based on energy of this hypothesis. */
    @Getter(AccessLevel.PACKAGE) @Setter(AccessLevel.PACKAGE)
    private double probability;
    
    /** The source of randomness that can be used in the hypothesis.
     */
    protected final static Random RANDOM = new Random();

    /** Randomly initialize the hypothesis. */
    protected abstract T randomInit();

    /** Returns a two-element crossover offspring of this and the other hypothesis. 
     * @param other the other hypothesis to cross-over with.
     * @return a two-element List of the offsprings.
     */
    protected abstract List<T> crossOver(T other);
    
    /** Calculates the fitness of this hypothesis.
     * @return a fitness where a bigger number means more fitness. Please
     * be advised that negative numbers will be problematic for the algorithm.
     */
    protected abstract double calculateFitness();
    
    /** Implements the mutation operator. The mutation operator flips one bit
     * of the genome of the hypothesis.
     */
    protected abstract void mutate();
}
