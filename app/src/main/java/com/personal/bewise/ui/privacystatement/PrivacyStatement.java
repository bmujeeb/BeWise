package com.personal.bewise.ui.privacystatement;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.personal.bewise.R;

public class PrivacyStatement extends Fragment {

    public TextView mPrivacyStatement;

    public PrivacyStatement() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.privacy_statement, container, false);

        mPrivacyStatement = (TextView) view.findViewById(R.id.privacy_statement);
        mPrivacyStatement.setText(Html.fromHtml(getResources().getString(R.string.privacy_statement_string)));
        return view;
    }

}
