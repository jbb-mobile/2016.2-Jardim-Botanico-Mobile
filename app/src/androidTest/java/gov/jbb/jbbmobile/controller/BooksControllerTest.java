package gov.jbb.jbbmobile.controller;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import gov.jbb.jbbmobile.controller.BooksController;
import gov.jbb.jbbmobile.controller.LoginController;
import gov.jbb.jbbmobile.controller.RegisterExplorerController;
import gov.jbb.jbbmobile.dao.ElementDAO;
import gov.jbb.jbbmobile.dao.ExplorerDAO;
import gov.jbb.jbbmobile.model.Explorer;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class BooksControllerTest {
    private BooksController booksController;
    private Context context;
    private RegisterExplorerController registerExplorerController;
    private LoginController loginController;
    private ExplorerDAO explorerDAO;
    private ElementDAO elementDAO;

    @Before
    public void setup(){
        Context context = InstrumentationRegistry.getTargetContext();
        this.context = context;
        booksController = new BooksController();
        booksController.insertBooks(this.context);

        explorerDAO = new ExplorerDAO(context);
        explorerDAO.onUpgrade(explorerDAO.getWritableDatabase(), 1,1);

        elementDAO = new ElementDAO(context);
        elementDAO.onUpgrade(elementDAO.getWritableDatabase(), 1,1);

        registerExplorerController = new RegisterExplorerController();
        loginController = new LoginController();
    }

    @Test
    public void testIfCurrentPeriodIsValid(){
        booksController.currentPeriod();
        assertEquals(3, booksController.getCurrentPeriod());
    }

    @Test
    public void testIfAccessBooksIsSuccessful() throws Exception{
        registerExplorerController.register("Roger", "rogerlenke@gmail.com", "000000", "000000", this.context);

        while(!registerExplorerController.isAction());
        loginController.doLogin("rogerlenke@gmail.com", "000000", this.context);

        while(!loginController.isAction());
        Thread.sleep(100);

        booksController.findExplorerLogged(context);

        String nameBook,nameCurrentBook;
        nameBook = "Águas";

        nameCurrentBook = booksController.getBook(0).getNameBook();
        new ExplorerDAO(context).deleteExplorer(new Explorer("Roger", "rogerlenke@gmail.com","000000", "000000"));

        assertEquals(nameBook,nameCurrentBook);
    }

    @Test
    public void testIfAccessBooksIsNotSuccessful() throws Exception{
        String nameBook,nameCurrentBook;
        nameBook = "Periodo";
        nameCurrentBook = booksController.getBook(0).getNameBook();
        assertNotEquals(nameBook,nameCurrentBook);
    }
}
