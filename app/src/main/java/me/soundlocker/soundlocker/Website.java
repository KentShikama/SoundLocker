package me.soundlocker.soundlocker;

public class Website {
    private String shortname;
    private String loginUrl;
    private String passwordFieldElement;

    Website(String shortname, String loginUrl, String passwordFieldElement) {
        this.shortname = shortname;
        this.loginUrl = loginUrl;
        this.passwordFieldElement = passwordFieldElement;
    }

    public String getShortname() {
        return shortname;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public String getPasswordFieldElement() {
        return passwordFieldElement;
    }
}
