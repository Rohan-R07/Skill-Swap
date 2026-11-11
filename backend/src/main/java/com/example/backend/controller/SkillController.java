package com.example.backend.controller;



import com.example.backend.dto.AddSkillRequest;
import com.example.backend.model.UserSkill;
import com.example.backend.service.SkillService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/skills")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    // Add a skill
    @PostMapping
    public ResponseEntity<UserSkill> addSkill(
            @PathVariable Long userId,
            @Valid @RequestBody AddSkillRequest request) {

        UserSkill result = skillService.addSkill(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // Get all skills of a user
    @GetMapping
    public ResponseEntity<List<UserSkill>> getUserSkills(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                skillService.getUserSkills(userId));
    }

    // Delete a skill association
    @DeleteMapping("/{userSkillId}")
    public ResponseEntity<Void> deleteUserSkill(
            @PathVariable Long userId,
            @PathVariable Long userSkillId) {

        skillService.deleteUserSkill(userId, userSkillId);

        return ResponseEntity.noContent().build();
    }
}

