package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser

@DataJpaTest
class GetParticipationsByActivity extends SpockTest {
    def activity
    def activity2

    def setup() {
        def institution = institutionService.getDemoInstitution()
        and: "a theme"
        def themes = new ArrayList<>()
        themes.add(createTheme(THEME_NAME_1, Theme.State.APPROVED, null))
        and: "an activity"
        def activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,5,ACTIVITY_DESCRIPTION_1,
                ONE_DAY_AGO,IN_TWO_DAYS,IN_THREE_DAYS,null)
        activity = new Activity(activityDto, institution, themes)
        activityRepository.save(activity)
        and: "another activity"
        def activityDto2 = createActivityDto(ACTIVITY_NAME_2,ACTIVITY_REGION_2,5,ACTIVITY_DESCRIPTION_2,
                ONE_DAY_AGO,IN_TWO_DAYS,IN_THREE_DAYS,null)
        activity2 = new Activity(activityDto2, institution, themes)
        activityRepository.save(activity2)
        and: "a volunteer"
        def volunteer1 = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)
        userRepository.save(volunteer1)
        and: "another volunteer"
        def volunteer2 = new Volunteer(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)
        userRepository.save(volunteer2)
        and: "a participation"
        def participationDto1 = createParticipationDto(PARTICIPATION_RATING_1, NOW, (User) volunteer1, activity)
        participationService.createParticipation(activity.getId(), volunteer1.getId(), participationDto1)
        and: "another participation"
        def participationDto2 = createParticipationDto(PARTICIPATION_RATING_2, NOW, volunteer2, activity)
        participationService.createParticipation(activity.getId(), volunteer2.getId(), participationDto2)
        and: "a participation in another activity"
        def participationDto3 = createParticipationDto(PARTICIPATION_RATING_2, NOW, volunteer1, activity2)
        participationService.createParticipation(activity2.getId(), volunteer1.getId(), participationDto3)
    }

    def 'get participations by activity'() {
        when:
        def result = participationService.getParticipationsByActivity(activity.getId())

        then:
        result.size() == 2
        result[0].getRating() == PARTICIPATION_RATING_1
        result[1].getRating() == PARTICIPATION_RATING_2
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
