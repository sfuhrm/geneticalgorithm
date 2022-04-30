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

import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutorService;
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

    @Mocked
    SimpleComputeEngine<TestHypothesis> mockedComputeEngine;

    Random r;

    @BeforeEach
    public void init() {
        r = new Random(0);
    }

    @Test
    public void testInitWithValidArgs() {
        new Expectations() {{
            mockDefinition.initialize(r);
        }};

        GeneticAlgorithm<?> algorithm = new GeneticAlgorithm<>(0.3, 0.05, 100, mockDefinition, mockedComputeEngine, r);
        Assertions.assertEquals(0.3, algorithm.getCrossOverRate(), 0.01);
        Assertions.assertEquals(0.05, algorithm.getMutationRate(), 0.01);
        Assertions.assertEquals(100, algorithm.getGenerationSize());
    }

    @Test
    public void findMaximum() {
        GeneticAlgorithm<TestHypothesis> algorithm;
        algorithm = new GeneticAlgorithm<>(0.3, 0.1, 100, mockDefinition, mockedComputeEngine, r);

        List<Handle<TestHypothesis>> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(mockHandle);
        }
        new Expectations() {{
            mockedComputeEngine.createRandomHypothesisHandles(100);
            mockedComputeEngine.calculateNextGeneration((List) any, 100, 0.3, 0.1); result = list;
            mockedComputeEngine.max((List) any); result = Optional.of(mockHandle);
            mockDefinition.loop((TestHypothesis) any); times = 1; result = false;
        }};

        Optional<TestHypothesis> hypothesisOptional =
                algorithm.findMaximum();

        Assertions.assertNotNull(hypothesisOptional);
        Assertions.assertTrue(hypothesisOptional.isPresent(), "hypothesis must be present");
        Assertions.assertSame(mockHypothesis, hypothesisOptional.get());
    }

    @Test
    public void calculateNextGeneration() {
        GeneticAlgorithm<TestHypothesis> algorithm;
        algorithm = new GeneticAlgorithm<>(0.3, 0.1, 100, mockDefinition, mockedComputeEngine, r);

        List<TestHypothesis> hypothesisList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            hypothesisList.add(mockHypothesis);
        }
        List<Handle<TestHypothesis>> handleList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            handleList.add(mockHandle);
        }
        new Expectations() {{
            mockedComputeEngine.createRandomHypothesisHandles(anyInt); times = 0;
            mockedComputeEngine.calculateNextGeneration((List) any, 100, 0.3, 0.1); result = handleList;
        }};

        List<TestHypothesis> nextGeneration =
                algorithm.calculateNextGeneration(hypothesisList);

        Assertions.assertNotNull(nextGeneration);
        Assertions.assertEquals(100, nextGeneration.size());
    }
}
