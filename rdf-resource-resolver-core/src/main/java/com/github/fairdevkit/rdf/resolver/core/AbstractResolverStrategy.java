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
package com.github.fairdevkit.rdf.resolver.core;

import com.github.fairdevkit.rdf.resolver.api.ResolverStrategy;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParserRegistry;

public abstract class AbstractResolverStrategy implements ResolverStrategy {
    public static final Set<RDFFormat> FORMATS;
    public static final String ACCEPT_PARAMS;
    public static final HttpClient CLIENT;

    static {
        FORMATS = RDFParserRegistry.getInstance().getKeys();

        var params = new ArrayList<String>();
        for (RDFFormat format : FORMATS) {
            for (String mimeType: format.getMIMETypes()) {
                if (format == RDFFormat.RDFXML) {
                    params.add(mimeType);
                } else if (format == RDFFormat.TURTLE || format == RDFFormat.TRIG) {
                    params.add(mimeType + ";q=0.8");
                } else {
                    params.add(mimeType + ";q=0.5");
                }
            }
        }

        ACCEPT_PARAMS = String.join(",", params);

        CLIENT = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }

    @Override
    public <R> Optional<R> resolve(String iri, BiFunction<RDFFormat, InputStream, R> func) throws IOException {
        var request = configureRequest(iri);

        final HttpResponse<InputStream> response;
        try {
            response = CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream());
        } catch (InterruptedException e) {
            // decide
            throw new RuntimeException(e);
        }

        return resolveFormat(response)
                .map(format -> func.apply(format, response.body()));
    }

    protected abstract HttpRequest configureRequest(String iri);

    protected abstract Optional<RDFFormat> resolveFormat(HttpResponse<InputStream> response);
}
