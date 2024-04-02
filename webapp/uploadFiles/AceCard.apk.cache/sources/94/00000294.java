package ch.boye.httpclientandroidlib.impl.client.cache;

import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.client.cache.HttpCacheEntry;
import ch.boye.httpclientandroidlib.client.cache.Resource;
import ch.boye.httpclientandroidlib.client.cache.ResourceFactory;
import ch.boye.httpclientandroidlib.impl.cookie.DateParseException;
import ch.boye.httpclientandroidlib.impl.cookie.DateUtils;
import com.baseapp.Constants;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

@Immutable
/* loaded from: classes.dex */
class CacheEntryUpdater {
    private final ResourceFactory resourceFactory;

    CacheEntryUpdater() {
        this(new HeapResourceFactory());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CacheEntryUpdater(ResourceFactory resourceFactory) {
        this.resourceFactory = resourceFactory;
    }

    public HttpCacheEntry updateCacheEntry(String requestId, HttpCacheEntry entry, Date requestDate, Date responseDate, HttpResponse response) throws IOException {
        if (response.getStatusLine().getStatusCode() != 304) {
            throw new IllegalArgumentException("Response must have 304 status code");
        }
        Header[] mergedHeaders = mergeHeaders(entry, response);
        Resource oldResource = entry.getResource();
        Resource resource = null;
        if (oldResource != null) {
            resource = this.resourceFactory.copy(requestId, entry.getResource());
            oldResource.dispose();
        }
        return new HttpCacheEntry(requestDate, responseDate, entry.getStatusLine(), mergedHeaders, resource);
    }

    protected Header[] mergeHeaders(HttpCacheEntry entry, HttpResponse response) {
        if (entryAndResponseHaveDateHeader(entry, response) && entryDateHeaderNewerThenResponse(entry, response)) {
            return entry.getAllHeaders();
        }
        List<Header> cacheEntryHeaderList = new ArrayList<>(Arrays.asList(entry.getAllHeaders()));
        removeCacheHeadersThatMatchResponse(cacheEntryHeaderList, response);
        removeCacheEntry1xxWarnings(cacheEntryHeaderList, entry);
        cacheEntryHeaderList.addAll(Arrays.asList(response.getAllHeaders()));
        return (Header[]) cacheEntryHeaderList.toArray(new Header[cacheEntryHeaderList.size()]);
    }

    private void removeCacheHeadersThatMatchResponse(List<Header> cacheEntryHeaderList, HttpResponse response) {
        Header[] arr$ = response.getAllHeaders();
        for (Header responseHeader : arr$) {
            ListIterator<Header> cacheEntryHeaderListIter = cacheEntryHeaderList.listIterator();
            while (cacheEntryHeaderListIter.hasNext()) {
                String cacheEntryHeaderName = cacheEntryHeaderListIter.next().getName();
                if (cacheEntryHeaderName.equals(responseHeader.getName())) {
                    cacheEntryHeaderListIter.remove();
                }
            }
        }
    }

    private void removeCacheEntry1xxWarnings(List<Header> cacheEntryHeaderList, HttpCacheEntry entry) {
        ListIterator<Header> cacheEntryHeaderListIter = cacheEntryHeaderList.listIterator();
        while (cacheEntryHeaderListIter.hasNext()) {
            String cacheEntryHeaderName = cacheEntryHeaderListIter.next().getName();
            if ("Warning".equals(cacheEntryHeaderName)) {
                Header[] arr$ = entry.getHeaders("Warning");
                for (Header cacheEntryWarning : arr$) {
                    if (cacheEntryWarning.getValue().startsWith(Constants.CLIENT_NUMBER)) {
                        cacheEntryHeaderListIter.remove();
                    }
                }
            }
        }
    }

    private boolean entryDateHeaderNewerThenResponse(HttpCacheEntry entry, HttpResponse response) {
        try {
            Date entryDate = DateUtils.parseDate(entry.getFirstHeader("Date").getValue());
            Date responseDate = DateUtils.parseDate(response.getFirstHeader("Date").getValue());
            return entryDate.after(responseDate);
        } catch (DateParseException e) {
            return false;
        }
    }

    private boolean entryAndResponseHaveDateHeader(HttpCacheEntry entry, HttpResponse response) {
        return (entry.getFirstHeader("Date") == null || response.getFirstHeader("Date") == null) ? false : true;
    }
}