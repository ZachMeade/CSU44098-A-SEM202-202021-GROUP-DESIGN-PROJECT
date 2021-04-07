package com.example.beaumontocrapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.RequestQueue;
import com.android.volley.Request;
import com.android.volley.VolleyError;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;



import javax.crypto.Cipher;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_STRING = "com.android.beaumontocrapp.EXTRA_STRING";
    public static final String auth_url = "http://ec2-3-91-65-51.compute-1.amazonaws.com:10001/";
    public static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC1kkV9411AWnBZvVcYNoxdKwBW\n" +
            "NrRIM0ePR4CDwf/9r2TRXLRNwzQKz4hVStveBO2LCKWftAOQbCP6qUXQgjEvocCd\n" +
            "42Jr2cVZxA5klH6yy2y6IE/76gUjlBCX/szCp6S+kFrxXFZIToL3ViczJvhPYcje\n" +
            "rwYK7Fq+wiaxzLGZkwIDAQAB\n";


    private SharedPreferences mSharedPreferences;
    Button loginButton, registerButton,registerSubmitButton,forgetButton;
    CheckBox rememberMe;
    ImageButton registerReturnButton;
    EditText username, password, confirmPassword;
    RequestQueue queue;
    RSAPublicKey pk;
    String p;


    private Button captureButton, howToButton, analyticsButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);
        setContentView(R.layout.log_in);
        try {
            pk = get_pubKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        queue = Volley.newRequestQueue(getApplicationContext());
        mSharedPreferences = getSharedPreferences("auth",MODE_PRIVATE);
        set_login_listener();
    }

    RSAPublicKey get_pubKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] encoded = Base64.decode(PUBLIC_KEY,Base64.DEFAULT);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(encoded));
        return pubKey;
    }

    public static String encryptf(String rawText, PublicKey publicKey) throws IOException, GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return Base64.encodeToString(cipher.doFinal(rawText.getBytes("UTF-8")),Base64.DEFAULT);
    }


    void set_login_listener(){
        String storedusername = mSharedPreferences.getString("username","");
        String storedpassword = mSharedPreferences.getString("password","");
        if(!storedusername.isEmpty()&&!storedpassword.isEmpty()){
            String url = auth_url + "login/"+storedusername+"/"+storedpassword;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                @Override
                public void onResponse(String response) {
                    Log.d("1",response);
                    if(response.contains("success")){
                        homePage();
                    }
                    else if(response.contains("wrong")){
                        Toast.makeText(getApplicationContext(), "Wrong username/password or you haven't registered", Toast.LENGTH_LONG).show();
                    }
                    else if(response.contains("not user")){
                        Toast.makeText(getApplicationContext(), "Wrong username", Toast.LENGTH_LONG).show();
                    }
                    else if(response.contains("failed")){
                        Toast.makeText(getApplicationContext(), "Error with server", Toast.LENGTH_LONG).show();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "No connection", Toast.LENGTH_LONG).show();

                }
            });
            queue.add(stringRequest);
        }
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        loginButton = (Button)findViewById(R.id.login);
        forgetButton = (Button)findViewById(R.id.forgot);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernameString = username.getText().toString();
                String passwordString = password.getText().toString();
                if(usernameString.length() >= 8 )
                {
                    if(passwordString.length() >= 8 && passwordString.length() <= 16)
                    {
                        if(usernameString.substring(usernameString.length()-7,usernameString.length()).equalsIgnoreCase("@tcd.ie")||usernameString.substring(usernameString.length()-12,usernameString.length()).equalsIgnoreCase("@beaumont.ie"))
                        {
                            p="";
                            //send to server
                            try {
                                p = encryptf(passwordString,pk);
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            } catch (GeneralSecurityException generalSecurityException) {
                                generalSecurityException.printStackTrace();
                            }
                            String url = auth_url + "login/"+usernameString+"/"+p;
                            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                                @Override
                                public void onResponse(String response) {
                                    Log.d("1",response);
                                    if(response.contains("success")){
                                        rememberMe = (CheckBox) findViewById(R.id.rememberMe);
                                        if(rememberMe.isChecked()){
                                            SharedPreferences.Editor auth = mSharedPreferences.edit();
                                            auth.putString("username",usernameString);
                                            auth.putString("password",p);
                                            auth.apply();
                                        }
                                        homePage();
                                    }
                                    else if(response.contains("wrong")){
                                        Toast.makeText(getApplicationContext(), "Wrong username/password or you haven't registered", Toast.LENGTH_LONG).show();
                                    }
                                    else if(response.contains("not user")){
                                        Toast.makeText(getApplicationContext(), "Wrong username", Toast.LENGTH_LONG).show();
                                    }
                                    else if(response.contains("failed")){
                                        Toast.makeText(getApplicationContext(), "Error with server", Toast.LENGTH_LONG).show();
                                    }

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getApplicationContext(), "No connection", Toast.LENGTH_LONG).show();

                                }
                            });
                            queue.add(stringRequest);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Please log in with either TCD or Beaumont hospital email", Toast.LENGTH_LONG).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Password is between 8-16 characters", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Please log in with either TCD or Beaumont hospital email", Toast.LENGTH_LONG).show();
                }

            }
        });

        registerButton = (Button)findViewById(R.id.register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.register);
                set_register_listener();
            }
        });

        forgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Please contact IT service", Toast.LENGTH_LONG).show();
            }
        });

    }

    void set_register_listener(){
        registerReturnButton = (ImageButton)findViewById((R.id.returnButton));
        registerReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.log_in);
                set_login_listener();
            }
        });
        registerSubmitButton = (Button)findViewById(R.id.submit);
        registerSubmitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                username = (EditText)findViewById(R.id.username);
                password = (EditText)findViewById(R.id.password);
                confirmPassword = (EditText)findViewById(R.id.confirmPassword);
                String usernameString = username.getText().toString();
                String passwordString = password.getText().toString();
                String confirmPasswordString = confirmPassword.getText().toString();
                if(usernameString.length() >= 8 )
                {
                    if (passwordString.length() >= 8 && passwordString.length() <= 16)
                    {
                        if (usernameString.substring(usernameString.length() - 7, usernameString.length()).equalsIgnoreCase("@tcd.ie") || usernameString.substring(usernameString.length() - 12, usernameString.length()).equalsIgnoreCase("@beaumont.ie"))
                        {
                            if(passwordString.equals(confirmPasswordString)){
                                String url = auth_url + "reg/"+usernameString+"/"+passwordString;
                                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                                    @Override
                                    public void onResponse(String response) {
                                        Log.d("1",response);
                                        if(response.contains("success")){
                                            homePage();
                                        }
                                        else if(response.contains("already")){
                                            Toast.makeText(getApplicationContext(), "You have already registered", Toast.LENGTH_LONG).show();
                                        }
                                        else if(response.contains("not user")){
                                            Toast.makeText(getApplicationContext(), "Wrong username", Toast.LENGTH_LONG).show();
                                        }
                                        else if(response.contains("failed")){
                                            Toast.makeText(getApplicationContext(), "Error with server", Toast.LENGTH_LONG).show();
                                        }

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(getApplicationContext(), "No connection", Toast.LENGTH_LONG).show();

                                    }
                                });
                                queue.add(stringRequest);
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Two passwords are different", Toast.LENGTH_LONG).show();
                            }

                        }
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Password is between 8-16 characters", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Please register with either TCD or Beaumont hospital email", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    boolean remember_me_log_in(){
        return false;
    }

    private void homePage(){
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
    }



}