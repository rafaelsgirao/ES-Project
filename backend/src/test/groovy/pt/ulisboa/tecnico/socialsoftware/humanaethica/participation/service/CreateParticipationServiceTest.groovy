package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.service

import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;


@DataJpaTest
class CreateParticipationServiceTest extends SpockTest {

    def activity
    def volunteer

    def setup() {
        def institution = institutionService.getDemoInstitution()
        def activityDto = createActivityDto(ACTIVITY_NAME_1, ACTIVITY_REGION_1, 1, ACTIVITY_DESCRIPTION_1,
                IN_ONE_DAY,IN_TWO_DAYS,IN_THREE_DAYS, [])
        activity = new Activity(activityDto, institution, [])
        activity.setParticipantsNumberLimit(ACTIVITY_LIMIT_2) //FIXME: this should work with just 1!
        activityRepository.save(activity)
        volunteer = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)
        userRepository.save(volunteer)
    }

    def "create participation"() {
        given: "a participation dto"
        def participationDto = createParticipationDto(
            10, IN_TWO_DAYS, volunteer, activity
        )
        when: "create a participation"
        def participation = participationService.createParticipation(
            activity.id, volunteer.id, participationDto
        )
        then: "returned data is correct"
        participation.rating == 10
        participation.acceptanceDate == IN_TWO_DAYS
        participation.activity.id == activity.id
        participation.volunteer.id == volunteer.id
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
//FIXME: add more test cases

//FIXME: remove
