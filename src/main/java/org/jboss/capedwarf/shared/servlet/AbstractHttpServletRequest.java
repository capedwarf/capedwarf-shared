package org.jboss.capedwarf.shared.servlet;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

/**
 * Abstract http servlet request.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 * @author <a href="mailto:mluksa@redhat.com">Marko Luksa</a>
 */
public abstract class AbstractHttpServletRequest extends AbstractServletRequest implements HttpServletRequest {
    private String method;
    private List<Cookie> cookies = new ArrayList<>();
    private Map<String, Set<String>> headers = new HashMap<>();
    private Map<String, Part> parts = new HashMap<>();
    private HttpSession session;
    // paths
    private String path;
    private String servletPath;
    private String pathInfo;
    private String queryString;
    // security
    private Set<String> roles = new HashSet<>();

    protected AbstractHttpServletRequest(ServletContext context) {
        super(context);
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    public void setPathInfo(String pathInfo) {
        this.pathInfo = pathInfo;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public void addCookie(Cookie cookie) {
        cookies.add(cookie);
    }

    protected void addRole(String role) {
        roles.add(role);
    }

    public void addHeader(String name, String value) {
        name = normalizeHeaderName(name);
        Set<String> set = headers.get(name);
        if (set == null) {
            set = new HashSet<>();
            headers.put(name, set);
        }
        set.add(value);
    }

    public void addHeaders(String name, String[] values) {
        name = normalizeHeaderName(name);
        Set<String> set = headers.get(name);
        if (set == null) {
            set = new HashSet<>();
            headers.put(name, set);
        }
        set.addAll(Arrays.asList(values));
    }

    public void addHeaders(Map<String, Set<String>> map) {
        for (Map.Entry<String, Set<String>> entry : map.entrySet()) {
            String name = normalizeHeaderName(entry.getKey());
            Set<String> values = entry.getValue();
            addHeaders(name, values.toArray(new String[values.size()]));
        }
    }

    public void addPart(String name, Part part) {
        parts.put(name, part);
    }

    public String getAuthType() {
        return null;  // TODO
    }

    public Cookie[] getCookies() {
        return cookies.toArray(new Cookie[cookies.size()]);
    }

    public long getDateHeader(String name) {
        final String header = getHeader(name);
        return header != null ? Long.parseLong(header) : -1;
    }

    public String getHeader(String name) {
        final Set<String> h = headers.get(normalizeHeaderName(name));
        return (h != null && h.isEmpty() == false) ? h.iterator().next() : null;
    }

    public Enumeration<String> getHeaders(String name) {
        final Set<String> h = headers.get(normalizeHeaderName(name));
        return (h != null && h.isEmpty() == false) ? Collections.enumeration(h) : Collections.enumeration(Collections.<String>emptySet());
    }

    private String normalizeHeaderName(String name) {
        return name.toLowerCase();
    }

    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(headers.keySet());
    }

    public int getIntHeader(String name) {
        final String header = getHeader(name);
        return header != null ? Integer.parseInt(header) : -1;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPathInfo() {
        return pathInfo;
    }

    public String getPathTranslated() {
        return null;  // TODO
    }

    public String getContextPath() {
        return getServletContext().getContextPath();
    }

    public String getQueryString() {
        return queryString;
    }

    public String getRemoteUser() {
        return null;  // TODO
    }

    public boolean isUserInRole(String role) {
        return roles.contains(role);
    }

    public Principal getUserPrincipal() {
        return new Principal() {
            public String getName() {
                return String.format("[Mock: %s]", getClass().getName());
            }
        };
    }

    public String getRequestedSessionId() {
        return null;  // TODO
    }

    public String getRequestURI() {
        return getContextPath() + path;
    }

    public StringBuffer getRequestURL() {
        return new StringBuffer(path); // OK?
    }

    public String getServletPath() {
        return servletPath;
    }

    public HttpSession getSession(boolean create) {
        if (session == null && create) {
            session = new MockHttpSession(getServletContext());
        }
        return session;
    }

    public HttpSession getSession() {
        return getSession(true);
    }

    public boolean isRequestedSessionIdValid() {
        return false;  // TODO
    }

    public boolean isRequestedSessionIdFromCookie() {
        return false;  // TODO
    }

    public boolean isRequestedSessionIdFromURL() {
        return false;  // TODO
    }

    public boolean isRequestedSessionIdFromUrl() {
        return false;  // TODO
    }

    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        return false;  // TODO
    }

    public void login(String username, String password) throws ServletException {
        // TODO
    }

    public void logout() throws ServletException {
        roles.clear();
        // TODO
    }

    public Collection<Part> getParts() throws IOException, ServletException {
        return parts.values();
    }

    public Part getPart(String name) throws IOException, ServletException {
        return parts.get(name);
    }

    public String changeSessionId() {
        return null;
    }

    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
        return null;
    }

    public long getContentLengthLong() {
        return (long) getContentLength();
    }

    @Override
    public String getContentType() {
        return getHeader("content-type");
    }
}
