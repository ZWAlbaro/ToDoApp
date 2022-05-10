package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todoapp.db.TaskContract;
import com.example.todoapp.db.TaskDbHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private TaskDbHelper mHelper;// the word private makes the data secure and not be influenced by other classes.
    private ArrayAdapter<String> mAdapter;
    private ListView mTaskListView;

    final String TAG = "Main Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        updateUI();
    }
    void init(){
        mHelper = new TaskDbHelper(this);
        mTaskListView = (ListView) findViewById(R.id.list_todo);
    }

    private void updateUI() {
        ArrayList<String> taskList = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE,
                new String[]{TaskContract.TaskEntry._ID, TaskContract.TaskEntry.COL_TASK_TITLE},
                null, null, null, null, null);
        while (cursor.moveToNext()) {//returns either a row or nothing.
            int idx = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE);
            taskList.add(cursor.getString(idx));
        }
//ArrayAdapter is capable of reading from the the Array list and at the same time, set up the items in the array list “to do structure”, and get connected to the the TaskList.
        if (mAdapter == null) {// mAdapter is null because when we initialize it it gets the default
            //value and that is null.
            mAdapter = new ArrayAdapter<>(this,
                    R.layout.item_todo,
                    R.id.task_title,
                    taskList);
            mTaskListView.setAdapter(mAdapter);
        } else {
            mAdapter.clear();
            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged();
        }

        cursor.close();
        db.close();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //what happens when we click the Add icon:
        // here we are making the dialog box pop up:
        switch (item.getItemId()){
            case(R.id.action_add_task):
               // Log.d(TAG, "Add A New Task");
                final EditText taskEditText = new EditText(this);
                AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Add A New Task")
                        .setMessage("What do you want to do next?")
                        .setView(taskEditText)
                        //positive button is Add
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Take the value of the TaskEditText from the dialogue box
                                String task = String.valueOf(taskEditText.getText());
                                Log.d(TAG, "Task to add:  " + task);
                                //need to embed code that would send the data into the db
                                SQLiteDatabase db = mHelper.getWritableDatabase();// open the database.
                                // ContentValues can insert data from the dialog box into a specific place in the table.
                                ContentValues values = new ContentValues();
                                values.put(TaskContract.TaskEntry.COL_TASK_TITLE, task);
                                db.insertWithOnConflict(TaskContract.TaskEntry.TABLE,
                                        null,
                                        values,
                                        SQLiteDatabase.CONFLICT_REPLACE);
                                db.close(); //close the database
                            }
                        })
                        //Negative button is Cancel
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();

                Toast.makeText(this, "Item one selected", Toast.LENGTH_SHORT).show();
                return true;
            default: return super.onOptionsItemSelected(item);
}}
// SO that when we hit the done button, the task will be deleted.
    public void deleteTask(View view) {
        View parent = (View) view.getParent();// we locate the item's id by using .getParent function.
        TextView taskTextView = (TextView) parent.findViewById(R.id.task_title);
        String task = String.valueOf(taskTextView.getText());
        SQLiteDatabase db = mHelper.getWritableDatabase();
        //locating the actual item and deleting it inside the table using SQl.
        // Always one deleted cuz only one is selected.
        db.delete(TaskContract.TaskEntry.TABLE,
                TaskContract.TaskEntry.COL_TASK_TITLE + " = ?",
                new String[]{task});
        db.close();
        updateUI();

    }}