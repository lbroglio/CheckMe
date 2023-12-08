package com.example.emailaggregatorapp2;


import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;

import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.slowSwipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

import android.util.Log;

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

    String groupName = "TestGroup" + (int)(Math.random() * 100000);

    String proxyUser = "BaseballBob";
    String proxyPass = "CubsGo123";

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

    @Test
    public void replyMessage(){
        proxyLogin();
//        onView(allOf(withTagValue(is((Object) "Reply0")), isDisplayed())).perform(click());
        onView(first(withText("REPLY"))).perform(click());
        onView(withId(R.id.replyContent)).perform(typeText("Test Reply"), closeSoftKeyboard());
        onView(withId(R.id.replySend)).perform(click());
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }
        onView(withText("Reply Sent")).check(matches(isDisplayed()));
        onView(withId(android.R.id.button1)).perform(click());
    }

    @Test
    public void addProxyCrews(){
        signupAndLogin();
        onView(withId(R.id.navBarToggle)).perform(click());
        onView(withText("Add Account")).perform(click());
        onView(withText("Crews")).perform(click());
        onView(withText("Target URL")).perform(replaceText("http://coms-309-047.class.las.iastate.edu:8443/crews/messages"), closeSoftKeyboard());
        onView(withText("Username")).perform(replaceText("DEMO3-USER"), closeSoftKeyboard());
        onView(withText("Password")).perform(replaceText("DEMO3"), closeSoftKeyboard());
        onView(withText("Add Account")).perform(click());
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }
        onView(withText("Account added")).check(matches(isDisplayed()));
        onView(withId(android.R.id.button1)).perform(click());
    }

    @Test
    public void addProxyChaos(){
        signupAndLogin();
        onView(withId(R.id.navBarToggle)).perform(click());
        onView(withText("Add Account")).perform(click());
        onView(withText("Chaos")).perform(click());
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }
        onView(withText("Target URL")).perform(replaceText("http://coms-309-047.class.las.iastate.edu:8443/crews/messages/BaseballBob"), closeSoftKeyboard());
        onView(withText("API Token")).perform(replaceText("6583000229007365"), closeSoftKeyboard());
        onView(withText("Add Account")).perform(click());
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }
        onView(withText("Account added")).check(matches(isDisplayed()));
        onView(withId(android.R.id.button1)).perform(click());
    }

    @Test
    public void addProxyCmail(){
        signupAndLogin();
        onView(withId(R.id.navBarToggle)).perform(click());
        onView(withText("Add Account")).perform(click());
        onView(withText("Cmail")).perform(click());
        onView(withText("Target URL")).perform(replaceText("http://coms-309-047.class.las.iastate.edu:8443/cmail/messages"), closeSoftKeyboard());
        onView(withText("Username")).perform(replaceText("BaseballBob"), closeSoftKeyboard());
        onView(withText("Password")).perform(replaceText("CubsGo123"), closeSoftKeyboard());
        onView(withText("Add Account")).perform(click());
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }
        onView(withText("Account added")).check(matches(isDisplayed()));
        onView(withId(android.R.id.button1)).perform(click());
    }


//    @Test
    public void liveChatSendMessage(){
        createGroup();
        onView(withText(groupName)).perform(click());
        onView(withId(R.id.groupChatButton)).perform(click());
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }
        onView(withId(R.id.messageInput)).perform(typeText("Test Message"), closeSoftKeyboard());
        onView(withId(R.id.sendButton)).perform(click());

        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }
//        onView(isRoot()).perform(swipeUp());
//        onView(withId(R.id.chatMessageListView)).perform(swipeDown());
        onView(withId(R.id.messageInput)).perform(typeText("Test Message"), closeSoftKeyboard());
        onView(withId(R.id.sendButton)).perform(click());
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }
        onView(withId(R.id.chatMessageListView)).check(matches(withText(testUser+": Test Message")));
    }

    public void proxyLogin(){
        onView(withId(R.id.loginbut1)).perform(click());
        onView(withId(R.id.loginusernameedittext)).perform(typeText(proxyUser), closeSoftKeyboard());
        onView(withId(R.id.loginpasswordedittext)).perform(typeText(proxyPass), closeSoftKeyboard());
        onView(withId(R.id.loginbut2)).perform(click());

        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }


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

    private <T> Matcher<T> first(final Matcher<T> matcher) {
        return new BaseMatcher<T>() {
            boolean isFirst = true;

            @Override
            public boolean matches(final Object item) {
                if (isFirst && matcher.matches(item)) {
                    isFirst = false;
                    return true;
                }

                return false;
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("should return first matching item");
            }
        };
    }

}
