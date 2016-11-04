package com.personal.bewise.ui.filedialog;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.personal.bewise.R;
import com.personal.bewise.database.TransactionsDatabase;
import com.personal.bewise.ui.CustomListFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class FileBrowserFragment extends CustomListFragment {

    private List<String> directoryEntries = new ArrayList<String>();

    private List<String> directoryType = new ArrayList<String>();

    private File currentDirectory = new File("/");

    private String SELECTED_FILE = null;

    private String dialogTitle;

    private String databaseName;

    private TextView fileDialogTitle;

    private TextView dbFileName;

    private Button acceptButton;

    private Button cancelButton;

    public FileBrowserFragment(String dialogTitle, String databaseName) {
        this.dialogTitle = dialogTitle;
        this.databaseName = databaseName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.filebrowser, container, false);

        fileDialogTitle = (TextView) view.findViewById(R.id.fragment_title);
        fileDialogTitle.setText(dialogTitle);

        dbFileName = (TextView) view.findViewById(R.id.file_name);
        dbFileName.setText(databaseName);

        acceptButton = (Button) view.findViewById(R.id.accept_button);
        acceptButton.setText(dialogTitle);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Save or restore DB and go back to settings dialog.

            }
        });

        cancelButton = (Button) view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }

    @Override
    public void setCheckBoxSelections(int itemId, boolean checkedState) {
        // TODO Auto-generated method stub

    }

    private void browseToRoot() {
        browseTo(new File("/sdcard/"));
    }

    private void upOneLevel() {
        if (this.currentDirectory.getParent() != null)
            this.browseTo(this.currentDirectory.getParentFile());
    }

    private void browseTo(final File aDirectory) {
        if (aDirectory.isDirectory()) {
            this.currentDirectory = aDirectory;
            fill(aDirectory.listFiles());
        } else {
            openFile(aDirectory);
        }
    }

    private void openFile(File aFile) {
        // SELECTED_FILE = aFile.toString();
        // dataBase = new DrawDatabase(this);
        // dataBase.addImage(SELECTED_FILE);
        // Intent i = new Intent(Intent.ACTION_DEFAULT);
        // i.setClass(FileBrowser.this, ImageManipulator.class);
        // i.putExtra(FileBrowser.IMAGE_FILE_NAME, SELECTED_FILE);
        // startActivity(i);
    }

    private void fill(File[] files) {
        this.directoryEntries.clear();
        this.directoryType.clear();
        // Add the "Refresh" == "current directory"
        this.directoryEntries.add(0, "Refresh");
        this.directoryType.add(0, "Refresh");
        // and the "Up" == 'Up one level'
        if (this.currentDirectory.getParent() != null) {
            this.directoryEntries.add(1, "Up");
            this.directoryType.add(1, "Up");
        }
        // if (files != null) {
        // for (File currentFile : files) {
        // String fileName = currentFile.getName();
        // if (checkEndsWithInStringArray(fileName, getResources().getStringArray(R.array.Datafile))) {
        // this.directoryEntries.add(fileName);
        // this.directoryType.add("Image");
        // } else if (currentFile.isDirectory()) {
        // this.directoryEntries.add(fileName);
        // this.directoryType.add("Directory");
        // }
        // }
        // }
        // Context context = getApplicationContext();
        // _fileBrowserAdapter = new FileBrowser_out(context, R.layout.filebrowser_out, directoryEntries.toArray(new String[directoryEntries.size()]),
        // directoryType.toArray(new String[directoryType.size()]));
        // setListAdapter(_fileBrowserAdapter);
    }

    protected void onSelect(int position) {
        String selectedFileString = this.directoryEntries.get(position);
        if (selectedFileString.equals("Refresh")) {
            this.browseTo(this.currentDirectory);
        } else if (selectedFileString.equals("Up")) {
            this.upOneLevel();
        } else {
            File clickedFile = new File(this.currentDirectory.getAbsolutePath() + "/" + this.directoryEntries.get(position));
            this.browseTo(clickedFile);
        }
    }

    /**
     * Checks whether checkItsEnd ends with one of the Strings from fileEndings
     */
    private boolean checkEndsWithInStringArray(String checkItsEnd, String[] fileEndings) {
        for (String aEnd : fileEndings) {
            if (checkItsEnd.endsWith(aEnd))
                return true;
        }
        return false;
    }

    /**
     * Import Database.
     *
     * @param sourcePath Import DB location.
     */
    private void importDB(String sourcePath) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            if (sd.canWrite()) {
                String currentDBPath = TransactionsDatabase.APPLICATION_DB_LOCATION + "//" + TransactionsDatabase.APPLICATION_DB_NAME;
                File backupDB = new File(data, currentDBPath);
                File currentDB = new File(sd, sourcePath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(getActivity(), "Import Successful!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Import Failed!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @param destination Database save location.
     */
    private void exportDB(String destination) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = TransactionsDatabase.APPLICATION_DB_LOCATION + "//" + TransactionsDatabase.APPLICATION_DB_NAME;
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, destination);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(getActivity(), "Backup Successful!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Backup Failed!", Toast.LENGTH_SHORT).show();
        }
    }
}
