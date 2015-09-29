package com.nolan.shoppinglist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    public static final String PREFS_NAME = "Prefs";

    ListView list;
    Button btnAdd;
    ArrayList<String> things, lists;
    EditText editAdd;
    Spinner spinner;
    String listName;
    ArrayAdapter<String> adapter, lAdapter;
    CheckBox cBoxFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (isFirstLaunch()) {
            showHelp();
        }

        things = new ArrayList<>();
        //load(things, "things.txt");
        lists = new ArrayList<>();
        load(lists, "lists.txt");
        if (lists.isEmpty()) {
            lists.add("none");
        }
        listName = lists.get(0);
        list = (ListView) findViewById(R.id.list);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        editAdd = (EditText) findViewById(R.id.editAdd);
        spinner = (Spinner) findViewById(R.id.spinner);
        cBoxFavorite = (CheckBox) findViewById(R.id.cBoxFavorite);
        cBoxFavorite.setChecked(true);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, things);
        list.setAdapter(adapter);

        lAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, android.R.id.text1, lists);
        spinner.setAdapter(lAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cBoxFavorite.setChecked(false);
                if (!spinner.getSelectedItem().equals("none")) {
                    listName = spinner.getItemAtPosition(position).toString();
                    load(things, listName + ".txt");
                    update(adapter);
                    if (listName.equals(lists.get(0))) {
                        cBoxFavorite.setChecked(true);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemValue = (String) list.getItemAtPosition(position);
                //Toast.makeText(getApplicationContext(), ItemValue, Toast.LENGTH_SHORT).show();
                showDelete(things, itemValue, position, adapter);
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String itemValue = (String) list.getItemAtPosition(position);
                edit(itemValue);
                return true;
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editAdd.getText().toString().isEmpty() && !lists.isEmpty()) {
                    add(things, editAdd.getText().toString(), adapter);
                    editAdd.setText(null);
                } else {
                    Toast.makeText(getApplicationContext(), "No Lists Available", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cBoxFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = cBoxFavorite.isChecked();
                if (isChecked)
                    setFavorite(listName);
                else
                    cBoxFavorite.setChecked(true);
            }
        });

    }

    public void add(ArrayList<String> array, String item, ArrayAdapter<String> adapter) {
        if (listName != null) {
            if (array.equals(lists) && array.contains("none"))
                array.remove("none");
            array.add(item);
            update(adapter);
            save(array, listName + ".txt");
        }
    }

    public void remove(ArrayList<String> array, int position, ArrayAdapter<String> adapter) {
        array.remove(position);
        update(adapter);
        save(array, listName + ".txt");
    }

    public void update(ArrayAdapter<String> adapter) {
        adapter.notifyDataSetChanged();
    }

    public void save(ArrayList<String> array, String file) {
        BufferedWriter bufferedWriter;
        try {
            FileOutputStream fileOutputStream = openFileOutput(file, Context.MODE_PRIVATE);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
            for (int i=0; i<array.size(); i++) {
                bufferedWriter.write(array.get(i) + "\r\n");
            }
            bufferedWriter.close();
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            //Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
        }
    }

    public void load(ArrayList<String> array, String file) {
        array.clear();
        BufferedReader bufferedReader;
        //stringBuilder result = new StringBuilder();
        try {
            FileInputStream fileInputStream = openFileInput(file);
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                array.add(line);
            }
            bufferedReader.close();
        }catch (FileNotFoundException e) {
            BufferedWriter bufferedWriter;
            try {
                FileOutputStream fileOutputStream = openFileOutput(file, Context.MODE_PRIVATE);
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
                bufferedWriter.write("");
                bufferedWriter.close();
            }catch (Exception ex) {
                ex.printStackTrace();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            //Toast.makeText(getApplicationContext(), "Loaded", Toast.LENGTH_SHORT).show();
        }
    }

    public void setFavorite(String name) {
        ArrayList<String> tmpArray = new ArrayList<>();
        tmpArray.add(name);
        for (int i=0; i<lists.size(); i++) {
            if (!lists.get(i).equals(name))
                tmpArray.add(lists.get(i));
        }
        lists.clear();
        for (int j=0; j<tmpArray.size(); j++) {
            lists.add(tmpArray.get(j));
        }
        save(lists, "lists.txt");
        update(lAdapter);
        spinner.setSelection(0);
    }

    public void edit(final String item) {
        AlertDialog.Builder editBuilder = new AlertDialog.Builder(this);
        editBuilder.setTitle("Edit");
        final EditText txtEdit = new EditText(this);
        txtEdit.setText(item);
        editBuilder.setView(txtEdit);
        editBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!txtEdit.getText().toString().isEmpty()) {
                    String newItem = txtEdit.getText().toString();
                    int position = things.indexOf(item);
                    things.set(position, newItem);
                    save(things, listName + ".txt");
                    update(adapter);
                }
            }
        });
        editBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog editDialog = editBuilder.create();
        editDialog.show();
    }

    public void addList() {
        AlertDialog.Builder addBuilder = new AlertDialog.Builder(this);
        addBuilder.setTitle("Add a list");
        final EditText txtNewList = new EditText(this);
        addBuilder.setView(txtNewList);
        addBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!txtNewList.getText().toString().isEmpty()) {
                    listName = "lists";
                    add(lists, txtNewList.getText().toString(), lAdapter);
                    spinner.setSelection(lists.indexOf(txtNewList.getText().toString()));
                }
            }
        });
        addBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog addDialog = addBuilder.create();
        addDialog.show();
    }

    public void removeList(final ArrayList<String> array, final String name) {
        if (listName != null) {
            int position = lists.indexOf(name);
            spinner.setSelection(position);
            AlertDialog.Builder delListBuilder = new AlertDialog.Builder(this);
            delListBuilder.setTitle("Remove a list");
            delListBuilder.setMessage("Remove the list \"" + listName + "\" ?");
            delListBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    listName = "lists";
                    remove(array, array.indexOf(name), lAdapter);
                    things.clear();
                    File file = new File(getFilesDir() + "/" + name + ".txt");
                    boolean deleted = file.delete();
                }
            });
            delListBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog delListDialog = delListBuilder.create();
            delListDialog.show();
        }else {
            Toast.makeText(getApplicationContext(), "No lists to remove.", Toast.LENGTH_SHORT).show();
        }
    }

    public void renameList(final String name) {
        if (listName != null) {
            final int position = lists.indexOf(name);
            AlertDialog.Builder renameBuilder = new AlertDialog.Builder(this);
            renameBuilder.setTitle("Rename " + name + "?");
            final EditText txtEdit = new EditText(this);
            renameBuilder.setView(txtEdit);
            renameBuilder.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!txtEdit.getText().toString().isEmpty()) {
                        String newName = txtEdit.getText().toString();
                        lists.set(position, newName);
                        save(lists, "lists.txt");
                        update(lAdapter);
                        save(things, newName + ".txt");
                        update(adapter);
                        File file = new File(getFilesDir() + "/" + name + ".txt");
                        boolean deleted = file.delete();
                    }
                }
            });
            renameBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog renameDialog = renameBuilder.create();
            renameDialog.show();
        }
    }

    public void showDelete(final ArrayList<String> array, String name, final int position, final ArrayAdapter<String> adapter) {
        AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(this);
        deleteBuilder.setTitle("Delete");
        deleteBuilder.setMessage("Delete item \"" + name + "\" ?");
        deleteBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Delete item
                remove(array, position, adapter);
            }
        });
        deleteBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Do nothing and close
            }
        });

        AlertDialog deleteDialog = deleteBuilder.create();
        deleteDialog.show();
    }

    public void showAbout() {
        AlertDialog.Builder aboutBuilder = new AlertDialog.Builder(this);
        aboutBuilder.setTitle("About");
        aboutBuilder.setMessage("Shopping Lists app\r\nVersion: " + getString(R.string.app_version) + "\n\rCreated by BeckerSoft");
        aboutBuilder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Nothing
            }
        });

        AlertDialog aboutDialog = aboutBuilder.create();
        aboutDialog.show();
    }

    public void showHelp() {
        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
        helpBuilder.setTitle("Help");
        helpBuilder.setMessage("Menu:\n" +
                "Add/Remove/Rename Lists.\n" +
                "View About & Help.\n\n" +
                "Favorite Checkbox:\n" +
                "Set the list that shows up by default.\n\n" +
                "Edit an item:\n" +
                "Hold down on an item to edit it.");
        helpBuilder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog helpDialog = helpBuilder.create();
        helpDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            default:
                break;
            case R.id.menu_about:
                showAbout();
                return true;
            case R.id.menu_add:
                addList();
                return true;
            case R.id.menu_delete:
                removeList(lists, listName);
                return true;
            case R.id.menu_rename:
                renameList(listName);
                return true;
            case R.id.menu_help:
                showHelp();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isFirstLaunch() {
        // Restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean isFirstLaunch = settings.getBoolean("isFirstLaunch", true);
        //  Log.i(TAG + ".isFirstLaunch", "sharedPreferences ");
        return isFirstLaunch;
    }

    @Override
    protected void onStop() {
        super.onStop();

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("isFirstLaunch", false);

        // Commit the edits!
        editor.commit();
    }
}
