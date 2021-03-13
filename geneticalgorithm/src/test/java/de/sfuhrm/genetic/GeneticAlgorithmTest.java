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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

/**
 * Tests the {@linkplain GeneticAlgorithm} with some mock
 * hypothesis.
 * @author Stephan Fuhrmann
 */
public class GeneticAlgorithmTest {


    @Mocked TestHypothesis mockInstance;
    @Mocked ExecutorService mockExecutor;

    @Test
    public void testInitWithValidArgs() {
        GeneticAlgorithm<?> algorithm = new GeneticAlgorithm<>(0.3, 0.05, 100);
        Assertions.assertEquals(0.3, algorithm.getCrossOverRate(), 0.01);
        Assertions.assertEquals(0.05, algorithm.getMutationRate(), 0.01);
        Assertions.assertEquals(100, algorithm.getGenerationSize());
    }

    @Test
    public void testInitWithTooLowCrossOver() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
                    GeneticAlgorithm<?> algorithm = new GeneticAlgorithm<>(-0.1, 0.05, 100);
            }
        );
    }

    @Test
    public void testInitWithTooHighCrossOver() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
                    GeneticAlgorithm<?> algorithm = new GeneticAlgorithm<>(1.1, 0.05, 100);
                }
        );
    }

    @Test
    public void testInitWithTooLowMutation() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
                    GeneticAlgorithm<?> algorithm = new GeneticAlgorithm<>(0.3, -0.1, 100);
                }
        );
    }

    @Test
    public void testInitWithTooHighMutation() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
                    GeneticAlgorithm<?> algorithm = new GeneticAlgorithm<>(0.3, 1.1, 100);
                }
        );
    }

    @Test
    public void testInitWithTooLowGenerationSize() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
                    GeneticAlgorithm<?> algorithm = new GeneticAlgorithm<>(0.3, 0.1, 1);
                }
        );
    }

    @Test
    public void testFindMaximumSingleThread() {
        GeneticAlgorithm<TestHypothesis> algorithm;
        algorithm = new GeneticAlgorithm<>(0.3, 0.1, 100);

        new Expectations() {{
            mockInstance.randomInit(); times = 100;
            mockInstance.calculateFitness(); times = 100; result = 1.0;
            mockInstance.setFitness(1.0); times = 100;
            mockInstance.getFitness(); result = 1.0; maxTimes = 1000;
            mockInstance.setProbability(1.0 / 100.); times = 100;
            mockInstance.crossOver(mockInstance); result = Arrays.asList(mockInstance, mockInstance); times = 15;
            mockInstance.mutate(); times = 10;
        }};

        Optional<TestHypothesis> hypothesisOptional =
                algorithm.findMaximum(
                        h -> false,
                        () -> mockInstance);

        Assertions.assertNotNull(hypothesisOptional);
        Assertions.assertTrue(hypothesisOptional.isPresent(), "hypothesis must be present");
        Assertions.assertSame(mockInstance, hypothesisOptional.get());

        new Verifications() {{
            mockInstance.randomInit(); times = 100;
            mockInstance.getFitness(); minTimes = 200;
            mockInstance.setProbability(1. / 100.); times = 100;
        }};
    }

    @Test
    public void testFindMaximumMultiThread() {
        GeneticAlgorithm<TestHypothesis> algorithm;
        algorithm = new GeneticAlgorithm<>(0.3, 0.1, 100);

        new Expectations() {{
            mockInstance.randomInit(); times = 100;
            mockInstance.getFitness(); result = 1.0; maxTimes = 1000;
            mockInstance.setProbability(1.0 / 100.); times = 100;
            mockInstance.crossOver(mockInstance); result = Arrays.asList(mockInstance, mockInstance); times = 15;
            mockInstance.mutate(); times = 10;
        }};

        Optional<TestHypothesis> hypothesisOptional =
                algorithm.findMaximum(
                        h -> false,
                        () -> mockInstance,
                        mockExecutor);

        Assertions.assertNotNull(hypothesisOptional);
        Assertions.assertTrue(hypothesisOptional.isPresent(), "hypothesis must be present");
        Assertions.assertSame(mockInstance, hypothesisOptional.get());

        new Verifications() {{
            mockInstance.randomInit(); times = 100;
            mockInstance.getFitness(); minTimes = 200;
            mockInstance.setProbability(1. / 100.); times = 100;
        }};
    }
}
