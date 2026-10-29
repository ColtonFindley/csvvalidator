package com.coltonfindley.csvvalidator.model;

import jakarta.persistence.*;   // Entity, Table, Id, Column, OneToMany, etc.
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity // this class maps to a database table
@Table(name = "rule_sets") // which table
public class RuleSet {

    // columns in rule_sets table
    @Id // this field is the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // database generates it
    private Long id;

    @Column(nullable = false, length = 100) // column settings
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    // table relationship - one rule set has many rules
    @OneToMany(mappedBy = "ruleSet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rule> rules = new ArrayList<>();

    // JPA lifecycle callback
    @PrePersist // function called automatically just before an insert
    void onCreate() {
        // define createdAt
        createdAt = Instant.now();
    }

    // keeps table relationship in sync, adds to rule list and sets rule's back-reference
    public void addRule(Rule rule) {
        rules.add(rule);
        rule.setRuleSet(this);
    }

    // getters and setters
    public Long getId() {return id;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;} 

    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}

    public Instant getCreatedAt() {return createdAt;}
    // set by onCreate()

    public List<Rule> getRules() {return rules;}
}