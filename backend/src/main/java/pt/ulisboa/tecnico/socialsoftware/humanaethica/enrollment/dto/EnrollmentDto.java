package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto;

import java.time.LocalDateTime;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto;

public abstract class EnrollmentDto {

    // EnrollmentDto attributes
    private Integer id;
    private String motivation;
    private LocalDateTime enrollmentDate;
    private ActivityDto activityDto;
    private UserDto volunteerDto;

    // EnrollmentDto getters
    public Integer getId() { return id; }
    public String getMotivation() { return motivation; }
    public LocalDateTime getEnrollmentDate() { return enrollmentDate; }
    public ActivityDto getActivityDto() { return activityDto; }
    public UserDto getVolunteerDto() { return volunteerDto; }

    // EnrollmentDto setters
    public void setId(Integer id) { this.id = id; }
    public void setMotivation(String motivation) { this.motivation = motivation; }
    public void setEnrollmentDate(LocalDateTime enrollmentDate) { this.enrollmentDate = enrollmentDate; }
    public void setActivityDto(ActivityDto activityDto) { this.activityDto = activityDto; }
    public void setVolunteerDto(UserDto volunteerDto) { this.volunteerDto = volunteerDto; }

}