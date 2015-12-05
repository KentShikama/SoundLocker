package me.soundlocker.soundlocker.models;

public class PreregisteredWebsite {
    private String shortName;
    private String loginUrl;
    private String passwordFieldElement;

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
