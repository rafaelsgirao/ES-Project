package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.INSTITUTION_NOT_FOUND;
import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.USER_NOT_FOUND;
import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.INVALID_ASSESSMENT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.repository.AssessmentRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.repository.InstitutionRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import java.util.List;


import org.springframework.transaction.annotation.Isolation;

@Service
public class AssessmentService {
    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<AssessmentDto> getAssessmentsByInstitution(Integer institutionId) {
        return assessmentRepository.findByInstitutionId(institutionId).stream()
                .map(assessment -> new AssessmentDto(assessment, true, true)).toList();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AssessmentDto createAssessment(Integer userId, Integer institutionId, AssessmentDto assessmentDto) {
        if (userId == null) throw new HEException(USER_NOT_FOUND);
        if (institutionId == null) throw new HEException(INSTITUTION_NOT_FOUND);
        if(assessmentDto == null) throw new HEException(INVALID_ASSESSMENT);

        Volunteer volunteer = (Volunteer) userRepository.findById(userId).orElseThrow(() -> new HEException(USER_NOT_FOUND, userId));
        Institution institution = institutionRepository.findById(institutionId).orElseThrow(() -> new HEException(INSTITUTION_NOT_FOUND, institutionId));

        Assessment assessment = new Assessment(institution, volunteer, assessmentDto);
        assessmentRepository.save(assessment);

        return new AssessmentDto(assessment, true, true);
    }

}