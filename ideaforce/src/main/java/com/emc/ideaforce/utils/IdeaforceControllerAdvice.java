package com.emc.ideaforce.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller advice wrapping all controller APIs to handle exceptions, etc
 */
@ControllerAdvice(basePackages = "com.emc.ideaforce.controller")
public class IdeaforceControllerAdvice {

    private static final Logger LOG = LoggerFactory.getLogger(IdeaforceControllerAdvice.class);

    private static final String MESSAGE = "message";

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ModelAndView handleException(MaxUploadSizeExceededException exception) {
        String exceptionMsg = "File being uploaded should be less than 5 MB";

        // log
        LOG.error(exceptionMsg, exception);

        // and update the view
        ModelAndView mv = new ModelAndView("submit-story");
        mv.addObject(MESSAGE, exceptionMsg);
        return mv;
    }

}
