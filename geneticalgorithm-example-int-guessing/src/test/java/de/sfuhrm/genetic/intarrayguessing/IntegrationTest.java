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
package de.sfuhrm.genetic.intarrayguessing;

import de.sfuhrm.genetic.GeneticAlgorithm;
import de.sfuhrm.genetic.Handle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Integration test that checks the convergence of the
 * {@linkplain IntGuessingDefinition} towards the
 * goal.
 * @see GeneticAlgorithm#findMaximum()
 * @author Stephan Fuhrmann
 */
public class IntegrationTest {

    @Test
    public void testFindMaximumSingleThread() {
        int numbers = 4;
        GeneticAlgorithm<int[]> algorithm = new GeneticAlgorithm<>(
                0.3,
                0.1,
                100,
                new IntGuessingDefinition(numbers, true),
                null,
                new Random(0));
        Optional<int[]> hypothesisOptional =
                algorithm.findMaximum();

        Assertions.assertNotNull(hypothesisOptional);
        Assertions.assertTrue(hypothesisOptional.isPresent(), "hypothesis must be present");
        Assertions.assertArrayEquals(new int[] {0,1,2,3}, hypothesisOptional.get());
        System.out.printf("Generations: %d%n", algorithm.getGenerationNumber());
    }

    @Test
    public void testFindMaximumMultiThread() {
        int numbers = 4;
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        GeneticAlgorithm<int[]> algorithm = new GeneticAlgorithm<>(
                0.3,
                0.1,
                100,
                new IntGuessingDefinition(numbers, true),
                executorService,
                new Random(0));
        Optional<int[]> hypothesisOptional =
                algorithm.findMaximum();
        executorService.shutdown();
        Assertions.assertNotNull(hypothesisOptional);
        Assertions.assertTrue(hypothesisOptional.isPresent(), "hypothesis must be present");
        Assertions.assertArrayEquals(new int[] {0,1,2,3}, hypothesisOptional.get());
        System.out.printf("Generations: %d%n", algorithm.getGenerationNumber());
    }
}
