package teammates.ui.newcontroller;

import teammates.common.util.NationalityHelper;

import java.util.List;

/**
 * Action: get a list of valid nationalities
 */
public class GetNationalitiesAction extends Action {
    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    public void checkSpecificAccessControl() {
        return;
    }

    @Override
    public ActionResult execute() {
        NationalityData nationalities = new NationalityData(NationalityHelper.getNationalities());
        return new JsonResult(nationalities);
    }

    public static class NationalityData extends ActionResult.ActionOutput {
        private List<String> nationalities;

        public NationalityData(List<String> nationalities) {
            this.nationalities = nationalities;
        }

        public List<String> getNationalities() {
            return nationalities;
        }
    }
}
