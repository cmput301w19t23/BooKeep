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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class AddEditBookTest extends ActivityTestRule<AddEditBookActivity> {
    private Solo solo;

    public  AddEditBookTest() {
        super (AddEditBookActivity.class, true, true);
    }

    @Rule
    public ActivityTestRule<AddEditBookActivity> rule =
            new ActivityTestRule<>(AddEditBookActivity.class, true, true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    @Test
    public void checkBook1() {
        solo.assertCurrentActivity("Wrong Activity", AddEditBookActivity.class);

        solo.enterText ((EditText) solo.getView(R.id.editISBN), "059035342X");

        solo.clickOnButton("Save");

        solo.assertCurrentActivity("Wrong Activity", AddEditBookActivity.class);

        solo.clickOnText("Harry");

        solo.clickOnButton("Save");

        assertFalse(solo.getCurrentActivity().getLocalClassName() == "AddEditBookActivity");
    }

    @Test
    public void checkBook2() {
        solo.assertCurrentActivity("Wrong Activity", AddEditBookActivity.class);

        solo.enterText ((EditText) solo.getView(R.id.editISBN), "9780544003415");

        solo.clickOnButton("Save");

        solo.assertCurrentActivity("Wrong Activity", AddEditBookActivity.class);

        solo.clickOnText("9780544003415");

        solo.clickOnText("title");
    }

    @Test
    public void invlidEntry() {
        solo.clickOnButton("Save");

        solo.assertCurrentActivity("Wrong Activity", AddEditBookActivity.class);

        solo.clickOnText("title");

        solo.clickOnText("Author");

        solo.clickOnText("ISBN");
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

}

