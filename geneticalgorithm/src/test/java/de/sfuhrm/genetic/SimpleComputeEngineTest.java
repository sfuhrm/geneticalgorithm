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
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Test for the {@linkplain SimpleComputeEngine}.
 * @author Stephan Fuhrmann
 */
@ExtendWith(MockitoExtension.class)
public class SimpleComputeEngineTest {
    @Mock
    TestHypothesis mockInstance;

    @Mock
    Handle<TestHypothesis> mockHandle;

    @Mock
    AlgorithmDefinition<TestHypothesis> mockDefinition;

    @Mock
    Random mockRandom;

    private SimpleComputeEngine<TestHypothesis> instance;

    @BeforeEach
    public void setup() {
        instance = new SimpleComputeEngine<>(mockRandom, mockDefinition);
    }

    @Test
    public void init() {
        new SimpleComputeEngine<>(mockRandom, mockDefinition);
    }

    @Test
    public void probabilisticSelect() {
        when(mockHandle.getProbability()).thenReturn(.5);
        when(mockRandom.nextDouble()).thenReturn(0.6);

        List<Handle<TestHypothesis>> population = Arrays.asList(mockHandle, mockHandle);
        Handle<TestHypothesis> result = instance.probabilisticSelect(population);

        Assertions.assertSame(mockHandle, result);
    }

    @Test
    public void select() {
        when(mockHandle.getProbability()).thenReturn(.5);
        when(mockRandom.nextDouble()).thenReturn(0.6);

        List<Handle<TestHypothesis>> population = Arrays.asList(mockHandle, mockHandle);
        List<Handle<TestHypothesis>> target = new ArrayList<>();
        instance.select(population, 3, target);

        Assertions.assertEquals(3, target.size());

        for (Handle<TestHypothesis> hypothesis : target) {
            Assertions.assertSame(mockHandle, hypothesis);
        }
    }

    @Test
    public void crossover() {
        when(mockHandle.getProbability()).thenReturn(.5);
        when(mockDefinition.crossOverHypothesis(any(), any())).thenReturn(Arrays.asList(mockInstance, mockInstance));
        when(mockRandom.nextDouble()).thenReturn(0.6);
        when(mockHandle.getHypothesis()).thenReturn(mockInstance);

        List<Handle<TestHypothesis>> population = Arrays.asList(mockHandle, mockHandle);
        List<Handle<TestHypothesis>> target = new ArrayList<>();
        instance.crossover(population, 2, target);

        Assertions.assertEquals(2, target.size());
        for (Handle<TestHypothesis> handle : target) {
            Assertions.assertSame(mockInstance, handle.getHypothesis());
        }
    }

    @Test
    public void mutate() {
        when(mockDefinition.mutateHypothesis(any())).thenReturn(mockInstance);
        when(mockRandom.nextInt(anyInt())).thenReturn(0);

        List<Handle<TestHypothesis>> population = Arrays.asList(mockHandle, mockHandle);
        instance.mutate(population, 1);

        verify(mockDefinition).mutateHypothesis(any());
        verify(mockRandom).nextInt(anyInt());
    }

    @Test
    public void updateFitnessAndGetSumOfProbabilities() {
        when(mockDefinition.calculateFitness(any())).thenReturn(0.3);
        when(mockHandle.getFitness()).thenReturn(0.3);
        when(mockHandle.isHasFitness()).thenReturn(false);
        when(mockHandle.getHypothesis()).thenReturn(mockInstance);

        List<Handle<TestHypothesis>> population = Arrays.asList(mockHandle, mockHandle);
        instance.updateFitness(population);

        verify(mockDefinition, times(2)).calculateFitness(any());
        verify(mockHandle, times(2)).setFitness(0.3);
        verify(mockHandle, times(4)).getFitness();
        verify(mockRandom, never()).nextDouble();
        verify(mockRandom, never()).nextInt(anyInt());
    }
}
