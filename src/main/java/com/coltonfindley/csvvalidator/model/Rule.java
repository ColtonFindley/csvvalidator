package com.coltonfindley.csvvalidator.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity // class maps to a database table
@Table(name = "rules") // maps to the rules table
public class Rule {
    
    // column in rules table
    @Id // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // table relationship
    // LAZY: RuleSet is only loaded from the database when actually accessed
    @ManyToOne(fetch = FetchType.LAZY, optional = false)

    @JoinColumn(name = "rule_set_id", nullable = false) // foreign key
    private RuleSet ruleSet;

    @Column(name = "column_name", length = 100, nullable = false)
    private String columnName;

    @Enumerated(EnumType.STRING) // store the enum's name, from RuleType.java (RANGE, etc...)
    @Column(name = "rule_type", length = 30, nullable = false)
    private RuleType ruleType;

    @Enumerated(EnumType.STRING) // store enum's name, from DataType.java (INTEGER, etc...)
    @Column(name = "data_type", length = 20)
    private DataType dataType;

    @Column(name = "min_value", precision = 19, scale = 4)
    private BigDecimal minValue;

    @Column(name = "max_value", precision = 19, scale = 4)
    private BigDecimal maxValue;

    @Column(length = 500)
    private String pattern;

    @Column(name = "allowed_values", length = 1000)
    private String allowedValues;

    // Getters and setters
    public Long getId() { return id; }

    public RuleSet getRuleSet() { return ruleSet; }
    public void setRuleSet(RuleSet ruleSet) {this.ruleSet = ruleSet;}

    public String getColumnName() {return columnName;}
    public void setColumnName(String columnName) {this.columnName = columnName;}

    public RuleType getRuleType() {return ruleType;}
    public void setRuleType(RuleType ruleType) {this.ruleType = ruleType;}

    public DataType getDataType() {return dataType;}
    public void setDataType(DataType dataType) {this.dataType = dataType;}

    public BigDecimal getminValue() {return minValue;}
    public void setMinValue(BigDecimal minValue) {this.minValue = minValue;}

    public BigDecimal getMaxValue() {return maxValue;}
    public void setMaxValue(BigDecimal maxValue) {this.maxValue = maxValue;}

    public String getPattern() {return pattern;}
    public void setPattern(String pattern) {this.pattern = pattern;}

    public String getAllowedValues() {return allowedValues;}
    public void setAllowedValues(String allowedValues) {this.allowedValues = allowedValues;}
}
