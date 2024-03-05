package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment

@DataJpaTest
class RegisterEnrollmentServiceTest extends SpockTest {

    def activity
    def volunteer

    def setup() {
        activity = activityService.getDemoActivity()
        volunteer = authUserService.loginDemoMemberAuth().getUser()
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}