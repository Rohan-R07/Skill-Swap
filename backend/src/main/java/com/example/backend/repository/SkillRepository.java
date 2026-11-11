package com.example.backend.repository;



import com.example.backend.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface SkillRepository extends JpaRepository<Skill, Long> {

    Optional<Skill> findByNameIgnoreCaseAndCategoryIgnoreCase(
            String name, String category);
}

