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
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import spock.lang.Unroll
import spock.lang.Shared

@DataJpaTest
class CreateParticipationServiceTest extends SpockTest {

    def activity
    def volunteer
    public static final String EXIST = "exist"
    public static final String NO_EXIST = "noExist"
    public static final String EXIST_FAKE = "existFake"

    def setup() {
        def institution = institutionService.getDemoInstitution()
        def activityDto = createActivityDto(ACTIVITY_NAME_1, ACTIVITY_REGION_1, 1, ACTIVITY_DESCRIPTION_1,
                ONE_DAY_AGO,IN_TWO_DAYS,IN_THREE_DAYS, [])
        activity = new Activity(activityDto, institution, [])
        activity.setParticipantsNumberLimit(ACTIVITY_LIMIT_2) //FIXME: this should work with just 1!
        activityRepository.save(activity)
        volunteer = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)
        userRepository.save(volunteer)
    }

    def "create participation"() {
        when: "create a participation"
        def participationDto = createParticipationDto(
            PARTICIPATION_RATING_1, NOW, volunteer, activity
        )
        def participation = participationService.createParticipation(
            activity.id, participationDto
        )
        then: "returned data is correct"
        participation.rating == PARTICIPATION_RATING_1
        participation.activity.id == activity.id
        participation.volunteer.id == volunteer.id
        and: "the data is stored, and the stored data is correct"
        def storedParticipation = participationRepository.findById(participation.id).get()
        storedParticipation.rating == PARTICIPATION_RATING_1
        storedParticipation.activity.id == activity.id
        storedParticipation.volunteer.id == volunteer.id
    }

    @Unroll
    def "create participation with invalid arguments: volunteerId=#volunteerId | activityId=#activityId | participationDto = #participationDto"() {
        given: "a combination of invalid arguments"
        when: "create a participation"
        def newVolunteer = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)
        newVolunteer.setId(getVolunteerId())
        def newParticipationDto = getParticipationDto(participationDto)
        if (getActivityId(activityId) != 222 && newParticipationDto != null && newParticipationDto.getVolunteer() != null) {
            newParticipationDto.setVolunteer(new UserDto(newVolunteer))
        }
        participationService.createParticipation(
            getActivityId(activityId), newParticipationDto
        )
        then: "an error is thrown"
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and: "no participation is stored in the database"
        participationRepository.findAll().size() == 0

        where:
        volunteerId            | activityId | participationDto  || errorMessage
        NO_EXIST            | EXIST    | EXIST    || ErrorMessage.USER_NOT_FOUND
        EXIST_FAKE | EXIST     | EXIST    || ErrorMessage.USER_NOT_FOUND
        EXIST | NO_EXIST | EXIST    || ErrorMessage.ACTIVITY_NOT_FOUND
        EXIST | EXIST_FAKE | EXIST    || ErrorMessage.ACTIVITY_NOT_FOUND
        EXIST | EXIST | NO_EXIST    || ErrorMessage.INVALID_PARTICIPATION
        EXIST | EXIST | EXIST_FAKE    || ErrorMessage.USER_NOT_FOUND
    }

    def getActivityId(activityId) {
        if (activityId == EXIST) {
            return activity.id
        }
        else if (activityId == EXIST_FAKE) {
            return 222
        }
        return null
    }

    def getVolunteerId(volunteerId) {
        if (volunteerId == EXIST) {
            return volunteer.id
        }
        else if (volunteerId == EXIST_FAKE) {
            return 22194
        }
        return null
    }

    def getParticipationDto(participationDto) {
        if (participationDto == EXIST) {
            return createParticipationDto(
                PARTICIPATION_RATING_1, NOW, volunteer, activity
            )
        }
        else if (participationDto == EXIST_FAKE) {
            return new ParticipationDto()
        }
        else
            return null
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration { }

}
