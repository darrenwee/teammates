package teammates.test.cases.util;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.testng.annotations.Test;

import teammates.common.util.TimeHelper;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link TimeHelper}.
 */
public class TimeHelperTest extends BaseTestCase {

    @Test
    public void testCombineDateTime() {
        String testDate = "01/02/2013";
        String testTime = "0";
        LocalDateTime expectedOutput = LocalDateTime.of(2013, 2, 1, 0, 0);

        testTime = "0";
        ______TS("boundary case: time = 0");
        assertEquals(expectedOutput, TimeHelper.combineDateTimeNew(testDate, testTime));

        ______TS("boundary case: time = 24");
        testTime = "24";
        expectedOutput = LocalDateTime.of(2013, 2, 1, 23, 59);
        assertEquals(expectedOutput, TimeHelper.combineDateTimeNew(testDate, testTime));

        ______TS("negative time");
        assertNull(TimeHelper.combineDateTimeNew(testDate, "-5"));

        ______TS("large time");
        assertNull(TimeHelper.combineDateTimeNew(testDate, "68"));

        ______TS("date null");
        assertNull(TimeHelper.combineDateTimeNew(null, testTime));

        ______TS("time null");
        assertNull(TimeHelper.combineDateTimeNew(testDate, null));

        ______TS("invalid time");
        assertNull(TimeHelper.combineDateTimeNew(testDate, "invalid time"));

        ______TS("fractional time");
        assertNull(TimeHelper.combineDateTimeNew(testDate, "5.5"));

        ______TS("invalid date");
        assertNull(TimeHelper.combineDateTimeNew("invalid date", testDate));
    }

    @Test
    public void testIsTimeWithinPeriod() {
        Instant startTime = Instant.now().minus(Duration.ofDays(5));
        Instant endTime = Instant.now().plus(Duration.ofDays(5));
        Instant time;

        ______TS("Time within period test");
        time = Instant.now();

        assertTrue(TimeHelper.isTimeWithinPeriod(startTime, endTime, time, true, true));
        assertTrue(TimeHelper.isTimeWithinPeriod(startTime, endTime, time, true, false));
        assertTrue(TimeHelper.isTimeWithinPeriod(startTime, endTime, time, false, true));
        assertTrue(TimeHelper.isTimeWithinPeriod(startTime, endTime, time, false, false));

        ______TS("Time on start time test");
        time = startTime;

        assertTrue(TimeHelper.isTimeWithinPeriod(startTime, endTime, time, true, true));
        assertTrue(TimeHelper.isTimeWithinPeriod(startTime, endTime, time, true, false));
        assertFalse(TimeHelper.isTimeWithinPeriod(startTime, endTime, time, false, true));
        assertFalse(TimeHelper.isTimeWithinPeriod(startTime, endTime, time, false, false));

        ______TS("Time before start time test");
        time = startTime.minus(Duration.ofDays(10));

        assertFalse(TimeHelper.isTimeWithinPeriod(startTime, endTime, time, true, true));
        assertFalse(TimeHelper.isTimeWithinPeriod(startTime, endTime, time, true, false));
        assertFalse(TimeHelper.isTimeWithinPeriod(startTime, endTime, time, false, true));
        assertFalse(TimeHelper.isTimeWithinPeriod(startTime, endTime, time, false, false));

        ______TS("Time on end time test");
        time = endTime;

        assertTrue(TimeHelper.isTimeWithinPeriod(startTime, endTime, time, true, true));
        assertFalse(TimeHelper.isTimeWithinPeriod(startTime, endTime, time, true, false));
        assertTrue(TimeHelper.isTimeWithinPeriod(startTime, endTime, time, false, true));
        assertFalse(TimeHelper.isTimeWithinPeriod(startTime, endTime, time, false, false));

        ______TS("Time after start time test");
        time = endTime.plus(Duration.ofDays(10));

        assertFalse(TimeHelper.isTimeWithinPeriod(startTime, endTime, time, true, true));
        assertFalse(TimeHelper.isTimeWithinPeriod(startTime, endTime, time, true, false));
        assertFalse(TimeHelper.isTimeWithinPeriod(startTime, endTime, time, false, true));
        assertFalse(TimeHelper.isTimeWithinPeriod(startTime, endTime, time, false, false));

        ______TS("Start time null test");
        assertFalse(TimeHelper.isTimeWithinPeriod(null, endTime, time, true, true));
        assertFalse(TimeHelper.isTimeWithinPeriod(null, endTime, time, true, false));
        assertFalse(TimeHelper.isTimeWithinPeriod(null, endTime, time, false, true));
        assertFalse(TimeHelper.isTimeWithinPeriod(null, endTime, time, false, false));

        ______TS("End time null test");
        assertFalse(TimeHelper.isTimeWithinPeriod(startTime, null, time, true, true));
        assertFalse(TimeHelper.isTimeWithinPeriod(startTime, null, time, true, false));
        assertFalse(TimeHelper.isTimeWithinPeriod(startTime, null, time, false, true));
        assertFalse(TimeHelper.isTimeWithinPeriod(startTime, null, time, false, false));

        ______TS("Time null test");
        assertFalse(TimeHelper.isTimeWithinPeriod(startTime, endTime, null, true, true));
        assertFalse(TimeHelper.isTimeWithinPeriod(startTime, endTime, null, true, false));
        assertFalse(TimeHelper.isTimeWithinPeriod(startTime, endTime, null, false, true));
        assertFalse(TimeHelper.isTimeWithinPeriod(startTime, endTime, null, false, false));
    }

    @Test
    public void testEndOfYearDates() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.clear();
        cal.set(2015, 11, 30, 12, 0, 0);
        Date date = cal.getTime();
        assertEquals("30/12/2015", TimeHelper.formatDate(date));
        assertEquals("Wed, 30 Dec 2015, 12:00 NOON", TimeHelper.formatTime12H(date));
        assertEquals("Wed, 30 Dec 2015, 12:00 NOON UTC+0000", TimeHelper.formatDateTimeForSessions(date, 0));
        assertEquals("30 Dec 12:00 NOON", TimeHelper.formatDateTimeForInstructorHomePage(date));
    }

    @Test
    public void testFormatDateTimeForSessions() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.clear();
        cal.set(2015, 10, 30, 12, 0, 0);
        Date date = cal.getTime();
        assertEquals("Mon, 30 Nov 2015, 12:00 NOON UTC+0000", TimeHelper.formatDateTimeForSessions(date, 0));

        cal.clear();
        cal.set(2015, 10, 30, 4, 0, 0);
        date = cal.getTime();
        assertEquals("Mon, 30 Nov 2015, 12:00 NOON UTC+0800", TimeHelper.formatDateTimeForSessions(date, 8));

        cal.clear();
        cal.set(2015, 10, 30, 4, 0, 0);
        date = cal.getTime();
        assertEquals("Mon, 30 Nov 2015, 04:00 PM UTC+1200", TimeHelper.formatDateTimeForSessions(date, 12));

        cal.clear();
        cal.set(2015, 10, 30, 16, 0, 0);
        date = cal.getTime();
        assertEquals("Mon, 30 Nov 2015, 12:00 NOON UTC-0400", TimeHelper.formatDateTimeForSessions(date, -4));

        cal.clear();
        cal.set(2015, 10, 30, 16, 0, 0);
        date = cal.getTime();
        assertEquals("Mon, 30 Nov 2015, 11:45 AM UTC-0415", TimeHelper.formatDateTimeForSessions(date, -4.25));
    }

    @Test
    public void testGetNextHourFromInstant() {
        ______TS("one minute after hour rounds to next hour");
        Instant input = getInstantOf(2017, Month.JUNE, 15, 13, 1);
        Instant expected = getInstantOf(2017, Month.JUNE, 15, 14, 0);
        assertEquals(expected, TimeHelper.getNextHourFromInstant(input));

        ______TS("one minute before next hour rounds to next hour");
        input = getInstantOf(2017, Month.JUNE, 15, 13, 59);
        assertEquals(expected, TimeHelper.getNextHourFromInstant(input));

        ______TS("on the hour rounds to next hour");
        input = getInstantOf(2017, Month.JUNE, 15, 13, 0);
        assertEquals(expected, TimeHelper.getNextHourFromInstant(input));

        ______TS("one minute to next hour rounds to next hour");
        input = getInstantOf(2017, Month.JUNE, 15, 12, 59);
        expected = getInstantOf(2017, Month.JUNE, 15, 13, 0);
        assertEquals(expected, TimeHelper.getNextHourFromInstant(input));
    }

    private Instant getInstantOf(int year, Month month, int day, int hour, int minute) {
        return LocalDateTime.of(year, month, day, hour, minute).toInstant(ZoneOffset.UTC);
    }

    @Test
    public void testFormatTimeZoneToUtcOffset() {
        ______TS("timezone offset of +8.0");
        assertEquals("UTC +08:00", TimeHelper.formatTimeZoneToUtcOffset(8.0));

        ______TS("timezone offset of -8.0");
        assertEquals("UTC -08:00", TimeHelper.formatTimeZoneToUtcOffset(-8.0));

        ______TS("timezone offset of +18.0");
        assertEquals("UTC +18:00", TimeHelper.formatTimeZoneToUtcOffset(18.0));

        ______TS("timezone offset of -18.0");
        assertEquals("UTC -18:00", TimeHelper.formatTimeZoneToUtcOffset(-18.0));

        ______TS("timezone offset of +0.25");
        assertEquals("UTC +00:15", TimeHelper.formatTimeZoneToUtcOffset(0.25));

        ______TS("timezone offset of +0.5");
        assertEquals("UTC +00:30", TimeHelper.formatTimeZoneToUtcOffset(0.5));

        ______TS("timezone offset of +0.75");
        assertEquals("UTC +00:45", TimeHelper.formatTimeZoneToUtcOffset(0.75));

        ______TS("timezone offset of -0.25");
        assertEquals("UTC -00:15", TimeHelper.formatTimeZoneToUtcOffset(-0.25));

        ______TS("timezone offset of -0.5");
        assertEquals("UTC -00:30", TimeHelper.formatTimeZoneToUtcOffset(-0.5));

        ______TS("timezone offset of -0.75");
        assertEquals("UTC -00:45", TimeHelper.formatTimeZoneToUtcOffset(-0.75));
    }

}
