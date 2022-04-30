package de.sfuhrm.genetic;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Random;

/**
 * Test for the {@linkplain ComputeEngine}.
 * @author Stephan Fuhrmann
 */
public class ComputeEngineTest {

    @Injectable
    Handle<TestHypothesis> mockHandleOne;

    @Injectable
    Handle<TestHypothesis> mockHandleTwo;

    @Injectable
    Handle<TestHypothesis> mockHandleThree;

    @Mocked
    AlgorithmDefinition<TestHypothesis> mockDefinition;

    @Mocked
    Random mockRandom;

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
        new Expectations() {{
            mockRandom.nextDouble(); result = 0.05; times = 1;
            mockHandleOne.getProbability(); result = 1.; times = 1;
        }};
        Handle<TestHypothesis> result = instance.probabilisticSelect(
                Collections.singletonList(mockHandleOne));

        Assertions.assertEquals(mockHandleOne, result);
    }

    @Test
    public void probabilisticSelectWithTwoElementList() {
        new Expectations() {{
            mockRandom.nextDouble(); result = 0.05; times = 1;
            mockHandleOne.getProbability(); result = .0; times = 1;
            mockHandleTwo.getProbability(); result = 1.; times = 1;
        }};
        Handle<TestHypothesis> result = instance.probabilisticSelect(
                Arrays.asList(mockHandleOne, mockHandleTwo));

        Assertions.assertEquals(mockHandleTwo, result);
    }

    @Test
    public void probabilisticSelectWithThreeElementList() {
        new Expectations() {{
            mockRandom.nextDouble(); result = 0.35; times = 1;
            mockHandleOne.getProbability(); result = .0; times = 1;
            mockHandleTwo.getProbability(); result = .1; times = 1;
            mockHandleThree.getProbability(); result = .9; times = 1;
        }};
        Handle<TestHypothesis> result = instance.probabilisticSelect(
                Arrays.asList(mockHandleOne, mockHandleTwo, mockHandleThree));

        Assertions.assertEquals(mockHandleThree, result);
    }

    @Test
    public void maxWithEmptyList() {
        new Expectations() {{
            mockHandleOne.getFitness(); result = .5; times = 0;
        }};

        Optional<Handle<TestHypothesis>> result = instance.max(Collections.emptyList());

        Assertions.assertFalse(result.isPresent());
    }

    @Test
    public void maxWithSingleElement() {
        new Expectations() {{
            mockHandleOne.getFitness(); result = .5; times = 1;
        }};

        Optional<Handle<TestHypothesis>> result = instance.max(Collections.singletonList(mockHandleOne));

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(Optional.of(mockHandleOne), result);
    }

    @Test
    public void maxWithTwoElements() {
        new Expectations() {{
            mockHandleOne.getFitness(); result = .5; times = 1;
            mockHandleTwo.getFitness(); result = .6; times = 1;
        }};

        Optional<Handle<TestHypothesis>> result = instance.max(Arrays.asList(mockHandleOne, mockHandleTwo));

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(Optional.of(mockHandleTwo), result);
    }

    @Test
    public void maxWithThreeElements() {
        new Expectations() {{
            mockHandleOne.getFitness(); result = .5; times = 1;
            mockHandleTwo.getFitness(); result = .6; times = 1;
            mockHandleThree.getFitness(); result = .8; times = 1;
        }};

        Optional<Handle<TestHypothesis>> result = instance.max(Arrays.asList(mockHandleOne, mockHandleTwo, mockHandleThree));

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(Optional.of(mockHandleThree), result);
    }
}
