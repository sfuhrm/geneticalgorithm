/**
 * Framework for a calculating genetic algorithm optimization.
 *
 * <h2>Steps for usage</h2>
 * You are usually conducting work with these classes:
 * <ol>
 *     <li> Instantiate your {@link de.sfuhrm.genetic.AlgorithmDefinition}
 *     with how to
 *     create, update and evaluate so-called hypothesis.
 *     <li> Create a {@link de.sfuhrm.genetic.GeneticAlgorithm}
 *     with the help of a
 * {@link de.sfuhrm.genetic.GeneticAlgorithmBuilder}.
 * <li> Call either {@link de.sfuhrm.genetic.GeneticAlgorithm#findMaximum()}
 * for the
 * generational loop to be executed for you (less control), or
 * {@link
 * de.sfuhrm.genetic.GeneticAlgorithm#calculateNextGeneration(java.util.List)}
 * for one generational step to be executed for you (more control).
 * <li> Evaluate the results, and eventually adapt the parameters in the
 * {@linkplain de.sfuhrm.genetic.GeneticAlgorithmBuilder}.
 * </ol>
 *
 * <h2>What's a hypothesis</h2>
 * A hypothesis is a model of a possible solution that can be
 * any Java object. A hypothesis needs to be evaluated
 * with a
 * {@link
 * de.sfuhrm.genetic.AlgorithmDefinition#calculateFitness(java.lang.Object)
 * fitness}
 * towards the fitness in respect to the solution of the problem.
 *
 * Examples:
 * <ul>
 * <li> A hypothesis for solving a
 * 9x9 Sudoku can be a two-dimensional 9x9 int array.
 * <li> A hypothesis for one Tic-Tac-Toe move can be a x/y coordinate pair
 * of the next move.
 * </ul>
 * <h2>Genetic operations in AlgorithmDefinition</h2>
 * The genetic operations are defined on hypothesis basically the following:
 * <ul>
 * <li>
 * {@link
 * de.sfuhrm.genetic.AlgorithmDefinition#newRandomHypothesis()
 * Random initialization}: A (usually valid) hypothesis is generated
 * using random values for its components / genes.
 * <li>
 * {@link
 * AlgorithmDefinition#mutateHypothesis(java.lang.Object)
 * Mutation}: A single gene/bit/character/cell of the hypothesis is flipped
 * to another (usually valid) value.
 * <li>
 * {@link
 * AlgorithmDefinition#crossOverHypothesis(java.lang.Object, java.lang.Object)
 * Cross-over}: Two hypothesis are combined at a so-called cross-over
 * point and are recombined to
 * typically two off-springs. The critical part in the cross-over is that
 * the two off-springs should be valid. This restricts the
 * genetic representation of the hypothesis.
 * </ul>
 *
 * @author Stephan Fuhrmann
 */
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
