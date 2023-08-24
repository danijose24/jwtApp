package com.training.fileOp.entity;

public class DiffDelta {

    private final String originalLine;
    private final String revisedLine;

    public DiffDelta(String originalLine, String revisedLine) {
        this.originalLine = originalLine;
        this.revisedLine = revisedLine;
    }

    public String getOriginalLine() {
        return originalLine;
    }

    public String getRevisedLine() {
        return revisedLine;
    }
}
