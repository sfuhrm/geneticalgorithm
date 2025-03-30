Genetic Algorithm in Java
===================
[![Java CI with Maven](https://github.com/sfuhrm/geneticalgorithm/actions/workflows/maven-ref.yml/badge.svg)](https://github.com/sfuhrm/geneticalgorithm/actions/workflows/maven.yml)
[![Coverage](https://raw.githubusercontent.com/sfuhrm/geneticalgorithm/gh-pages/jacoco.svg)]() 
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.sfuhrm/geneticalgorithm/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.sfuhrm/geneticalgorithm)
[![javadoc](https://javadoc.io/badge2/de.sfuhrm/geneticalgorithm/javadoc.svg)](https://javadoc.io/doc/de.sfuhrm/geneticalgorithm)
[![ReleaseDate](https://img.shields.io/github/release-date/sfuhrm/geneticalgorithm)](https://github.com/sfuhrm/geneticalgorithm/releases)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## Purpose

A genetic algorithm library in Java with focus on easy usage and high performance.
Genetic algorithms are
meta heuristics inspired by the process of natural selection
that belongs to the larger class of evolutionary algorithms.
Genetic algorithms can solve optimization problems when there's only
an evaluation function available (also known as fitness function).

## Illustration: Integer guessing

The following illustration shows the library [guessing a sequence of
30 int values](https://github.com/sfuhrm/geneticalgorithm/blob/master/geneticalgorithm-example-int-guessing/src/main/java/de/sfuhrm/genetic/intarrayguessing/GuessingExample.java) with a genetic population size of 3000 hypothesis.
At the beginning many components of the population are wrong (=red).
With almost every generation (=line) the population improves
in fitness and more and more components turn from wrong (=red)
to correct (=green).

![Guessing an int sequence](https://raw.githubusercontent.com/sfuhrm/geneticalgorithm/master/.github/IntGuessingExample.gif
"Guessing an int sequence")

See [sample code](https://github.com/sfuhrm/geneticalgorithm/blob/master/geneticalgorithm-example-int-guessing/src/main/java/de/sfuhrm/genetic/intarrayguessing/GuessingExample.java).

## Using the library

The process of using the library is summarized in the following picture:
![Steps for using GeneticAlgorithm](https://raw.githubusercontent.com/sfuhrm/geneticalgorithm/master/doc/uml/activity-process-steps-for-geneticalgorithm/activity_process_steps_for_geneticalgorithm.png
"Steps for using GeneticAlgorithm")

An overview of the classes is given in the following class diagram:
![Class diagram of GeneticAlgorithm](https://raw.githubusercontent.com/sfuhrm/geneticalgorithm/master/doc/uml/class-geneticalgorithm/class_geneticalgorithm.png
"Class diagram of GeneticAlgorithm")

There is a [![javadoc](https://javadoc.io/badge2/de.sfuhrm/geneticalgorithm/javadoc.svg)](https://javadoc.io/doc/de.sfuhrm/geneticalgorithm)
documentation online for a more detailed discussion on the classes.

## Example

There is a stand-alone Java application in the Maven subproject
geneticalgorithm-example-int-guessing. This example tries to
guess one sequence of integer numbers. One example is to
guess the sequence [0, 1, 2, 3, 4].

Please see the example code in
[GuessingExample.java](https://github.com/sfuhrm/geneticalgorithm/blob/master/geneticalgorithm-example-int-guessing/src/main/java/de/sfuhrm/genetic/intarrayguessing/GuessingExample.java).

## Including genetic algorithm in your projects

The recommended way of including the library into your project is using 
Apache Maven:

---------------------------------------

```xml
<dependency>
    <groupId>de.sfuhrm</groupId>
    <artifactId>geneticalgorithm</artifactId>
    <version>3.0.0</version>
</dependency>
```

---------------------------------------

## Requirements

The library uses Java 8 functions and will only work with Java 8 and above.
There are no libraries needed besides those build-in the JDK.

## Algorithm reference
The algorithm is based on the book
_Machine learning. Tom M. Mitchell. Published by McGraw-Hill_
(ISBN 0-07-115467-1).

## License

  Copyright 2016-2025 Stephan Fuhrmann

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
