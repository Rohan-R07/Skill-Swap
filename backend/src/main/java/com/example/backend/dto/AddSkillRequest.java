package com.example.backend.dto;



import com.example.backend.model.SkillType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AddSkillRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String category;

    @NotNull
    private SkillType type;

    public AddSkillRequest() {
    }

    public AddSkillRequest(String name, String category, SkillType type) {
        this.name = name;
        this.category = category;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public SkillType getType() {
        return type;
    }

    public void setType(SkillType type) {
        this.type = type;
    }
}
