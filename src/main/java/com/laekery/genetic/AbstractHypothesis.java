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
package com.laekery.genetic;

import java.util.List;
import java.util.Random;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/** Hypothesis base class. 
 * @author Stephan Fuhrmann
 */
public abstract class AbstractHypothesis<T extends AbstractHypothesis> {

    /** Fitness of this hypothesis. */
    @Getter(AccessLevel.PACKAGE) @Setter(AccessLevel.PACKAGE)
    private double fitness;
    
    /** Selection probability based on energy of this hypothesis. */
    @Getter(AccessLevel.PACKAGE) @Setter(AccessLevel.PACKAGE)
    private double probability;
    
    protected final static Random RANDOM = new Random();

    /** Randomly initialize the target zones. */
    protected abstract T randomInit();

    /** Returns a two-element crossover offspring of this and the other hypothesis. 
     * @param other the other hypothesis to cross-over with.
     * @return a two-element List of the offsprings.
     */
    protected abstract List<T> crossOver(T other);
    
    protected abstract double calculateFitness();
    
    protected abstract void mutate();
}
