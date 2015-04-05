package com.nolan.shopping;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    ListView list;
    Button btnAdd;
    ArrayList<String> things;
    EditText editAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        things = new ArrayList<String>();
        load(things);
        list = (ListView) findViewById(R.id.list);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        editAdd = (EditText) findViewById(R.id.editAdd);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, things);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String ItemValue = (String) list.getItemAtPosition(position);
                //Toast.makeText(getApplicationContext(), ItemValue, Toast.LENGTH_SHORT).show();
                showDelete(things, ItemValue, position, adapter);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editAdd.getText().toString().isEmpty()) {
                    add(things, editAdd.getText().toString(), adapter);
                    editAdd.setText(null);
                }
            }
        });

    }

    public void add(ArrayList<String> things, String item, ArrayAdapter<String> adapter) {
        things.add(item);
        update(adapter);
        save(things);
    }

    public void remove(ArrayList<String> things, int position, ArrayAdapter<String> adapter) {
        things.remove(position);
        update(adapter);
        save(things);
    }

    public void update(ArrayAdapter<String> adapter) {
        adapter.notifyDataSetChanged();
    }

    public void save(ArrayList<String> things) {
        BufferedWriter bufferedWriter;
        try {
            FileOutputStream fileOutputStream = openFileOutput("things.txt", Context.MODE_PRIVATE);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
            for (int i=0; i<things.size(); i++) {
                bufferedWriter.write(things.get(i) + "\r\n");
            }
            bufferedWriter.close();
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            //Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
        }
    }

    public void load(ArrayList<String> things) {
        BufferedReader bufferedReader;
        //tringBuilder result = new StringBuilder();
        try {
            FileInputStream fileInputStream = openFileInput("things.txt");
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                things.add(line);
            }
            bufferedReader.close();
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            //Toast.makeText(getApplicationContext(), "Loaded", Toast.LENGTH_SHORT).show();
        }
    }

    public void showDelete(final ArrayList<String> things, String name, final int position, final ArrayAdapter<String> adapter) {
        AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(this);
        deleteBuilder.setTitle("Delete");
        deleteBuilder.setMessage("Delete item \"" + name + "\" ?");
        deleteBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Delete item
                remove(things, position, adapter);
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
        aboutBuilder.setMessage("Shopping List app\r\nCreated by BeckerSoft");
        aboutBuilder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Nothing
            }
        });

        AlertDialog aboutDialog = aboutBuilder.create();
        aboutDialog.show();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_about) {
            showAbout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
