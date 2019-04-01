package com.example.bookeep;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class SearchActivityTest extends ActivityTestRule<SearchActivity>{
    private Solo solo;

    public SearchActivityTest() {
        super(SearchActivity.class, true, true);
    }

    @Rule
    public ActivityTestRule<SearchActivity> rule
            = new ActivityTestRule(SearchActivity.class, true, true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    @Test
    public void clickBook() {
        solo.assertCurrentActivity("Wrong Activity", SearchActivity.class);
        solo.clickOnImage(0);
        solo.sleep(1000);
        solo.assertCurrentActivity("Wrong Activity", BookDetailsActivity.class);
    }

    @Test
    public void searchBook1() {
        solo.assertCurrentActivity("Wrong Activity", SearchActivity.class);
        solo.clickOnView(solo.getView(R.id.search_action));
        solo.typeText(0, "Harry");
        solo.sendKey(Solo.ENTER);
        solo.clickOnImage(0);
        solo.sleep(1000);
        solo.assertCurrentActivity("Wrong Activity", BookDetailsActivity.class);
        assertTrue(solo.waitForText("Harry Potter"));
    }
    @Test
    public void searchBook2() {
        solo.assertCurrentActivity("Wrong Activity", SearchActivity.class);
        solo.clickOnView(solo.getView(R.id.search_action));
        solo.typeText(0, "Lord");
        solo.clickOnImage(0);
        solo.sleep(1000);
        solo.assertCurrentActivity("Wrong Activity", BookDetailsActivity.class);
        assertTrue(solo.waitForText("Lord"));
    }

    @Test
    public void searchInvalidBook() {
        solo.assertCurrentActivity("Wrong Activity", SearchActivity.class);
        solo.clickOnView(solo.getView(R.id.search_action));
        solo.typeText(0, "zxyzxy");
        solo.clickOnImage(0);
        solo.assertCurrentActivity("Wrong Activity", SearchActivity.class);
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }


}
