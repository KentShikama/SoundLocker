package me.soundlocker.soundlocker;

public class Application {
    private String applicationName;
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
