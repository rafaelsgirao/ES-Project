package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain;

import jakarta.persistence.*;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

import java.time.LocalDateTime;

@Entity
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String motivation;
    private LocalDateTime enrollmentDate;

    @ManyToOne
    private Activity activity;

    @ManyToOne
    private Volunteer volunteer;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getMotivation() {
        return motivation;
    }
    public void setMotivation(String motivation) {
        this.motivation = motivation;
    }

    public LocalDateTime getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDateTime enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
        volunteer.addEnrollment(this);
    }

    public Activity getActivity() {
        return activity;
    }
    public void setActivity(Activity activity) {
        this.activity = activity;
        activity.addEnrollment(this);
    }

    public Enrollment() {
    }

    public Enrollment(EnrollmentDto enrollmentDto, Volunteer volunteer, Activity activity) {
        setMotivation(enrollmentDto.getMotivation());
        setEnrollmentDate(DateHandler.toLocalDateTime(enrollmentDto.getEnrollmentDate()));
        setVolunteer(volunteer);
        setActivity(activity);

        verifyInvariants();
    }

    private void verifyInvariants() {
        verifyMotivation();
        verifyApplicationDate();
    }

    private void verifyMotivation() {
        if (getMotivation() == null || getMotivation().trim().isEmpty()) {
            throw new HEException(MOTIVATION_IS_EMPTY, this.motivation);

        }
        if (getMotivation().length() < 10) {
            throw new HEException(MOTIVATION_TOO_SHORT, this.motivation);
        }
    }

    public void verifyVolunteerEnrolled() {
        if (getActivity().getEnrollments().stream().anyMatch(e -> e.getVolunteer().equals(getVolunteer()))) {
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
