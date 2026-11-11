package com.example.backend.service;


import com.example.backend.dto.AddSkillRequest;
import com.example.backend.model.Skill;
import com.example.backend.model.SkillType;
import com.example.backend.model.User;
import com.example.backend.model.UserSkill;
import com.example.backend.repository.SkillRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.UserSkillRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SkillService {

    private final SkillRepository skillRepository;
    private final UserSkillRepository userSkillRepository;
    private final UserRepository userRepository;

    public SkillService(
            SkillRepository skillRepository,
            UserSkillRepository userSkillRepository,
            UserRepository userRepository) {

        this.skillRepository = skillRepository;
        this.userSkillRepository = userSkillRepository;
        this.userRepository = userRepository;
    }

    // Add a skill for a user
    @Transactional
    public UserSkill addSkill(Long userId, AddSkillRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        SkillType type = request.getType();

        Skill skill = skillRepository
                .findByNameIgnoreCaseAndCategoryIgnoreCase(
                        request.getName().trim(),
                        request.getCategory().trim())
                .orElseGet(() -> {
                    Skill newSkill = new Skill();
                    newSkill.setName(request.getName().trim());
                    newSkill.setCategory(request.getCategory().trim());
                    return skillRepository.save(newSkill);
                });

        boolean alreadyExists =
                userSkillRepository.existsByUser_IdAndSkill_IdAndType(
                        userId, skill.getId(), type);

        if (alreadyExists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "You have already added this skill with this type");
        }

        UserSkill userSkill = new UserSkill();
        userSkill.setUser(user);
        userSkill.setSkill(skill);
        userSkill.setType(type);

        return userSkillRepository.save(userSkill);
    }

    // Get all skills associated with a user
    public List<UserSkill> getUserSkills(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User not found");
        }

        return userSkillRepository.findByUser_Id(userId);
    }

    // Delete a skill association belonging to a user
    @Transactional
    public void deleteUserSkill(Long userId, Long userSkillId) {

        UserSkill userSkill = userSkillRepository
                .findByIdAndUser_Id(userSkillId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Skill association not found for this user"));

        userSkillRepository.delete(userSkill);
    }
}
