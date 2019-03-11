package com.example.bookeep;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.widget.EditText;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class SignUpActivityTest extends ActivityTestRule<SignUpActivity> {
    private Solo solo;
    public SignUpActivityTest() {
        super(SignUpActivity.class, true, true);
    }

    @Rule
    public ActivityTestRule<SignUpActivity> rule =
            new ActivityTestRule<>(SignUpActivity.class, true, true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    @Test
    public void blankTest() {
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
        solo.clickOnButton("Create User");
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
    }
    @Test
    public void currentUser(){
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
        solo.enterText((EditText) solo.getView(R.id.user_name), "owner");
        solo.enterText((EditText) solo.getView(R.id.first_name), "owner");
        solo.enterText((EditText) solo.getView(R.id.last_name), "owner");
        solo.enterText((EditText) solo.getView(R.id.signup_email), "ownertest@mail.com");
        solo.enterText((EditText) solo.getView(R.id.signup_password), "123456");
        solo.enterText((EditText) solo.getView(R.id.phone), "1234567890");
        solo.clickOnButton("Create User");
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
    }
    @Test
    public void nonUniqueUserName(){
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
        solo.enterText((EditText) solo.getView(R.id.user_name), "owner");
        solo.enterText((EditText) solo.getView(R.id.first_name), "owner");
        solo.enterText((EditText) solo.getView(R.id.last_name), "owner");
        solo.enterText((EditText) solo.getView(R.id.signup_email), Integer.toString((int) Math.floor(Math.random() * 1000)) + "@mail.com");
        solo.enterText((EditText) solo.getView(R.id.signup_password), "123456");
        solo.enterText((EditText) solo.getView(R.id.phone), "1234567890");
        solo.clickOnButton("Create User");
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
    }
    @Test
    public void nonUniqueEmail(){
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
        solo.enterText((EditText) solo.getView(R.id.user_name), Integer.toString((int) Math.floor(Math.random() * 1000)) + "1234");
        solo.enterText((EditText) solo.getView(R.id.first_name), "owner");
        solo.enterText((EditText) solo.getView(R.id.last_name), "owner");
        solo.enterText((EditText) solo.getView(R.id.signup_email), "ownertest@mail.com");
        solo.enterText((EditText) solo.getView(R.id.signup_password), "123456");
        solo.enterText((EditText) solo.getView(R.id.phone), "1234567890");
        solo.clickOnButton("Create User");
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
    }
    @Test
    public void validSignup(){
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
        solo.enterText((EditText) solo.getView(R.id.user_name), Integer.toString((int) Math.floor(Math.random() * 1000)) + "1234");
        solo.enterText((EditText) solo.getView(R.id.first_name), "owner");
        solo.enterText((EditText) solo.getView(R.id.last_name), "owner");
        solo.enterText((EditText) solo.getView(R.id.signup_email), Integer.toString((int) Math.floor(Math.random() * 1000)) + "@mail.com");
        solo.enterText((EditText) solo.getView(R.id.signup_password), "123456");
        solo.enterText((EditText) solo.getView(R.id.phone), "1234567890");
        solo.clickOnButton("Create User");
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
    }
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
