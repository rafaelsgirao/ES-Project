package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain;

import jakarta.persistence.*;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
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
        setEnrollmentDate(enrollmentDto.getEnrollmentDate());
        setVolunteer(volunteer);
        setActivity(activity);

        verifyInvariants();
    }

    private void verifyInvariants() {
        verifyMotivation();
    }

    private void verifyMotivation() {
        if (getMotivation() == null || getMotivation().trim().isEmpty()) {
            throw new IllegalArgumentException("Motivation cannot be empty");
        }
        if (getMotivation().length() < 10) {
            throw new IllegalArgumentException("Motivation must have at least 10 characters");
        }
    }

    public void verifyVolunteerEnrolled() {
        if (getActivity().getEnrollments().stream().anyMatch(e -> e.getVolunteer().equals(getVolunteer()))) {
            throw new HEException(VOLUNTEER_ALREADY_ENROLLED, this.volunteer.getName());
        }
    }

}
