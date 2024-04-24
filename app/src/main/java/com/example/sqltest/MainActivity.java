package com.example.sqltest;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        execSqlOnce("DROP TABLE users");
        execSqlOnce("CREATE TABLE IF NOT EXISTS users (id LONG, name TEXT, age INTEGER)");

        updateInfo();
    }

    private void addUser(User user) {
        String value = user.id + ", '" + user.name + "', " + user.age;

        execSqlOnce("INSERT OR IGNORE INTO users VALUES (" + value + ");");

        updateInfo();
    }

    public void removeUserWithId(String id) {
        String question = "Remove user " + Objects.requireNonNull(getUser(id)).name + "?";

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Database")
                .setMessage(question)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    execSqlOnce("DELETE FROM users WHERE id=" + id + ";");

                    updateInfo();
                }).setNegativeButton(R.string.no, (dialog, which) -> {}).show();
    }

    public void editUserWithId(String id) {
        User oldUser = getUser(id);

        Dialog addDialog = new Dialog(this);

        addDialog.setContentView(R.layout.add_dialog);

        EditText userName = addDialog.findViewById(R.id.nameText);
        assert oldUser != null;
        userName.setText(oldUser.name);

        EditText userAge = addDialog.findViewById(R.id.ageText);
        userAge.setText(oldUser.age);

        TextView textView = addDialog.findViewById(R.id.newUserInfo);
        textView.setText(getString(R.string.edit_user));

        addDialog.findViewById(R.id.createButton).setOnClickListener(v -> {
            User user = new User(oldUser.id, userAge.getText().toString(), userName.getText().toString());

            execSqlOnce("UPDATE users SET name='"
                    + user.name + "', age=" + user.age + " WHERE id=" + id + ";");

            updateInfo();

            addDialog.hide();
        });

        addDialog.show();
    }

    private User getUser(String _id) {
        SQLiteDatabase db = getBaseContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null);
        Cursor query = db.rawQuery("SELECT * FROM users WHERE id=" + _id + ";", null);

        ArrayList<User> users = new ArrayList<>();

        while (query.moveToNext()) {
            int id = query.getInt(0);
            String name = query.getString(1);
            int age = query.getInt(2);

            users.add(new User(Integer.toString(id), Integer.toString(age), name));

            Debug.log("User " + id);
        }

        return !users.isEmpty() ? users.get(0) : null;
    }

    private void execSqlOnce(String command) {
        SQLiteDatabase db = getBaseContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null);

        db.execSQL(command);

        db.close();
    }

    public void clearAll(View view) {
        new AlertDialog.Builder(MainActivity.this)
            .setTitle("Database")
            .setMessage("Remove all users?")
            .setPositiveButton(R.string.yes, (dialog, which) -> {
                execSqlOnce("DELETE FROM users");

                updateInfo();
            }).setNegativeButton(R.string.no, (dialog, which) -> {}).show();
    }

    private void updateInfo() {
        SQLiteDatabase db = getBaseContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null);
        Cursor query = db.rawQuery("SELECT * FROM users;", null);

        ArrayList<User> users = new ArrayList<>();

        while (query.moveToNext()) {
            int id = query.getInt(0);
            String name = query.getString(1);
            int age = query.getInt(2);

            users.add(new User(Integer.toString(id), Integer.toString(age), name));

            Debug.log("User " + id);
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        UserAdapter adapter = new UserAdapter(this, users, this);
        recyclerView.setAdapter(adapter);

        query.close();
        db.close();
    }

    private int getMaxId() {
        int maxId = 0;

        SQLiteDatabase db = getBaseContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null);
        Cursor query = db.rawQuery("SELECT * FROM users;", null);

        while (query.moveToNext()) {
            int id = query.getInt(0);

            if (maxId <= id) maxId = id + 1;
        }

        return maxId;
    }

    public void createNewUser(View view) {
        Dialog addDialog = new Dialog(this);

        addDialog.setContentView(R.layout.add_dialog);

        EditText userName = addDialog.findViewById(R.id.nameText);
        EditText userAge = addDialog.findViewById(R.id.ageText);

        TextView textView = addDialog.findViewById(R.id.newUserInfo);
        textView.setText(R.string.new_user);

        addDialog.findViewById(R.id.createButton).setOnClickListener(v -> {
            User user = new User(Integer.toString(getMaxId()), userAge.getText().toString(), userName.getText().toString());

            addUser(user);

            addDialog.hide();
        });

        addDialog.show();
    }
}