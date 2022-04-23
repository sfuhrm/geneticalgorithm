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

import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.Executors;

/**
 * Tests the {@linkplain GeneticAlgorithm} with some mock
 * hypothesis.
 * @author Stephan Fuhrmann
 */
public class GeneticAlgorithmTest {


    @Mocked TestHypothesis mockHypothesis;
    @Mocked Handle<TestHypothesis> mockHandle;

    @Mocked
    AlgorithmDefinition<TestHypothesis> mockDefinition;

    @Test
    public void testInitWithValidArgs() {
        GeneticAlgorithm<?> algorithm = new GeneticAlgorithm<>(0.3, 0.05, 100, mockDefinition);
        Assertions.assertEquals(0.3, algorithm.getCrossOverRate(), 0.01);
        Assertions.assertEquals(0.05, algorithm.getMutationRate(), 0.01);
        Assertions.assertEquals(100, algorithm.getGenerationSize());
    }

    @Test
    public void testInitWithTooLowCrossOver() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
                    GeneticAlgorithm<?> algorithm = new GeneticAlgorithm<>(-0.1, 0.05, 100, mockDefinition);
            }
        );
    }

    @Test
    public void testInitWithTooHighCrossOver() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
                    GeneticAlgorithm<?> algorithm = new GeneticAlgorithm<>(1.1, 0.05, 100, mockDefinition);
                }
        );
    }

    @Test
    public void testInitWithTooLowMutation() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
                    GeneticAlgorithm<?> algorithm = new GeneticAlgorithm<>(0.3, -0.1, 100, mockDefinition);
                }
        );
    }

    @Test
    public void testInitWithTooHighMutation() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
                    GeneticAlgorithm<?> algorithm = new GeneticAlgorithm<>(0.3, 1.1, 100, mockDefinition);
                }
        );
    }

    @Test
    public void testInitWithTooLowGenerationSize() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
                    GeneticAlgorithm<?> algorithm = new GeneticAlgorithm<>(0.3, 0.1, 1, mockDefinition);
                }
        );
    }

    @Test
    public void testFindMaximumSingleThread() {
        GeneticAlgorithm<TestHypothesis> algorithm;
        algorithm = new GeneticAlgorithm<>(0.3, 0.1, 100, mockDefinition);

        new Expectations() {{
            mockDefinition.newRandomHypothesis(); times = 100;
            mockDefinition.calculateFitness((TestHypothesis) any); times = 100; result = 1.0;
            mockHandle.getHypothesis(); result = mockHypothesis;
            mockHandle.setFitness(1.0); times = 100;
            mockHandle.getFitness(); result = 1.0; maxTimes = 1000;
            mockHandle.setProbability(1.0 / 100.); times = 100;
            mockDefinition.crossOverHypothesis((TestHypothesis) mockHypothesis, (TestHypothesis) mockHypothesis);
            result = Arrays.asList(mockHypothesis, mockHypothesis); times = 15;

            mockDefinition.mutateHypothesis((TestHypothesis) any); times = 10;
        }};

        Optional<Handle<TestHypothesis>> hypothesisOptional =
                algorithm.findMaximum();

        Assertions.assertNotNull(hypothesisOptional);
        Assertions.assertTrue(hypothesisOptional.isPresent(), "hypothesis must be present");
        Assertions.assertSame(mockHypothesis, hypothesisOptional.get().getHypothesis());

        new Verifications() {{
            mockDefinition.newRandomHypothesis(); times = 100;
            mockHandle.getFitness(); minTimes = 200;
            mockHandle.setProbability(1. / 100.); times = 100;
        }};
    }

    @Test
    public void testFindMaximumMultiThread() {
        GeneticAlgorithm<TestHypothesis> algorithm;
        algorithm = new GeneticAlgorithm<>(0.3, 0.1, 100, mockDefinition);

        new Expectations() {{
            mockDefinition.newRandomHypothesis(); times = 100;
            mockHandle.getFitness(); result = 1.0; maxTimes = 1000;
            mockHandle.setProbability(1.0 / 100.); times = 100;
            mockHandle.getHypothesis(); result = mockHypothesis;
            mockDefinition.crossOverHypothesis(mockHypothesis, mockHypothesis); result = Arrays.asList(mockHypothesis, mockHypothesis); times = 15;
            mockDefinition.mutateHypothesis((TestHypothesis) any); times = 10;
        }};

        Optional<Handle<TestHypothesis>> hypothesisOptional =
                algorithm.findMaximum(
                        Executors.newFixedThreadPool(2));

        Assertions.assertNotNull(hypothesisOptional);
        Assertions.assertTrue(hypothesisOptional.isPresent(), "hypothesis must be present");
        Assertions.assertSame(mockHandle.getHypothesis(), hypothesisOptional.get().getHypothesis());

        new Verifications() {{
            mockDefinition.newRandomHypothesis(); times = 100;
            mockHandle.getFitness(); minTimes = 200;
            mockHandle.setProbability(1. / 100.); times = 100;
        }};
    }
}
