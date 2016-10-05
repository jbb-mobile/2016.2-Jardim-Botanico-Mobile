package com.example.jbbmobile.controller;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.jbbmobile.dao.ExplorerDAO;
import com.example.jbbmobile.model.Explorer;
import java.io.IOException;
import static android.content.Context.MODE_PRIVATE;

public class LoginController {
    private Explorer explorer;
    private Context context;
    private ExplorerDAO explorerDAO;

    private static final String PREF_NAME = "MainActivityPreferences";

    public LoginController(Context context){
        explorer = new Explorer();
        this.context = context;
        this.explorerDAO = new ExplorerDAO(context);
    }

    public void tablesCreate(){
        explorerDAO.createExplorerTable(explorerDAO.getWritableDatabase());
    }

    //LoginController to normal register Accounts
    public boolean realizeLogin(String email, String password, Context context) {
        Explorer explorer = explorerDAO.findExplorerLogin(new Explorer(email,password));
        explorerDAO.close();

        if (explorer == null || explorer.getEmail() == null || explorer.getPassword() == null) {
            return false;
        }
        saveFile(explorer.getEmail(),context);
        return true;
    }

    //LoginController to Google Accounts
    public boolean realizeLogin(String email, Context context) throws IOException{
        Explorer explorer = explorerDAO.findExplorer(new Explorer(email));
        explorerDAO.close();

        if (explorer == null || explorer.getEmail() == null) {
            return false;
        }

        saveFile(explorer.getEmail(),context);
        return true;
    }

    private void saveFile(String email, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.apply();
    }

    public void deleteFile(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("email");
        editor.apply();
    }

    public void loadFile(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String email;

        if ((email =  sharedPreferences.getString("email",null))!= null){
            Explorer explorer = explorerDAO.findExplorer(new Explorer(email));
            explorerDAO.close();
            this.explorer = new Explorer(explorer.getEmail(),explorer.getNickname(),explorer.getPassword());
        }
    }

    public void checkifGoogleHasGooglePassword(){
        getExplorer().getPassword().equals(null);
    }

    public boolean checkIfUserHasGoogleNickname(){
        return getExplorer().getNickname().equals("Placeholder");
    }

    public Explorer getExplorer(){
        return explorer;
    }

    public boolean remainLogin(){
        return getExplorer().getEmail() != null;
    }
}