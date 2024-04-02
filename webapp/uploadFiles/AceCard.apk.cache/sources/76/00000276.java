package ch.boye.httpclientandroidlib.impl.client;

import ch.boye.httpclientandroidlib.ConnectionReuseStrategy;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpEntityEnclosingRequest;
import ch.boye.httpclientandroidlib.HttpException;
import ch.boye.httpclientandroidlib.HttpHost;
import ch.boye.httpclientandroidlib.HttpRequest;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.ProtocolException;
import ch.boye.httpclientandroidlib.ProtocolVersion;
import ch.boye.httpclientandroidlib.androidextra.HttpClientAndroidLog;
import ch.boye.httpclientandroidlib.annotation.NotThreadSafe;
import ch.boye.httpclientandroidlib.auth.AuthProtocolState;
import ch.boye.httpclientandroidlib.auth.AuthScheme;
import ch.boye.httpclientandroidlib.auth.AuthState;
import ch.boye.httpclientandroidlib.auth.UsernamePasswordCredentials;
import ch.boye.httpclientandroidlib.client.AuthenticationHandler;
import ch.boye.httpclientandroidlib.client.AuthenticationStrategy;
import ch.boye.httpclientandroidlib.client.HttpRequestRetryHandler;
import ch.boye.httpclientandroidlib.client.NonRepeatableRequestException;
import ch.boye.httpclientandroidlib.client.RedirectException;
import ch.boye.httpclientandroidlib.client.RedirectHandler;
import ch.boye.httpclientandroidlib.client.RedirectStrategy;
import ch.boye.httpclientandroidlib.client.RequestDirector;
import ch.boye.httpclientandroidlib.client.UserTokenHandler;
import ch.boye.httpclientandroidlib.client.methods.AbortableHttpRequest;
import ch.boye.httpclientandroidlib.client.methods.HttpUriRequest;
import ch.boye.httpclientandroidlib.client.params.ClientPNames;
import ch.boye.httpclientandroidlib.client.params.HttpClientParams;
import ch.boye.httpclientandroidlib.client.protocol.ClientContext;
import ch.boye.httpclientandroidlib.client.utils.URIUtils;
import ch.boye.httpclientandroidlib.conn.BasicManagedEntity;
import ch.boye.httpclientandroidlib.conn.ClientConnectionManager;
import ch.boye.httpclientandroidlib.conn.ClientConnectionRequest;
import ch.boye.httpclientandroidlib.conn.ConnectionKeepAliveStrategy;
import ch.boye.httpclientandroidlib.conn.ManagedClientConnection;
import ch.boye.httpclientandroidlib.conn.routing.BasicRouteDirector;
import ch.boye.httpclientandroidlib.conn.routing.HttpRoute;
import ch.boye.httpclientandroidlib.conn.routing.HttpRouteDirector;
import ch.boye.httpclientandroidlib.conn.routing.HttpRoutePlanner;
import ch.boye.httpclientandroidlib.conn.scheme.Scheme;
import ch.boye.httpclientandroidlib.impl.auth.BasicScheme;
import ch.boye.httpclientandroidlib.impl.conn.ConnectionShutdownException;
import ch.boye.httpclientandroidlib.message.BasicHttpRequest;
import ch.boye.httpclientandroidlib.params.HttpConnectionParams;
import ch.boye.httpclientandroidlib.params.HttpParams;
import ch.boye.httpclientandroidlib.params.HttpProtocolParams;
import ch.boye.httpclientandroidlib.protocol.ExecutionContext;
import ch.boye.httpclientandroidlib.protocol.HttpContext;
import ch.boye.httpclientandroidlib.protocol.HttpProcessor;
import ch.boye.httpclientandroidlib.protocol.HttpRequestExecutor;
import ch.boye.httpclientandroidlib.util.EntityUtils;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

@NotThreadSafe
/* loaded from: classes.dex */
public class DefaultRequestDirector implements RequestDirector {
    private final HttpAuthenticator authenticator;
    protected final ClientConnectionManager connManager;
    private int execCount;
    protected final HttpProcessor httpProcessor;
    protected final ConnectionKeepAliveStrategy keepAliveStrategy;
    public HttpClientAndroidLog log;
    protected ManagedClientConnection managedConn;
    private int maxRedirects;
    protected final HttpParams params;
    @Deprecated
    protected final AuthenticationHandler proxyAuthHandler;
    protected final AuthState proxyAuthState;
    protected final AuthenticationStrategy proxyAuthStrategy;
    private int redirectCount;
    @Deprecated
    protected final RedirectHandler redirectHandler;
    protected final RedirectStrategy redirectStrategy;
    protected final HttpRequestExecutor requestExec;
    protected final HttpRequestRetryHandler retryHandler;
    protected final ConnectionReuseStrategy reuseStrategy;
    protected final HttpRoutePlanner routePlanner;
    @Deprecated
    protected final AuthenticationHandler targetAuthHandler;
    protected final AuthState targetAuthState;
    protected final AuthenticationStrategy targetAuthStrategy;
    protected final UserTokenHandler userTokenHandler;
    private HttpHost virtualHost;

    @Deprecated
    public DefaultRequestDirector(HttpRequestExecutor requestExec, ClientConnectionManager conman, ConnectionReuseStrategy reustrat, ConnectionKeepAliveStrategy kastrat, HttpRoutePlanner rouplan, HttpProcessor httpProcessor, HttpRequestRetryHandler retryHandler, RedirectHandler redirectHandler, AuthenticationHandler targetAuthHandler, AuthenticationHandler proxyAuthHandler, UserTokenHandler userTokenHandler, HttpParams params) {
        this(new HttpClientAndroidLog(DefaultRequestDirector.class), requestExec, conman, reustrat, kastrat, rouplan, httpProcessor, retryHandler, new DefaultRedirectStrategyAdaptor(redirectHandler), new AuthenticationStrategyAdaptor(targetAuthHandler), new AuthenticationStrategyAdaptor(proxyAuthHandler), userTokenHandler, params);
    }

    @Deprecated
    public DefaultRequestDirector(HttpClientAndroidLog log, HttpRequestExecutor requestExec, ClientConnectionManager conman, ConnectionReuseStrategy reustrat, ConnectionKeepAliveStrategy kastrat, HttpRoutePlanner rouplan, HttpProcessor httpProcessor, HttpRequestRetryHandler retryHandler, RedirectStrategy redirectStrategy, AuthenticationHandler targetAuthHandler, AuthenticationHandler proxyAuthHandler, UserTokenHandler userTokenHandler, HttpParams params) {
        this(new HttpClientAndroidLog(DefaultRequestDirector.class), requestExec, conman, reustrat, kastrat, rouplan, httpProcessor, retryHandler, redirectStrategy, new AuthenticationStrategyAdaptor(targetAuthHandler), new AuthenticationStrategyAdaptor(proxyAuthHandler), userTokenHandler, params);
    }

    public DefaultRequestDirector(HttpClientAndroidLog log, HttpRequestExecutor requestExec, ClientConnectionManager conman, ConnectionReuseStrategy reustrat, ConnectionKeepAliveStrategy kastrat, HttpRoutePlanner rouplan, HttpProcessor httpProcessor, HttpRequestRetryHandler retryHandler, RedirectStrategy redirectStrategy, AuthenticationStrategy targetAuthStrategy, AuthenticationStrategy proxyAuthStrategy, UserTokenHandler userTokenHandler, HttpParams params) {
        if (log == null) {
            throw new IllegalArgumentException("Log may not be null.");
        }
        if (requestExec == null) {
            throw new IllegalArgumentException("Request executor may not be null.");
        }
        if (conman == null) {
            throw new IllegalArgumentException("Client connection manager may not be null.");
        }
        if (reustrat == null) {
            throw new IllegalArgumentException("Connection reuse strategy may not be null.");
        }
        if (kastrat == null) {
            throw new IllegalArgumentException("Connection keep alive strategy may not be null.");
        }
        if (rouplan == null) {
            throw new IllegalArgumentException("Route planner may not be null.");
        }
        if (httpProcessor == null) {
            throw new IllegalArgumentException("HTTP protocol processor may not be null.");
        }
        if (retryHandler == null) {
            throw new IllegalArgumentException("HTTP request retry handler may not be null.");
        }
        if (redirectStrategy == null) {
            throw new IllegalArgumentException("Redirect strategy may not be null.");
        }
        if (targetAuthStrategy == null) {
            throw new IllegalArgumentException("Target authentication strategy may not be null.");
        }
        if (proxyAuthStrategy == null) {
            throw new IllegalArgumentException("Proxy authentication strategy may not be null.");
        }
        if (userTokenHandler == null) {
            throw new IllegalArgumentException("User token handler may not be null.");
        }
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        this.log = log;
        this.authenticator = new HttpAuthenticator(log);
        this.requestExec = requestExec;
        this.connManager = conman;
        this.reuseStrategy = reustrat;
        this.keepAliveStrategy = kastrat;
        this.routePlanner = rouplan;
        this.httpProcessor = httpProcessor;
        this.retryHandler = retryHandler;
        this.redirectStrategy = redirectStrategy;
        this.targetAuthStrategy = targetAuthStrategy;
        this.proxyAuthStrategy = proxyAuthStrategy;
        this.userTokenHandler = userTokenHandler;
        this.params = params;
        if (redirectStrategy instanceof DefaultRedirectStrategyAdaptor) {
            this.redirectHandler = ((DefaultRedirectStrategyAdaptor) redirectStrategy).getHandler();
        } else {
            this.redirectHandler = null;
        }
        if (targetAuthStrategy instanceof AuthenticationStrategyAdaptor) {
            this.targetAuthHandler = ((AuthenticationStrategyAdaptor) targetAuthStrategy).getHandler();
        } else {
            this.targetAuthHandler = null;
        }
        if (proxyAuthStrategy instanceof AuthenticationStrategyAdaptor) {
            this.proxyAuthHandler = ((AuthenticationStrategyAdaptor) proxyAuthStrategy).getHandler();
        } else {
            this.proxyAuthHandler = null;
        }
        this.managedConn = null;
        this.execCount = 0;
        this.redirectCount = 0;
        this.targetAuthState = new AuthState();
        this.proxyAuthState = new AuthState();
        this.maxRedirects = this.params.getIntParameter(ClientPNames.MAX_REDIRECTS, 100);
    }

    private RequestWrapper wrapRequest(HttpRequest request) throws ProtocolException {
        return request instanceof HttpEntityEnclosingRequest ? new EntityEnclosingRequestWrapper((HttpEntityEnclosingRequest) request) : new RequestWrapper(request);
    }

    protected void rewriteRequestURI(RequestWrapper request, HttpRoute route) throws ProtocolException {
        URI uri;
        try {
            URI uri2 = request.getURI();
            if (route.getProxyHost() != null && !route.isTunnelled()) {
                if (!uri2.isAbsolute()) {
                    HttpHost target = route.getTargetHost();
                    uri = URIUtils.rewriteURI(uri2, target, true);
                } else {
                    uri = URIUtils.rewriteURI(uri2);
                }
            } else if (uri2.isAbsolute()) {
                uri = URIUtils.rewriteURI(uri2, null, true);
            } else {
                uri = URIUtils.rewriteURI(uri2);
            }
            request.setURI(uri);
        } catch (URISyntaxException ex) {
            throw new ProtocolException("Invalid URI: " + request.getRequestLine().getUri(), ex);
        }
    }

    @Override // ch.boye.httpclientandroidlib.client.RequestDirector
    public HttpResponse execute(HttpHost target, HttpRequest request, HttpContext context) throws HttpException, IOException {
        HttpHost target2;
        String s;
        context.setAttribute(ClientContext.TARGET_AUTH_STATE, this.targetAuthState);
        context.setAttribute(ClientContext.PROXY_AUTH_STATE, this.proxyAuthState);
        RequestWrapper origWrapper = wrapRequest(request);
        origWrapper.setParams(this.params);
        HttpRoute origRoute = determineRoute(target, origWrapper, context);
        this.virtualHost = (HttpHost) origWrapper.getParams().getParameter(ClientPNames.VIRTUAL_HOST);
        if (this.virtualHost != null && this.virtualHost.getPort() == -1) {
            HttpHost host = target != null ? target : origRoute.getTargetHost();
            int port = host.getPort();
            if (port != -1) {
                this.virtualHost = new HttpHost(this.virtualHost.getHostName(), port, this.virtualHost.getSchemeName());
            }
        }
        RoutedRequest roureq = new RoutedRequest(origWrapper, origRoute);
        boolean reuse = false;
        boolean done = false;
        HttpResponse response = null;
        HttpHost target3 = target;
        while (!done) {
            try {
                RequestWrapper wrapper = roureq.getRequest();
                HttpRoute route = roureq.getRoute();
                Object userToken = context.getAttribute(ClientContext.USER_TOKEN);
                if (this.managedConn == null) {
                    ClientConnectionRequest connRequest = this.connManager.requestConnection(route, userToken);
                    if (request instanceof AbortableHttpRequest) {
                        ((AbortableHttpRequest) request).setConnectionRequest(connRequest);
                    }
                    long timeout = HttpClientParams.getConnectionManagerTimeout(this.params);
                    try {
                        this.managedConn = connRequest.getConnection(timeout, TimeUnit.MILLISECONDS);
                        if (HttpConnectionParams.isStaleCheckingEnabled(this.params) && this.managedConn.isOpen()) {
                            this.log.debug("Stale connection check");
                            if (this.managedConn.isStale()) {
                                this.log.debug("Stale connection detected");
                                this.managedConn.close();
                            }
                        }
                    } catch (InterruptedException interrupted) {
                        InterruptedIOException iox = new InterruptedIOException();
                        iox.initCause(interrupted);
                        throw iox;
                    }
                }
                if (request instanceof AbortableHttpRequest) {
                    ((AbortableHttpRequest) request).setReleaseTrigger(this.managedConn);
                }
                try {
                    tryConnect(roureq, context);
                    String userinfo = wrapper.getURI().getUserInfo();
                    if (userinfo != null) {
                        this.targetAuthState.update(new BasicScheme(), new UsernamePasswordCredentials(userinfo));
                    }
                    HttpHost proxy = route.getProxyHost();
                    if (this.virtualHost != null) {
                        target2 = this.virtualHost;
                    } else {
                        URI requestURI = wrapper.getURI();
                        target2 = requestURI.isAbsolute() ? new HttpHost(requestURI.getHost(), requestURI.getPort(), requestURI.getScheme()) : target3;
                    }
                    if (target2 == null) {
                        try {
                            target2 = route.getTargetHost();
                        } catch (HttpException e) {
                            ex = e;
                            abortConnection();
                            throw ex;
                        } catch (ConnectionShutdownException e2) {
                            ex = e2;
                            InterruptedIOException ioex = new InterruptedIOException("Connection has been shut down");
                            ioex.initCause(ex);
                            throw ioex;
                        } catch (IOException e3) {
                            ex = e3;
                            abortConnection();
                            throw ex;
                        } catch (RuntimeException e4) {
                            ex = e4;
                            abortConnection();
                            throw ex;
                        }
                    }
                    wrapper.resetHeaders();
                    rewriteRequestURI(wrapper, route);
                    context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, target2);
                    context.setAttribute(ExecutionContext.HTTP_PROXY_HOST, proxy);
                    context.setAttribute(ExecutionContext.HTTP_CONNECTION, this.managedConn);
                    this.requestExec.preProcess(wrapper, this.httpProcessor, context);
                    response = tryExecute(roureq, context);
                    if (response == null) {
                        target3 = target2;
                    } else {
                        response.setParams(this.params);
                        this.requestExec.postProcess(response, this.httpProcessor, context);
                        reuse = this.reuseStrategy.keepAlive(response, context);
                        if (reuse) {
                            long duration = this.keepAliveStrategy.getKeepAliveDuration(response, context);
                            if (this.log.isDebugEnabled()) {
                                if (duration > 0) {
                                    s = "for " + duration + " " + TimeUnit.MILLISECONDS;
                                } else {
                                    s = "indefinitely";
                                }
                                this.log.debug("Connection can be kept alive " + s);
                            }
                            this.managedConn.setIdleDuration(duration, TimeUnit.MILLISECONDS);
                        }
                        RoutedRequest followup = handleResponse(roureq, response, context);
                        if (followup == null) {
                            done = true;
                        } else {
                            if (reuse) {
                                HttpEntity entity = response.getEntity();
                                EntityUtils.consume(entity);
                                this.managedConn.markReusable();
                            } else {
                                this.managedConn.close();
                                if (this.proxyAuthState.getState().compareTo(AuthProtocolState.CHALLENGED) > 0 && this.proxyAuthState.getAuthScheme() != null && this.proxyAuthState.getAuthScheme().isConnectionBased()) {
                                    this.log.debug("Resetting proxy auth state");
                                    this.proxyAuthState.reset();
                                }
                                if (this.targetAuthState.getState().compareTo(AuthProtocolState.CHALLENGED) > 0 && this.targetAuthState.getAuthScheme() != null && this.targetAuthState.getAuthScheme().isConnectionBased()) {
                                    this.log.debug("Resetting target auth state");
                                    this.targetAuthState.reset();
                                }
                            }
                            if (!followup.getRoute().equals(roureq.getRoute())) {
                                releaseConnection();
                            }
                            roureq = followup;
                        }
                        if (this.managedConn != null) {
                            if (userToken == null) {
                                userToken = this.userTokenHandler.getUserToken(context);
                                context.setAttribute(ClientContext.USER_TOKEN, userToken);
                            }
                            if (userToken != null) {
                                this.managedConn.setState(userToken);
                            }
                        }
                        target3 = target2;
                    }
                } catch (TunnelRefusedException ex) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug(ex.getMessage());
                    }
                    response = ex.getResponse();
                }
            } catch (HttpException e5) {
                ex = e5;
            } catch (ConnectionShutdownException e6) {
                ex = e6;
            } catch (IOException e7) {
                ex = e7;
            } catch (RuntimeException e8) {
                ex = e8;
            }
        }
        if (response == null || response.getEntity() == null || !response.getEntity().isStreaming()) {
            if (reuse) {
                this.managedConn.markReusable();
            }
            releaseConnection();
        } else {
            HttpEntity entity2 = response.getEntity();
            response.setEntity(new BasicManagedEntity(entity2, this.managedConn, reuse));
        }
        return response;
    }

    private void tryConnect(RoutedRequest req, HttpContext context) throws HttpException, IOException {
        HttpRoute route = req.getRoute();
        HttpRequest wrapper = req.getRequest();
        int connectCount = 0;
        while (true) {
            context.setAttribute(ExecutionContext.HTTP_REQUEST, wrapper);
            connectCount++;
            try {
                if (!this.managedConn.isOpen()) {
                    this.managedConn.open(route, context, this.params);
                } else {
                    this.managedConn.setSocketTimeout(HttpConnectionParams.getSoTimeout(this.params));
                }
                establishRoute(route, context);
                return;
            } catch (IOException ex) {
                try {
                    this.managedConn.close();
                } catch (IOException e) {
                }
                if (this.retryHandler.retryRequest(ex, connectCount, context)) {
                    if (this.log.isInfoEnabled()) {
                        this.log.info("I/O exception (" + ex.getClass().getName() + ") caught when connecting to the target host: " + ex.getMessage());
                        if (this.log.isDebugEnabled()) {
                            this.log.debug(ex.getMessage(), ex);
                        }
                        this.log.info("Retrying connect");
                    }
                } else {
                    throw ex;
                }
            }
        }
    }

    private HttpResponse tryExecute(RoutedRequest req, HttpContext context) throws HttpException, IOException {
        RequestWrapper wrapper = req.getRequest();
        HttpRoute route = req.getRoute();
        Exception retryReason = null;
        while (true) {
            this.execCount++;
            wrapper.incrementExecCount();
            if (!wrapper.isRepeatable()) {
                this.log.debug("Cannot retry non-repeatable request");
                if (retryReason != null) {
                    throw new NonRepeatableRequestException("Cannot retry request with a non-repeatable request entity.  The cause lists the reason the original request failed.", retryReason);
                }
                throw new NonRepeatableRequestException("Cannot retry request with a non-repeatable request entity.");
            }
            try {
                if (!this.managedConn.isOpen()) {
                    if (!route.isTunnelled()) {
                        this.log.debug("Reopening the direct connection.");
                        this.managedConn.open(route, context, this.params);
                    } else {
                        this.log.debug("Proxied connection. Need to start over.");
                        return null;
                    }
                }
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Attempt " + this.execCount + " to execute request");
                }
                HttpResponse response = this.requestExec.execute(wrapper, this.managedConn, context);
                return response;
            } catch (IOException ex) {
                this.log.debug("Closing the connection.");
                try {
                    this.managedConn.close();
                } catch (IOException e) {
                }
                if (this.retryHandler.retryRequest(ex, wrapper.getExecCount(), context)) {
                    if (this.log.isInfoEnabled()) {
                        this.log.info("I/O exception (" + ex.getClass().getName() + ") caught when processing request: " + ex.getMessage());
                    }
                    if (this.log.isDebugEnabled()) {
                        this.log.debug(ex.getMessage(), ex);
                    }
                    this.log.info("Retrying request");
                    retryReason = ex;
                } else {
                    throw ex;
                }
            }
        }
    }

    protected void releaseConnection() {
        try {
            this.managedConn.releaseConnection();
        } catch (IOException ignored) {
            this.log.debug("IOException releasing connection", ignored);
        }
        this.managedConn = null;
    }

    protected HttpRoute determineRoute(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
        if (target == null) {
            target = (HttpHost) request.getParams().getParameter(ClientPNames.DEFAULT_HOST);
        }
        if (target == null) {
            throw new IllegalStateException("Target host must not be null, or set in parameters.");
        }
        return this.routePlanner.determineRoute(target, request, context);
    }

    protected void establishRoute(HttpRoute route, HttpContext context) throws HttpException, IOException {
        int step;
        HttpRouteDirector rowdy = new BasicRouteDirector();
        do {
            HttpRoute fact = this.managedConn.getRoute();
            step = rowdy.nextStep(route, fact);
            switch (step) {
                case -1:
                    throw new HttpException("Unable to establish route: planned = " + route + "; current = " + fact);
                case 0:
                    break;
                case 1:
                case 2:
                    this.managedConn.open(route, context, this.params);
                    continue;
                case 3:
                    boolean secure = createTunnelToTarget(route, context);
                    this.log.debug("Tunnel to target created.");
                    this.managedConn.tunnelTarget(secure, this.params);
                    continue;
                case 4:
                    int hop = fact.getHopCount() - 1;
                    boolean secure2 = createTunnelToProxy(route, hop, context);
                    this.log.debug("Tunnel to proxy created.");
                    this.managedConn.tunnelProxy(route.getHopTarget(hop), secure2, this.params);
                    continue;
                case 5:
                    this.managedConn.layerProtocol(context, this.params);
                    continue;
                default:
                    throw new IllegalStateException("Unknown step indicator " + step + " from RouteDirector.");
            }
        } while (step > 0);
    }

    /* JADX WARN: Code restructure failed: missing block: B:20:0x00bc, code lost:
        r8 = r2.getStatusLine().getStatusCode();
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x00c6, code lost:
        if (r8 <= 299) goto L32;
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x00c8, code lost:
        r7 = r2.getEntity();
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x00cc, code lost:
        if (r7 == null) goto L29;
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x00ce, code lost:
        r2.setEntity(new ch.boye.httpclientandroidlib.entity.BufferedHttpEntity(r7));
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x00d6, code lost:
        r10.managedConn.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x00f7, code lost:
        throw new ch.boye.httpclientandroidlib.impl.client.TunnelRefusedException("CONNECT refused by proxy: " + r2.getStatusLine(), r2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x00f8, code lost:
        r10.managedConn.markReusable();
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x00fe, code lost:
        return false;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected boolean createTunnelToTarget(ch.boye.httpclientandroidlib.conn.routing.HttpRoute r11, ch.boye.httpclientandroidlib.protocol.HttpContext r12) throws ch.boye.httpclientandroidlib.HttpException, java.io.IOException {
        /*
            r10 = this;
            ch.boye.httpclientandroidlib.HttpHost r1 = r11.getProxyHost()
            ch.boye.httpclientandroidlib.HttpHost r9 = r11.getTargetHost()
            r2 = 0
        L9:
            ch.boye.httpclientandroidlib.conn.ManagedClientConnection r0 = r10.managedConn
            boolean r0 = r0.isOpen()
            if (r0 != 0) goto L18
            ch.boye.httpclientandroidlib.conn.ManagedClientConnection r0 = r10.managedConn
            ch.boye.httpclientandroidlib.params.HttpParams r3 = r10.params
            r0.open(r11, r12, r3)
        L18:
            ch.boye.httpclientandroidlib.HttpRequest r6 = r10.createConnectRequest(r11, r12)
            ch.boye.httpclientandroidlib.params.HttpParams r0 = r10.params
            r6.setParams(r0)
            java.lang.String r0 = "http.target_host"
            r12.setAttribute(r0, r9)
            java.lang.String r0 = "http.proxy_host"
            r12.setAttribute(r0, r1)
            java.lang.String r0 = "http.connection"
            ch.boye.httpclientandroidlib.conn.ManagedClientConnection r3 = r10.managedConn
            r12.setAttribute(r0, r3)
            java.lang.String r0 = "http.request"
            r12.setAttribute(r0, r6)
            ch.boye.httpclientandroidlib.protocol.HttpRequestExecutor r0 = r10.requestExec
            ch.boye.httpclientandroidlib.protocol.HttpProcessor r3 = r10.httpProcessor
            r0.preProcess(r6, r3, r12)
            ch.boye.httpclientandroidlib.protocol.HttpRequestExecutor r0 = r10.requestExec
            ch.boye.httpclientandroidlib.conn.ManagedClientConnection r3 = r10.managedConn
            ch.boye.httpclientandroidlib.HttpResponse r2 = r0.execute(r6, r3, r12)
            ch.boye.httpclientandroidlib.params.HttpParams r0 = r10.params
            r2.setParams(r0)
            ch.boye.httpclientandroidlib.protocol.HttpRequestExecutor r0 = r10.requestExec
            ch.boye.httpclientandroidlib.protocol.HttpProcessor r3 = r10.httpProcessor
            r0.postProcess(r2, r3, r12)
            ch.boye.httpclientandroidlib.StatusLine r0 = r2.getStatusLine()
            int r8 = r0.getStatusCode()
            r0 = 200(0xc8, float:2.8E-43)
            if (r8 >= r0) goto L7b
            ch.boye.httpclientandroidlib.HttpException r0 = new ch.boye.httpclientandroidlib.HttpException
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Unexpected response to CONNECT request: "
            java.lang.StringBuilder r3 = r3.append(r4)
            ch.boye.httpclientandroidlib.StatusLine r4 = r2.getStatusLine()
            java.lang.StringBuilder r3 = r3.append(r4)
            java.lang.String r3 = r3.toString()
            r0.<init>(r3)
            throw r0
        L7b:
            ch.boye.httpclientandroidlib.params.HttpParams r0 = r10.params
            boolean r0 = ch.boye.httpclientandroidlib.client.params.HttpClientParams.isAuthenticating(r0)
            if (r0 == 0) goto L9
            ch.boye.httpclientandroidlib.impl.client.HttpAuthenticator r0 = r10.authenticator
            ch.boye.httpclientandroidlib.client.AuthenticationStrategy r3 = r10.proxyAuthStrategy
            ch.boye.httpclientandroidlib.auth.AuthState r4 = r10.proxyAuthState
            r5 = r12
            boolean r0 = r0.isAuthenticationRequested(r1, r2, r3, r4, r5)
            if (r0 == 0) goto Lbc
            ch.boye.httpclientandroidlib.impl.client.HttpAuthenticator r0 = r10.authenticator
            ch.boye.httpclientandroidlib.client.AuthenticationStrategy r3 = r10.proxyAuthStrategy
            ch.boye.httpclientandroidlib.auth.AuthState r4 = r10.proxyAuthState
            r5 = r12
            boolean r0 = r0.authenticate(r1, r2, r3, r4, r5)
            if (r0 == 0) goto Lbc
            ch.boye.httpclientandroidlib.ConnectionReuseStrategy r0 = r10.reuseStrategy
            boolean r0 = r0.keepAlive(r2, r12)
            if (r0 == 0) goto Lb5
            ch.boye.httpclientandroidlib.androidextra.HttpClientAndroidLog r0 = r10.log
            java.lang.String r3 = "Connection kept alive"
            r0.debug(r3)
            ch.boye.httpclientandroidlib.HttpEntity r7 = r2.getEntity()
            ch.boye.httpclientandroidlib.util.EntityUtils.consume(r7)
            goto L9
        Lb5:
            ch.boye.httpclientandroidlib.conn.ManagedClientConnection r0 = r10.managedConn
            r0.close()
            goto L9
        Lbc:
            ch.boye.httpclientandroidlib.StatusLine r0 = r2.getStatusLine()
            int r8 = r0.getStatusCode()
            r0 = 299(0x12b, float:4.19E-43)
            if (r8 <= r0) goto Lf8
            ch.boye.httpclientandroidlib.HttpEntity r7 = r2.getEntity()
            if (r7 == 0) goto Ld6
            ch.boye.httpclientandroidlib.entity.BufferedHttpEntity r0 = new ch.boye.httpclientandroidlib.entity.BufferedHttpEntity
            r0.<init>(r7)
            r2.setEntity(r0)
        Ld6:
            ch.boye.httpclientandroidlib.conn.ManagedClientConnection r0 = r10.managedConn
            r0.close()
            ch.boye.httpclientandroidlib.impl.client.TunnelRefusedException r0 = new ch.boye.httpclientandroidlib.impl.client.TunnelRefusedException
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "CONNECT refused by proxy: "
            java.lang.StringBuilder r3 = r3.append(r4)
            ch.boye.httpclientandroidlib.StatusLine r4 = r2.getStatusLine()
            java.lang.StringBuilder r3 = r3.append(r4)
            java.lang.String r3 = r3.toString()
            r0.<init>(r3, r2)
            throw r0
        Lf8:
            ch.boye.httpclientandroidlib.conn.ManagedClientConnection r0 = r10.managedConn
            r0.markReusable()
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: ch.boye.httpclientandroidlib.impl.client.DefaultRequestDirector.createTunnelToTarget(ch.boye.httpclientandroidlib.conn.routing.HttpRoute, ch.boye.httpclientandroidlib.protocol.HttpContext):boolean");
    }

    protected boolean createTunnelToProxy(HttpRoute route, int hop, HttpContext context) throws HttpException, IOException {
        throw new HttpException("Proxy chains are not supported.");
    }

    protected HttpRequest createConnectRequest(HttpRoute route, HttpContext context) {
        HttpHost target = route.getTargetHost();
        String host = target.getHostName();
        int port = target.getPort();
        if (port < 0) {
            Scheme scheme = this.connManager.getSchemeRegistry().getScheme(target.getSchemeName());
            port = scheme.getDefaultPort();
        }
        StringBuilder buffer = new StringBuilder(host.length() + 6);
        buffer.append(host);
        buffer.append(':');
        buffer.append(Integer.toString(port));
        String authority = buffer.toString();
        ProtocolVersion ver = HttpProtocolParams.getVersion(this.params);
        HttpRequest req = new BasicHttpRequest("CONNECT", authority, ver);
        return req;
    }

    protected RoutedRequest handleResponse(RoutedRequest roureq, HttpResponse response, HttpContext context) throws HttpException, IOException {
        HttpRoute route = roureq.getRoute();
        RequestWrapper request = roureq.getRequest();
        HttpParams params = request.getParams();
        if (HttpClientParams.isAuthenticating(params)) {
            HttpHost target = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
            if (target == null) {
                target = route.getTargetHost();
            }
            if (target.getPort() < 0) {
                Scheme scheme = this.connManager.getSchemeRegistry().getScheme(target);
                target = new HttpHost(target.getHostName(), scheme.getDefaultPort(), target.getSchemeName());
            }
            if (!this.authenticator.isAuthenticationRequested(target, response, this.targetAuthStrategy, this.targetAuthState, context) || !this.authenticator.authenticate(target, response, this.targetAuthStrategy, this.targetAuthState, context)) {
                HttpHost proxy = route.getProxyHost();
                if (this.authenticator.isAuthenticationRequested(proxy, response, this.proxyAuthStrategy, this.proxyAuthState, context)) {
                    if (proxy == null) {
                        proxy = route.getTargetHost();
                    }
                    if (this.authenticator.authenticate(proxy, response, this.proxyAuthStrategy, this.proxyAuthState, context)) {
                        return roureq;
                    }
                }
            } else {
                return roureq;
            }
        }
        if (HttpClientParams.isRedirecting(params) && this.redirectStrategy.isRedirected(request, response, context)) {
            if (this.redirectCount >= this.maxRedirects) {
                throw new RedirectException("Maximum redirects (" + this.maxRedirects + ") exceeded");
            }
            this.redirectCount++;
            this.virtualHost = null;
            HttpUriRequest redirect = this.redirectStrategy.getRedirect(request, response, context);
            HttpRequest orig = request.getOriginal();
            redirect.setHeaders(orig.getAllHeaders());
            URI uri = redirect.getURI();
            HttpHost newTarget = URIUtils.extractHost(uri);
            if (newTarget == null) {
                throw new ProtocolException("Redirect URI does not specify a valid host name: " + uri);
            }
            if (!route.getTargetHost().equals(newTarget)) {
                this.log.debug("Resetting target auth state");
                this.targetAuthState.reset();
                AuthScheme authScheme = this.proxyAuthState.getAuthScheme();
                if (authScheme != null && authScheme.isConnectionBased()) {
                    this.log.debug("Resetting proxy auth state");
                    this.proxyAuthState.reset();
                }
            }
            RequestWrapper wrapper = wrapRequest(redirect);
            wrapper.setParams(params);
            HttpRoute newRoute = determineRoute(newTarget, wrapper, context);
            RoutedRequest newRequest = new RoutedRequest(wrapper, newRoute);
            if (this.log.isDebugEnabled()) {
                this.log.debug("Redirecting to '" + uri + "' via " + newRoute);
            }
            return newRequest;
        }
        return null;
    }

    private void abortConnection() {
        ManagedClientConnection mcc = this.managedConn;
        if (mcc != null) {
            this.managedConn = null;
            try {
                mcc.abortConnection();
            } catch (IOException ex) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug(ex.getMessage(), ex);
                }
            }
            try {
                mcc.releaseConnection();
            } catch (IOException ignored) {
                this.log.debug("Error releasing connection", ignored);
            }
        }
    }
}