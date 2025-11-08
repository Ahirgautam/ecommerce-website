package com.ecommerce.ecommerce_site.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jdk.jfr.Category;

import java.util.List;
/*
    Sql Query
    create table Categories (
    id bigint AUTO_INCREMENT primary key,
    name varchar(255),
    parent_id bigint,
    foreign key (parent_id) references category (id));
*/
@Entity
@Table(name = "categories")
public class Categories {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    private String name;

    // one parent can map to many subcategories
    @OneToMany(mappedBy = "parent")
    @JsonManagedReference
    private List<Categories> children;

    @ManyToOne
    @JsonBackReference
    private Categories parent;

    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private List<Products> products;




    public Long getId() { return categoryId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Categories getParent() { return parent; }
    public void setParent(Categories parent) { this.parent = parent; }
    public List<Categories> getChildren() { return children; }
    public void setChildren(List<Categories> children) { this.children = children; }
    public List<Products> getProducts() { return products; }
    public void setProducts(List<Products> products) { this.products = products; }
}
