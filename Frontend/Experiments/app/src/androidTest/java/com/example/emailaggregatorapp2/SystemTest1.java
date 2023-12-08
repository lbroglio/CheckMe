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
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
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

        signupAndLogin();

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
        navToAdmin();
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

    @Test
    public void adminCreateUserEmptyUsername(){
        navToAdmin();
        onView(withText("Create User")).perform(click());
        onView(withId(R.id.editTextEmail)).perform(typeText("AdminMade"+email), closeSoftKeyboard());
        onView(withId(R.id.editTextPassword)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.btnSubmit)).perform(click());

        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }

        //Check if username input has error message
        onView(withId(R.id.editTextUsername)).check(matches(hasErrorText("Username is required")));
    }

    @Test
    public void adminCreateUserEmptyEmail(){
        navToAdmin();
        onView(withText("Create User")).perform(click());
        onView(withId(R.id.editTextUsername)).perform(typeText("AdminMade"+testUser), closeSoftKeyboard());
        onView(withId(R.id.editTextPassword)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.btnSubmit)).perform(click());

        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }

        //Check if email input has error message
        onView(withId(R.id.editTextEmail)).check(matches(hasErrorText("Email is required")));
    }

    @Test
    public void adminCreateUserEmptyPassword(){
        navToAdmin();
        onView(withText("Create User")).perform(click());
        onView(withId(R.id.editTextUsername)).perform(typeText("AdminMade"+testUser), closeSoftKeyboard());
        onView(withId(R.id.editTextEmail)).perform(typeText("AdminMade"+email), closeSoftKeyboard());
        onView(withId(R.id.btnSubmit)).perform(click());

        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }

        //Check if password input has error message
        onView(withId(R.id.editTextPassword)).check(matches(hasErrorText("Password is required")));
    }

    @Test
    public void adminCreateUserEmptyAll(){
        navToAdmin();
        onView(withText("Create User")).perform(click());
        onView(withId(R.id.btnSubmit)).perform(click());

        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }

        //Check if username input has error message
        onView(withId(R.id.editTextUsername)).check(matches(hasErrorText("Username is required")));

        //Check if email input has error message
        onView(withId(R.id.editTextEmail)).check(matches(hasErrorText("Email is required")));

        //Check if password input has error message
        onView(withId(R.id.editTextPassword)).check(matches(hasErrorText("Password is required")));
    }

    @Test
    public void createGroup(){
        String groupName = "TestGroup" + (int)(Math.random() * 100000);
        signupAndLogin();
        onView(withId(R.id.navBarToggle)).perform(click());
        onView(withText("Groups")).perform(click());
        onView(withText("New Group")).perform(click());
        onView(withId(R.id.createGroupTB)).perform(replaceText(groupName), closeSoftKeyboard());
        onView(withId(R.id.createGroupButton)).perform(click());
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }
        onView(withText("Back")).perform(click());
        //check if a new button with the name of the group exists
        onView(withText(groupName)).check(matches(isDisplayed()));
    }


    private void signupAndLogin(){
        onView(withId(R.id.signupbut1)).perform(click());
        onView(withId(R.id.emailInp)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.usernameInp)).perform(typeText(testUser), closeSoftKeyboard());
        onView(withId(R.id.passInp)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.passConfirmInp)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.loginbut2)).perform(click());

        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }

        onView(withId(R.id.loginusernameedittext)).perform(typeText(testUser), closeSoftKeyboard());
        onView(withId(R.id.loginpasswordedittext)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.loginbut2)).perform(click());

        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }


    }
    private void navToAdmin(){
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
    }

}
