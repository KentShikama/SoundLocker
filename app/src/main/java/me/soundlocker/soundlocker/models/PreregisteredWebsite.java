package me.soundlocker.soundlocker.models;

public class PreregisteredWebsite {
    private final String shortName;
    private final String loginUrl;
    private final String passwordFieldElement;

    public PreregisteredWebsite(String shortName, String loginUrl, String passwordFieldElement) {
        this.shortName = shortName;
        this.loginUrl = loginUrl;
        this.passwordFieldElement = passwordFieldElement;
    }

    public String getShortName() {
        return shortName;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public String getPasswordFieldElement() {
        return passwordFieldElement;
    }
}
