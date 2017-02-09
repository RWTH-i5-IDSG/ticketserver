package de.rwth.idsg.barti.web.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Slf4j
public class ResourceApiKeyHeaderInterceptor extends HandlerInterceptorAdapter {

    private static final String HEADER_KEY = "Authorization-Key";

    private final String HEADER_RESOURCE_VALUE;

    public ResourceApiKeyHeaderInterceptor(String apiKey) {
        HEADER_RESOURCE_VALUE = apiKey;
    }

    // -------------------------------------------------------------------------
    // Barti as REST resource
    // -------------------------------------------------------------------------

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {

        switch (request.getDispatcherType()) {
            case REQUEST:
                return preHandleInternal(request, response);
            default:
                break;
        }

        return super.preHandle(request, response, handler);
    }

    private boolean preHandleInternal(HttpServletRequest request, HttpServletResponse response) {
        String value = request.getHeader(HEADER_KEY);

        if (isValid(value)) {
            // Continue processing with the handler chain
            return true;
        } else {
            log.error("Unauthorized incoming request [API key missing or invalid]. Dropping the message.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            // Break the chain and return 401
            return false;
        }
    }

    private boolean isValid(String value) {
        return HEADER_RESOURCE_VALUE.equals(value);
    }
}
