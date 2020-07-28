package id.co.myproject.yukkajian.helper;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Weeks;
import org.joda.time.Years;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeStampFormatter {

    /**
     * For use with java.util.Date
     */
    public String format(Date timestamp) {
        long millisFromNow = getMillisFromNow(timestamp);

        long minutesFromNow = TimeUnit.MILLISECONDS.toMinutes(millisFromNow);
        if (minutesFromNow < 1) {
            return "baru saja";
        }
        long hoursFromNow = TimeUnit.MILLISECONDS.toHours(millisFromNow);
        if (hoursFromNow < 1) {
            return formatMinutes(minutesFromNow);
        }
        long daysFromNow = TimeUnit.MILLISECONDS.toDays(millisFromNow);
        if (daysFromNow < 1) {
            return formatHours(hoursFromNow);
        }
        long weeksFromNow = TimeUnit.MILLISECONDS.toDays(millisFromNow) / 7;
        if (weeksFromNow < 1) {
            return formatDays(daysFromNow);
        }
        long monthsFromNow = TimeUnit.MILLISECONDS.toDays(millisFromNow) / 30;
        if (monthsFromNow < 1) {
            return formatWeeks(weeksFromNow);
        }
        long yearsFromNow = TimeUnit.MILLISECONDS.toDays(millisFromNow) / 365;
        if (yearsFromNow < 1) {
            return formatMonths(monthsFromNow);
        }
        return formatYears(yearsFromNow);
    }

    private long getMillisFromNow(Date commentedAt) {
        long commentedAtMillis = commentedAt.getTime();
        long nowMillis = System.currentTimeMillis();
        return nowMillis - commentedAtMillis;
    }

    /**
     * For use with org.joda.DateTime
     */
    public String format(DateTime commentedAt) {
        DateTime now = DateTime.now();
        Minutes minutesBetween = Minutes.minutesBetween(commentedAt, now);
        if (minutesBetween.isLessThan(Minutes.ONE)) {
            return "baru saja";
        }
        Hours hoursBetween = Hours.hoursBetween(commentedAt, now);
        if (hoursBetween.isLessThan(Hours.ONE)) {
            return formatMinutes(minutesBetween.getMinutes());
        }
        Days daysBetween = Days.daysBetween(commentedAt, now);
        if (daysBetween.isLessThan(Days.ONE)) {
            return formatHours(hoursBetween.getHours());
        }
        Weeks weeksBetween = Weeks.weeksBetween(commentedAt, now);
        if (weeksBetween.isLessThan(Weeks.ONE)) {
            return formatDays(daysBetween.getDays());
        }
        Months monthsBetween = Months.monthsBetween(commentedAt, now);
        if (monthsBetween.isLessThan(Months.ONE)) {
            return formatWeeks(weeksBetween.getWeeks());
        }
        Years yearsBetween = Years.yearsBetween(commentedAt, now);
        if (yearsBetween.isLessThan(Years.ONE)) {
            return formatMonths(monthsBetween.getMonths());
        }
        return formatYears(yearsBetween.getYears());
    }

    private String formatMinutes(long minutes) {
        return format(minutes, " menit yang lalu", " menit yang lalu");
    }

    private String formatHours(long hours) {
        return format(hours, " jam yang lalu", " jam yang lalu");
    }

    private String formatDays(long days) {
        return format(days, " hari yang lalu", " hari yang lalu");
    }

    private String formatWeeks(long weeks) {
        return format(weeks, " minggu yang lalu", " minggu yang lalu");
    }

    private String formatMonths(long months) {
        return format(months, " bulan yang lalu", " bulan yang lalu");
    }

    private String formatYears(long years) {
        return format(years, " tahun yang lalu", " tahun yang lalu");
    }

    private String format(long hand, String singular, String plural) {
        if (hand == 1) {
            return hand + singular;
        } else {
            return hand + plural;
        }
    }

}
