package com.emc.ideaforce;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * Controller advice wrapping all controller APIs to handle exceptions, etc
 */
@ControllerAdvice
public class IdeaforceControllerAdvice {

    private static final Logger LOG = LoggerFactory.getLogger(IdeaforceControllerAdvice.class);

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public void handleException(MaxUploadSizeExceededException ex) {
        LOG.error(ex.getMessage(), ex);
    }

}
