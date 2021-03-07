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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

/**
 * Generic genetic algorithm implementation.
 * @param <H> The hypothesis class to use.
 * @author Stephan Fuhrmann
 **/
public class GeneticAlgorithm<H extends AbstractHypothesis<H>> {

    /** Randomness source for genetic algorithm operations. */
    private static final Random RANDOM = new Random();

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
     * @param inCrossOverRate the fraction at which the cross over
     *                      operator is applied to the population,
     *                      between 0 and 1.
     * @param inMutationRate the fraction at which the mutation operator
     *                     is applied to the population, between 0 and 1.
     * @param inGenerationSize the number of individual hypothesis in the
     *                       population for each generation, greater than 1.
     * @throws IllegalArgumentException if the parameters are illegal.
     */
    public GeneticAlgorithm(final double inCrossOverRate,
                            final double inMutationRate,
                            final int inGenerationSize) {
        if (inCrossOverRate < 0. || inCrossOverRate > 1.) {
            throw new IllegalArgumentException(
                    "Cross over rate not between 0 and 1: " + inCrossOverRate);
        }
        if (inMutationRate < 0. || inMutationRate > 1.) {
            throw new IllegalArgumentException(
                    "Mutation rate not between 0 and 1: " + inMutationRate);
        }
        if (inGenerationSize < 2) {
            throw new IllegalArgumentException(
                    "Generation size is < 2: " + inMutationRate);
        }
        this.crossOverRate = inCrossOverRate;
        this.mutationRate = inMutationRate;
        this.generationSize = inGenerationSize;
    }

    /** Selects a fraction of {@code 1-crossOverRate} hypothesis
     * relative to their fitness.
     * @param population the population to select on.
     * @param sumOfProbabilities the
     * sum of probabilities of the population.
     * @param selectedList the target list to put selected elements to.
     */
    private void select(final List<H> population,
                          final double sumOfProbabilities,
                          final Collection<H> selectedList) {
        int selectSize = (int) ((1. - crossOverRate) * population.size());
        while (selectedList.size() < selectSize) {
            H selected = probabilisticSelect(
                    population,
                    sumOfProbabilities
            );
            selectedList.add(selected);
        }
    }

    /** Cross-overs a fraction of {@code crossOverRate} hypothesis
     * relative to their fitness.
     * @param population the population to select on.
     * @param sumOfProbabilities the
     * sum of probabilities of the population.
     * @param selectedSet the target set to put crossed over elements to.
     */
    private void crossover(
            final List<H> population,
            final double sumOfProbabilities,
            final Collection<H> selectedSet) {
        int crossOverSize = (int) ((crossOverRate) * population.size());
        for (int i = 0; i < crossOverSize / 2; i++) {
            H first = probabilisticSelect(population,
                    sumOfProbabilities
            );
            H second = probabilisticSelect(population,
                    sumOfProbabilities
            );
            selectedSet.addAll(first.crossOver(second));
        }
    }

    /** Mutates a fraction of {@code mutationRate} hypothesis.
     * @param selectedSet the population to mutate on.
     */
    private void mutate(final List<H> selectedSet) {
        int mutationSize = (int) (mutationRate * selectedSet.size());
        for (int i = 0; i < mutationSize; i++) {
            int index = RANDOM.nextInt(selectedSet.size());
            H current = selectedSet.get(index);
            current.mutate();
        }
    }

    /** Probabilistically selects one hypothesis relative to the selection
     * probability of it.
     * @param population the population to select from.
     * @param sumOfProbabilities the
     * sum of probabilities of the population.
     * @return the selected element.
     */
    private H probabilisticSelect(final List<H> population,
                                  final double sumOfProbabilities
    ) {
        H result = population.get(0);
        double randomPoint = RANDOM.nextDouble(); // random number
        // a random point in the sum of probabilities
        double inflatedPoint = randomPoint * sumOfProbabilities;

        double soFar = 0;
        for (int i = 0; i < population.size() && soFar < inflatedPoint; i++) {
            result = population.get(i);
            soFar += result.getProbability();
        }
        return result;
    }

    /** Find the maximum fitness element of the given collection.
     * @param in the population to find the maximum in.
     * @return the maximum element, if any.
     */
    private Optional<H> max(final Collection<H> in) {
        return in.stream()
                .max(Comparator.comparingDouble(h2 -> h2.getFitness()));
    }

    /** Perform the genetic operation.
     * @param loopCondition the abort condition that stays true while the
     *                      maximum is not yet reached. Gets presented
     *                      the best hypothesis as input.
     * @param hypothesisSupplier creation function for new hypothesis.
     * @param fitnessCalculator a consumer that calculates the fitness.
     * @return the maximum element, if any.
     */
    private Optional<H> innerFindMaximum(
            final Function<H, Boolean> loopCondition,
            final Supplier<H> hypothesisSupplier,
            final Consumer<List<H>> fitnessCalculator) {
        List<H> population = new ArrayList<>();
        List<H> selected = new ArrayList<>();
        Optional<H> max = Optional.empty();

        for (int i = 0; i < generationSize; i++) {
            population.add(hypothesisSupplier.get().randomInit());
        }

        do {
            fitnessCalculator.accept(population);
            double sumFitness = population.stream()
                    .mapToDouble(h -> h.getFitness()).sum();
            population.forEach(h ->
                    h.setProbability(h.getFitness() / sumFitness));
            double sumOfProbabilities = population
                    .stream()
                    .mapToDouble(AbstractHypothesis::getProbability)
                    .sum();

            Optional<H> curMax = max(population);
            if (curMax.isPresent()) {
                if (max.isPresent()) {
                    max = max(Arrays.asList(curMax.get(), max.get()));
                } else {
                    max = curMax;
                }
            }

            selected.clear();
            select(population, sumOfProbabilities, selected);
            crossover(population, sumOfProbabilities, selected);
            population.clear();
            population.addAll(selected);
            mutate(population);
        } while (max.isPresent() && loopCondition.apply(max.get()));

        return max;
    }

    /** Perform the genetic operation.
     * @param loopCondition the abort condition that stays true while the
     *                      maximum is not yet reached. Gets presented
     *                      the best hypothesis as input.
     * @param hypothesisSupplier creation function for new hypothesis.
     * @param executorService the executor service to use when calculating the
     *                        fitness.
     * @return the maximum element, if any.
     */
    public Optional<H> findMaximum(final Function<H, Boolean> loopCondition,
                                   final Supplier<H> hypothesisSupplier,
                                   final ExecutorService executorService) {

        return innerFindMaximum(loopCondition, hypothesisSupplier, list -> {
            // compute the energy per hypothesis
            List<Future<?>> futureList = list
                    .stream()
                    .map(h -> executorService.submit(
                            () -> h.setFitness(h.calculateFitness())))
                    .collect(Collectors.toList());

            futureList.forEach(f -> {
                try {
                    f.get();
                } catch (ExecutionException | InterruptedException e) {
                    // must not happen
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            });
        });
    }

    /** Perform the genetic operation.
     * @param loopCondition the abort condition that stays true while the
     *                      maximum is not yet reached. Gets presented
     *                      the best hypothesis as input.
     * @param hypothesisSupplier creation function for new hypothesis.
     * @return the maximum element, if any.
     */
    public Optional<H> findMaximum(final Function<H, Boolean> loopCondition,
                                   final Supplier<H> hypothesisSupplier) {
        return innerFindMaximum(loopCondition, hypothesisSupplier, list -> {
            // compute the energy per hypothesis
            list.forEach(h -> h.setFitness(h.calculateFitness()));
        });
    }
}
