package teammates.ui.newcontroller;

import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.util.Const;

/**
 * Action: GET a student's profile details.
 */
public class GetStudentProfileAction extends Action {
    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        // TODO complete later
    }

    @Override
    public ActionResult execute() {
        String studentId = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_ID);
        StudentProfileAttributes studentProfile = logic.getStudentProfile(studentId);
        StudentInfo output = new StudentInfo(studentProfile);
        return new JsonResult(output);
    }

    /**
     * Response format.
     */
    public static class StudentInfo extends ActionResult.ActionOutput {
        private final StudentProfileAttributes studentProfile;

        public StudentInfo(StudentProfileAttributes studentProfile) {
            this.studentProfile = studentProfile;
        }

        public StudentProfileAttributes getStudentProfile() {
            return this.studentProfile;
        }
    }
}
