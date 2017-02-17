package gov.jbb.missaonascente.view;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import gov.jbb.missaonascente.R;
import gov.jbb.missaonascente.controller.LoginController;
import gov.jbb.missaonascente.controller.MainController;
import gov.jbb.missaonascente.controller.RegisterExplorerController;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class RegisterScreenActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edtUser;
    private EditText edtPassword;
    private EditText edtEqualsPassword;
    private EditText edtEmail;
    private Resources resources;
    protected final RegisterExplorerController registerExplorerController = new RegisterExplorerController();
    protected ProgressDialog progressDialog;
    private LoginController loginController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);
        initViews();
    }

    private void initViews() {
        resources = getResources();

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        edtUser = (EditText) findViewById(R.id.nicknameEditText);
        edtUser.addTextChangedListener(textWatcher);

        edtPassword = (EditText) findViewById(R.id.passwordEditText);
        edtPassword.addTextChangedListener(textWatcher);

        edtEqualsPassword = (EditText) findViewById(R.id.passwordConfirmEditText);
        edtEqualsPassword.addTextChangedListener(textWatcher);

        edtEmail = (EditText) findViewById(R.id.emailEditText);
        edtEmail.addTextChangedListener(textWatcher);

        Button btnEnter = (Button) findViewById(R.id.registerButton);
        btnEnter.setOnClickListener(this);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.registerButton) {
            try{
                if(new MainController().checkIfUserHasInternet(this)) {
                    progressDialog = new ProgressDialog(this){
                        @Override
                        public void onBackPressed() {
                            dismiss();
                        }
                    };

                    progressDialog.setMessage(getString(R.string.registering_user));
                    registerExplorerController.register(edtUser.getText().toString(), edtEmail.getText().toString(),
                            edtPassword.getText().toString(), edtEqualsPassword.getText().toString(),
                            this.getApplicationContext());

                    loginController = new LoginController();
                    loginController.deleteFile(RegisterScreenActivity.this);

                    progressDialog.setMessage(getString(R.string.logging_in));
                    loginController.realizeLogin(edtEmail.getText().toString(), edtPassword.getText().toString(), this.getApplicationContext());
                    loginController.loadFile(this.getApplicationContext());


                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }

                    progressDialog.show();

                    new RegisterWebService().execute();
                }else{
                    connectionError();
                }
            }catch (IllegalArgumentException e){
                if((e.getLocalizedMessage()).equals("wrongNickname")){
                    nicknameError();
                }
                if((e.getLocalizedMessage()).equals("wrongPassword")){
                    passwordError();
                }
                if((e.getLocalizedMessage()).equals("wrongConfirmPassword")){
                    passwordNotEquals();
                }
                if((e.getLocalizedMessage()).equals("wrongEmail")){
                    emailError();
                }
            } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private void nicknameError(){
        edtUser.requestFocus();
        edtUser.setError(resources.getString(R.string.nicknameValidation));
    }

    private void passwordError(){
        edtPassword.requestFocus();
        edtPassword.setError(resources.getString(R.string.passwordValidation));
    }

    private void passwordNotEquals(){
        edtEqualsPassword.requestFocus();
        edtEqualsPassword.setError(resources.getString(R.string.passwordConfirmValidation));
    }

    private void emailError(){
        edtEmail.requestFocus();
        edtEmail.setError(resources.getString(R.string.invalidEmail));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent startScreenIntent = new Intent(RegisterScreenActivity.this, StartScreenActivity.class);
        RegisterScreenActivity.this.startActivity(startScreenIntent);
        finish();
    }


    private void registerErrorMessage(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.errorMessage);
        alert.setMessage(R.string.userAlreadyExists);
        alert.setPositiveButton(R.string.OKMessage, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RegisterScreenActivity.this.recreate();
            }
        });
        alert.show();
    }

    private void connectionError(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.errorMessage);
        alert.setMessage(R.string.noInternetConnection);
        alert.setPositiveButton(R.string.OKMessage, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RegisterScreenActivity.this.recreate();
            }
        });
        alert.show();
    }

    private class RegisterWebService extends AsyncTask<Void, Void, Boolean>{
        @Override
        protected Boolean doInBackground(Void... params) {
            Looper.prepare();
            while(!registerExplorerController.isAction());

            return registerExplorerController.isResponse();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(registerExplorerController.isAction()) {
                if (registerExplorerController.isResponse()) {
                    progressDialog.dismiss();
                    Intent mainScreen = new Intent(RegisterScreenActivity.this, MainScreenActivity.class);
                    RegisterScreenActivity.this.startActivity(mainScreen);
                    finish();
                } else {
                    progressDialog.dismiss();
                    registerErrorMessage();
                    loginController.deleteFile(getApplicationContext());
                }
            }
        }
    }
}