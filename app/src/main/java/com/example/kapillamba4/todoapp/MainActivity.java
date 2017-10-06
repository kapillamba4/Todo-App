package com.example.kapillamba4.todoapp;


import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static View alertView, expandView;
    private ArrayList<ListItem> todos;
    private Button btnDatePicker, btnTimePicker;
    private TextView txtDate, txtTime;
    private TodoCustomAdapter adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("TodoList");
        listView = (ListView) findViewById(R.id.todo_list);

        todos = new ArrayList<>();
        TodoOpenHelper todoOpenHelper = TodoOpenHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = todoOpenHelper.getReadableDatabase();
        Cursor cursor = db.query(Contract.TODO_TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
        }

        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndex(Contract.TODO_TITLE));
            String content = cursor.getString(cursor.getColumnIndex(Contract.TODO_CONTENT));
            String date = cursor.getString(cursor.getColumnIndex(Contract.TODO_DATE));
            String time = cursor.getString(cursor.getColumnIndex(Contract.TODO_TIME));
            long id = cursor.getLong(cursor.getColumnIndex(Contract.TODO_ID));
            ListItem listItem = new ListItem(title, content, id, date, time);
            todos.add(listItem);
        }

        cursor.close();
        adapter = new TodoCustomAdapter(this, todos, new TodoCustomAdapter.DeleteButtonClickListener() {
            @Override
            public void onDeleteClicked(int position, View view) {
                TodoOpenHelper todoOpenHelper = TodoOpenHelper.getInstance(getApplicationContext());
                SQLiteDatabase db = todoOpenHelper.getReadableDatabase();
                String id = String.valueOf(todos.get(position).getId());
                db.delete(Contract.TODO_TABLE_NAME, Contract.TODO_ID + " = " + id, null);
                todos.remove(position);
                adapter.notifyDataSetChanged();
            }
        });

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListItem listItem = todos.get(i);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                expandView = getLayoutInflater().inflate(R.layout.item_expanded_view, null);
                builder.setView(expandView);
                TextView todoTitle = expandView.findViewById(R.id.expanded_todo_title);
                TextView todoContent = expandView.findViewById(R.id.expanded_todo_content);
                todoTitle.setText(listItem.getTitle());
                todoContent.setText(listItem.getContent());
                builder.setView(expandView);
                builder.show();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, final long l) {
                Log.i("TAG", "onItemLongClick: " + todos.size());
                Log.i("TAG", "onItemLongClick: ");
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                TextView title = new TextView(MainActivity.this);
                title.setText("Add/Edit a task");
                title.setBackgroundColor(Color.DKGRAY);
                title.setPadding(10, 10, 10, 10);
                title.setGravity(Gravity.CENTER);
                title.setTextColor(Color.WHITE);
                title.setTextSize(20);
                builder.setCustomTitle(title);

                alertView = getLayoutInflater().inflate(R.layout.item_todo, null);
                builder.setView(alertView);

                btnDatePicker = alertView.findViewById(R.id.btn_date);
                btnTimePicker = alertView.findViewById(R.id.btn_time);
                txtDate = alertView.findViewById(R.id.in_date);
                txtTime = alertView.findViewById(R.id.in_time);
                btnDatePicker.setOnClickListener(MainActivity.this);
                btnTimePicker.setOnClickListener(MainActivity.this);

                final ListItem listItem = todos.get(i);
                final EditText inputTitle = alertView.findViewById(R.id.title);
                final EditText inputContent = alertView.findViewById(R.id.content);
                inputTitle.setText(listItem.getTitle());
                inputContent.setText(listItem.getContent());
                txtDate.setText(listItem.getDate());
                txtTime.setText(listItem.getTime());

                builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        TodoOpenHelper todoOpenHelper = TodoOpenHelper.getInstance(getApplicationContext());
                        SQLiteDatabase db = todoOpenHelper.getWritableDatabase();

                        ContentValues contentValues = new ContentValues();
                        contentValues.put(Contract.TODO_TITLE, inputTitle.getText().toString());
                        contentValues.put(Contract.TODO_CONTENT, inputContent.getText().toString());
                        contentValues.put(Contract.TODO_DATE, txtDate.getText().toString());
                        contentValues.put(Contract.TODO_TIME, txtTime.getText().toString());
                        db.update(Contract.TODO_TABLE_NAME, contentValues, Contract.TODO_ID + "=?", null);
                        listItem.setTitle(inputTitle.getText().toString());
                        listItem.setContent(inputContent.getText().toString());
                        listItem.setDate(txtDate.getText().toString());
                        listItem.setTime(txtTime.getText().toString());
                        adapter.notifyDataSetChanged();
                        // Toast.makeText(MainActivity.this, "Add pressed", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Toast.makeText(MainActivity.this, "CANCEL pressed", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.show();
                return true;
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_task:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                TextView title = new TextView(this);
                title.setText("Add/Edit a task");
                title.setBackgroundColor(Color.DKGRAY);
                title.setPadding(10, 10, 10, 10);
                title.setGravity(Gravity.CENTER);
                title.setTextColor(Color.WHITE);
                title.setTextSize(20);
                builder.setCustomTitle(title);

                builder.setCancelable(false);
                alertView = getLayoutInflater().inflate(R.layout.item_todo, null);
                builder.setView(alertView);

                btnDatePicker = alertView.findViewById(R.id.btn_date);
                btnTimePicker = alertView.findViewById(R.id.btn_time);
                txtDate = alertView.findViewById(R.id.in_date);
                txtTime = alertView.findViewById(R.id.in_time);
                btnDatePicker.setOnClickListener(this);
                btnTimePicker.setOnClickListener(this);

                builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText inputTitle = alertView.findViewById(R.id.title);
                        EditText inputContent = alertView.findViewById(R.id.content);
                        TodoOpenHelper todoOpenHelper = TodoOpenHelper.getInstance(getApplicationContext());
                        SQLiteDatabase db = todoOpenHelper.getWritableDatabase();

                        ContentValues contentValues = new ContentValues();
                        contentValues.put(Contract.TODO_TITLE, inputTitle.getText().toString());
                        contentValues.put(Contract.TODO_CONTENT, inputContent.getText().toString());
                        contentValues.put(Contract.TODO_DATE, txtDate.getText().toString());
                        contentValues.put(Contract.TODO_TIME, txtTime.getText().toString());
                        long id = db.insert(Contract.TODO_TABLE_NAME, null, contentValues);

                        ListItem listItem = new ListItem(inputTitle.getText().toString(), inputContent.getText().toString(), id, txtDate.getText().toString(), txtTime.getText().toString());
                        todos.add(listItem);
                        Log.i("TAG", "onClick: " + todos.size());
                        adapter.notifyDataSetChanged();

                        // Toast.makeText(MainActivity.this, "Add pressed", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Toast.makeText(MainActivity.this, "CANCEL pressed", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_date:
                DialogFragment newDateFragment = new DatePickerFragment();
                newDateFragment.show(this.getFragmentManager(), "datePicker");
                break;
            case R.id.btn_time:
                DialogFragment newTimeFragment = new TimePickerFragment();
                newTimeFragment.show(this.getFragmentManager(), "timePicker");
                break;
        }
    }
}
