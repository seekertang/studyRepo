package com.backstopsolutions.morpheus.filter;

import com.backstopsolutions.morpheus.core.RequestContext;
import com.backstopsolutions.morpheus.core.RequestContextHolder;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component("workflowFilter")
public class RequestContextFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        try {
            RequestContext context = new RequestContext();
            context.setExternalSource(request.getHeader("external-source"));
            context.setExternalId(request.getHeader("external-id"));

            RequestContextHolder.set(context);

            chain.doFilter(req, res);
        } finally {
            RequestContextHolder.clear();
        }
    }
}
