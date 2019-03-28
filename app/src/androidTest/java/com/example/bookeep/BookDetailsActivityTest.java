package com.example.bookeep;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class BookDetailsActivityTest extends ActivityTestRule<LoginActivity>{

    private Solo solo;

    public BookDetailsActivityTest() {
        super(LoginActivity.class, true, true);
    }

    @Rule
    public ActivityTestRule<LoginActivity> rule
            = new ActivityTestRule(LoginActivity.class, true, true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    @Test
    public void testOwned() {
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
        solo.enterText(0, "test72@gmail.com");
        solo.enterText(1, "123456");
        solo.clickOnButton("Sign In");
        solo.sleep(1000);
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnImage(2);
        solo.sleep(1000);
        solo.assertCurrentActivity("Wrong Activity", BookDetailsActivity.class);
        solo.clickOnView(solo.getView(R.id.fab));
        solo.sleep(1000);
        solo.assertCurrentActivity("Wrong Activity", AddEditBookActivity.class);
    }

    @Test
    public void testSearched() {
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
        solo.enterText(0, "test73@gmail.com");
        solo.enterText(1, "123456");
        solo.clickOnButton("Sign In");
        solo.sleep(1000);
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.search_button));
        solo.clickOnImage(2);
        solo.sleep(1000);
        solo.assertCurrentActivity("Wrong Activity", BookDetailsActivity.class);
        solo.clickOnView(solo.getView(R.id.fab));
        solo.waitForText("REQUESTED");
    }
}
