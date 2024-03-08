package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain;

import jakarta.persistence.*;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

import java.time.LocalDateTime;
@Table(name = "enrollment")
@Entity
public class Enrollment {

    // Attributes

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String motivation;
    private LocalDateTime enrollmentDate;
    @ManyToOne private Activity activity;
    @ManyToOne private Volunteer volunteer;

    // Constructors

    public Enrollment() {}

    public Enrollment(Activity activity, Volunteer volunteer, EnrollmentDto enrollmentDto) {
        setMotivation(enrollmentDto.getMotivation());
        setEnrollmentDate(DateHandler.now());
        setVolunteer(volunteer);
        setActivity(activity);

        verifyInvariants();
    }

    // Getters

    public Integer getId() { return id; }
    public String getMotivation() { return motivation; }
    public LocalDateTime getEnrollmentDate() { return enrollmentDate; }
    public Volunteer getVolunteer() { return volunteer; }
    public Activity getActivity() { return activity; }
    
    // Setters

    public void setId(Integer id) { this.id = id; }
    public void setMotivation(String motivation) { this.motivation = motivation; }
    public void setEnrollmentDate(LocalDateTime enrollmentDate) { this.enrollmentDate = enrollmentDate; }
    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
        volunteer.addEnrollment(this);
    }
    public void setActivity(Activity activity) {
        this.activity = activity;
        activity.addEnrollment(this);
    }

    // Invariants

    private void verifyInvariants() {
        verifyMotivation();
        verifyApplicationDate();
        verifyVolunteerEnrolled();
    }

    private void verifyMotivation() {
        if (getMotivation() == null) {
            throw new HEException(MOTIVATION_IS_EMPTY, this.motivation);
        }
        if (getMotivation().trim().length() < 10) {
            throw new HEException(MOTIVATION_TOO_SHORT, this.motivation);
        }
    }

    public void verifyVolunteerEnrolled() {
        if (getActivity().getEnrollments().stream().anyMatch(
                e -> e.getVolunteer().equals(getVolunteer()) && !e.equals(this)
        )) {
            throw new HEException(VOLUNTEER_ALREADY_ENROLLED, this.volunteer.getName());
        }
    }

    private void verifyApplicationDate() {
        if (getEnrollmentDate() == null) {
            throw new HEException(ENROLLMENT_INVALID_DATE, "enrollment date");
        }
        if (getEnrollmentDate().isAfter(this.activity.getApplicationDeadline())) {
            throw new HEException(ENROLLMENT_DATE_AFTER_DEADLINE);
        }
    }

}
