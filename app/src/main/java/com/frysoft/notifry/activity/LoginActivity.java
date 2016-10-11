package com.frysoft.notifry.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import com.frysoft.notifry.R;
import com.frysoft.notifry.data.MySQLListener;
import com.frysoft.notifry.data.User;
import com.frysoft.notifry.utils.App;

/**
 * Created by Edwin Pichler on 04.05.2016.
 */
public class LoginActivity extends AppCompatActivity implements MySQLListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final AppCompatEditText view_user = (AppCompatEditText) findViewById(R.id.login_user);
        final AppCompatEditText view_password = (AppCompatEditText) findViewById(R.id.login_password);

        App.setContext(this);
        App.load();

        AppCompatButton button_login = (AppCompatButton) findViewById(R.id.button_login);
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = view_user.getText().toString();
                String password = view_password.getText().toString();
                if (!user.equals("") && !password.equals("")){
                    User.login(user, password);
                }
            }
        });

        AppCompatButton button_local = (AppCompatButton) findViewById(R.id.button_login_local);
        button_local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(App.getContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    @Override
    public void mysql_finished() {

    }
}
