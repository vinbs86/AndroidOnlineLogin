package com.example.androidonlinelogin;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;

import java.util.ArrayList;
import java.util.List;


public class Login extends ActionBarActivity {
    ProgressDialog dialog;
    EditText emailTextBox, passwordTextBox;
    Button loginButton, registerButton, forgotButton;
    HttpClient httpclient;
    HttpPost httppost;
    List nameValuePairs;
    CheckBox saveLoginCheckBox;
    TextView tv;
    private String email, password;
    private SharedPreferences loginPreferences;
    private android.content.SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.loginButton);
        registerButton = (Button) findViewById(R.id.registerButton);
        forgotButton = (Button) findViewById(R.id.forgotButton);
        emailTextBox = (EditText) findViewById(R.id.emailTextBox);
        passwordTextBox = (EditText) findViewById(R.id.passwordTextBox);
        saveLoginCheckBox = (CheckBox) findViewById(R.id.saveLoginCheckBox);
        tv = (TextView) findViewById(R.id.tv);

        //remembers email and password if saveLoginCheckBox is ticked
        loginPreferences = getSharedPreferences("loginPrefs", 0);
        loginPrefsEditor = loginPreferences.edit();
        saveLogin = Boolean.valueOf(loginPreferences.getBoolean("saveLogin", false));

        //automatically login if remember me is checked
        if (saveLogin.booleanValue()) {
            emailTextBox.setText(loginPreferences.getString("email", ""));
            passwordTextBox.setText(loginPreferences.getString("password", ""));
            saveLoginCheckBox.setChecked(true);

            //show a progress dialog while logging in
            dialog = ProgressDialog.show(this, "", "Logging in..", true);
            new Thread(new Runnable() {
                public void run() {
                    login();
                }
            }).start();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if remember me is checked, automatically fill up the email and password fields. else, clear text in fields
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(emailTextBox.getWindowToken(), 0);
                email = emailTextBox.getText().toString();
                password = passwordTextBox.getText().toString();
                if (saveLoginCheckBox.isChecked()) {
                    loginPrefsEditor.putBoolean("saveLogin", true);
                    loginPrefsEditor.putString("email", email);
                    loginPrefsEditor.putString("password", password);
                    loginPrefsEditor.commit();

                }else {
                    loginPrefsEditor.clear();
                    loginPrefsEditor.commit();
                }
                dialog = ProgressDialog.show(Login.this, "", "Logging in..", true);
                new Thread(new Runnable() {
                    public void run() {
                        login();
                    }
                }).start();
            }
        });

        //open signup activity
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });

        //open forgot password activity
        forgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ForgotPassword.class);
                startActivity(intent);
            }
        });
    }



    public void login() {
        try {
            DefaultHttpClient defaulthttpclient = new DefaultHttpClient();
            httpclient = defaulthttpclient;

            //replace "http://androidonlinelogintestform.com/login.php" with your login process
            HttpPost httppost = new HttpPost("http://androidonlinelogintestform.com/login.php");

            // Always use the same variable name for posting i.e the android side variable name and php side variable name should be similar
            // Ex: $Edittext_value = $_POST['Edittext_value'];
            nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("email", emailTextBox.getText().toString().trim()));
            nameValuePairs.add(new BasicNameValuePair("password", passwordTextBox.getText().toString().trim()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            //Execute HTTP Post Request and let php do all the thinking
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);

            //store cookies for next activity
            BasicCookieStore basiccookiestore = new BasicCookieStore();
            CookieStoreHelper.cookieStore = basiccookiestore;
            BasicHttpContext basichttpcontext = new BasicHttpContext();
            basichttpcontext.setAttribute("http.cookie-store", CookieStoreHelper.cookieStore);
            httpclient.execute(httppost, basichttpcontext);
            CookieStoreHelper.sessionCookie = CookieStoreHelper.cookieStore.getCookies();

            //run next activity if email and password is correct
            if (response.trim().matches("User Found")) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(Login.this, "Login Success", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                        //replace Login.class with your main activity (ex: MainActivity.class)
                        Intent intent = new Intent(Login.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                });
                finish();
            //show this dialog if for some reason there is no response from server
            } else if (response.equalsIgnoreCase("")) {
                dialog.dismiss();
                showNoResponseAlert();
            } else {
                dialog.dismiss();
                showAlert();
            }
        } catch (Exception e) {
            dialog.dismiss();
            System.out.println("Exception : " + e.getMessage());
        }
    }

    //Alert for incorrect password
    public void showAlert() {
        Login.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                builder.setTitle("Login Error");
                builder.setMessage("Incorrect Email/Password!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    //Alert for no response from server
    public void showNoResponseAlert() {
        Login.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                builder.setTitle("Server Error");
                builder.setMessage("No Response from Server.. Please report this to us!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}

