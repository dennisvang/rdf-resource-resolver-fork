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
package org.fairdatateam.rdf.resolver.core;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import org.apache.http.HttpHeaders;
import org.eclipse.rdf4j.rio.RDFFormat;

/**
 * Resolver strategy based on matching the file extension in the URL context path.
 */
public class PathExtensionStrategy extends AbstractResolverStrategy {
    @Override
    protected HttpRequest configureRequest(String iri) {
        return HttpRequest.newBuilder()
                .uri(URI.create(iri))
                .build();
    }

    @Override
    protected Optional<RDFFormat> resolveFormat(HttpResponse<InputStream> response) {
        var path = response.headers()
                .firstValue(HttpHeaders.LOCATION)
                .map(URI::create)
                .orElseGet(response::uri)
                .getPath();

        return RDFFormat.matchFileName(path, FORMATS);
    }
}
