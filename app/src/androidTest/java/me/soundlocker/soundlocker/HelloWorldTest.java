package me.soundlocker.soundlocker;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class HelloWorldTest {

    @Rule
    public ActivityTestRule<PasswordScreen> mActivityRule = new ActivityTestRule(PasswordScreen.class);

    @Test
    public void listGoesOverTheFold() {
        onView(withText("Facebook.com")).check(matches(isDisplayed()));
    }

    @Test
    public void checkChooseSong() {
        onView(withId(R.id.chooseSong))             // withId(R.id.my_view) is a ViewMatcher
                .perform(click())                   // click() is a ViewAction
                .check(matches(isDisplayed()));     // matches(isDisplayed()) is a ViewAssertion
    }

    @Test
    public void checkCopy() {
        onView(withId(R.id.copy))               // withId(R.id.my_view) is a ViewMatcher
                .perform(click())               // click() is a ViewAction
                .check(matches(isDisplayed())); // matches(isDisplayed()) is a ViewAssertion
    }
}
