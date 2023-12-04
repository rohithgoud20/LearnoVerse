package com.example.learnoverse;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CalendarUtils
{
    public static LocalDate selectedDate;

    public static String formattedDate(LocalDate date)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        return date.format(formatter);
    }

    public static String formattedTime(LocalTime time)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
        return time.format(formatter);
    }

    public static String monthYearFromDate(LocalDate date) {
        if (date != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
            return date.format(formatter);
        } else {
            return "";
        }
    }

    public static ArrayList<LocalDate> daysInMonthArray(LocalDate date)
    {
        ArrayList<LocalDate> daysInMonthArray = new ArrayList<>();
        if(date!=null) {
            YearMonth yearMonth = YearMonth.from(date);
            int daysInMonth = yearMonth.lengthOfMonth();

            LocalDate firstOfMonth = CalendarUtils.selectedDate.withDayOfMonth(1);
            int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

            for (int i = 1; i <= 42; i++) {
                if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                    daysInMonthArray.add(null);
                } else {
                    LocalDate day = LocalDate.of(selectedDate.getYear(), selectedDate.getMonth(), i - dayOfWeek);
                    daysInMonthArray.add(day);
                }
            }
        }
        return  daysInMonthArray;
    }

    public static ArrayList<LocalDate> daysInWeekArray(LocalDate selectedDate)
    {
        ArrayList<LocalDate> days = new ArrayList<>();
        if(selectedDate !=null) {
            LocalDate current = sundayForDate(selectedDate);
            LocalDate endDate = current != null ? current.plusWeeks(1) : null;

            while (current != null && current.isBefore(endDate)) {
                days.add(current);
                current = current.plusDays(1);
            }
        }
        return days;
    }

    private static LocalDate sundayForDate(LocalDate current)
    {
        if(current !=null) {
            LocalDate oneWeekAgo = current.minusWeeks(1);

            while (current.isAfter(oneWeekAgo)) {
                if (current.getDayOfWeek() == DayOfWeek.SUNDAY)
                    return current;

                current = current.minusDays(1);
            }
        }

        return LocalDate.now();
    }
}

