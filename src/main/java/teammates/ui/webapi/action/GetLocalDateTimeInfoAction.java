package teammates.ui.webapi.action;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.ui.webapi.output.LocalDateTimeInfoData;

/**
 * Resolve local date time under certain timezone to an UNIX timestamp.
 */
public class GetLocalDateTimeInfoAction extends Action {

    private static final DateTimeFormatter LOCAL_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Only instructor can get local date time information");
        }
    }

    @Override
    public ActionResult execute() {
        String localDateTimeStr = getNonNullRequestParamValue(Const.ParamsNames.LOCAL_DATE_TIME);
        String zoneIdStr = getNonNullRequestParamValue(Const.ParamsNames.TIME_ZONE);

        LocalDateTime localDateTime = null;
        ZoneId zoneId = null;

        try {
            localDateTime = LocalDateTime.parse(localDateTimeStr, LOCAL_DATE_TIME_FORMATTER);
            zoneId = ZoneId.of(zoneIdStr);
        } catch (DateTimeException e) {
            throw new InvalidHttpParameterException(e.getMessage(), e);
        }

        LocalDateTimeInfoData ldtInfo = null;
        switch(TimeHelper.LocalDateTimeAmbiguityStatus.of(localDateTime, zoneId)) {
        case UNAMBIGUOUS:
            ldtInfo = LocalDateTimeInfoData.unambiguous(localDateTime.atZone(zoneId).toInstant().toEpochMilli());
            break;
        case GAP:
            ldtInfo = LocalDateTimeInfoData.gap(localDateTime.atZone(zoneId).toInstant().toEpochMilli());
            break;
        case OVERLAP:
            Instant earlierInterpretation = localDateTime.atZone(zoneId).withEarlierOffsetAtOverlap().toInstant();
            Instant laterInterpretation = localDateTime.atZone(zoneId).withLaterOffsetAtOverlap().toInstant();
            ldtInfo = LocalDateTimeInfoData.overlap(localDateTime.atZone(zoneId).toInstant().toEpochMilli(),
                    earlierInterpretation.toEpochMilli(), laterInterpretation.toEpochMilli());
            break;
        default:
            Assumption.fail("Unreachable case");
            break;
        }

        return new JsonResult(ldtInfo);
    }
}
