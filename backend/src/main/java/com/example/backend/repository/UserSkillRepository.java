package com.example.backend.repository;



import com.example.backend.model.SkillType;
import com.example.backend.model.UserSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.List;
import java.util.Optional;

public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {



    List<UserSkill> findByUser_Id(Long userId);

    boolean existsByUser_IdAndSkill_IdAndType(
            Long userId, Long skillId, SkillType type);

    Optional<UserSkill> findByIdAndUser_Id(
            Long id, Long userId);
}
