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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/** Hypothesis handle class. This is a wrapper around the
 * real hypothesis.
 *
 * @param <T> the type of the hypothesis.
 * @author Stephan Fuhrmann
 * @since 3.0.0
 */
class Handle<T> {

    /** Is the {@linkplain #fitness} calculated? This flag is used
     * for lazy evaluation.
     * @see AlgorithmDefinition#calculateFitness(Object)
     * */
    @Getter(AccessLevel.PACKAGE) @Setter(AccessLevel.PACKAGE)
    private boolean hasFitness;

    /** Fitness of this hypothesis.
     * @see AlgorithmDefinition#calculateFitness(Object)
     * */
    @Getter @Setter(AccessLevel.PACKAGE)
    private double fitness;

    /** Selection probability based on {@linkplain #fitness}
     * of this hypothesis.
     * */
    @Getter(AccessLevel.PACKAGE) @Setter(AccessLevel.PACKAGE)
    private double probability;

    /** Reference to the hypothesis.
     * */
    @Getter
    private final T hypothesis;

    /** Creates an instance.
     * @param inputHypothesis the hypothesis to create a handle for.
     * */
    Handle(@NonNull final T inputHypothesis) {
        this.hypothesis = inputHypothesis;
        this.hasFitness = false;
    }
}
