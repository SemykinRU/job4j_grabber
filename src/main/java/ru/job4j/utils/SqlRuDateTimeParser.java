package ru.job4j.utils;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class SqlRuDateTimeParser implements DateTimeParser {
    private static final String[] SHORT_MONTH = {
            "янв", "фев", "мар", "апр", "май", "июн",
            "июл", "авг", "сен", "окт", "ноя", "дек"};
    private static final String TODAY = "сегодня";
    private static final String YESTERDAY = "вчера";
    private static final Integer ONE_DAY = 1;
    private static final Locale LOCALE = new Locale("ru");
    private static final DateFormatSymbols DATE_FORMAT_SYMBOLS = DateFormatSymbols.getInstance(LOCALE);
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd MMM yy',' HH:mm", LOCALE);
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_SHORT = new SimpleDateFormat("dd MMM yy", LOCALE);

    public SqlRuDateTimeParser() {
        DATE_FORMAT_SYMBOLS.setShortMonths(SHORT_MONTH);
        SIMPLE_DATE_FORMAT.setDateFormatSymbols(DATE_FORMAT_SYMBOLS);
        SIMPLE_DATE_FORMAT_SHORT.setDateFormatSymbols(DATE_FORMAT_SYMBOLS);
    }

    @Override
    public LocalDateTime parse(String parse) throws ParseException {
        String day = replace(parse);
        return SIMPLE_DATE_FORMAT
                .parse(day).toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    private String replace(String str) {
        if (str.contains(TODAY)) {
            String format = SIMPLE_DATE_FORMAT_SHORT.format(new Date());
            str = str.replace(TODAY, format);
        }
        if (str.contains(YESTERDAY)) {
            String format = SIMPLE_DATE_FORMAT_SHORT
                    .format(Date.from((LocalDate.now().minusDays(ONE_DAY)
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant())));
            str = str.replace(YESTERDAY, format);
        }
        return str;
    }
}

