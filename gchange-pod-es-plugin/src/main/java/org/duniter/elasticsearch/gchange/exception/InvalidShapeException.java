package org.duniter.elasticsearch.gchange.exception;

import org.duniter.elasticsearch.exception.DuniterElasticsearchException;

public class InvalidShapeException extends DuniterElasticsearchException {
    public InvalidShapeException(Throwable cause) {
        super(cause);
    }

    public InvalidShapeException(String msg, Object... args) {
        super(msg, args);
    }

    public InvalidShapeException(String msg, Throwable cause, Object... args) {
        super(msg, args, cause);
    }
}
