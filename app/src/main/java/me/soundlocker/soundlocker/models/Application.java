package me.soundlocker.soundlocker.models;

public class Application {
    private final String applicationName;
    private int passwordLength;

    public Application(String applicationName, int passwordLength) {
        this.applicationName = applicationName;
        this.passwordLength = passwordLength;
    }

    public int getPasswordLength() {
        return passwordLength;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setPasswordLength(int passwordLength) {
        this.passwordLength = passwordLength;
    }
}
