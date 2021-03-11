Genetic Algorithm in Java
===================
[![Java CI with Maven](https://github.com/sfuhrm/geneticalgorithm/actions/workflows/maven.yml/badge.svg)](https://github.com/sfuhrm/geneticalgorithm/actions/workflows/maven.yml)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/218f01a59758476fab45aa373c3e0ec9)](https://www.codacy.com/app/sfuhrm/geneticalgorithm?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=sfuhrm/geneticalgorithm&amp;utm_campaign=Badge_Grade)
[![Codacy Badge](https://app.codacy.com/project/badge/Coverage/218f01a59758476fab45aa373c3e0ec9)](https://www.codacy.com/gh/sfuhrm/geneticalgorithm/dashboard?utm_source=github.com&utm_medium=referral&utm_content=sfuhrm/geneticalgorithm&utm_campaign=Badge_Coverage)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.sfuhrm/geneticalgorithm/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.sfuhrm/geneticalgorithm)
[![javadoc](https://javadoc.io/badge2/de.sfuhrm/geneticalgorithm/javadoc.svg)](https://javadoc.io/doc/de.sfuhrm/geneticalgorithm)
[![ReleaseDate](https://img.shields.io/github/release-date/sfuhrm/geneticalgorithm)](https://github.com/sfuhrm/geneticalgorithm/releases)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## Purpose

A genetic algorithm library in Java with focus on easy usage and high performance.
Genetic algorithms can be used to solve optimization problems where there's an evaluation
function available (also known as cost).

## Requirements

The library uses Java 8 functions and will only work with Java 8 and above.
There are no libraries needed besides those build-in the JDK.

## Usage

Usage is done in the following steps:
1. Implement your own subclass of
  [AbstractHypothesis](https://javadoc.io/doc/de.sfuhrm/geneticalgorithm/latest/de/sfuhrm/genetic/AbstractHypothesis.html).
  The constructor can rely on the randInit() method to initialize the hypothesis.
2. Instantiate the [GeneticAlgorithm](https://javadoc.io/doc/de.sfuhrm/geneticalgorithm/latest/de/sfuhrm/genetic/GeneticAlgorithm.html)
  class with the desired parameters. Good starting values for
  cross-over rate is `0.3` and for mutation rate is `0.05`.
3. Call one of the [GeneticAlgorithm.findMaximum()](https://javadoc.io/static/de.sfuhrm/geneticalgorithm/1.2.2/de/sfuhrm/genetic/GeneticAlgorithm.html#findMaximum(java.util.function.Function,java.util.function.Supplier))
  methods with an appropriate loop condition function and hypothesis creation function.
  There are multiple variants: One variant for simple usage, and one variant that
  uses an ExecutorService to calculate the fitness in parallel threads.
  The loop condition stays true while you want to loop.
  The hypothesis creation function will usually just create a new instance of your
  `Abstracthypothesis` subclass.

There is a [![javadoc](https://javadoc.io/badge2/de.sfuhrm/geneticalgorithm/javadoc.svg)](https://javadoc.io/doc/de.sfuhrm/geneticalgorithm)
documentation online.

## Example

There is a simple [example](https://github.com/sfuhrm/geneticalgorithm/blob/master/src/test/java/de/sfuhrm/genetic/example/intguessing/GuessingExample.java) that implements the challenge to guess
a sequence of integer numbers. Each correct digit will increase
the fitness score by one.

The example usage is shown here:

```java
GeneticAlgorithm<IntGuessingHypothesis> algorithm = new GeneticAlgorithm<>(
        0.5,
        0.02,
        150);
int size = 9;
Optional<IntGuessingHypothesis> max = algorithm.findMaximum(h -> Math.abs(h.calculateFitness()) < size, 
                () -> new IntGuessingHypothesis(size));
System.out.println("Maximum is "+max);
```

## Including it in your projects

The recommended way of including the library into your project is using maven:

---------------------------------------

```xml
<dependency>
    <groupId>de.sfuhrm</groupId>
    <artifactId>geneticalgorithm</artifactId>
    <version>2.0.0</version>
</dependency>
```

---------------------------------------

## License

  Copyright 2016-2021 Stephan Fuhrmann

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
