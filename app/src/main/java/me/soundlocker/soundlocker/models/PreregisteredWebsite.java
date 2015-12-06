package me.soundlocker.soundlocker.models;

/**
 * A website that has been preregistered to work with the "insert password into WebView" function.
 * The list of website are stored in assets/websites.json.
 */
public class PreregisteredWebsite {

    /**
     * The label for the common name for the website, e.g, Diaspora or Ebay
     */
    public static final String SHORT_NAME = "shortname";
    /**
     * The label for the URL to the login page for the mobile site of the website
     */
    public static final String LOGIN_URL = "loginUrl";
    /**
     * The label for the HTML element that holds the password field in the login page
     */
    public static final String PASSWORD_FIELD_ELEMENT = "passwordFieldElement";

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
