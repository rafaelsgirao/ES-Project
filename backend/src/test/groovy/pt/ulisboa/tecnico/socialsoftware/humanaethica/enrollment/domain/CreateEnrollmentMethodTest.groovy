package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import spock.lang.Unroll

import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto;

import java.time.LocalDateTime
@DataJpaTest
class CreateEnrollmentMethodTest extends SpockTest {
    Enrollment otherEnrollment = Mock()
    Volunteer volunteer = Mock()
    Activity activity = Mock()
    def enrollmentDto

    def setup() {
        given: "enrollment info"
        enrollmentDto = new EnrollmentDto()
        enrollmentDto.motivation = ENROLLMENT_MOTIVATION
    }

    def "create enrollment sucessfully"() {
        given:
        activity.getApplicationDeadline() >> IN_ONE_DAY
        activity.getEnrollments() >> []

        when:
        def result = new Enrollment(activity, volunteer, enrollmentDto)

        then: "check result"
        result.getMotivation() == ENROLLMENT_MOTIVATION
        result.getActivity() == activity
        result.getVolunteer() == volunteer


    }

    @Unroll
    def "create enrollment and violate invariant date: applicationDeadline=#applicationDeadline"(){
        given:
        activity.getApplicationDeadline() >> applicationDeadline
        activity.getEnrollments() >> []
        
        when:
        new Enrollment(activity, volunteer, enrollmentDto)
        
        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        applicationDeadline          | errorMessage
        ONE_DAY_AGO                  | ErrorMessage.ENROLLMENT_DATE_AFTER_DEADLINE             
        ONE_SEC_AGO                  | ErrorMessage.ENROLLMENT_DATE_AFTER_DEADLINE             
        TWO_DAYS_AGO                 | ErrorMessage.ENROLLMENT_DATE_AFTER_DEADLINE             
    }

    @Unroll
    def "create enrollment and violate invariant motivation: motivation=#motivation"() {
        given:
        enrollmentDto.motivation = motivation

        when:
        new Enrollment(activity, volunteer, enrollmentDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        motivation                  || errorMessage
        null                        || ErrorMessage.MOTIVATION_IS_EMPTY
        " "                         || ErrorMessage.MOTIVATION_TOO_SHORT
        ENROLLMENT_SHORT_MOTIVATION || ErrorMessage.MOTIVATION_TOO_SHORT
    }

    @Unroll
    def "create enrollment and violate invariant already enrolled"() {
        given:
        activity.getApplicationDeadline() >> IN_ONE_DAY
        activity.getEnrollments() >> [otherEnrollment]
        otherEnrollment.getVolunteer() >> volunteer

        when:
        new Enrollment(activity, volunteer, enrollmentDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.VOLUNTEER_ALREADY_ENROLLED
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}      

}