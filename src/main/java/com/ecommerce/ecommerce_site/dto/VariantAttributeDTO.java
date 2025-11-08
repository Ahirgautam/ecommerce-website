package com.ecommerce.ecommerce_site.dto;

public class VariantAttributeDTO {
    private String name;
    private String value;

    public VariantAttributeDTO(){};
    public VariantAttributeDTO( String attributeName, String attributeValue) {
        this.name = attributeName;
        this.value = attributeValue;
    }


    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}