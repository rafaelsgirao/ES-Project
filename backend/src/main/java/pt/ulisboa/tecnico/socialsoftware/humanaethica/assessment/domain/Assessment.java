package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain;

import java.time.LocalDateTime;
import java.util.List;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;
import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import jakarta.persistence.*;

@Entity
@Table(name = "assessments")
public class Assessment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String review;

    private LocalDateTime reviewDate;

    @ManyToOne
    @JoinColumn(name = "volunteer_id")
    private Volunteer volunteer;

    @ManyToOne
    @JoinColumn(name = "institution_id")
    private Institution institution;

    public Assessment() {
    }

    public Assessment(Institution institution, Volunteer volunteer,AssessmentDto assessmentDto) {
        setReview(assessmentDto.getReview());
        setReviewDate(DateHandler.now());
        setInstitution(institution);
        setVolunteer(volunteer);
        verifyInvariants();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public LocalDateTime getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
        volunteer.addAssessment(this);
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
        institution.addAssessment(this);
    }

    private void verifyInvariants() {
        reviewHasAtLeast10Characters();
        verifyUniqueVolunteerAssessment();
        verifyInstitutionHasFinishedActivities();
    }

    private void reviewHasAtLeast10Characters() {
        if (this.review == null || this.review.trim().length() < 10) {
           throw new HEException(ASSESSMENT_REVIEW_TOO_SHORT);
        }
    }

    private void verifyUniqueVolunteerAssessment() {
        institution.getAssessments().stream().forEach(assessment -> {
            if (assessment.getVolunteer().equals(this.volunteer)) {
                throw new HEException(VOLUNTEER_ALREADY_ASSESSED_INSTITUTION);
            }
        });
    }

    private void verifyInstitutionHasFinishedActivities() {
        List<Activity> activities = institution.getActivities();
        if(!activities.stream().anyMatch(activity -> activity.getEndingDate()
                .isBefore(LocalDateTime.now()))) {
                    throw new HEException(INSTITUTION_WITHOUT_ACTIVITIES_FINISHED);
        }
    }



}