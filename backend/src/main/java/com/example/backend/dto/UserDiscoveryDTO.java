package com.example.backend.dto;

import java.util.List;

public class UserDiscoveryDTO {

    private Long id;
    private String name;
    private String email;
    private String bio;
    private List<SkillDTO> teachSkills;
    private List<SkillDTO> learnSkills;

    public UserDiscoveryDTO() {
    }

    public UserDiscoveryDTO(Long id, String name, String email, String bio,
                            List<SkillDTO> teachSkills, List<SkillDTO> learnSkills) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.bio = bio;
        this.teachSkills = teachSkills;
        this.learnSkills = learnSkills;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public List<SkillDTO> getTeachSkills() {
        return teachSkills;
    }

    public void setTeachSkills(List<SkillDTO> teachSkills) {
        this.teachSkills = teachSkills;
    }

    public List<SkillDTO> getLearnSkills() {
        return learnSkills;
    }

    public void setLearnSkills(List<SkillDTO> learnSkills) {
        this.learnSkills = learnSkills;
    }
}
