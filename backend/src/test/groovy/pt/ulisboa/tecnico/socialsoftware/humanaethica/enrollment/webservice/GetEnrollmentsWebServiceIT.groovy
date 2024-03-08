package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.webservice

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
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetEnrollmentsWebServiceIT extends SpockTest{

    @LocalServerPort
    private int port

    def activity
    def activityId
    def volunteer
    def volunteer2

    def setup(){
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        given: "a institution"
        def institution = institutionService.getDemoInstitution()
        and: "a volunteer"
        volunteer = createVolunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED, USER_1_PASSWORD)
        and: "another volunteer"
        volunteer2 = createVolunteer(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED, USER_2_PASSWORD)
        and: "an activity"
        activity = createActivity(ACTIVITY_NAME_1, IN_TWO_DAYS, IN_THREE_DAYS, IN_ONE_DAY, institution)
        and: "enrollment info"
        def enrollmentDto = createEnrollmentDto(ENROLLMENT_MOTIVATION)
        and: 'an enrollment'
        def enrollment = new Enrollment(enrollmentDto, volunteer, activity)
        enrollmentRepository.save(enrollment)
        and: 'another enrollment'
        enrollmentDto.motivation = ENROLLMENT_MOTIVATION_2
        enrollment = new Enrollment(enrollmentDto, volunteer2, activity)
        enrollmentRepository.save(enrollment)

        activityId = activity.getId()
    }

    def "get enrollments"() {
        given:
        demoMemberLogin()

        when:
        def response = webClient.get()
                .uri('/enrollments/' + activityId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(List<EnrollmentDto>.class)
                .block()

        then: "check response"
        response.size() == 2
        response.get(0).motivation == ENROLLMENT_MOTIVATION
        response.get(0).volunteerDto.id == volunteer.getId()
        response.get(0).activityDto.id == activity.getId()
        response.get(1).motivation == ENROLLMENT_MOTIVATION_2
        response.get(1).volunteerDto.id == volunteer2.getId()
        response.get(1).activityDto.id == activity.getId()
    }

    def "a volunteer tries to list enrollments"(){
        given: 'a volunteer'
        demoVolunteerLogin()

        when:
        def response = webClient.get()
            .uri('/enrollments/' + activityId)
            .headers(httpHeaders -> httpHeaders.putAll(headers))
            .retrieve()
            .bodyToMono(List<EnrollmentDto>.class)
            .block()

        then: "an error is returned"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
    }

    def "a admin tries to list enrollments"(){
        given: 'a admin'
        demoAdminLogin()

        when:
        def response = webClient.get()
            .uri('/enrollments/' + activityId)
            .headers(httpHeaders -> httpHeaders.putAll(headers))
            .retrieve()
            .bodyToMono(List<EnrollmentDto>.class)
            .block()

        then: "an error is returned"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
    }


    def cleanup() {
        deleteAll()
    }

}