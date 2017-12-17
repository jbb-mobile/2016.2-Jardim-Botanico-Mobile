package gov.jbb.jbbmobile.controller;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import gov.jbb.jbbmobile.dao.ExplorerDAO;
import gov.jbb.jbbmobile.model.Explorer;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class PreferenceControllerTest {
    private PreferenceController controller;
    private Context context;
    private ExplorerDAO explorerDAO;

    @Before
    public void setup() throws Exception{
        Context context = InstrumentationRegistry.getTargetContext();
        this.context = context;
        controller = new PreferenceController();
        explorerDAO = new ExplorerDAO(context);
        explorerDAO.onUpgrade(explorerDAO.getReadableDatabase(),1,1);
    }

    @Test
    public void testIfNicknameWasAltered() throws Exception{
        RegisterExplorerController registerExplorerController = new RegisterExplorerController();
        registerExplorerController.register("testUser4", "testUser4@user.com", "000000", "000000", context);
        while(!registerExplorerController.isAction());
        controller.updateNickname("Resu","testUser4@user.com", context);
        new ExplorerDAO(context).deleteExplorer(new Explorer("testUser4", "testUser4@user.com", "000000", "000000"));
        assertEquals(false, controller.isResponse());
    }

}
