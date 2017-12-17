package gov.jbb.jbbmobile.view;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import gov.jbb.jbbmobile.R;
import gov.jbb.jbbmobile.controller.HistoryController;
import gov.jbb.jbbmobile.controller.LoginController;
import gov.jbb.jbbmobile.controller.MainController;
import gov.jbb.jbbmobile.controller.PreferenceController;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class PreferenceScreenActivity extends AppCompatActivity implements View.OnClickListener{
    private Button editNickname;
    private Button deleteAccount;
    private TextView nicknameShow;
    private TextView emailShow;
    private LoginController loginController;
    private final int DELETE = 25;
    private Button signOut;
    protected PreferenceController preferenceController;
    protected ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference_screen);
        initViews();
        this.loginController = new LoginController();
        this.loginController.loadFile(this.getApplicationContext());

        this.nicknameShow.setText(Html.fromHtml("<b>Nickname: </b>" + loginController.getExplorer().getNickname()));
        this.emailShow.setText(Html.fromHtml("<b>Email: </b>" + loginController.getExplorer().getEmail()));
        //this.nicknameShow.setText("Nickname: "+ loginController.getExplorer().getNickname());
        //this.emailShow.setText("Email: "+ loginController.getExplorer().getEmail());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.editNicknameButton:
                editAccount();
                break;
            case R.id.deleteAccount:
                normalDelete();
                break;
            case R.id.signOutButton:
                signOut();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mainScreenIntent = new Intent(PreferenceScreenActivity.this, MainScreenActivity.class);
        PreferenceScreenActivity.this.startActivity(mainScreenIntent);
        finish();
    }

    private void initViews(){
        this.editNickname = (Button) findViewById(R.id.editNicknameButton);
        this.deleteAccount = (Button)findViewById(R.id.deleteAccount);
        this.nicknameShow = (TextView) findViewById(R.id.nicknameShow);
        this.emailShow = (TextView)findViewById(R.id.emailShow);
        this.editNickname.setOnClickListener((View.OnClickListener) this);
        this.deleteAccount.setOnClickListener((View.OnClickListener) this);
        this.signOut = (Button) findViewById(R.id.signOutButton);
        this.signOut.setOnClickListener((View.OnClickListener) this);
    }

    private void signOut() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.signOut);
        alert.setMessage(R.string.signOutMessage);
        alert.setPositiveButton(R.string.yesMessage, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loginController.deleteFile(getApplicationContext());
                HistoryController historyController = new HistoryController();
                historyController.deleteSave(getApplicationContext());
                getSharedPreferences("mainScreenFirstTime",0).edit().putBoolean("mainScreenFirstTime",true).apply();
                Intent startScreenIntent = new Intent(PreferenceScreenActivity.this, StartScreenActivity.class);
                PreferenceScreenActivity.this.startActivity(startScreenIntent);
                finish();
            }
        });

        alert.setNegativeButton(R.string.cancelMessage, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.show();
    }

    private void normalDelete(){
        System.out.println("Normal ++++++++++++++++");
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setId(R.id.deleteAccountEditText);
        input.setSelection(input.getText().length());
        alert.setTitle(R.string.deleteAccount);
        alert.setMessage(R.string.enterPassword);
        alert.setMessage(R.string.deleteAccountSmallSizeText);
        alert.setView(input);
        Log.i ("INPUTTT", input.getText().toString());

        alert.setPositiveButton(R.string.OKMessage, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{
                    if(new MainController().checkIfUserHasInternet(PreferenceScreenActivity.this)){
                        Log.i ("INPUTTT", input.getText().toString());
                        PreferenceController preferenceController = new PreferenceController();
                        preferenceController.deleteExplorer(input.getText().toString(), loginController.getExplorer().getEmail(), PreferenceScreenActivity.this.getApplicationContext());
                        loginController.deleteFile(PreferenceScreenActivity.this);
                        getSharedPreferences("mainScreenFirstTime",0).edit().putBoolean("mainScreenFirstTime",true).commit();
                        HistoryController historyController = new HistoryController();
                        historyController.deleteSave(getApplicationContext());
                        Intent startScreenIntet = new Intent(PreferenceScreenActivity.this, StartScreenActivity.class);
                        PreferenceScreenActivity.this.startActivity(startScreenIntet);
                        finish();
                    }else{
                        connectionError();
                    }
                }catch(IllegalArgumentException i){
                    if(i.getLocalizedMessage().equals("confirmPassword")){
                        passwordWrongError();
                    }else{
                        passwordError();
                    }
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        alert.setNegativeButton(R.string.cancelMessage, new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.show();
    }

    private void editAccount(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        alert.setTitle(R.string.nickname);
        alert.setMessage(R.string.editNicknameDescription);
        input.setId(R.id.editNicknameEditText);
        input.setMaxLines(1);
        alert.setView(input);
        input.setInputType(96);
        alert.setPositiveButton(R.string.OKMessage, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(new MainController().checkIfUserHasInternet(PreferenceScreenActivity.this)){

                    String newNickname = input.getText().toString();
                    preferenceController = new PreferenceController();

                    try{
                        preferenceController.updateNickname(newNickname, loginController.getExplorer().getEmail(), PreferenceScreenActivity.this.getApplicationContext());
                        loginController.deleteFile(PreferenceScreenActivity.this);

                        progressDialog = new ProgressDialog(PreferenceScreenActivity.this){
                            @Override
                            public void onBackPressed() {
                                dismiss();
                            }
                        };
                        progressDialog.setTitle(R.string.loading);
                        if(progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }

                        progressDialog.show();
                        new PreferenceWebService().execute();

                    } catch(IllegalArgumentException i){
                        invalidNicknameError();
                    } catch(SQLiteConstraintException e){
                        existentNickname();
                    }
                }else{
                    connectionError();
                }
            }
        });

        alert.setNegativeButton(R.string.cancelMessage, new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.show();
    }

    private void existentNickname(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.errorMessage);
        alert.setMessage(R.string.nicknameExists);
        alert.setPositiveButton(R.string.OKMessage, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PreferenceScreenActivity.this.recreate();
            }
        });
        alert.show();
    }

    private void passwordError(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.errorMessage);
        alert.setMessage(R.string.passwordValidation);
        alert.setPositiveButton(R.string.OKMessage, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PreferenceScreenActivity.this.recreate();
            }
        });
        alert.show();
    }

    private void passwordWrongError(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.errorMessage);
        alert.setMessage(R.string.wrongPassword);
        alert.setPositiveButton(R.string.OKMessage, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PreferenceScreenActivity.this.recreate();
            }
        });
        alert.show();
    }

    private void invalidNicknameError(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.errorMessage);
        alert.setMessage(R.string.nicknameValidation);
        alert.setPositiveButton(R.string.OKMessage, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PreferenceScreenActivity.this.recreate();
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
                PreferenceScreenActivity.this.recreate();
            }
        });
        alert.show();
    }

    private class PreferenceWebService extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {
            Looper.prepare();
            while(!preferenceController.isAction());

            return preferenceController.isResponse();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            try {

                if (aBoolean) {
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }

                    new LoginController().deleteFile(PreferenceScreenActivity.this);
                    new LoginController().realizeLogin(loginController.getExplorer().getEmail(),
                            PreferenceScreenActivity.this);

                    PreferenceScreenActivity.this.recreate();

                } else {
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    existentNickname();
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }
}