package com.no.badeeb.salespoi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.prefs.Preferences;

public class LoginActivity extends AppCompatActivity {


    private RequestQueue requestQueue;

    private EditText usernameEditText;
    private EditText passwordEditText;
    private View progressView;
    private View loginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSharedPreferences("User", MODE_PRIVATE).contains("auth_token")){
            startActivity(new Intent(this, CustomerListActivity.class));
            finish();
        }

        setContentView(R.layout.activity_login);
        usernameEditText = (EditText) findViewById(R.id.email);

        passwordEditText = (EditText) findViewById(R.id.password);

        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        loginFormView = findViewById(R.id.login_form);
        progressView = findViewById(R.id.login_progress);
    }

    private RequestQueue getRequestQueue() {
        return requestQueue == null ? Volley.newRequestQueue(this) : requestQueue;
    }

    private void attemptLogin() {
        usernameEditText.setError(null);
        passwordEditText.setError(null);

        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError(getString(R.string.error_field_required));
        }

        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError(getString(R.string.error_field_required));
        }

        String url = Constants.HOST + "/api/sales_men/sign_in.json";
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, createRequestBody(username, password),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        boolean success = false;
                        String authToken = null;
                        try {
                            authToken = response.getString("auth_token");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        showProgress(false);
                        SharedPreferences preferences = LoginActivity.this.getSharedPreferences("User", MODE_PRIVATE);
                        preferences.edit().putString("auth_token", authToken).commit();
                        startActivity(new Intent(LoginActivity.this, CustomerListActivity.class));
                        finish();

                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        passwordEditText.setError(getString(R.string.error_incorrect_password));
                        passwordEditText.requestFocus();
                    }
                });

        showProgress(true);
        getRequestQueue().add(jsonRequest);
    }

    private JSONObject createRequestBody(String username, String password) {
        try {
            JSONObject data = new JSONObject();
            data.put("username", username);
            data.put("password", password);
            JSONObject wrapper = new JSONObject();
            wrapper.put("api_sales_man", data);
            return wrapper;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    private void showProgress(final boolean show) {
        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}

