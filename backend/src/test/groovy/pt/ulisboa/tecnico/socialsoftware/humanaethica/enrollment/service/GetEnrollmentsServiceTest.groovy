package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto


@DataJpaTest
class GetEnrollmentsServiceTest extends SpockTest{
    def setup(){
        given: "a institution"
        def institution = institutionService.getDemoInstitution()
        and: "a volunteer"
        def volunteer = createMember(USER_1_NAME, USER_1_USERNAME, USER_1_PASSWORD, USER_1_EMAIL, Member.Type.VOLUNTEER, institution, Member.State.APPROVED)
        and: "another volunteer"
        def volunteer2 = createMember(USER_2_NAME, USER_2_USERNAME, USER_2_PASSWORD, USER_2_EMAIL, Member.Type.VOLUNTEER, institution, Member.State.APPROVED)
        and: "an activity"
        def activity = createActivity(ACTIVITY_NAME_1, IN_TWO_DAYS, IN_THREE_DAYS, IN_ONE_DAY)
        and: "enrollment info"
        def enrollmentDto = createEnrollmentDto(ENROLLMENT_MOTIVATION,NOW)
        and: 'an enrollment'
        def enrollment = new Enrollment(enrollmentDto, activity, volunteer)
        activityRepository.save(enrollment)
        and: 'another enrollment'
        enrollmentDto.motivation = ENROLLMENT_MOTIVATION_2
        enrollmentDto.enrollmentDate = NOW
        enrollment = new Enrollment(enrollmentDto, activity, volunteer2)
        activityRepository.save(enrollment)
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}