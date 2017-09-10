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

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.Setter;

/**
 * Generic genetic algorithm implementation. 
 * @param <H> The hypothesis class to use.
 * @author Stephan Fuhrmann
 **/
public class GeneticAlgorithm<H extends AbstractHypothesis<H>> {
        
    /** Randomness source for genetic algorithm operations. */
    private final static Random RANDOM = new Random();
            
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
    
    /** Selects a fraction of {@code 1-crossOverRate} hypothesis relative to their fitness. 
     * @param population the population to select on.
     * @param selectedList the target list to put selected elements to.
     */
    protected void select(List<H> population, Collection<H> selectedList) {
        int selectSize = (int)((1. - crossOverRate) * population.size());
        while (selectedList.size() < selectSize) {
            probabilisticSelect(population, selectedList, true);
        }
    }
    
    /** Cross-overs a fraction of {@code crossOverRate} hypothesis relative to their fitness. 
     * @param population the population to select on.
     * @param selectedSet the target set to put crossed over elements to.
     */
    protected void crossover(List<H> population, Collection<H> selectedSet) {
        int crossOverSize = (int)((crossOverRate) * population.size());
        for (int i = 0; i < crossOverSize / 2; i++) {
            H first = probabilisticSelect(population, Collections.emptyList(), false);
            H second = probabilisticSelect(population, Collections.emptyList(), false);
            selectedSet.addAll(first.crossOver(second));
        }
    }
    
    /** Mutates a fraction of {@code mutationRate} hypothesis. 
     * @param selectedSet the population to mutate on.
     */
    protected void mutate(List<H> selectedSet) {
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
    protected H probabilisticSelect(List<H> population, Collection<H> targetList, boolean addToTargetList) {
        H result = population.get(0);
        double sumOfProbabilities = population.stream().mapToDouble(h -> h.getProbability()).sum();
        double randomPoint = RANDOM.nextDouble(); // random number
        double inflatedPoint = randomPoint * sumOfProbabilities; // a random point in the sum of probabilities
        
        double soFar = 0;
        for (int i=0; i < population.size() && soFar < inflatedPoint; i++) {
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
    protected Optional<H> max(Collection<H> in) {
        Optional<H> max = in.stream().sorted(Comparator.comparingDouble(h -> h.getFitness())).skip(in.size() - 1).findFirst();
        return max;
    }
    
    /** Perform the genetic operation.
     * @param loopCondition the abort condition that stays true while the maximum is not yet reached. Gets presented the best hypothesis as input.
     * @param hypothesisSupplier creation function for new hypothesis.
     * @return the maximum element, if any.
     */
    public Optional<H> findMaximum(Function<H, Boolean> loopCondition, Supplier<H> hypothesisSupplier) {
        List<H> population = new ArrayList<>();
        List<H> selected = new ArrayList<>();
        Optional<H> max = Optional.empty();
        
        for (int i=0; i < generationSize; i++) {
            population.add(hypothesisSupplier.get().randomInit());
        }
        
        do {
            // compute the energy per hypothesis
            population.forEach(h -> h.setFitness(h.calculateFitness()));
            double sumFitness = population.stream().mapToDouble(h -> h.getFitness()).sum();
            population.forEach(h -> h.setProbability(h.getFitness()/ sumFitness));
            
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
        } while (loopCondition.apply(max.get()));
        
        return max;
    }
}
