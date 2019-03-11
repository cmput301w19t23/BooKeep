package com.example.bookeep;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;

import com.example.bookeep.Fragments.ShelfFragment;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class NavDrawerTest extends ActivityTestRule<MainActivity> {

    private Solo solo;
    public NavDrawerTest() {
        super(MainActivity.class, true, true);
    }


    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void openDrawer(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnImageButton(0);
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }
    @Test
    public void navToShelf(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnImageButton(0);
        solo.clickOnActionBarItem(0);
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }
    @Test
    public void navToStand(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnImageButton(0);
        solo.clickOnActionBarItem(1);
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
