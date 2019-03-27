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

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class SetLocationActivityTest extends ActivityTestRule<SetLocationActivity> {
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
        Activity activity = rule.getActivity();
    }

    @Test
    public void test1() {
        solo.assertCurrentActivity("Wrong Activity", SetLocationActivity.class);
    }
}