package com.example.emailaggregatorapp2;


import org.junit.After;
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
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.rule.ActivityTestRule;

@RunWith(AndroidJUnit4ClassRunner.class)
@LargeTest
public class SystemTest1 {
    private static final int SIMULATED_DELAY_MS = 500;

    String testUser = "testUser" + (int)(Math.random() * 100000);
    String email = testUser + "@gmail.com";
    String password = "password";

    String adminUser = "Admin1";
    String adminPass = "AdminPassword123";

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);


    @Before
    public void setUp(){
        Intents.init();
//        activityRule.launchActivity(null);
    }


    @After
    public void tearDown(){
        Intents.release();
    }
    @Test
    public void signUpSuccessandLogin(){
        //generate random username


        //input info into sign up page, then see if activity changes to login page or if an error message pops up
        onView(withId(R.id.signupbut1)).perform(click());
        onView(withId(R.id.usernameInp)).perform(typeText(testUser), closeSoftKeyboard());
        onView(withId(R.id.emailInp)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.passInp)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.passConfirmInp)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.loginbut2)).perform(click());

        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }

//        intended(hasComponent(LoginActivity.class.getName()));


        onView(withId(R.id.loginusernameedittext)).perform(typeText(testUser), closeSoftKeyboard());
        onView(withId(R.id.loginpasswordedittext)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.loginbut2)).perform(click());

        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }

        intended(hasComponent(MessagesActivity.class.getName()));


    }

    @Test
    public void signUpFailPassMisMatch() {
        onView(withId(R.id.signupbut1)).perform(click());
        onView(withId(R.id.usernameInp)).perform(typeText(testUser), closeSoftKeyboard());
        onView(withId(R.id.emailInp)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.passInp)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.passConfirmInp)).perform(typeText(password+"ABC"), closeSoftKeyboard());
        onView(withId(R.id.loginbut2)).perform(click());

        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }

        onView(withText("Passwords do not match")).check(matches(isDisplayed()));
        onView(withId(android.R.id.button1)).perform(click());

    }

    @Test
    public void signUpFailUserExists() {
        onView(withId(R.id.signupbut1)).perform(click());
        onView(withId(R.id.usernameInp)).perform(typeText(testUser), closeSoftKeyboard());
        onView(withId(R.id.emailInp)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.passInp)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.passConfirmInp)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.loginbut2)).perform(click());

        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }

        onView(withId(R.id.backbutton)).perform(click());
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }
        onView(withId(R.id.signupbut1)).perform(click());
        onView(withId(R.id.usernameInp)).perform(typeText(testUser), closeSoftKeyboard());
        onView(withId(R.id.emailInp)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.passInp)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.passConfirmInp)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.loginbut2)).perform(click());

        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }

        onView(withText("Username or email already exists")).check(matches(isDisplayed()));
        onView(withId(android.R.id.button1)).perform(click());

    }

    @Test
    public void loginFailWrongPassword() {
        onView(withId(R.id.loginbut1)).perform(click());
        onView(withId(R.id.loginusernameedittext)).perform(typeText(testUser), closeSoftKeyboard());
        onView(withId(R.id.loginpasswordedittext)).perform(typeText(password+"ABC"), closeSoftKeyboard());
        onView(withId(R.id.loginbut2)).perform(click());

        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }

        onView(withText("Incorrect username or password")).check(matches(isDisplayed()));
        onView(withId(android.R.id.button1)).perform(click());

    }

    @Test
    public void adminCreateUser(){
        onView(withId(R.id.loginbut1)).perform(click());
        onView(withId(R.id.loginusernameedittext)).perform(typeText(adminUser), closeSoftKeyboard());
        onView(withId(R.id.loginpasswordedittext)).perform(typeText(adminPass), closeSoftKeyboard());
        onView(withId(R.id.loginbut2)).perform(click());

        try {
            Thread.sleep(SIMULATED_DELAY_MS);
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }

        onView(withId(R.id.navBarToggle)).perform(click());
        onView(withText("Admin")).perform(click());
        onView(withText("Create User")).perform(click());
        onView(withId(R.id.editTextUsername)).perform(typeText("AdminMade"+testUser), closeSoftKeyboard());
        onView(withId(R.id.editTextEmail)).perform(typeText("AdminMade"+email), closeSoftKeyboard());
        onView(withId(R.id.editTextPassword)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.btnSubmit)).perform(click());

        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }

        onView(withText("User successfully created")).check(matches(isDisplayed()));
        onView(withId(android.R.id.button1)).perform(click());
    }

    @Test
    public void adminCreateUserAlreadyExists(){
        adminCreateUser();
        onView(withId(R.id.btnBack)).perform(click());

        onView(withText("Create User")).perform(click());
        onView(withId(R.id.editTextUsername)).perform(typeText("AdminMade"+testUser), closeSoftKeyboard());
        onView(withId(R.id.editTextEmail)).perform(typeText("AdminMade"+email), closeSoftKeyboard());
        onView(withId(R.id.editTextPassword)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.btnSubmit)).perform(click());

        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }

        onView(withText("User already exists")).check(matches(isDisplayed()));
        onView(withId(android.R.id.button1)).perform(click());

    }

}
