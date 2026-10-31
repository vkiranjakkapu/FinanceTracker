package com.platform.logging.properties;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.platform.logging.constants.LoggingConstants;

@ConfigurationProperties(prefix = "platform.logging")
public class LoggingProperties {

    private boolean enabled = true;

    private final RequestProperties request = new RequestProperties();

    private final ExceptionProperties exceptionLogging = new ExceptionProperties();

    private final int filterOrderHighest = LoggingConstants.CORRELATION_FILTER_ORDER;

    private final Map<String, String> headerMappings = new LinkedHashMap<>();

    public LoggingProperties() {
        headerMappings.put(
                LoggingConstants.CORRELATION_HEADER,
                LoggingConstants.CORRELATION_ID);
    }

    public Map<String, String> getHeaderMappings() {
        return headerMappings;
    }

    public String getCorrelationHeader() {
        return LoggingConstants.CORRELATION_HEADER;
    }

    public static class RequestProperties {

        private boolean enabled = true;

        private boolean includeQueryString = true;

        private boolean includeClientIp = false;

        private List<String> excludedPaths = List.of();

        private int requestLoggingFilterOrder = LoggingConstants.REQUEST_LOGGING_FILTER_ORDER;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isIncludeQueryString() {
            return includeQueryString;
        }

        public void setIncludeQueryString(boolean includeQueryString) {
            this.includeQueryString = includeQueryString;
        }

        public boolean isIncludeClientIp() {
            return includeClientIp;
        }

        public void setIncludeClientIp(boolean includeClientIp) {
            this.includeClientIp = includeClientIp;
        }

        public List<String> getExcludedPaths() {
            return excludedPaths;
        }

        public void setExcludedPaths(List<String> excludedPaths) {
            this.excludedPaths = excludedPaths;
        }

        public int getRequestLoggingFilterOrder() {
            return requestLoggingFilterOrder;
        }

        public void setRequestLoggingFilterOrder(int requestLoggingFilterOrder) {
            this.requestLoggingFilterOrder = requestLoggingFilterOrder;
        }
    }

    public static class ExceptionProperties {

        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public RequestProperties getRequest() {
        return request;
    }

    public ExceptionProperties getExceptionLogging() {
        return exceptionLogging;
    }

    public int getFilterOrderHighest() {
        return filterOrderHighest;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}