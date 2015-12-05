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
public class ApplicationsListScreenTest {

    @Rule
    public ActivityTestRule<ApplicationsListScreen> rule = new ActivityTestRule(ApplicationsListScreen.class);
    private Boolean run;

    @Before
    public void setUp() {
        run = Boolean.valueOf(InstrumentationRegistry.getArguments().getString("UI"));
    }

    @Test
    public void passwordScreenTests() {
        if (run) {
            selectedAddBtnDisplayed();
        }
    }

    public void selectedAddBtnDisplayed() {
        onView(withId(R.id.addBtn)).check(matches(isDisplayed()));
    }

    public void clickingAddBtnBringsUserToApplicationAdderScreen() {
        onView(withId(R.id.addBtn)).perform(click());
        onView(withId(R.id.addApp)).check(matches(isDisplayed()));
    }
}
