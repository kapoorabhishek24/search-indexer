package org.reactome.server.tools.indexer.util;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;

import java.io.IOException;

/**
 * Authentication Interceptor is used for providing credentials to Solr
 *
 * Created by:
 * @author Florian Korninger (florian.korninger@ebi.ac.uk)
 * @since 16.01.16.
 */
public class PreemptiveAuthInterceptor implements HttpRequestInterceptor {

    @Override
    public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
        AuthState authState = (AuthState) context.getAttribute(HttpClientContext.TARGET_AUTH_STATE);

        if (authState.getAuthScheme() == null) {
            CredentialsProvider credentialsProvider = (CredentialsProvider) context.getAttribute(HttpClientContext.CREDS_PROVIDER);
            HttpHost targetHost = (HttpHost) context.getAttribute(HttpCoreContext.HTTP_TARGET_HOST);
            Credentials credentials = credentialsProvider.getCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()));
            if (credentials == null) {
                throw new HttpException("No credentials for preemptive authentication");
            }
            request.addHeader(new BasicScheme().authenticate(credentials,request,context));
        }
    }
}