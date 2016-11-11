package com.example.jbbmobile.controller;

import android.content.Context;
import android.database.SQLException;
import android.util.Log;

import com.example.jbbmobile.dao.ElementDAO;
import com.example.jbbmobile.dao.ExplorerDAO;
import com.example.jbbmobile.model.Element;
import com.example.jbbmobile.model.Explorer;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RegisterElementController {
    private String currentPhotoPath = EMPTY_STRING;
    private LoginController loginController;
    private Element element;
    private ElementDAO elementDAO;
    private ExplorerDAO explorerDAO;
    private String email;
    private String date;

    private static final String EMPTY_STRING = "";

    private final String TAG = "RegisterElement";

    public RegisterElementController(LoginController loginController){
        this.loginController = loginController;
    }

    public void associateElementByQrCode(String code, Context context) throws SQLException,IllegalArgumentException{
        int currentBookPeriod;
        int currentBook;
        int newScore;
        int qrCodeNumber;

        qrCodeNumber = Integer.parseInt(code);

        elementDAO = new ElementDAO(context);
        explorerDAO = new ExplorerDAO(context);
        element = elementDAO.findElementByQrCode(qrCodeNumber);

        String catchCurrentDate = getCurrentDate();
        currentBook = element.getIdBook();

        Explorer explorer = loginController.getExplorer();
        email = explorer.getEmail();
        date = catchCurrentDate;

        BooksController booksController = new BooksController();
        booksController.currentPeriod();
        currentBookPeriod = booksController.getCurrentPeriod();

        if(currentBook == currentBookPeriod ) {
            try {
                elementDAO.insertElementExplorer(email, catchCurrentDate, qrCodeNumber, EMPTY_STRING);
                newScore = element.getElementScore();

                loginController.loadFile(context);

                Log.i("Old ","Score: "+loginController.getExplorer().getScore());

                loginController.getExplorer().updateScore(newScore);
                explorerDAO.updateExplorer(loginController.getExplorer());

                ExplorerController explorerController = new ExplorerController();

                MainController mainController = new MainController();
                if(mainController.checkIfUserHasInternet(context)) {
                    explorerController.insertExplorerElement(context,
                            loginController.getExplorer().getEmail(),
                            element.getIdElement(),
                            element.getUserImage(),
                            date);

                    explorerController.updateExplorerScore(context,
                            loginController.getExplorer().getScore(),
                            loginController.getExplorer().getEmail());
                }
            }catch (SQLException sqlException){
                currentPhotoPath = findImagePathByAssociation();
                throw sqlException;
            }
        }else{
            throw new IllegalArgumentException("Periodo Inválido");
        }
        Log.i("New ","Score: "+loginController.getExplorer().getScore());
    }

    public File createImageFile(File storageDirectory) throws IOException {
        File image;

        if(currentPhotoPath.equals(EMPTY_STRING)){
            String imageFileName = "USER_ELEMENT_ID_" + Integer.toString(element.getIdElement()) + "_";
            image = File.createTempFile(imageFileName, ".jpg", storageDirectory);

            Log.d(TAG, "[" + imageFileName+ "]" + "[" + Integer.toString(element.getIdElement()) + "]");
        }else{
            image = new File(currentPhotoPath);
        }

        currentPhotoPath = image.getAbsolutePath();

        Log.d(TAG, "[" + currentPhotoPath + "]");

        return image;
    }

    public String getCurrentPhotoPath() {
        return currentPhotoPath;
    }

    public void setCurrentPhotoPath(String currentPhotoPath) {
        this.currentPhotoPath = currentPhotoPath;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public void updateElementImage() {
        int result = elementDAO.updateElementExplorer(element.getIdElement(), email, date, currentPhotoPath);
    }


    private String getCurrentDate(){
        DateFormat formatBR = DateFormat.getDateInstance(DateFormat.LONG, new Locale("pt", "BR"));
        Date today = Calendar.getInstance().getTime();

        return formatBR.format(today);
    }

    public String findImagePathByAssociation(){
        Element element = null;

        try {
            element = elementDAO.findElementFromRelationTable(this.element.getIdElement(), email);
        }catch(IllegalArgumentException ex){
            ex.printStackTrace();
        }

        return (element == null) ? "" : element.getUserImage();
    }
}