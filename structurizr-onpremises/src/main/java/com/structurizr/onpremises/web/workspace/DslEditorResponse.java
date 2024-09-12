package com.structurizr.onpremises.web.workspace;

public final class DslEditorResponse {

    private final boolean success;
    private final String message;
    private final int lineNumber;
    private final String workspace;

    public DslEditorResponse(boolean success, String message) {
        this(success, message, 0);
    }

    public DslEditorResponse(boolean success, String message, int lineNumber) {
        this.success = success;
        this.message = message;
        this.lineNumber = lineNumber;
        this.workspace = null;
    }

    public DslEditorResponse(String workspace) {
        this.success = true;
        this.workspace = workspace;
        this.message = null;
        this.lineNumber = 1;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getWorkspace() {
        return workspace;
    }

}