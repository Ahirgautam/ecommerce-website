package com.ecommerce.ecommerce_site.dto;

public class CategoryDTO {
    private Long id;
    private String name;
    private Long parentId;

    public CategoryDTO(Long id, String name, Long parentId) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getParentId() {
        return parentId;
    }
}