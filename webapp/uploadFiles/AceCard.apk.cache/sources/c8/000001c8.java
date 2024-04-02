package ch.boye.httpclientandroidlib.client.utils;

import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.util.EntityUtils;
import java.io.IOException;

/* loaded from: classes.dex */
public class HttpClientUtils {
    private HttpClientUtils() {
    }

    public static void closeQuietly(HttpResponse response) {
        HttpEntity entity;
        if (response != null && (entity = response.getEntity()) != null) {
            try {
                EntityUtils.consume(entity);
            } catch (IOException e) {
            }
        }
    }

    public static void closeQuietly(HttpClient httpClient) {
        if (httpClient != null) {
            httpClient.getConnectionManager().shutdown();
        }
    }
}