package ru.javawebinar.topjava.web;

import static org.springframework.http.HttpStatus.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import ru.javawebinar.topjava.AuthorizedUser;
import ru.javawebinar.topjava.util.ValidationUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public static final String EMAIL_DUPLICATE_MESSAGE = "User with this email already exists";
    public static final String DATE_TIME_DUPLICATE_MESSAGE = "Meal with this date/time already exists";

    @ExceptionHandler(Exception.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        return createModelAndView(req.getRequestURI(), e, INTERNAL_SERVER_ERROR, null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ModelAndView duplicateEmailError(HttpServletRequest req, Exception e) throws Exception {
        return createModelAndView(req.getRequestURI(), e, CONFLICT, EMAIL_DUPLICATE_MESSAGE);
    }

    private ModelAndView createModelAndView(String url,Exception e, HttpStatus httpStatus, String message) {
        log.error("Exception at request " + url, e);
        Throwable rootCause = ValidationUtil.getRootCause(e);

        ModelAndView mav = new ModelAndView("exception",
            Map.of(
                "exception", rootCause,
                "message", StringUtils.isEmpty(message) ? rootCause.getMessage() : message,
                "status", httpStatus));
        mav.setStatus(httpStatus);

        // Interceptor is not invoked, put userTo
        AuthorizedUser authorizedUser = SecurityUtil.safeGet();
        if (authorizedUser != null) {
            mav.addObject("userTo", authorizedUser.getUserTo());
        }
        return mav;
    }
}
