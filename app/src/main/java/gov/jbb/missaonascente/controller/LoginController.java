package gov.jbb.missaonascente.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import gov.jbb.missaonascente.dao.ExplorerDAO;
import gov.jbb.missaonascente.model.Explorer;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import static android.content.Context.MODE_PRIVATE;

public class LoginController {
    private Explorer explorer;
    private boolean action = false;
    private boolean response;
    private static final String PREF_NAME = "MainActivityPreferences";

    private DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference explorersReference = rootReference.child(ExplorerDAO.TABLE);

    public LoginController() {
        explorer = new Explorer();
    }

    //LoginController to normal register Accounts

    public boolean realizeLogin(String email, String password, Context context) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        ExplorerDAO db = new ExplorerDAO(context);
        Explorer explorer = new Explorer();
        String passwordDigest;
        passwordDigest = explorer.cryptographyPassword(password);
        explorer = db.findExplorerLogin(email,passwordDigest);
        db.close();

        if (explorer == null || explorer.getEmail() == null || explorer.getPassword() == null) {
            return false;
        }
        saveFile(explorer.getEmail(), context);
        return true;
    }

    public void doLogin(final String email, final String password, final Context context){
        final Explorer explorerFromLogin = new Explorer();
        explorerFromLogin.setEmail(email);

        try {
            explorerFromLogin.setPassword(password, password);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        DatabaseReference newExplorerReference = explorersReference.child(explorerFromLogin.firebaseEmail());

        newExplorerReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setResponse(dataSnapshot.exists());
                if (dataSnapshot.exists()) {
                    Explorer explorer = dataSnapshot.getValue(Explorer.class);
                    if (explorer.getPassword().equals(explorerFromLogin.getPassword())) {
                        saveFile(email, context);
                    }else{
                        setResponse(false);
                    }

                } else {
                    Log.d("DB", "Usuário não existe");
                }

                setAction(true);
            }
            @Override
            public void onCancelled(DatabaseError databaseError){
            }
        });

    }

    public boolean isAction() {
        return action;
    }

    public void setAction(boolean action) {
        this.action = action;
    }

    public boolean isResponse() {
        return response;
    }

    public void setResponse(boolean response) {
        this.response = response;
    }

    public boolean realizeLogin(String email, Context context) throws IOException {
        ExplorerDAO dataBase = new ExplorerDAO(context);
        Explorer explorer = dataBase.findExplorer(email);
        dataBase.close();

        if (explorer == null || explorer.getEmail() == null) {
            return false;
        }

        saveFile(explorer.getEmail(), context);
        return true;
    }

    private void saveFile(String email, Context context) {
        Log.d("Entra aqui?", "Sim");
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.apply();
    }

    public void deleteFile(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("email");
        editor.apply();
    }

    public void loadFile(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String email;

        Log.i("A preferencia existe", "Não");

        if ((email = sharedPreferences.getString("email", null)) != null) {
            Log.i("A preferencia existe", "Sim");
            Log.d("Email", email);
            ExplorerDAO dataBase = new ExplorerDAO(context);
            Explorer explorer = dataBase.findExplorer(email);
            dataBase.close();
            this.explorer = explorer;
        }

    }


    public void checkIfGoogleHasGooglePassword() {
        try {
            getExplorer().getPassword().equals(null);
        } catch (NullPointerException exception) {
            throw exception;
        }
    }

    public boolean checkIfUserHasGoogleNickname() {
        return getExplorer().getNickname().equals("Placeholder");
    }

    public Explorer getExplorer() {
        return explorer;
    }

    public boolean remainLogin() {
        return getExplorer().getEmail() != null;
    }
}