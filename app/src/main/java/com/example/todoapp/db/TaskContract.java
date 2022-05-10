package com.example.todoapp.db;

import android.provider.BaseColumns;
// just a class where you define the data base name that you have.
public class TaskContract {
    public static final String DB_NAME = "com.example.todoapp.db";
    public static final int DB_VERSION = 1;
    public class TaskEntry implements BaseColumns{
        public static final String TABLE = "tasks"; //named the table
        public static final String COL_TASK_TITLE = "title"; // named the column.
    }
}
