package com.example.androidonlinelogin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class SignUp extends ActionBarActivity {
    EditText firstNameText, emailTextBox, confirmPasswordText, passwordTextBox;
    ProgressDialog dialog;
    HttpClient httpclient;
    HttpPost httppost;
    List nameValuePairs;
    Button registerButton;
    HttpResponse response;
    TextView tv;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firstNameText = (EditText) findViewById(R.id.firstNameText);
        emailTextBox = (EditText) findViewById(R.id.emailTextBox);
        confirmPasswordText = (EditText) findViewById(R.id.confirmPasswordText);
        passwordTextBox = (EditText) findViewById(R.id.passwordTextBox);
        firstNameText = (EditText) findViewById(R.id.firstNameText);
        firstNameText = (EditText) findViewById(R.id.firstNameText);
        registerButton = (Button) findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = ProgressDialog.show(SignUp.this, "", "Signing up..", true);
                if (SignUp.isEmailValid(emailTextBox.getText().toString().trim())) {
                    if (isPasswordShort(passwordTextBox.getText().toString())) {
                        if (isPasswordRight(passwordTextBox.getText().toString(), confirmPasswordText.getText().toString())) {
                            register();
                        } else {
                            showConfirmAlert();
                            dialog.dismiss();
                        }
                    } else {
                        showLengthAlert();
                        dialog.dismiss();
                    }
                } else {
                    showEmailAlert();
                    dialog.dismiss();
                }
            }
        });
    }


    public void register() {
        try {
            DefaultHttpClient defaulthttpclient = new DefaultHttpClient();
            httpclient = defaulthttpclient;
            //replace "http://androidonlinelogintestform.com/signup.php" with your signup process
            HttpPost httppost = new HttpPost("http://androidonlinelogintestform.com/signup.php");
            nameValuePairs = new ArrayList<NameValuePair>();
            // Always use the same variable name for posting i.e the android side variable name and php side variable name should be similar,
            nameValuePairs.add(new BasicNameValuePair("firstName", firstNameText.getText().toString().trim()));
            nameValuePairs.add(new BasicNameValuePair("email", emailTextBox.getText().toString().trim()));  // $Edittext_value = $_POST['Edittext_value'];
            nameValuePairs.add(new BasicNameValuePair("password", passwordTextBox.getText().toString().trim()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            //Execute HTTP Post Request
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);

            if (response.equalsIgnoreCase("Success")) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(SignUp.this, "Sign up successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUp.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                });
            } else if (response.equalsIgnoreCase("Email Exists")) {
                showDupeEmailAlert();
                dialog.dismiss();
            } else if (response.equalsIgnoreCase("Insertion Error")) {
                showInsertAlert();
                dialog.dismiss();
            } else if (response.equalsIgnoreCase("Query Error")) {
                showQueryAlert();
                dialog.dismiss();
            } else if (response.equalsIgnoreCase("Database Error")) {
                showDatabaseAlert();
                dialog.dismiss();
            } else {
                showNoIdeaAlert();
                dialog.dismiss();
            }
        } catch (Exception e) {
            dialog.dismiss();
            System.out.println("Exception : " + e.getMessage());
        }
    }

    public static boolean isEmailValid(String s) {
        boolean flag = Pattern.compile("^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$", 2).matcher(s).matches();
        boolean flag1 = false;
        if (flag) {
            flag1 = true;
        }
        return flag1;
    }

    public boolean isPasswordRight(String s, String s1) {
        return s.equals(s1);
    }

    public boolean isPasswordShort(String s) {
        return s.length() > 5;
    }

    public void showConfirmAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
        builder.setTitle("Password Error")
                .setMessage("Password and Confirm Password fields do not match!")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SignUp.this.finish();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void showDatabaseAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
        builder.setTitle("Database Error")
                .setMessage("Please report this error to us! We will help you from there.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void showDupeEmailAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
        builder.setTitle("Email Already Registered")
                .setMessage("You already have an account with us! Perhaps you need to reset your password?")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void showEmailAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
        builder.setTitle("Invalid Email")
                .setMessage("Please use a valid email!")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void showInsertAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
        builder.setTitle("Insert Error")
                .setMessage("Please report this error to us! We will help you from there.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void showLengthAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
        builder.setTitle("Password too short")
                .setMessage("Please use a longer password!")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void showNoIdeaAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
        builder.setTitle("Server Side Error")
                .setMessage("Please report this error to us! We will help you from there.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void showQueryAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
        builder.setTitle("Query Error")
                .setMessage("Please report this error to us! We will help you from there.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
