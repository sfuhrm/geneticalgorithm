Genetic Algorithm in Java
===================
![Travis CI Status](https://travis-ci.org/sfuhrm/geneticalgorithm.svg?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/218f01a59758476fab45aa373c3e0ec9)](https://www.codacy.com/app/sfuhrm/geneticalgorithm?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=sfuhrm/geneticalgorithm&amp;utm_campaign=Badge_Grade)
[![Coverage Status](https://coveralls.io/repos/github/sfuhrm/geneticalgorithm/badge.svg)](https://coveralls.io/github/sfuhrm/geneticalgorithm) 
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.sfuhrm/geneticalgorithm/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.sfuhrm/geneticalgorithm)

## Purpose

This is a simple implementation of a genetic algorithm library in Java.
It is not meant to work for every case.

it comes with an example to simplify making the first steps with
genetic algorithms.

## Requirements

The library uses Java 8 functions and will only work with Java 8 and above.

## Usage

Using can be done in the following steps:
* Implement your own implementation of AbstractHypothesis.
* Instantiate the GeneticAlgorithm class with the desired parameters.
* Call GeneticAlgorithm.findMaximum with an appropriate loop abortion function
and hypothesis creation function.

## Example

There is a simple [example](https://github.com/sfuhrm/geneticalgorithm/blob/master/src/test/java/de/sfuhrm/genetic/example/intguessing/GuessingExample.java) that implements the challenge to guess
a sequence of integer numbers. Each correct digit will increase
the fitness score by one.

## License

  Copyright 2016 Stephan Fuhrmann

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
