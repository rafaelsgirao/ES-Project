package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;

import java.time.LocalDateTime;

public abstract class Enrollment {

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

}
