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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Test for the {@linkplain SimpleComputeEngine}.
 * @author Stephan Fuhrmann
 */
public class ExecutorServiceComputeEngineTest {
    @Mocked
    TestHypothesis mockHypothesis;

    @Mocked
    Handle<TestHypothesis> mockHandle;

    @Mocked
    AlgorithmDefinition<TestHypothesis> mockDefinition;

    @Mocked
    Random mockRandom;

    ExecutorService executorService;

    private ExecutorServiceComputeEngine<TestHypothesis> instance;

    @BeforeEach
    public void setup() {
        executorService = Executors.newSingleThreadExecutor();
        instance = new ExecutorServiceComputeEngine<>(mockRandom, mockDefinition, executorService);
    }

    @Test
    public void init() {
        new ExecutorServiceComputeEngine<>(mockRandom, mockDefinition, executorService);
    }

    @Test
    public void probabilisticSelect() {
        new Expectations() {{
            mockHandle.getProbability(); result = .5; maxTimes = 1000;
            mockRandom.nextDouble(); result = 0.6;
        }};

        List<Handle<TestHypothesis>> population = Arrays.asList(mockHandle, mockHandle);
        Handle<TestHypothesis> result = instance.probabilisticSelect(population);

        Assertions.assertSame(mockHandle, result);

        new Verifications() {{
        }};
    }

    @Test
    public void select() {
        new Expectations() {{
            mockHandle.getProbability(); result = .5; maxTimes = 1000;
            mockRandom.nextDouble(); result = 0.6;
        }};

        List<Handle<TestHypothesis>> population = Arrays.asList(mockHandle, mockHandle);
        List<Handle<TestHypothesis>> target = new ArrayList<>();
        instance.select(population,  3, target);

        Assertions.assertEquals(3, target.size());

        for (Handle<TestHypothesis> hypothesis : target) {
            Assertions.assertSame(mockHandle, hypothesis);
        }

        new Verifications() {{
        }};
    }

    @Test
    public void crossover() {
        new Expectations() {{
            mockHandle.getProbability(); result = .5; maxTimes = 1000;
            mockDefinition.crossOverHypothesis((TestHypothesis) any, (TestHypothesis) any); result = Arrays.asList(mockHypothesis, mockHypothesis);
            mockRandom.nextDouble(); result = 0.6;
        }};

        List<Handle<TestHypothesis>> population = Arrays.asList(mockHandle, mockHandle);
        List<Handle<TestHypothesis>> target = new ArrayList<>();
        instance.crossover(population,  2, target);

        Assertions.assertEquals(2, target.size());
        for (Handle<TestHypothesis> handle : target) {
            Assertions.assertFalse(handle.isHasFitness());
            Assertions.assertSame(mockHypothesis, handle.getHypothesis());
        }

        new Verifications() {{
        }};
    }

    @Test
    public void mutate() {
        new Expectations() {{
            mockDefinition.mutateHypothesis((TestHypothesis) any); times = 1;
            mockRandom.nextInt(anyInt); result = 0;
        }};

        List<Handle<TestHypothesis>> population = Arrays.asList(mockHandle, mockHandle);
        instance.mutate(population, 1);

        Assertions.assertFalse(mockHandle.isHasFitness());
        new Verifications() {{
            mockHandle.setHasFitness(false);
        }};
    }

    @Test
    public void updateFitnessAndGetSumOfProbabilities() {
        new Expectations() {{
            mockDefinition.calculateFitness((TestHypothesis) any); result = 0.3; times = 2;
            mockHandle.setFitness(0.3); times = 2;
            mockHandle.getFitness(); result = 0.3; times = 4;
            mockRandom.nextDouble(); times = 0;
            mockRandom.nextInt(anyInt); times = 0;
        }};

        List<Handle<TestHypothesis>> population = Arrays.asList(mockHandle, mockHandle);
        instance.updateFitness(population);

        new Verifications() {{
        }};
    }
}
