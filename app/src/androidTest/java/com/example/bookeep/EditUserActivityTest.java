package com.example.bookeep;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.EditText;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class EditUserActivityTest extends ActivityTestRule<EditUserActivity> {
    private Solo solo;
    public EditUserActivityTest() {
        super(EditUserActivity.class, true, true);
    }

    @Rule
    public ActivityTestRule<EditUserActivity> rule =
            new ActivityTestRule<>(EditUserActivity.class, true, true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }
    @Test
    public void currentUser() {
        solo.assertCurrentActivity("Wrong Activity",SignUpActivity.class);
        solo.enterText((EditText) solo.getView(R.id.user_name),"owner");
        solo.enterText((EditText) solo.getView(R.id.first_name),"owner");
        solo.enterText((EditText) solo.getView(R.id.last_name),"owner");
        solo.enterText((EditText) solo.getView(R.id.signup_email),"ownertest@mail.com");
        solo.enterText((EditText) solo.getView(R.id.signup_password),"123456");
        solo.enterText((EditText) solo.getView(R.id.phone),"1234567890");
    }
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
