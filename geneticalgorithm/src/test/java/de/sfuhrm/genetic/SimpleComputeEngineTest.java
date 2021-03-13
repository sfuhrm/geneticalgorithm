package de.sfuhrm.genetic;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Test for the {@linkplain SimpleComputeEngine}.
 * @author Stephan Fuhrmann
 */
public class SimpleComputeEngineTest {
    @Mocked
    TestHypothesis mockInstance;

    @Mocked
    Random mockRandom;

    private SimpleComputeEngine<TestHypothesis> instance;

    @BeforeEach
    public void setup() {
        instance = new SimpleComputeEngine(mockRandom);
    }

    @Test
    public void init() {
        new SimpleComputeEngine<>(mockRandom);
    }

    @Test
    public void probabilisticSelect() {
        new Expectations() {{
            mockInstance.getProbability(); result = .5; maxTimes = 1000;
            mockRandom.nextDouble(); result = 0.6;
        }};

        List<TestHypothesis> population = Arrays.asList(mockInstance, mockInstance);
        TestHypothesis result = instance.probabilisticSelect(population, 1);

        Assertions.assertSame(mockInstance, result);

        new Verifications() {{
        }};
    }

    @Test
    public void select() {
        new Expectations() {{
            mockInstance.getProbability(); result = .5; maxTimes = 1000;
            mockRandom.nextDouble(); result = 0.6;
        }};

        List<TestHypothesis> population = Arrays.asList(mockInstance, mockInstance);
        List<TestHypothesis> target = new ArrayList<>();
        instance.select(population, 1, 3, target);

        Assertions.assertEquals(3, target.size());

        for (TestHypothesis hypothesis : target) {
            Assertions.assertSame(mockInstance, hypothesis);
        }

        new Verifications() {{
        }};
    }

    @Test
    public void crossover() {
        new Expectations() {{
            mockInstance.getProbability(); result = .5; maxTimes = 1000;
            mockInstance.crossOver((TestHypothesis) any); result = Arrays.asList(mockInstance, mockInstance);
            mockRandom.nextDouble(); result = 0.6;
        }};

        List<TestHypothesis> population = Arrays.asList(mockInstance, mockInstance);
        List<TestHypothesis> target = new ArrayList<>();
        instance.crossover(population, 1, 2, target);

        Assertions.assertEquals(2, target.size());
        for (TestHypothesis hypothesis : target) {
            Assertions.assertSame(mockInstance, hypothesis);
        }

        new Verifications() {{
        }};
    }

    @Test
    public void mutate() {
        new Expectations() {{
            mockInstance.mutate(); times = 1;
            mockRandom.nextInt(anyInt); result = 0;
        }};

        List<TestHypothesis> population = Arrays.asList(mockInstance, mockInstance);
        instance.mutate(population, 1);

        new Verifications() {{
        }};
    }

    @Test
    public void updateFitnessAndGetSumOfProbabilities() {
        new Expectations() {{
            mockInstance.calculateFitness(); result = 0.3; times = 2;
            mockInstance.setFitness(0.3); times = 2;
            mockInstance.getFitness(); result = 0.3; times = 4;
            mockRandom.nextDouble(); times = 0;
            mockRandom.nextInt(anyInt); times = 0;
        }};

        List<TestHypothesis> population = Arrays.asList(mockInstance, mockInstance);
        instance.updateFitnessAndGetSumOfProbabilities(population);

        new Verifications() {{
        }};
    }
}
