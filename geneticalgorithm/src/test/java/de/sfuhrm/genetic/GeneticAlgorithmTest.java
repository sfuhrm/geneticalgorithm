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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests the {@linkplain GeneticAlgorithm} with some mock
 * hypothesis.
 * @author Stephan Fuhrmann
 */
@ExtendWith(MockitoExtension.class)
public class GeneticAlgorithmTest {

    @Mock TestHypothesis mockHypothesis;
    @Mock Handle<TestHypothesis> mockHandle;

    @Mock
    AlgorithmDefinition<TestHypothesis> mockDefinition;

    @Mock
    SimpleComputeEngine<TestHypothesis> mockedComputeEngine;

    Random r;

    @BeforeEach
    public void init() {
        r = new Random(0);
    }

    @Test
    public void testInitWithValidArgs() {
        GeneticAlgorithm<?> algorithm = new GeneticAlgorithm<>(0.3, 0.05, 100, mockDefinition, mockedComputeEngine, r);
        Assertions.assertEquals(0.3, algorithm.getCrossOverRate(), 0.01);
        Assertions.assertEquals(0.05, algorithm.getMutationRate(), 0.01);
        Assertions.assertEquals(100, algorithm.getGenerationSize());

        verify(mockDefinition).initialize(r);
    }

    @Test
    public void findMaximum() {
        GeneticAlgorithm<TestHypothesis> algorithm;
        algorithm = new GeneticAlgorithm<>(0.3, 0.1, 100, mockDefinition, mockedComputeEngine, r);

        List<Handle<TestHypothesis>> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(mockHandle);
        }

        when(mockedComputeEngine.calculateNextGeneration(any(), eq(100), eq(0.3), eq(0.1))).thenReturn(list);
        when(mockedComputeEngine.max(any())).thenReturn(Optional.of(mockHandle));
        when(mockDefinition.loop(any())).thenReturn(false);
        when(mockHandle.getHypothesis()).thenReturn(mockHypothesis);

        Optional<TestHypothesis> hypothesisOptional =
                algorithm.findMaximum();

        Assertions.assertNotNull(hypothesisOptional);
        Assertions.assertTrue(hypothesisOptional.isPresent(), "hypothesis must be present");
        Assertions.assertSame(mockHypothesis, hypothesisOptional.get());

        verify(mockedComputeEngine).createRandomHypothesisHandles(100);
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

        when(mockedComputeEngine.calculateNextGeneration(any(), eq(100), eq(0.3), eq(0.1))).thenReturn(handleList);

        List<TestHypothesis> nextGeneration =
                algorithm.calculateNextGeneration(hypothesisList);

        Assertions.assertNotNull(nextGeneration);
        Assertions.assertEquals(100, nextGeneration.size());

        verify(mockedComputeEngine, never()).createRandomHypothesisHandles(anyInt());
    }
}
