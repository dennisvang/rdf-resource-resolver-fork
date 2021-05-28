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
package com.github.fairdevkit.rdf.resolver.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.BiFunction;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

/**
 * The Resource Resolver takes a resource identifier and attempts to resolve the resource's content through any number
 * of {@link ResolverStrategy} instances that have been registered.
 */
public interface ResourceResolver {
    /**
     * Resolve the resource identifier {@code iri} through a {@link ResolverStrategy} and transform the resulting
     * content through the transformation function {@code func}.
     * @param iri the resource identifier
     * @param func the transformation function applied to the resolved content
     * @param <R> the resulting type
     * @return the transformed resulting type instance, or {@link Optional#empty()} if the resource could not be
     *         resolved.
     * @throws IOException when the resolving or transformation of the resource triggers an I/O exception
     * @see #resolveFormat(String)
     * @see #resolveResource(String)
     */
    <R> Optional<R> consumeResource(String iri, BiFunction<RDFFormat, InputStream, R> func) throws IOException;

    /**
     * Convenience method for an {@link IRI} overload of {@link #consumeResource(String, BiFunction)}.
     */
    default <R> Optional<R> consumeResource(IRI iri, BiFunction<RDFFormat, InputStream, R> func) throws IOException {
        return consumeResource(iri.stringValue(), func);
    }

    /**
     * Resolve the resource's RDF format.
     * @param iri the resource identifier
     * @return the resource format, or {@link Optional#empty()} if the format could not be determined
     */
    default Optional<RDFFormat> resolveFormat(String iri) throws IOException {
        return consumeResource(iri, (format, stream) -> format);
    }

    /**
     * Convenience method for an {@link IRI} overload of {@link #resolveFormat(String)}.
     */
    default Optional<RDFFormat> resolveFormat(IRI iri) throws IOException {
        return resolveFormat(iri.stringValue());
    }

    /**
     * Resolve the resource's content.
     * @param iri the resource identifier
     * @return the resource content, or {@link Optional#empty()} if the content could not be resolved
     */
    default Optional<Model> resolveResource(String iri) throws IOException {
        return consumeResource(iri, (format, stream) -> {
            try {
                return Rio.parse(stream, format);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    stream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     * Convenience method for an {@link IRI} overload of {@link #resolveResource(String)}.
     */
    default Optional<Model> resolveResource(IRI iri) throws IOException {
        return resolveResource(iri.stringValue());
    }
}
