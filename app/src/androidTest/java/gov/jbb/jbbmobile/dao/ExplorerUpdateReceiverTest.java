package gov.jbb.jbbmobile.dao;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;

import gov.jbb.jbbmobile.controller.ExplorerController;
import gov.jbb.jbbmobile.controller.ExplorerUpdateReceiver;
import gov.jbb.jbbmobile.controller.LoginController;
import gov.jbb.jbbmobile.dao.ElementDAO;
import gov.jbb.jbbmobile.dao.ExplorerDAO;
import gov.jbb.jbbmobile.model.Element;
import gov.jbb.jbbmobile.model.Explorer;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ExplorerUpdateReceiverTest {

    private ElementDAO elementDAO;
    private ExplorerController explorerController;
    private LoginController loginController;
    private Context context;
    private Intent intent;
    private ExplorerDAO explorerDAO;
    private Explorer explorer;
    private final String NICKNAME = "RecUser";
    private final String EMAIL = "test3@test3.com";
    private final String PASSWORD = "000000";
    private final String DATE = "24 de outubro de 2016";

    @Before
    public void setup() throws Exception{
        context = InstrumentationRegistry.getTargetContext();
        explorerController = new ExplorerController();
        loginController = new LoginController();
        elementDAO = new ElementDAO(context);
        explorerDAO = new ExplorerDAO(context);
        explorer = new Explorer(NICKNAME, EMAIL, PASSWORD, PASSWORD);

        explorerDAO.insertExplorer(explorer);
        elementDAO.onUpgrade(elementDAO.getWritableDatabase(), 1,1);
        loginController.realizeLogin(EMAIL, context);
    }

    @Test
    public void testIfUserDataIsSentToOnlineDatabaseInBackground() throws Exception{
        Element element = new Element(1, 1, 100, "ponto_2", "Pau-Santo", 1, "",15.123f,14.123f,10, 1, "Message");
        elementDAO.insertElementExplorer(element.getIdElement(), EMAIL, DATE,
                element.getDefaultImage());
        intent = new Intent(context, ExplorerUpdateReceiver.class);
        context.sendBroadcast(intent);
        Thread.sleep(3000);
        elementDAO.onUpgrade(elementDAO.getWritableDatabase(), 1,1);
        elementDAO.insertElement(element);
        explorerController.updateElementExplorerTable(context, EMAIL);
        Thread.sleep(3000);
        element = elementDAO.findElementFromRelationTable(element.getIdElement(), EMAIL);
        assertEquals(1, element.getIdElement());
    }
}
