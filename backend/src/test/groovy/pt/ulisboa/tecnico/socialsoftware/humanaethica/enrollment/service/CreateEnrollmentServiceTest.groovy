package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import spock.lang.Unroll

@DataJpaTest
class CreateEnrollmentServiceTest extends SpockTest {
    public static final String EXIST = "exist"
    public static final String NO_EXIST = "noExist"

    def activity
    def volunteer

    def setup() {
        activity = createActivity(ACTIVITY_NAME_1, IN_TWO_DAYS, IN_THREE_DAYS, IN_ONE_DAY)
        volunteer = authUserService.loginDemoVolunteerAuth().getUser()
    }

    def "create enrollment"() {
        given: "an enrollment dto"
        def enrollmentDto = createEnrollmentDto(ENROLLMENT_MOTIVATION)
        
        when:
        def result = enrollmentService.createEnrollment(volunteer.getId(), activity.getId(), enrollmentDto)
        then: "the returned data is correct"
        result.motivation == ENROLLMENT_MOTIVATION
        result.volunteerDto.id == volunteer.getId()
        result.activityDto.id == activity.getId()
        and: "the enrollment is saved in the database"
        enrollmentRepository.findAll().size() == 1
        and: "the stored data is correct"
        def storedEnrollment = enrollmentRepository.findById(result.id).get()
        storedEnrollment.motivation == ENROLLMENT_MOTIVATION
        storedEnrollment.volunteer.id == volunteer.getId()
        storedEnrollment.activity.id == activity.getId()
    }

    @Unroll
    def "invalid arguments: volunteerId=#volunteerId | activityId=#activityId | newEnrollmentDto=#newEnrollmentDto "() {
        when:
        def result = enrollmentService.createEnrollment(getVolunteerId(volunteerId), getActivityId(activityId), getEnrollmentDto(newEnrollmentDto))

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and: "no enrollment is stored in the database"
        enrollmentRepository.findAll().size() == 0

        where:
        volunteerId | activityId   | newEnrollmentDto   || errorMessage
        EXIST       | EXIST        | NO_EXIST           || ErrorMessage.INVALID_ENROLLMENT
        null        | EXIST        | EXIST              || ErrorMessage.USER_NOT_FOUND
        NO_EXIST    | EXIST        | EXIST              || ErrorMessage.USER_NOT_FOUND
        EXIST       | null         | EXIST              || ErrorMessage.ACTIVITY_NOT_FOUND
        EXIST       | NO_EXIST     | EXIST              || ErrorMessage.ACTIVITY_NOT_FOUND        
    }

    def getVolunteerId(volunteerId){
        if (volunteerId == EXIST)
            return volunteer.id
        else if (volunteerId == NO_EXIST)
            return 222
        return null
    }

    def getActivityId(activityId){
        if (activityId == EXIST)
            return activity.id
        else if (activityId == NO_EXIST)
            return 222
        return null
    }

    def getEnrollmentDto(exists) {
    if (exists == EXIST) {
      return createEnrollmentDto(ENROLLMENT_MOTIVATION)
    } else {
      return null
    }
  }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}