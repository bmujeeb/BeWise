package com.personal.bewise.ui.settings;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.personal.bewise.R;
import com.personal.bewise.ui.filedialog.FileBrowserFragment;

public class SettingsDialog extends DialogFragment {

    private Spinner currenciesSpinner;

    private Button databaseBackupButton;

    private Button databaseRestoreButton;

    private Button setPasswordButton;

    private Button clearPasswordButton;

    private Button okButton;

    private Button cancelButton;

    private ArrayAdapter<String> _adapter;

    public SettingsDialog() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.settings, container);
        getDialog().setTitle(R.string.label_settings);

        createCurrenciesSpinnerLayout(view);

        createDatabaseOptions(view);

        createPasswordOptions(view);

        createButtons(view);

        return view;
    }

    /**
     * @param view View
     */
    private void createCurrenciesSpinnerLayout(final View view) {
        currenciesSpinner = (Spinner) view.findViewById(R.id.currencies);
        String[] category = getResources().getStringArray(R.array.currencies);
        _adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, category);
        currenciesSpinner.setAdapter(_adapter);
    }

    /**
     * @param view View
     */
    private void createDatabaseOptions(final View view) {
        databaseBackupButton = (Button) view.findViewById(R.id.db_backup_button);
        databaseBackupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                FragmentManager fm = getFragmentManager();
                FileBrowserFragment fileBrowserFragment = new FileBrowserFragment("Backup Database", "xxx");
                fm.beginTransaction().replace(R.id.container, fileBrowserFragment).commit();
                getDialog().dismiss();
            }
        });

        databaseRestoreButton = (Button) view.findViewById(R.id.db_restore_button);
        databaseRestoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // TODO Auto-generated method stub

            }
        });
    }

    /**
     * @param view View
     */
    private void createPasswordOptions(final View view) {
        setPasswordButton = (Button) view.findViewById(R.id.set_password_button);
        setPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // TODO Auto-generated method stub

            }
        });

        clearPasswordButton = (Button) view.findViewById(R.id.clear_password_button);
        clearPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // TODO Auto-generated method stub

            }
        });

    }

    /**
     * @param view
     */
    private void createButtons(final View view) {

        okButton = (Button) view.findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

            }
        });

        cancelButton = (Button) view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                getDialog().dismiss();
            }
        });

    }

}
