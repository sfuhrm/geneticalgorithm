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
