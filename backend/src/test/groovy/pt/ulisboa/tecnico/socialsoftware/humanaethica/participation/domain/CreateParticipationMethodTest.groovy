package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

@DataJpaTest
class CreateParticipationMethodTest extends SpockTest {
    Activity activity = Mock()
    Volunteer volunteer = Mock()
    Volunteer otherVolunteer = Mock()
    Institution institution = Mock()
    Participation participation = Mock()
    Participation otherParticipation = Mock()
    Activity otherActivity = Mock()

    def participationDto

    def setup() {
        given: "participation info"
        participationDto = new ParticipationDto()
        participationDto.rating = PARTICIPATION_RATING_1
    }

    def "succesfully create participation"() {
        given:
        activity.getApplicationDeadline() >> TWO_DAYS_AGO
        activity.getParticipantsNumberLimit() >> ACTIVITY_LIMIT_2
        activity.getEndingDate() >> IN_TWO_DAYS
        otherParticipation.getActivity() >> otherActivity
        volunteer.getParticipations() >> [otherParticipation]
        activity.getParticipations() >> [otherParticipation]

        when:
        def result = new Participation(participationDto, activity, volunteer)

        then: "check result"
        result.getRating() == PARTICIPATION_RATING_1
        result.getVolunteer() == volunteer
        result.getActivity() == activity
    }

    @Unroll
    def "create participation and violate number of participants has to be <= to the limit"() {
        given:
        activity.getApplicationDeadline() >> TWO_DAYS_AGO
        activity.getParticipantsNumberLimit() >> ACTIVITY_LIMIT_1
        activity.getEndingDate() >> IN_TWO_DAYS
        otherParticipation.getActivity() >> activity
        otherVolunteer.getParticipations() >> [otherParticipation]
        activity.getParticipations() >> [otherParticipation]
        volunteer.getParticipations() >> []

        when:
        new Participation(participationDto, activity, volunteer)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_FULL
    }

    @Unroll
    def "create participation and violate volunteer can only participate once in an activity"() {
        given:
        activity.getApplicationDeadline() >> TWO_DAYS_AGO
        activity.getEndingDate() >> IN_TWO_DAYS
        otherParticipation.getActivity() >> activity
        volunteer.getParticipations() >> [otherParticipation]
        activity.getParticipations() >> [otherParticipation]

        when:
        new Participation(participationDto, activity, volunteer)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.DUPLICATE_PARTICIPATION
    }

    @Unroll
    def "create participation and violate volunteer can only be a participant after the application deadline"() {
        given:
        activity.getApplicationDeadline() >> IN_TWO_DAYS
        activity.getEndingDate() >> IN_THREE_DAYS
        activity.getParticipantsNumberLimit() >> ACTIVITY_LIMIT_2
        volunteer.getParticipations() >> []
        activity.getParticipations() >> []

        when:
        new Participation(participationDto, activity, volunteer)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ENROLLMENT_PERIOD_NOT_ENDED
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}

