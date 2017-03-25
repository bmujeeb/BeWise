package com.personal.bewise;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.personal.bewise.ui.budget.BudgetFragment;
import com.personal.bewise.ui.debug.DebugFragment;
import com.personal.bewise.ui.overview.OverviewFragment;
import com.personal.bewise.ui.pending.PendingTransactionsFragment;
import com.personal.bewise.ui.privacystatement.PrivacyStatement;
import com.personal.bewise.ui.recurrings.RecurringTransactionsFragment;
import com.personal.bewise.ui.settings.SettingsDialog;
import com.personal.bewise.ui.transactions.TransactionsFragment;

/**
 * @author bilal
 */
public class MainActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(this.getClass().toString(), "onCreate:...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    // onNavigationDrawerItemSelected is a method of
    // NavigationDrawerFragment.NavigationDrawerCallbacks and must be
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Log.d(this.getClass().toString(), "onNavigationDrawerItemSelected:...");
        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = new OverviewFragment();
                break;
            case 1:
                fragment = new TransactionsFragment();
                break;
            case 2:
                fragment = new PendingTransactionsFragment();
                break;
            case 3:
                fragment = new RecurringTransactionsFragment();
                break;
            case 4:
                fragment = new BudgetFragment();
                break;
            case 5:
                fragment = new PrivacyStatement();
                break;
            case 6:
                this.finish();
                break;
            case 7:
                fragment = new DebugFragment();
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
        }

    }

    public void onSectionAttached(int number) {
        Log.d(this.getClass().toString(), "onSectionAttached:...");
        switch (number) {
            case 1:
                mTitle = getString(R.string.label_overview);
                break;
            case 2:
                mTitle = getString(R.string.label_transactions);
                break;
            case 3:
                mTitle = getString(R.string.label_outstanding_transactions);
                break;
            case 4:
                mTitle = getString(R.string.label_recurring_transactions);
                break;
            case 5:
                mTitle = getString(R.string.label_budgets);
                break;
            case 6:
                mTitle = getString(R.string.label_privacy_statement);
                break;
            case 7:
                mTitle = getString(R.string.quit);
                break;
            case 8:
                mTitle = getString(R.string.debug);
                break;
        }
    }

    public void restoreActionBar() {
        Log.d(this.getClass().toString(), "restoreActionBar:...");
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(this.getClass().toString(), "onCreateOptionsMenu:...");
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(this.getClass().toString(), "onOptionsItemSelected:...");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            FragmentManager fm = getFragmentManager();
            SettingsDialog settingsDialog = new SettingsDialog();
            settingsDialog.show(fm, "settings_dialog");
            return true;
        } else if (id == R.id.action_help) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Log.d(this.getClass().toString(), "onBackPressed:...");
        FragmentManager fm = getFragmentManager();
        fm.popBackStack();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(this.getClass().toString(), "onConfigurationChanged:...");
        super.onConfigurationChanged(newConfig);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
            Log.d(this.getClass().toString(), "PlaceholderFragment:...");
        }

        /**
         * Returns a new instance of this fragment for the given section number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Log.d(this.getClass().toString(), "onCreateView:...");
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            Log.d(this.getClass().toString(), "onAttach:...");
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
