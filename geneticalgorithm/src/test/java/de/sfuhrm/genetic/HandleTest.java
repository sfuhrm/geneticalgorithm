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
public class HandleTest {

    @Test
    public void initWithNonNull() {
        Handle<String> handle = new Handle<>("foo");
        Assertions.assertEquals("foo", handle.getHypothesis());
        Assertions.assertEquals(false, handle.isHasFitness());
        Assertions.assertEquals(0., handle.getProbability());
        Assertions.assertEquals(0., handle.getFitness());
    }

    @Test
    public void initWithNull() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            new Handle<>(null);
        });
    }
}
