package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto;

import java.time.LocalDateTime;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto;

public abstract class EnrollmentDto {

    private Integer id;
    private String motivation;
    private LocalDateTime enrollmentDate;
    private ActivityDto activityDto;
    private UserDto volunteerDto;

    public abstract Integer getId();
    public abstract void setId(Integer id);

    public abstract String getMotivation();
    public abstract void setMotivation(String motivation);

    public abstract LocalDateTime getEnrollmentDate();
    public abstract void setEnrollmentDate(LocalDateTime enrollmentDate);

    public abstract ActivityDto getActivityDto();
    public abstract void setActivityDto(ActivityDto activityDto);

    public abstract UserDto getVolunteerDto();
    public abstract void setVolunteerDto(UserDto volunteerDto);

}