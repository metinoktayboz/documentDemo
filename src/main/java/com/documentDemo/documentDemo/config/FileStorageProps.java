package com.documentDemo.documentDemo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

public class FileStorageProps {
    @ConfigurationProperties(prefix = "file")
    public static class FileStorageProperties {
        private String uploadDir;

        public String getUploadDir() {
            return uploadDir;
        }

        public void setUploadDir(String uploadDir) {
            this.uploadDir = uploadDir;
        }
    }
}
