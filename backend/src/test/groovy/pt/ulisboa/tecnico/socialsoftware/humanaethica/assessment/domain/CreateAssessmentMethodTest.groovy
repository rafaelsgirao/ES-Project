package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity

@DataJpaTest
class CreateAssessmentMethodTest extends SpockTest {
    Volunteer volunteer = Mock()
    Institution institution = Mock()
    Activity activity = Mock()

    Assessment otherAssessment = Mock()
    Volunteer otherVolunteer = Mock()

    def assessmentDto

    def setup() {
        given: "assessment info"
        assessmentDto = new AssessmentDto()
        assessmentDto.review = REVIEW_1
        assessmentDto.reviewDate = NOW
    }

    def "create assessment sucessfully"() {
        given:
        activity.getEndingDate() >> ONE_DAY_AGO
        institution.getActivities() >> [activity]
        otherAssessment.getVolunteer() >> otherVolunteer
        institution.getAssessments() >> [otherAssessment]

        when:
        def assessment = new Assessment(institution, volunteer, assessmentDto)

        then:
        assessment.getReview() == REVIEW_1
        assessment.getReviewDate() == NOW
        assessment.getInstitution() == institution
        assessment.getVolunteer() == volunteer
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}