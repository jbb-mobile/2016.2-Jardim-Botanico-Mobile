package gov.jbb.jbbmobile.controller;

import android.content.Context;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import gov.jbb.jbbmobile.controller.BooksController;
import gov.jbb.jbbmobile.controller.LoginController;

import gov.jbb.jbbmobile.controller.RegisterElementController;
import gov.jbb.jbbmobile.dao.ElementDAO;
import gov.jbb.jbbmobile.dao.HelperDAO;
import gov.jbb.jbbmobile.model.Element;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RegisterElementControllerTest {
    private Context context;
    private RegisterElementController registerElementController;
    private Element element;
    private LoginController loginController;

    public RegisterElementControllerTest(){
        context = InstrumentationRegistry.getTargetContext();
        registerElementController = new RegisterElementController(loginController);


        HelperDAO helperDAO = new HelperDAO(context);
        helperDAO.onCreate(helperDAO.getWritableDatabase());

        BooksController booksController = new BooksController();
        booksController.insertBooks(context);

        ElementDAO elementDAO = new ElementDAO(this.context);
        Element elementInsert = new Element(18, 18, 200, "ponto_3", "Jacarandá do Cerrado", 3, "Planta do cerrado", -10,1,"Mensagem");
        elementDAO.insertElement(elementInsert);

        element = elementDAO.findElementFromElementTable(18);

        registerElementController.setElement(element);
    }

    @Test
    public void testIfValidStorageDirectoryCreatesImage() throws IOException{
        File storageDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        registerElementController.getElement().setUserImage("");
        File photoFile = registerElementController.createImageFile(storageDirectory);
        assertTrue(photoFile.getAbsolutePath().contains("USER_ELEMENT_ID_"));
    }

    @Test
    public void testIfPhotoAlreadyTakenReceivesSamePath() throws IOException{
        registerElementController.setCurrentPhotoPath("/image.jpg");
        File storageDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photoFile = registerElementController.createImageFile(storageDirectory);

        assertEquals(photoFile.getAbsolutePath(), "/image.jpg");
    }

}

















