package me.soundlocker.soundlocker;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SongPickerScreenTest {

    private static final String ALBUM_NAME = "Native";
    private static final String FAMOUS_SONG_FROM_ALBUM_1 = "Counting Stars";
    private static final String FAMOUS_SONG_FROM_ALBUM_2 = "Love Runs Out";
    @Rule
    public ActivityTestRule<SongPickerScreen> rule = new ActivityTestRule(SongPickerScreen.class);
    private Boolean run;

    @Before
    public void setUp() {
        run = Boolean.valueOf(InstrumentationRegistry.getArguments().getString("UI"));
    }

    @Test
    public void passwordScreenTests() {
        if (run) {
            songQueryFieldExists();
            songsFromAlbumAreDisplayed();
        }
    }

    public void songQueryFieldExists() {
        onView(withId(R.id.song_query)).check(matches(isDisplayed()));
    }

    public void songsFromAlbumAreDisplayed() {
        onView(withId(R.id.song_query)).perform(typeText(ALBUM_NAME));
        onView(withText(FAMOUS_SONG_FROM_ALBUM_1)).check(matches(isDisplayed()));
        onView(withText(FAMOUS_SONG_FROM_ALBUM_2)).check(matches(isDisplayed()));
    }
}
