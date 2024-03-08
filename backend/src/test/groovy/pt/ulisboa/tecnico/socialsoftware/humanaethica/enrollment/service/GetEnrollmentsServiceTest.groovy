package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException

import spock.lang.Unroll

@DataJpaTest
class GetEnrollmentsServiceTest extends SpockTest{

    public static final Integer NO_EXIST = 123

    def activity
    def activityId
    def volunteer
    def volunteer2

    def setup(){
        given: "a institution"
        def institution = institutionService.getDemoInstitution()
        and: "a volunteer"
        volunteer = createVolunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED, USER_1_PASSWORD)
        and: "another volunteer"
        volunteer2 = createVolunteer(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED, USER_2_PASSWORD)
        and: "an activity"
        activity = createActivity(ACTIVITY_NAME_1, IN_TWO_DAYS, IN_THREE_DAYS, IN_ONE_DAY, institution)
        and: "enrollment info"
        def enrollmentDto = createEnrollmentDto(ENROLLMENT_MOTIVATION)
        and: 'an enrollment'
        def enrollment = new Enrollment(enrollmentDto, volunteer, activity)
        enrollmentRepository.save(enrollment)
        and: 'another enrollment'
        enrollmentDto.motivation = ENROLLMENT_MOTIVATION_2
        enrollment = new Enrollment(enrollmentDto, volunteer2, activity)
        enrollmentRepository.save(enrollment)
        activityId = activity.getId()
    }

    def "get two enrollments"() {
        when:
        def result = enrollmentService.getEnrollmentsByActivity(activityId)

        then:
        result.size() == 2
        result.get(0).getMotivation() == ENROLLMENT_MOTIVATION
        result.get(1).getMotivation() == ENROLLMENT_MOTIVATION_2
    }

    def "list the enrollments of a non-existing activity"() {
        when:
        def result = enrollmentService.getEnrollmentsByActivity(NO_EXIST)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_NOT_FOUND  
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}