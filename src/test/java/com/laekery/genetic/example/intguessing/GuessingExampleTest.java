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
package com.laekery.genetic.example.intguessing;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import org.junit.Test;
import static org.junit.Assert.*;

/** 
 * Integration test for the GuessingExample.
 * @author Stephan Fuhrmann
 */
public class GuessingExampleTest {
    @Test
    public void testRun() {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        try (PrintStream capture = new PrintStream(arrayOutputStream)) {
            System.setOut(capture);
            GuessingExample.main(new String[0]);
            byte output[] = arrayOutputStream.toByteArray();
            CharBuffer buffer = Charset.defaultCharset().decode(ByteBuffer.wrap(output));
            assertEquals("Maximum is Optional[[0, 1, 2, 3, 4, 5, 6, 7, 8]]\n", buffer.toString());
        }
    }    
}
