package com.example.bookeep;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
//import com.google.gson.Gson;

/**
 * Log in page that allows users to log in or sign up
 * @author Nafee Khan, Jeff Kirker
 * @see User
 * @version 1.0.1
 */
public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail;
    private EditText edtPassword;
    private TextView txtAppLabel;
    private Button btnSignIn;
    private Button btnSignUp;
    private FireBaseController fireBaseController = new FireBaseController(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtAppLabel = (TextView) findViewById(R.id.bookeep);
        edtEmail = (EditText) findViewById(R.id.edit_email);
        edtPassword = (EditText) findViewById(R.id.edit_password);
        btnSignIn = (Button) findViewById(R.id.btn_sign_in);
        btnSignUp = (Button) findViewById(R.id.btn_sign_up);


//        if( fireBaseController.isUserLoggedIn()){
//
//            fireBaseController.launchMainActivity();
//
//        }

//<<<<<<< HEAD
        edtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId ==  EditorInfo.IME_ACTION_DONE)) {
                    btnSignIn.performClick();
                }
                return false;
            }
        });
//=======
//>>>>>>> firebase

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validation()){

                    fireBaseController.signIn(edtEmail.getText().toString(), edtPassword.getText().toString());


                }

            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);

            }
        });



    }

    /**
     * validates users log in info is correct
     * @return boolean of whether log in info is right
     */
    private boolean validation(){
        boolean emailValid = false;
        boolean passwordValid = false;
        //boolean heartValid = false;
        //boolean dateValid = false;
        //boolean timeValid = false;

        if (Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString()).matches()) {
            emailValid = true;
        } else {
            edtEmail.setError("Invalid email");
        }
        if (edtPassword.getText().toString().length() > 5) {
            passwordValid = true;
        } else {
            edtPassword.setError("Must be at least 6 characters");
        }

        return (emailValid && passwordValid);
    }
}
