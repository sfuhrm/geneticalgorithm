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
package de.sfuhrm.genetic.intguessing;

import de.sfuhrm.genetic.GeneticAlgorithm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Integration test that checks the convergence of the
 * {@linkplain IntGuessingHypothesis} towards the
 * goal.
 * @see GeneticAlgorithm#findMaximum(Function, Supplier, ExecutorService)
 * @see GeneticAlgorithm#findMaximum(Function, Supplier)
 * @author Stephan Fuhrmann
 */
public class IntegrationTest {

    @Test
    public void testFindMaximumSingleThread() {
        int numbers = 4;
        GeneticAlgorithm<IntGuessingHypothesis> algorithm = new GeneticAlgorithm<>(0.3, 0.1, 100);
        Optional<IntGuessingHypothesis> hypothesisOptional =
                algorithm.findMaximum(
                        h -> !Arrays.equals(h.getGenome(), new int[] {0,1,2,3}),
                        () -> new IntGuessingHypothesis(numbers));

        Assertions.assertNotNull(hypothesisOptional);
        Assertions.assertTrue(hypothesisOptional.isPresent(), "hypothesis must be present");
        Assertions.assertArrayEquals(new int[] {0,1,2,3}, hypothesisOptional.get().getGenome());
    }

    @Test
    public void testFindMaximumMultiThread() {
        int numbers = 4;
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        GeneticAlgorithm<IntGuessingHypothesis> algorithm = new GeneticAlgorithm<>(0.3, 0.1, 100);
        Optional<IntGuessingHypothesis> hypothesisOptional =
                algorithm.findMaximum(
                        h -> !Arrays.equals(h.getGenome(), new int[] {0,1,2,3}),
                        () -> new IntGuessingHypothesis(numbers),
                        executorService);
        executorService.shutdown();
        Assertions.assertNotNull(hypothesisOptional);
        Assertions.assertTrue(hypothesisOptional.isPresent(), "hypothesis must be present");
        Assertions.assertArrayEquals(new int[] {0,1,2,3}, hypothesisOptional.get().getGenome());
    }
}
