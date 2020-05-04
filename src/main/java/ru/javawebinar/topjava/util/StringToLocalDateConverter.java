package ru.javawebinar.topjava.util;

import java.time.LocalDate;
import org.springframework.core.convert.converter.Converter;

public class StringToLocalDateConverter implements Converter<String, LocalDate> {

    @Override
    public LocalDate convert(String source) {
        return DateTimeUtil.parseLocalDate(source);
    }
}
