package teammates.ui.newcontroller;

import com.google.gson.Gson;
import org.apache.http.HttpStatus;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;

import java.util.Optional;

/**
 * Action: Update a student's profile
 */
public class PutStudentProfileAction extends Action {
    private static final Gson GSON = new Gson();

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        // TODO populate with finer grain privilege control
    }

    @Override
    public ActionResult execute() {
        String studentId = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_ID);
        // the patch does not necessarily contain all fields; only those that require a change
        StudentProfileAttributes patch = GSON.fromJson(getRequestBody(), StudentProfileAttributes.class);

        // this normalizes the patch so we get the new "resultant profile"
        StudentProfileAttributes newProfile;
        try {
            newProfile = normalizeProfileDiff(studentId, patch);
        } catch (EntityDoesNotExistException ednee) {
            return new JsonResult(ednee.getMessage(), HttpStatus.SC_NOT_FOUND);
        }

        if (!isProfileValid(newProfile)) {
            return new JsonResult("Profile data is invalid", HttpStatus.SC_BAD_REQUEST);
        }

        try {
            logic.updateOrCreateStudentProfile(sanitizeProfile(newProfile));
        } catch (InvalidParametersException ipe) {
            return new JsonResult(ipe.getMessage(), HttpStatus.SC_BAD_REQUEST);
        }

        // TODO we don't actually need a body in the response, the response code is good enough to know if we succeeded
        return new JsonResult(Const.StatusMessages.STUDENT_PROFILE_EDITED, HttpStatus.SC_ACCEPTED);
    }

    private StudentProfileAttributes sanitizeProfile(StudentProfileAttributes studentProfile) {
        studentProfile.shortName = StringHelper.trimIfNotNull(studentProfile.shortName);
        studentProfile.email = StringHelper.trimIfNotNull(studentProfile.email);
        studentProfile.gender = StringHelper.trimIfNotNull(studentProfile.gender);
        studentProfile.nationality = StringHelper.trimIfNotNull(studentProfile.nationality);
        studentProfile.institute = StringHelper.trimIfNotNull(studentProfile.institute);
        studentProfile.moreInfo = StringHelper.trimIfNotNull(studentProfile.moreInfo);

        return studentProfile;
    }

    /**
     * This converts a diff of the student's profile into a normalized one with other unchanging fields populated.
     *
     * e.g. if only shortName is being changed, then only shortName is not null inside {@code diff}. This function will
     * populate the other fields of the {@code diff} object with the profile's original values.
     *
     * This allows the PUT request to contain only the new value of the fields being changed instead of the entire
     * profile.
     *
     * @param studentId the google ID of the student whose profile is being changed (used to get original profile data)
     * @param diff the StudentProfileAttributes populated from the body of the PUT request
     * @throws EntityDoesNotExistException if the original profile could not be found
     * @return
     */
    private StudentProfileAttributes normalizeProfileDiff(String studentId, StudentProfileAttributes diff)
            throws EntityDoesNotExistException {
        StudentProfileAttributes original = logic.getStudentProfile(studentId);
        if (original == null) {
            throw new EntityDoesNotExistException(
                    String.format("Original student profile could not be found for studentId=%s", studentId)
            );
        }

        diff.googleId = studentId;
        diff.shortName = Optional.ofNullable(diff.shortName).orElse(original.shortName);
        diff.email = Optional.ofNullable(diff.email).orElse(original.email);
        diff.gender = Optional.ofNullable(diff.gender).orElse(original.gender);
        diff.nationality = Optional.ofNullable(diff.nationality).orElse(original.nationality);
        diff.institute = Optional.ofNullable(diff.institute).orElse(original.institute);
        diff.moreInfo = Optional.ofNullable(diff.moreInfo).orElse(original.moreInfo);
        diff.pictureKey = original.pictureKey;

        return diff;
    }

    private boolean isProfileValid(StudentProfileAttributes profile) {
        return profile != null && profile.shortName != null && profile.email != null && profile.institute != null
                && profile.nationality != null && profile.gender != null && profile.moreInfo != null
                && profile.googleId != null && profile.pictureKey != null;
    }
}
