package com.laekery.genetic;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.Setter;

/**
 * Generic genetic algorithm implementation. 
 **/
public class GeneticAlgorithm<H extends AbstractHypothesis<H>> {
        
    private final static Random RANDOM = new Random();
        
    public final static double DEFAULT_CROSSOVER_RATE = 0.5;
    public final static double DEFAULT_MUTATION_RATE = 0.02;
    
    @Getter @Setter
    private double crossOverRate;
    
    @Getter @Setter
    private double mutationRate;
        
    @Getter @Setter
    private int generationSize;
    
    public GeneticAlgorithm(double crossOverRate, double mutationRate, int generationSize) {
        this.crossOverRate = crossOverRate;
        this.mutationRate = mutationRate;
        this.generationSize = generationSize;
    }
    
    void select(List<H> population, Collection<H> selectedList) {
        int selectSize = (int)((1. - crossOverRate) * population.size());
        while (selectedList.size() < selectSize) {
            H selected = probabilisticSelect(population, selectedList, true);
        }
    }
    
    void crossover(List<H> population, Collection<H> selectedSet) {
        int crossOverSize = (int)((crossOverRate) * population.size());
        for (int i = 0; i < crossOverSize / 2; i++) {
            H first = probabilisticSelect(population, Collections.emptyList(), false);
            H second = probabilisticSelect(population, Collections.emptyList(), false);
            selectedSet.addAll(first.crossOver(second));
        }
    }
    
    void mutate(List<H> selectedSet) {
        int mutationSize = (int)(mutationRate * selectedSet.size());
        for (int i = 0; i < mutationSize; i++) {
            int index = RANDOM.nextInt(selectedSet.size());
            H current = selectedSet.get(index);
            current.mutate();
        }
    }
        
    /** Selects one hypothesis based on its probability.
     * @param population the total population to select from with Hypothesis.propability fields pre-calculated.
     * @param targetList target list of which hypothesis were selected. After selection the selected hypothesis is added to the target list.
     */
    H probabilisticSelect(List<H> population, Collection<H> targetList, boolean addToTargetList) {
        H result = null;
        while (result == null) {
            int index = RANDOM.nextInt(population.size());
            H selected = population.get(index);
            double q = RANDOM.nextDouble();
            if (q < selected.getProbability()) {
                if (addToTargetList) {
                    targetList.add(selected);
                }
                result = selected;
            }
        }
        return result;
    }
    
    Optional<H> max(Collection<H> in) {
        Optional<H> max = in.stream().sorted(Comparator.comparingDouble(h -> h.getEnergy())).skip(in.size() - 1).findFirst();
        return max;
    }
    
    /** Number of old population elements to re-use. */
    public Optional<H> step(Function<H, Boolean> loopCondition, Supplier<H> hypothesisSupplier) {
        List<H> population = new ArrayList<>();
        List<H> selected = new ArrayList<>();
        Optional<H> max = Optional.empty();
        
        for (int i=0; i < generationSize; i++) {
            population.add(hypothesisSupplier.get().randomInit());
        }
        
        int iteration = 0;
        do {
            // compute the energy per hypothesis
            population.forEach(h -> h.setEnergy(h.calculateFitness()));
            double sumFitness = population.stream().mapToDouble(h -> h.getEnergy()).sum();
            population.forEach(h -> h.setProbability(h.getEnergy() / sumFitness));
            
            Optional<H> curMax = max(population);
            if (curMax.isPresent()) {
                max = max.isPresent() ? max(Arrays.asList(curMax.get(), max.get())) : curMax;
            }
        
            selected.clear();
            select(population, selected);
            crossover(population, selected);
            population.clear();
            population.addAll(selected);
            mutate(population);
            iteration++;
        } while (loopCondition.apply(max.get()));
        
        return max;
    }
}
