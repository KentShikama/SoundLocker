package me.soundlocker.soundlocker.ui;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import me.soundlocker.soundlocker.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ApplicationsListTest {

    @Rule
    public ActivityTestRule<ApplicationsList> rule = new ActivityTestRule(ApplicationsList.class);
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
        onView(ViewMatchers.withId(R.id.addApplicationButton)).check(matches(isDisplayed()));
    }

    public void clickingAddBtnBringsUserToApplicationAdderScreen() {
        onView(withId(R.id.addApplicationButton)).perform(click());
        onView(withId(R.id.addApplication)).check(matches(isDisplayed()));
    }
}
