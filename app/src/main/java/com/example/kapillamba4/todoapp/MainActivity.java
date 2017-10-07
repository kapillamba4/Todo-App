package com.example.kapillamba4.todoapp;


import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static View alertView, expandView;
    private ArrayList<ListItem> todos;
    private Button btnDatePicker, btnTimePicker;
    private TextView txtDate, txtTime;
    private TodoCustomAdapter adapter;
    private ListView listView;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("TodoList");
        listView = (ListView) findViewById(R.id.todo_list);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);


        fab.setOnTouchListener(new View.OnTouchListener() {
            float dX;
            float dY;
            int lastAction;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = view.getX() - event.getRawX();
                        dY = view.getY() - event.getRawY();
                        lastAction = MotionEvent.ACTION_DOWN;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        view.setY(event.getRawY() + dY);
                        view.setX(event.getRawX() + dX);
                        lastAction = MotionEvent.ACTION_MOVE;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (lastAction == MotionEvent.ACTION_DOWN)
                            fab.callOnClick();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });

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
            // TODO implement share feature
//            case R.id.share:
//                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
//                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private long convertToEpoch(String date, String time) throws ParseException {
        // TODO handle case when date is empty string or time is empty string
        time = time + ":00.000";
        Log.i("date", date + " " + time);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date value = sdf.parse(date + " " + time);
        long epoch = value.getTime();
        return epoch;
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
            case R.id.fab:
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

                        long notifyTime = System.currentTimeMillis();
                        Log.i("start", String.valueOf(notifyTime));
                        try {
                            notifyTime = convertToEpoch(txtDate.getText().toString(), txtTime.getText().toString());
                            Log.i("end", String.valueOf(notifyTime));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                        intent.putExtra("title", inputTitle.getText().toString());
                        intent.putExtra("content", inputContent.getText().toString());
                        PendingIntent alarmIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
                        alarmMgr.set(AlarmManager.RTC_WAKEUP, notifyTime, alarmIntent);
                    }
                });

                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Toast.makeText(MainActivity.this, "CANCEL pressed", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.show();
                break;
        }
    }
}
