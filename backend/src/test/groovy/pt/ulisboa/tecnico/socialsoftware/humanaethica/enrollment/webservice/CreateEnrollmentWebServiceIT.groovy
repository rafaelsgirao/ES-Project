package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.webservice

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler


import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateEnrollmentWebServiceIT extends SpockTest {
    
    @LocalServerPort
    private int port
    
    def enrollmentDto
    def activityId

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        
        enrollmentDto = createEnrollmentDto(ENROLLMENT_MOTIVATION, NOW)

        def activity = createActivity(ACTIVITY_NAME_1, IN_TWO_DAYS, IN_THREE_DAYS, IN_ONE_DAY)
        activityId = activity.getId()
    }

    def "login as volunteer, and create an enrollment"() {
        given: 
        demoVolunteerLogin()

        when:
        def response = webClient.post()
                .uri('/enrollments/' + activityId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(enrollmentDto)
                .retrieve()
                .bodyToMono(EnrollmentDto.class)
                .block()

        then: "check response data"
        response.motivation == ENROLLMENT_MOTIVATION
        response.enrollmentDate == DateHandler.toISOString(NOW)
        and: 'check database data'
        enrollmentRepository.count() == 1
        def enrollment = enrollmentRepository.findAll().get(0)
        enrollment.getMotivation() == ENROLLMENT_MOTIVATION
        enrollment.getEnrollmentDate().withNano(0) == NOW.withNano(0)
    }

    def "login as volunteer, and create an enrollment with error"() {
        given:
        demoVolunteerLogin()
        and: 'a empty motivation'
        enrollmentDto.motivation = " "

        when: 'the volunteer tries to create an enrollment'
        webClient.post()
                .uri('/enrollments/' + activityId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(enrollmentDto)
                .retrieve()
                .bodyToMono(EnrollmentDto.class)
                .block()

        then: 'check response status' 
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.BAD_REQUEST
        enrollmentRepository.count() == 0
    }

    def "login as member, and create an enrollment"() {
        given: 'a member'
        demoMemberLogin()

        when: 'the member tries to create enrollment'
        webClient.post()
                .uri('/enrollments/' + activityId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(enrollmentDto)
                .retrieve()
                .bodyToMono(EnrollmentDto.class)
                .block()

        then: "an error is returned"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        enrollmentRepository.count() == 0
    }

    def "login as admin, and create an enrollment"() {
        
    }

    def cleanup() {
        deleteAll()
    }
    
}