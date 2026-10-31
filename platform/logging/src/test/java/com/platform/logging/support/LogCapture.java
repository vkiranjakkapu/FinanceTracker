package com.platform.logging.support;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;

public final class LogCapture implements AutoCloseable {

    private final Logger logger;
    private final ListAppender<ILoggingEvent> appender;

    private LogCapture(Logger logger,
                       ListAppender<ILoggingEvent> appender) {

        this.logger = logger;
        this.appender = appender;
    }

    public static LogCapture attach(Class<?> loggerClass) {

        Logger logger = (Logger) LoggerFactory.getLogger(loggerClass);

        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();

        logger.addAppender(appender);

        return new LogCapture(logger, appender);
    }

    public int size() {
        return appender.list.size();
    }

    public String message(int index) {
        return appender.list.get(index).getFormattedMessage();
    }

    public ILoggingEvent event(int index) {
        return appender.list.get(index);
    }

    public void clear() {
        appender.list.clear();
    }

    @Override
    public void close() {
        logger.detachAppender(appender);
        appender.stop();
    }
}