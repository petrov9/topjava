package ru.javawebinar.topjava;

import static ru.javawebinar.topjava.util.exception.ErrorType.*;

import ru.javawebinar.topjava.util.exception.ErrorInfo;

public class ErrorInfoTestData {
    public static TestMatcher<ErrorInfo> ERROR_MATCHER = TestMatcher.usingFieldsComparator(ErrorInfo.class);
    private static String host = "http://localhost";

    public static ErrorInfo create422ErrorEntity(String url, String detail) {
        return new ErrorInfo(host + url, VALIDATION_ERROR, detail);
    }

    public static ErrorInfo create409ErrorEntity(String url, String detail) {
        return new ErrorInfo(host + url, DATA_ERROR, detail);
    }
}
