package com.example.bookeep;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;



/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
//Ensure a book has been accepted!
@RunWith(AndroidJUnit4.class)
public class SetLocationActivityTest extends ActivityTestRule<SetLocationActivity> {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    private Solo solo;

    public  SetLocationActivityTest() {
        super (SetLocationActivity.class, true, true);
    }

    @Rule
    public ActivityTestRule<SetLocationActivity> rule =
            new ActivityTestRule<>(SetLocationActivity.class, true, true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void start() throws Exception {
        rule.getActivity();
    }

    @Test
    public void test1() {
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
        solo.sleep(1000);
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnImage(2);
        solo.sleep(1000);
        solo.assertCurrentActivity("Wrong Activity", BookDetailsActivity.class);
        solo.clickOnView(solo.getView(R.id.fab));
        solo.sleep(1000);
        solo.assertCurrentActivity("Wrong Activity", AddEditBookActivity.class);
        solo.sleep(70000);
        solo.clickOnScreen(300,300);
        solo.clickOnButton("Save");
        solo.waitForFragmentByTag("datePicker");
        solo.clickOnButton("Set");
        solo.waitForFragmentByTag("timePicker");
    }
}