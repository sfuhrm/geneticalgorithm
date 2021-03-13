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
package de.sfuhrm.genetic.intguessing;

import de.sfuhrm.genetic.GeneticAlgorithm;
import lombok.Getter;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.util.Optional;

/**
 * Example main program for the {@link IntGuessingHypothesis}.
 **/
public final class GuessingExample {

    /** RESET. */
    public static final String ANSI_RESET = "\u001B[0m";
    /** BLACK. */
    public static final String ANSI_BLACK = "\u001B[30m";
    /** RED. */
    public static final String ANSI_RED = "\u001B[31m";
    /** GREEN. */
    public static final String ANSI_GREEN = "\u001B[32m";
    /** YELLOW. */
    public static final String ANSI_YELLOW = "\u001B[33m";
    /** BLUE. */
    public static final String ANSI_BLUE = "\u001B[34m";
    /** PURPLE. */
    public static final String ANSI_PURPLE = "\u001B[35m";
    /** CYAN. */
    public static final String ANSI_CYAN = "\u001B[36m";
    /** WHITE. */
    public static final String ANSI_WHITE = "\u001B[37m";

    /** Is command line help wanted? */
    @Option(name = "-h", usage = "help", aliases = "-help", help = true)
    private boolean help;

    /** The default for the crossover rate for the CLI. */
    private static final double CROSS_OVER_RATE_DEFAULT = 0.5;

    /** The cross over rate (0..1). */
    @Getter
    @Option(name = "-x", usage = "cross over rate (0..1)",
            metaVar = "RATE",
            aliases = "-crossover")
    private double crossOverRate = CROSS_OVER_RATE_DEFAULT;

    /** The default for the mutation rate for the CLI. */
    private static final double MUTATION_RATE_DEFAULT = 0.02;

    /** The mutation rate (0..1). */
    @Getter
    @Option(name = "-m", usage = "mutation rate (0..1)",
            metaVar = "RATE",
            aliases = "-mutation")
    private double mutationRate = MUTATION_RATE_DEFAULT;

    /** The default for the generation size for the CLI. */
    private static final int GENERATION_SIZE_DEFAULT = 150;

    /** The generation/population size in individuals. */
    @Getter
    @Option(name = "-p", usage = "the size of the population/generation "
            + "in individuals (0..n)",
            metaVar = "INDIVIDUALS",
            aliases = "-population")
    private int generationSize = GENERATION_SIZE_DEFAULT;

    /** The default for the genetic array size for the CLI. */
    private static final int ARRAY_SIZE_DEFAULT = 9;

    /** The array size for the integers to guess. */
    @Getter
    @Option(name = "-s", usage = "the size of the array to guess the "
            + "elements of",
            aliases = "-genome")
    private int arraySize = ARRAY_SIZE_DEFAULT;

    private GuessingExample() {
        // no constructor
    }

    private static GuessingExample parse(final String[] args) {
        GuessingExample result = new GuessingExample();
        CmdLineParser parser = new CmdLineParser(result);

        try {
            // parse the arguments.
            parser.parseArgument(args);

            if (result.help) {
                parser.printUsage(System.err);
                return null;
            }

        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            return null;
        }
        return result;
    }

    /** The count of the current generation. */
    private static long generation;
    private static void print(final IntGuessingHypothesis h) {
        generation++;
        System.out.printf("%03d: ", generation);
        for (int i = 0; i < h.getGenome().length; i++) {
            printCell(i, h.getGenome()[i]);
        }
        System.out.println();
    }

    private static void printCell(final int index, final int cellValue) {
        System.out.printf("%s[%d]%s",
                index == cellValue ? ANSI_GREEN : ANSI_RED,
                cellValue,
                ANSI_RESET);
    }

    /** Milliseconds in a second. */
    private static final double MILLIS_PER_SECOND = 1000.;

    /**
     * Main method.
     * @param args ignored command line args.
     * */
    public static void main(final String[] args) {
        GuessingExample guessingExample = parse(args);
        if (guessingExample == null) {
            return;
        }

        GeneticAlgorithm<IntGuessingHypothesis> algorithm =
            new GeneticAlgorithm<>(
                    guessingExample.getCrossOverRate(),
                    guessingExample.getMutationRate(),
                    guessingExample.getGenerationSize());
        int size = guessingExample.getArraySize();
        long start = System.currentTimeMillis();

        Optional<IntGuessingHypothesis> max = algorithm.findMaximum(
                h -> {
                    print(h);
                    return h.calculateFitness() < h.maximumFitness();
                },
                () -> new IntGuessingHypothesis(size));
        System.out.println();
        double duration = (System.currentTimeMillis() - start)
                / MILLIS_PER_SECOND;
        System.out.printf("Maximum is %s with fitness=%.2f,"
                        + " speed=%.2f gen/s%n",
                max,
                max.get().calculateFitness(),
                GuessingExample.generation / duration
                );
    }
}
