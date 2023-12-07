package com.example.emailaggregatorapp2;


import com.example.emailaggregatorapp2.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;

import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import androidx.test.rule.ActivityTestRule;

@RunWith(AndroidJUnit4ClassRunner.class)
@LargeTest
public class SystemTest1 {
    private static final int SIMULATED_DELAY_MS = 500;


    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp(){
        Intents.init();
    }


    @Test
    public void signUpSuccess(){
        //generate random username
        String testUser = "testUser" + (int)(Math.random() * 1000);
        String email = testUser + "@gmail.com";
        String password = "password";



        //input info into sign up page, then see if activity changes to login page or if an error message pops up
        onView(ViewMatchers.withId(R.id.signupbut1)).perform(click());
        onView(ViewMatchers.withId(R.id.usernameInp)).perform(typeText(testUser), closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.emailInp)).perform(typeText(email), closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.passInp)).perform(typeText(password), closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.passConfirmInp)).perform(typeText(password), closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.loginbut2)).perform(click());

        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }

        intended(hasComponent(LoginActivity.class.getName()));

    }


}
