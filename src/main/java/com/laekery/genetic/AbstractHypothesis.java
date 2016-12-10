/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.laekery.genetic;

import java.util.List;
import java.util.Random;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/** Hypothesis base class. */
public abstract class AbstractHypothesis<T extends AbstractHypothesis> {

    /** Energy of this hypothesis. */
    @Getter(AccessLevel.PACKAGE) @Setter(AccessLevel.PACKAGE)
    private double energy;
    
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
