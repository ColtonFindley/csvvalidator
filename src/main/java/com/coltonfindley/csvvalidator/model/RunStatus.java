package com.coltonfindley.csvvalidator.model;

// outcome of a validation run
public enum RunStatus {
    COMPLETED, // file was fully processed (even if rows failed validation)
    FAILED // processing itself failed
}
