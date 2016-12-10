package com.laekery.genetic;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.Setter;

/**
 * Generic genetic algorithm implementation. 
 * @param <H> The hypothesis class to use.
 **/
public class GeneticAlgorithm<H extends AbstractHypothesis<H>> {
        
    /** Randomness source for genetic algorithm operations. */
    private final static Random RANDOM = new Random();
        
    /** Default crossover rate recommended. 
     * @see #crossOverRate
     */
    public final static double DEFAULT_CROSSOVER_RATE = 0.5;
    /** Default mutation rate recommended. 
     * @see #mutationRate
     */
    public final static double DEFAULT_MUTATION_RATE = 0.02;
    
    /** The fraction between 0 and 1 at which cross over operations are done.
     * The other part of the population will be filled with selected
     * hypothesis.
     */
    @Getter @Setter
    private double crossOverRate;
    
    /** The fraction between 0 and 1 at which mutations are done.
     * This is the fraction of hypothesis that the mutation operator
     * is applied to.
     */
    @Getter @Setter
    private double mutationRate;
        
    /** The size of each population generation as a count of individuals.
     */
    @Getter @Setter
    private int generationSize;
    
    /**
     * Constructs a new genetic algorithm.
     * @param crossOverRate the fraction at which the cross over operator is applied to the population, between 0 and 1.
     * @param mutationRate the fraction at which the mutation operator is applied to the population, between 0 and 1.
     * @param generationSize the number of individual hypothesis in the population for each generation, greater than 1.
     * @throws IllegalArgumentException if the parameters are illegal.
     */
    public GeneticAlgorithm(double crossOverRate, double mutationRate, int generationSize) {
        if (crossOverRate < 0. || crossOverRate > 1.) {
            throw new IllegalArgumentException("Cross over rate not between 0 and 1: "+crossOverRate);
        }
        if (mutationRate < 0. || mutationRate > 1.) {
            throw new IllegalArgumentException("Mutation rate not between 0 and 1: "+mutationRate);
        }
        if (generationSize < 2) {
            throw new IllegalArgumentException("Generation size is < 2: "+mutationRate);
        }
        this.crossOverRate = crossOverRate;
        this.mutationRate = mutationRate;
        this.generationSize = generationSize;
    }
    
    /** Selects {@code 1-crossOverRate} hypothesis relative to their fitness. 
     * @param population the population to select on.
     * @param selectedList the target list to put selected elements to.
     */
    void select(List<H> population, Collection<H> selectedList) {
        int selectSize = (int)((1. - crossOverRate) * population.size());
        while (selectedList.size() < selectSize) {
            H selected = probabilisticSelect(population, selectedList, true);
        }
    }
    
    /** Cross overs {@code crossOverRate} hypothesis relative to their fitness. 
     * @param population the population to select on.
     * @param selectedList the target list to put crossed over elements to.
     */
    void crossover(List<H> population, Collection<H> selectedSet) {
        int crossOverSize = (int)((crossOverRate) * population.size());
        for (int i = 0; i < crossOverSize / 2; i++) {
            H first = probabilisticSelect(population, Collections.emptyList(), false);
            H second = probabilisticSelect(population, Collections.emptyList(), false);
            selectedSet.addAll(first.crossOver(second));
        }
    }
    
    /** Mutates {@code mutationRate} hypothesis. 
     * @param selectedSet the population to mutate on.
     */
    void mutate(List<H> selectedSet) {
        int mutationSize = (int)(mutationRate * selectedSet.size());
        for (int i = 0; i < mutationSize; i++) {
            int index = RANDOM.nextInt(selectedSet.size());
            H current = selectedSet.get(index);
            current.mutate();
        }
    }
    
    /** Probabilistically selects one hypothesis relative to the selection
     * probability of it.
     * @param population the population to select from. 
     * @param targetList the target list to eventually add the element to. 
     * @param addToTargetList whether to add the element to the targetList. 
     * @return the selected element.
     */
    H probabilisticSelect(List<H> population, Collection<H> targetList, boolean addToTargetList) {
        H result = population.get(0);
        double totalProbability = population.stream().mapToDouble(h -> h.getProbability()).sum();
        double rand = RANDOM.nextDouble();
        double inflated = rand * totalProbability;
        
        double soFar = 0;
        for (int i=0; i < population.size() && soFar < inflated; i++) {
            result = population.get(i);
            soFar += result.getProbability();
        }
        if (addToTargetList) {
            targetList.add(result);
        }
        return result;
    }
        
    /** Find the maximum fitness element of the given collection.
     * @param in the population to find the maximum in. 
     * @return the maximum element, if any.
     */
    Optional<H> max(Collection<H> in) {
        Optional<H> max = in.stream().sorted(Comparator.comparingDouble(h -> h.getEnergy())).skip(in.size() - 1).findFirst();
        return max;
    }
    
    /** Perform the genetic operation.
     * @param loopCondition the abort condition that stays true while the maximum is not yet reached. Gets presented the best hypothesis as input.
     * @param hypothesisSupplier creation function for new hypothesis.
     * @return the maximum element, if any.
     */
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
