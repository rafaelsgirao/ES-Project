package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain;

import jakarta.persistence.*;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;

import java.time.LocalDateTime;

@Entity
public abstract class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String motivation;
    private LocalDateTime enrollmentDate;

    @ManyToOne
    private Activity activity;

    @ManyToOne
    private Volunteer volunteer;

    public abstract Integer getId();
    public abstract void setId(Integer id);

    public abstract String getMotivation();
    public abstract void setMotivation(String motivation);

    public abstract LocalDateTime getEnrollmentDate();
    public abstract void setEnrollmentDate(LocalDateTime enrollmentDate);

    public abstract Volunteer getVolunteer();
    public abstract void setVolunteer(Volunteer volunteer);

    public abstract Activity getActivity();
    public abstract void setActivity(Activity activity);

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

}
