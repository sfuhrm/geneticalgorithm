package com.laekery.genetic.example;

import com.laekery.genetic.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Energy reduction function approach for Game of Drones.
 **/
class GuessingExample<T extends AbstractHypothesis<T>> {
        
    
    public static void main(String args[]) {
        GeneticAlgorithm<IntGuessingHypothesis> algorithm = new GeneticAlgorithm<>(
                GeneticAlgorithm.DEFAULT_CROSSOVER_RATE,
                GeneticAlgorithm.DEFAULT_MUTATION_RATE,
                150);
        int size = 9;
        algorithm.step(h -> Math.abs(h.calculateFitness()) < size, 
                () -> new IntGuessingHypothesis(size));
    }
}
