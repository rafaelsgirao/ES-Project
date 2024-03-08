package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User

import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException

import spock.lang.Unroll

@DataJpaTest
class GetAssessmentsByInstitutionServicetest extends SpockTest{

    public static final Integer NO_EXIST = 123

    def activity
    def institutionId
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
        activity = createActivity(ACTIVITY_NAME_1, TWO_DAYS_AGO, ONE_DAY_AGO, NOW, institution)
        and: "assessment info"
        def assessmentDto = createAssessmentDto(REVIEW_1, NOW)
        and: 'an assessment'
        def assessment = new Assessment(institution, volunteer, assessmentDto)
        assessmentRepository.save(assessment)
        and: 'another assessment'
        assessmentDto.review = REVIEW_2
        assessment = new Assessment(institution, volunteer2, assessmentDto)
        assessmentRepository.save(assessment)
        institutionId = institution.getId()
    }

    def "get two assessments"() {
        when:
        def result = assessmentService.getAssessmentsByInstitution(institutionId)

        then:
        result.size() == 2
        result.get(0).getReview() == REVIEW_1
        result.get(1).getReview() == REVIEW_2
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}