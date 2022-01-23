package ru.job4j.grabber.utils;

import java.time.LocalDateTime;
import java.util.Map;

public class SqlRuDateTimeParser implements DateTimeParser {
    final  String yesterday = "вчера";
    final String today = "сегодня";
    private static final Map<String, Integer> MONTHS = Map.ofEntries(
            Map.entry("янв", 1),
            Map.entry("фев", 2),
            Map.entry("мар", 3),
            Map.entry("апр", 4),
            Map.entry("май", 5),
            Map.entry("июн", 6),
            Map.entry("июл", 7),
            Map.entry("авг", 8),
            Map.entry("сен", 9),
            Map.entry("окт", 10),
            Map.entry("ноя", 11),
            Map.entry("дек", 12)
    );

    @Override
    public LocalDateTime parse(String parse) {
        String[] date = parse.split(" ");
        if ((date[0]).contains(yesterday)) {
            return LocalDateTime.now().minusDays(1);
        } else if (date[0].contains(today)) {
            return LocalDateTime.now();
        }
        int day = Integer.parseInt(date[0]);
        int months = MONTHS.get(date[1]);
        int year = 2000 + Integer.parseInt(date[2].replace(",", ""));
        String[] time = date[3].split(":");
        int hour = Integer.parseInt(time[0]);
        int minute = Integer.parseInt(time[1]);
        return LocalDateTime.of(year, months, day, hour, minute);
    }
}

