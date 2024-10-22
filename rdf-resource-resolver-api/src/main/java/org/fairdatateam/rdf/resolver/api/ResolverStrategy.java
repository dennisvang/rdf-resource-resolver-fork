/*
 * MIT License
 *
 * Copyright (c) 2021 fairdevkit
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.fairdatateam.rdf.resolver.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.BiFunction;
import org.eclipse.rdf4j.rio.RDFFormat;

/**
 * Strategy interface for custom resource resolving logic. The resolver takes a resource location as input, and resolves
 * it to the corresponding {@link RDFFormat} and {@link InputStream}.
 */
@FunctionalInterface
public interface ResolverStrategy {
    /**
     * Resolves the resource IRI and, if found, applies the transformation function to the resolved resource.
     * @param iri resource location
     * @param func function for transforming the format and stream to the resulting type
     * @param <R> the resulting type
     * @return the transformed result instance, or {@link Optional#empty()} if the resource could not be resolved
     * @throws IOException when the underlying resolver mechanism propagates an I/O exception
     */
    <R> Optional<R> resolve(String iri, BiFunction<RDFFormat, InputStream, R> func) throws IOException;
}
