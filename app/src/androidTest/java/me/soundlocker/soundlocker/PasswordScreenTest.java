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
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class PasswordScreenTest {

    @Rule
    public ActivityTestRule<PasswordScreen> rule = new ActivityTestRule(PasswordScreen.class);
    private Boolean run;

    @Before
    public void setUp() {
        run = Boolean.valueOf(InstrumentationRegistry.getArguments().getString("UI"));
    }

    @Test
    public void passwordScreenTests() {
        if (run) {
            checkSelectedAppNameDisplayed();
            checkChooseSongOpensSongPickerScreen();
        }
    }

    public void checkSelectedAppNameDisplayed() {
        onView(withId(R.id.textView)).check(matches(isDisplayed()));
    }

    public void checkChooseSongOpensSongPickerScreen() {
        onView(withId(R.id.chooseSong)).perform(click());
        onView(withId(R.id.song_query)).check(matches(isDisplayed()));
    }
}
