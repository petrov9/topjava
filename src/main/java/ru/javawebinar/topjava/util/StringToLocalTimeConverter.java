package ru.javawebinar.topjava.util;

import java.time.LocalTime;
import org.springframework.core.convert.converter.Converter;

public class StringToLocalTimeConverter implements Converter<String, LocalTime> {

    @Override
    public LocalTime convert(String source) {
        return DateTimeUtil.parseLocalTime(source);
    }
}
