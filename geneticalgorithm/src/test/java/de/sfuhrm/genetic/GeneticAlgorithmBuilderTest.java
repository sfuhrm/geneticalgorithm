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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

/**
 * Tests the {@linkplain GeneticAlgorithmBuilder}.
 * @author Stephan Fuhrmann
 */
@ExtendWith(MockitoExtension.class)
public class GeneticAlgorithmBuilderTest {


    @Mock
    AlgorithmDefinition<TestHypothesis> mockDefinition;

    @Mock
    ExecutorService mockExecutorService;

    Random r;

    @BeforeEach
    public void init() {
        r = new Random(0);
    }

    @Test
    public void buildWithAlgorithmDefinitionThrowing() {
        assertThrows(NullPointerException.class, () -> {
            GeneticAlgorithm<Object> geneticAlgorithm =
                    new GeneticAlgorithmBuilder<>(null)
                            .build();
        });
    }

    @Test
    public void buildWithAlgorithmDefinition() {
        GeneticAlgorithm<TestHypothesis> geneticAlgorithm =
                new GeneticAlgorithmBuilder<>(mockDefinition)
                .build();

        assertNotNull(geneticAlgorithm);
        assertEquals(GeneticAlgorithmBuilder.GENERATION_SIZE_DEFAULT, geneticAlgorithm.getGenerationSize());
        assertEquals(GeneticAlgorithmBuilder.MUTATION_RATE_DEFAULT, geneticAlgorithm.getMutationRate());
        assertEquals(GeneticAlgorithmBuilder.CROSS_OVER_RATE_DEFAULT, geneticAlgorithm.getCrossOverRate());

        verify(mockDefinition).initialize(any(Random.class));
    }

    @Test
    public void buildWithRandom() {
        GeneticAlgorithm<TestHypothesis> geneticAlgorithm =
                new GeneticAlgorithmBuilder<>(mockDefinition)
                        .withRandom(r)
                        .build();

        assertNotNull(geneticAlgorithm);
        assertEquals(GeneticAlgorithmBuilder.GENERATION_SIZE_DEFAULT, geneticAlgorithm.getGenerationSize());
        assertEquals(GeneticAlgorithmBuilder.MUTATION_RATE_DEFAULT, geneticAlgorithm.getMutationRate());
        assertEquals(GeneticAlgorithmBuilder.CROSS_OVER_RATE_DEFAULT, geneticAlgorithm.getCrossOverRate());

        verify(mockDefinition).initialize(r);
    }

    @Test
    public void buildWithCrossOverThrowing() {
        assertThrows(IllegalArgumentException.class, () -> {
            GeneticAlgorithm<TestHypothesis> geneticAlgorithm =
                    new GeneticAlgorithmBuilder<>(mockDefinition)
                            .withCrossOverRate(-0.5)
                            .build();
        });
    }

    @Test
    public void buildWithCrossOver() {
        GeneticAlgorithm<TestHypothesis> geneticAlgorithm =
                new GeneticAlgorithmBuilder<>(mockDefinition)
                        .withCrossOverRate(0.5)
                        .build();

        assertNotNull(geneticAlgorithm);
        assertEquals(GeneticAlgorithmBuilder.GENERATION_SIZE_DEFAULT, geneticAlgorithm.getGenerationSize());
        assertEquals(GeneticAlgorithmBuilder.MUTATION_RATE_DEFAULT, geneticAlgorithm.getMutationRate());
        assertEquals(0.5, geneticAlgorithm.getCrossOverRate());
    }

    @Test
    public void buildWithMutationThrowing() {
        assertThrows(IllegalArgumentException.class, () -> {
                    GeneticAlgorithm<TestHypothesis> geneticAlgorithm =
                            new GeneticAlgorithmBuilder<>(mockDefinition)
                                    .withMutationRate(-0.5)
                                    .build();
                });
    }

    @Test
    public void buildWithMutation() {
        GeneticAlgorithm<TestHypothesis> geneticAlgorithm =
                new GeneticAlgorithmBuilder<>(mockDefinition)
                        .withMutationRate(0.5)
                        .build();

        assertNotNull(geneticAlgorithm);
        assertEquals(0.5, geneticAlgorithm.getMutationRate());
        assertEquals(GeneticAlgorithmBuilder.CROSS_OVER_RATE_DEFAULT, geneticAlgorithm.getCrossOverRate());
    }

    @Test
    public void buildWithExecutorService() {
        GeneticAlgorithm<TestHypothesis> geneticAlgorithm =
                new GeneticAlgorithmBuilder<>(mockDefinition)
                        .withExecutorService(mockExecutorService)
                        .build();

        assertNotNull(geneticAlgorithm);
        assertEquals(GeneticAlgorithmBuilder.GENERATION_SIZE_DEFAULT, geneticAlgorithm.getGenerationSize());
        assertEquals(GeneticAlgorithmBuilder.MUTATION_RATE_DEFAULT, geneticAlgorithm.getMutationRate());
        assertEquals(GeneticAlgorithmBuilder.CROSS_OVER_RATE_DEFAULT, geneticAlgorithm.getCrossOverRate());
    }

    @Test
    public void buildWithExecutorServiceThrowing() {
        assertThrows(NullPointerException.class, () -> {
            GeneticAlgorithm<TestHypothesis> geneticAlgorithm =
                    new GeneticAlgorithmBuilder<>(mockDefinition)
                            .withExecutorService(null)
                            .build();
        });
    }
}
