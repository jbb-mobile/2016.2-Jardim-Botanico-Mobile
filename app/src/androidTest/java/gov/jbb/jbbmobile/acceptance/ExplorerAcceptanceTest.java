package gov.jbb.jbbmobile.acceptance;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import gov.jbb.jbbmobile.R;
import gov.jbb.jbbmobile.dao.ExplorerDAO;
import gov.jbb.jbbmobile.model.Explorer;
import gov.jbb.jbbmobile.view.LoginScreenActivity;
import gov.jbb.jbbmobile.view.PreferenceScreenActivity;
import gov.jbb.jbbmobile.view.RegisterScreenActivity;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@LargeTest
public class ExplorerAcceptanceTest{
    @Rule
    public final ActivityTestRule<RegisterScreenActivity> register = new ActivityTestRule<>(RegisterScreenActivity.class);


    @Test
    public void testRegisterUser(){
        onView(ViewMatchers.withId(R.id.nicknameEditText))
                .perform(typeText("testuser"));
        onView(withId(R.id.passwordEditText))
                .perform(typeText("senha1234"));
        onView(withId(R.id.passwordConfirmEditText))
                .perform(typeText("senha1234"))
                .perform(closeSoftKeyboard());
        onView(withId(R.id.emailEditText))
                .perform(typeText("testuser@gmail.com"))
                .perform(closeSoftKeyboard());
        onView(withId(R.id.registerButton))
                .perform(click());
    }

    @RunWith(AndroidJUnit4.class)

    @LargeTest
    public class PreferenceAcceptanceTest{
        @Rule public final ActivityTestRule<PreferenceScreenActivity> login = new ActivityTestRule<>(PreferenceScreenActivity.class);

        @Test
        public void testLogUserOut(){
            onView(withId(R.id.signOutButton))
                    .perform(click());
        }
    }

    @LargeTest
    public static class LogInAcceptanceTest{
        @Rule public final ActivityTestRule<LoginScreenActivity> login = new ActivityTestRule<>(LoginScreenActivity.class);

        @Test
        public void logUser() throws Exception{
            onView(withId(R.id.emailEditText))
                    .perform(typeText("testuser@gmail.com"));
            onView(withId(R.id.passwordEditText))
                    .perform(typeText("senha1234"))
                    .perform(closeSoftKeyboard());
            onView(withId(R.id.loginButton))
                    .perform(click());

            new ExplorerDAO(login.getActivity().getApplicationContext())
                .deleteExplorer(new Explorer("testUser", "testuser@gmail.com", "senha1234", "senha1234"));
        }

    }
}
