package com.example.bookeep;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.EditText;

import com.google.android.gms.common.SignInButton;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest extends ActivityTestRule<LoginActivity> {
    private Solo solo;

    public LoginActivityTest(){
        super (LoginActivity.class, true, true);
    }
    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class, true, true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    @Test
    public void checkBlank(){
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);

        solo.clickOnButton("Sign In");
    }

    @Test
    public void checkEmailOnly(){
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
        solo.enterText((EditText) solo.getView(R.id.edit_email), "kyletest@gmail.com");
        solo.clickOnButton("Sign In");
    }

    @Test
    public void checkPasswordOnly(){
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
        solo.enterText((EditText) solo.getView(R.id.edit_password), "123456");
        solo.clickOnButton("Sign In");
    }
    @Test
    public void validLogin(){
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
        solo.enterText((EditText) solo.getView(R.id.edit_email), "kyletest@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.edit_password), "123456");
        solo.clickOnButton("Sign In");
        solo.assertCurrentActivity("Success", MainActivity.class);
    }
    @Test
    public void signup() {
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
        solo.clickOnButton("Sign Up");
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

}
