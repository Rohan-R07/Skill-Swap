package com.example.backend.service;

import com.example.backend.dto.SkillDTO;
import com.example.backend.dto.UserDiscoveryDTO;
import com.example.backend.model.User;
import com.example.backend.model.UserSkill;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.UserSkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DiscoveryService {

    private final UserRepository userRepository;
    private final UserSkillRepository userSkillRepository;

    public DiscoveryService(UserRepository userRepository,
                            UserSkillRepository userSkillRepository) {
        this.userRepository = userRepository;
        this.userSkillRepository = userSkillRepository;
    }

    @Transactional(readOnly = true)
    public List<UserDiscoveryDTO> discoverUsers(Long excludeUserId, String name) {
        List<User> users;
        if (name != null && !name.trim().isEmpty()) {
            String trimmedName = name.trim();
            users = userRepository.findByNameContainingIgnoreCase(trimmedName);
        } else {
            users = userRepository.findAll();
        }

        if (excludeUserId != null) {
            users = users.stream()
                    .filter(u -> !u.getId().equals(excludeUserId))
                    .collect(Collectors.toList());
        }

        List<UserSkill> allUserSkills = userSkillRepository.findAll();

        Map<Long, List<UserSkill>> skillsByUserId = new HashMap<>();
        for (UserSkill userSkill : allUserSkills) {
            skillsByUserId.computeIfAbsent(userSkill.getUser().getId(), k -> new ArrayList<>())
                    .add(userSkill);
        }

        List<UserDiscoveryDTO> result = new ArrayList<>();
        for (User user : users) {
            List<UserSkill> userSkills = skillsByUserId.getOrDefault(user.getId(), new ArrayList<>());

            List<SkillDTO> teachSkills = userSkills.stream()
                    .filter(us -> us.getType() == com.example.backend.model.SkillType.TEACH)
                    .map(us -> new SkillDTO(us.getSkill().getName(), us.getSkill().getCategory()))
                    .collect(Collectors.toList());

            List<SkillDTO> learnSkills = userSkills.stream()
                    .filter(us -> us.getType() == com.example.backend.model.SkillType.LEARN)
                    .map(us -> new SkillDTO(us.getSkill().getName(), us.getSkill().getCategory()))
                    .collect(Collectors.toList());

            result.add(new UserDiscoveryDTO(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getBio(),
                    teachSkills,
                    learnSkills
            ));
        }

        return result;
    }
}
