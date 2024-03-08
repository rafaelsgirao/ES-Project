package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.webservice

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetAssessmentsByInstitutionWebServiceIT extends SpockTest{

    @LocalServerPort
    private int port

    def activity
    def institutionId
    def institution
    def volunteer
    def volunteer2

    def setup(){
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        given: "a institution"
        institution = institutionService.getDemoInstitution()
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

    def "a volunteer tries to list assessments"() {
        given:
        demoVolunteerLogin()

        when:
        def response = webClient.get()
                .uri('/assessments/' + institutionId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(List<AssessmentDto>.class)
                .block()

        then: "check response"
        response.size() == 2
        response.get(0).review == REVIEW_1
        response.get(0).volunteer.id == volunteer.getId()
        response.get(0).institution.id == institution.getId()
        response.get(1).review == REVIEW_2
        response.get(1).volunteer.id == volunteer2.getId()
        response.get(1).institution.id == institution.getId()

        cleanup:
        deleteAll()
    }

    def "a member tries to list assessments"(){
        given:
        demoMemberLogin()

        when:
        def response = webClient.get()
                .uri('/assessments/' + institutionId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(List<AssessmentDto>.class)
                .block()

        then: "check response"
        response.size() == 2
        response.get(0).review == REVIEW_1
        response.get(0).volunteer.id == volunteer.getId()
        response.get(0).institution.id == institution.getId()
        response.get(1).review == REVIEW_2
        response.get(1).volunteer.id == volunteer2.getId()
        response.get(1).institution.id == institution.getId()

        cleanup:
        deleteAll()
    }

    
    def cleanup() {
        deleteAll()
    }

}