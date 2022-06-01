package com.documentDemo.documentDemo.models;

import java.util.ArrayList;

public class AllowedTypes {
    private ArrayList<String> allowedTypes;

    public AllowedTypes(ArrayList<String> allowedTypes) {
        this.allowedTypes = allowedTypes;
        allowedTypes.add("png");
        allowedTypes.add("jpeg");
        allowedTypes.add("jpg");
        allowedTypes.add("docx");
        allowedTypes.add("pdf");
        allowedTypes.add("xlsx");
    }

    public ArrayList<String> getAllowedTypes() {
        return allowedTypes;
    }

    public void setAllowedTypes(ArrayList<String> allowedTypes) {
        this.allowedTypes = allowedTypes;
    }
}
