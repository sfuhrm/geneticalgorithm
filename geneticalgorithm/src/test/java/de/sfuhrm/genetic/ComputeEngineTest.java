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

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;

/**
 * Test for the {@linkplain ComputeEngine}.
 * @author Stephan Fuhrmann
 */
@ExtendWith(MockitoExtension.class)
public class ComputeEngineTest {

    @Mock
    Handle<TestHypothesis> mockHandleOne;

    @Mock
    Handle<TestHypothesis> mockHandleTwo;

    @Mock
    Handle<TestHypothesis> mockHandleThree;

    @Mock
    AlgorithmDefinition<TestHypothesis> mockDefinition;

    TestRandom mockRandom = new TestRandom();

    private ComputeEngine<TestHypothesis> instance;

    @BeforeEach
    public void setup() {
        instance = new SimpleComputeEngine<>(mockRandom, mockDefinition);
    }

    @Test
    public void init() {
        new SimpleComputeEngine<>(mockRandom, mockDefinition);
    }

    @Test
    public void probabilisticSelectWithSingleElementList() {
        mockRandom.whenNextDouble(0.05);
        when(mockHandleOne.getProbability()).thenReturn(1.);

        Handle<TestHypothesis> result = instance.probabilisticSelect(
                Collections.singletonList(mockHandleOne));

        Assertions.assertEquals(mockHandleOne, result);

        Assertions.assertTrue(mockRandom.isNextDoubleCalled());
        verify(mockHandleOne).getProbability();
    }

    @Test
    public void probabilisticSelectWithTwoElementList() {
        mockRandom.whenNextDouble(0.05);
        when(mockHandleOne.getProbability()).thenReturn(.0);
        when(mockHandleTwo.getProbability()).thenReturn(1.);

        Handle<TestHypothesis> result = instance.probabilisticSelect(
                Arrays.asList(mockHandleOne, mockHandleTwo));

        Assertions.assertEquals(mockHandleTwo, result);
    }

    @Test
    public void probabilisticSelectWithThreeElementList() {
        mockRandom.whenNextDouble(0.35);
        when(mockHandleOne.getProbability()).thenReturn(.0);
        when(mockHandleTwo.getProbability()).thenReturn(.1);
        when(mockHandleThree.getProbability()).thenReturn(.9);

        Handle<TestHypothesis> result = instance.probabilisticSelect(
                Arrays.asList(mockHandleOne, mockHandleTwo, mockHandleThree));

        Assertions.assertEquals(mockHandleThree, result);
    }

    @Test
    public void maxWithEmptyList() {
        Optional<Handle<TestHypothesis>> result = instance.max(Collections.emptyList());

        Assertions.assertFalse(result.isPresent());

        verify(mockHandleOne, never()).getFitness();
    }

    @Test
    public void maxWithSingleElement() {
        when(mockHandleOne.getFitness()).thenReturn(.5);

        Optional<Handle<TestHypothesis>> result = instance.max(Collections.singletonList(mockHandleOne));

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(Optional.of(mockHandleOne), result);
    }

    @Test
    public void maxWithTwoElements() {
        when(mockHandleOne.getFitness()).thenReturn(.5);
        when(mockHandleTwo.getFitness()).thenReturn(.6);

        Optional<Handle<TestHypothesis>> result = instance.max(Arrays.asList(mockHandleOne, mockHandleTwo));

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(Optional.of(mockHandleTwo), result);
    }

    @Test
    public void maxWithThreeElements() {
        when(mockHandleOne.getFitness()).thenReturn(.5);
        when(mockHandleTwo.getFitness()).thenReturn(.6);
        when(mockHandleThree.getFitness()).thenReturn(.8);

        Optional<Handle<TestHypothesis>> result = instance.max(Arrays.asList(mockHandleOne, mockHandleTwo, mockHandleThree));

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(Optional.of(mockHandleThree), result);
    }
}
