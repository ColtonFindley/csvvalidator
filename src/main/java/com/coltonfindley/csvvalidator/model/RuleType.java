package com.coltonfindley.csvvalidator.model;

// kinds of rules a column can have - stored in the database as text
// enum is used instead of string so the compiler can catch typos
public enum RuleType {
    REQUIRED, // value cannot be blank
    DATA_TYPE, // value must parse as the rule's dataType
    RANGE, // numberic values must be within a min and/or max range
    ALLOWED_VALUES, // values must be one of the listed values
    REGEX, // values must match the pattern
    UNIQUE // value must not repeat within the file
}
