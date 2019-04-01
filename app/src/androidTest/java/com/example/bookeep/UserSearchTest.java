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
public class UserSearchTest extends ActivityTestRule<SearchUserPopupActivity>{
    private Solo solo;

    public UserSearchTest() {
        super(SearchUserPopupActivity.class, true, true);
    }

    @Rule
    public ActivityTestRule<SearchActivity> rule
            = new ActivityTestRule(SearchUserPopupActivity.class, true, true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    @Test
    public void search() {
        solo.assertCurrentActivity("Wrong Activity", SearchUserPopupActivity.class);
        solo.typeText(0, "kyletest");
        solo.clickOnView(solo.getView(R.id.SearchButton));
        solo.sleep(5000);
        solo.assertCurrentActivity("Wrong Activity", UserProfileActivity.class);
    }

    @Test
    public void searchInvalidBook() {
        solo.assertCurrentActivity("Wrong Activity", SearchUserPopupActivity.class);
        solo.typeText(0, "zddfaf");
        solo.clickOnView(solo.getView(R.id.SearchButton));
        solo.assertCurrentActivity("Wrong Activity", SearchUserPopupActivity.class);
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }


}
