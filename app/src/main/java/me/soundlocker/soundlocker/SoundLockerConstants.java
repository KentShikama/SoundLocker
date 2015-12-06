package me.soundlocker.soundlocker;

/**
 * Holds constants that are used throughout SoundLocker
 */
public class SoundLockerConstants {

    private SoundLockerConstants() {
    }

    /**
     * The label for the website name that will be passed into the WebView.
     * @see me.soundlocker.soundlocker.models.PreregisteredWebsite
     */
    public static final String PREREGISTERED_WEBSITE = "website";
    /**
     * The label for the boolean value of whether an application is a preregistered website or not.
     * @see me.soundlocker.soundlocker.models.PreregisteredWebsite
     */
    public static final String PREREGISTERED = "preregistered";
    /**
     * The label for name of the application you want to generate a password for.
     * This could be the same as the preregistered website for some applications.
     */
    public static final String APP_NAME = "app_name";
    /**
     * The label for name of the song that will be used to generate the password for
     * a specific application.
     */
    public static final String SONG_NAME = "song_name";
    /**
     * The label for the preview url for a song that will be used to generated the password.
     * This is the preview url corresponding to the song that {@link #SONG_NAME} labels.
     */
    public static final String PREVIEW_URL = "preview_url";
    /**
     * The master ID for this app. Note each installation of SoundLocker has a unique master ID.
     * This makes it so that each application will generate a different password for the same song.
     * See our README for architectural decisions on why passwords are based on this master ID.
     */
    public static final String MASTER_ID = "master_id";
    /**
     * The label for the generated password for a specific application.
     */
    public static final String PASSWORD = "password";
}