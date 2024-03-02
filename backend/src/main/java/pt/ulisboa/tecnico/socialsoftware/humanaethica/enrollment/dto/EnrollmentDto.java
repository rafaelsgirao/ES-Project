package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

public class EnrollmentDto {

    // EnrollmentDto attributes
    private Integer id;
    private String motivation;
    private String enrollmentDate;
    private ActivityDto activityDto;
    private UserDto volunteerDto;

    public EnrollmentDto() {
    }

   public EnrollmentDto(Enrollment enrollment, boolean deepCopyActivity, boolean deepCopyVolunteer) {
        setId(enrollment.getId());
        setMotivation(enrollment.getMotivation());
        setEnrollmentDate(DateHandler.toISOString(enrollment.getEnrollmentDate()));

        if (deepCopyActivity && (enrollment.getActivity() != null)){
            setActivityDto(new ActivityDto(enrollment.getActivity(), false));
        }
        if (deepCopyVolunteer && (enrollment.getVolunteer() != null)){
            setVolunteerDto(new UserDto(enrollment.getVolunteer()));
        }
    }

    // EnrollmentDto getters
    public Integer getId() { return id; }
    public String getMotivation() { return motivation; }
    public String getEnrollmentDate() { return enrollmentDate; }
    public ActivityDto getActivityDto() { return activityDto; }
    public UserDto getVolunteerDto() { return volunteerDto; }

    // EnrollmentDto setters
    public void setId(Integer id) { this.id = id; }
    public void setMotivation(String motivation) { this.motivation = motivation; }
    public void setEnrollmentDate(String enrollmentDate) { this.enrollmentDate = enrollmentDate; }
    public void setActivityDto(ActivityDto activityDto) { this.activityDto = activityDto; }
    public void setVolunteerDto(UserDto volunteerDto) { this.volunteerDto = volunteerDto; }

    @Override
    public String toString() {
        return "EnrollmentDto{" +
                "id=" + id +
                ", motivation='" + motivation + '\'' +
                ", enrollmentDate='" + enrollmentDate + '\'' +
                ", activity=" + activityDto  +
                ", volunteer=" + volunteerDto +
                '}';
    }
}