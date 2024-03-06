package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.repository.EnrollmentRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

@DataJpaTest
class CreateEnrollmentServiceTest extends SpockTest {

    def activity
    def volunteer

    def setup() {
        activity = createActivity(ACTIVITY_NAME_1, IN_TWO_DAYS, IN_THREE_DAYS, IN_ONE_DAY)
        volunteer = authUserService.loginDemoVolunteerAuth().getUser()
    }

    def "create enrollment"() {
        given: "an enrollment dto"
        def enrollmentDto = new EnrollmentDto()
        enrollmentDto.setMotivation(ENROLLMENT_MOTIVATION)
        enrollmentDto.setEnrollmentDate(DateHandler.toISOString(NOW))
        when:
        def result = enrollmentService.createEnrollment(volunteer.getId(), activity.getId(), enrollmentDto)
        then: "the returned data is correct"
        result.motivation == ENROLLMENT_MOTIVATION
        result.enrollmentDate == DateHandler.toISOString(NOW)
        result.volunteerDto.id == volunteer.getId()
        result.activityDto.id == activity.getId()
        and: "the enrollment is saved in the database"
        enrollmentRepository.findAll().size() == 1
        and: "the stored data is correct"
        def storedEnrollment = enrollmentRepository.findById(result.id).get()
        storedEnrollment.motivation == ENROLLMENT_MOTIVATION
        storedEnrollment.enrollmentDate == NOW
        storedEnrollment.volunteer.id == volunteer.getId()
        storedEnrollment.activity.id == activity.getId()
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}