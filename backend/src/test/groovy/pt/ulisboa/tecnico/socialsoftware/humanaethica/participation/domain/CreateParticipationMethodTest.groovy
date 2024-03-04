package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler

@DataJpaTest
class CreateParticipationMethodTest extends SpockTest {
    Activity activity = Mock()
    Volunteer volunteer = Mock()
    Institution institution = Mock()
    Participation otherParticipation = Mock()
    Activity otherActivity = Mock()

    def participationDto

    def setup() {
        given: "participation info"
        participationDto = new ParticipationDto()
        participationDto.rating = PARTICIPATION_RATING_1
        participationDto.acceptanceDate = NOW
    }

    def "succesfully create participation"() {
        given:
        activity.getApplicationDeadline() >> DateHandler.now().minusDays(3)
        activity.getParticipantsNumberLimit() >> 10
        activity.getEndingDate() >> IN_TWO_DAYS

        otherParticipation.getActivity() >> otherActivity
        volunteer.getParticipations() >> [otherParticipation]
        activity.getParticipations() >> [otherParticipation]


        when:
        def result = new Participation(participationDto, activity, volunteer)

        then: "check result"
        result.getRating() == PARTICIPATION_RATING_1
        result.getAcceptanceDate() == NOW
        result.getVolunteer() == volunteer
        result.getActivity() == activity
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
