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
package com.github.fairdatateam.rdf.resolver.core;

import com.github.fairdatateam.rdf.resolver.api.ResolverStrategy;
import com.github.fairdatateam.rdf.resolver.api.ResourceResolver;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import org.eclipse.rdf4j.rio.RDFFormat;

/**
 * Core implementation of the {@link ResourceResolver} API. Allows for basic strategy registration.
 */
public class CoreResourceResolver implements ResourceResolver {
    private final Collection<ResolverStrategy> strategies;

    public CoreResourceResolver() {
        strategies = new ArrayList<>();
    }

    public void register(ResolverStrategy strategy) {
        Objects.requireNonNull(strategy, "strategy must not be null");

        strategies.add(strategy);
    }

    /**
     * @throws NullPointerException if {@code iri} or {@code func} is null
     */
    @Override
    public <R> Optional<R> consumeResource(String iri, BiFunction<RDFFormat, InputStream, R> func) throws IOException {
        Objects.requireNonNull(iri, "iri must not be null");
        Objects.requireNonNull(func, "func must not be null");

        for (var strategy : strategies) {
            var result = strategy.resolve(iri, func);

            if (result.isPresent()) {
                return result;
            }
        }

        return Optional.empty();
    }
}
