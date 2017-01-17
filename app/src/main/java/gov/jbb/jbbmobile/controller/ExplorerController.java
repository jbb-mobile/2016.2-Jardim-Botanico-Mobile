package gov.jbb.jbbmobile.controller;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;;
import android.util.Log;

import java.util.List;

import gov.jbb.jbbmobile.dao.ElementDAO;
import gov.jbb.jbbmobile.dao.ElementExplorerRequest;
import gov.jbb.jbbmobile.dao.UpdateScoreRequest;
import gov.jbb.jbbmobile.model.Element;

public class ExplorerController {
    private boolean action = false;
    private boolean response;

    public ExplorerController(){
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

    public boolean updateExplorerScore( final Context preferenceContext,int score,String email) {
        Log.i("****Explorer: ",""+score);
        UpdateScoreRequest updateScoreRequest = new UpdateScoreRequest(score,email);
        updateScoreRequest.request(preferenceContext, new UpdateScoreRequest.Callback() {
            @Override
            public void callbackResponse(boolean response) {
                setResponse(response);
                setAction(true);
            }
        });

        return true;
    }

    public boolean insertExplorerElement(final Context preferenceContext, String email, int idElement, String userImage, String catchDate) {
        try{
            setAction(false);
            ElementExplorerRequest elementExplorerRequest = new ElementExplorerRequest(email,idElement,userImage,catchDate);
            elementExplorerRequest.requestUpdateElements(preferenceContext, new ElementExplorerRequest.Callback() {
                @Override
                public void callbackResponse(boolean response) {
                    setResponse(true);
                    setAction(true);
                }
                MainController mainController = new MainController();

                @Override
                public void callbackResponse(List<Element> elements) {}
            });
        }catch(SQLiteConstraintException exception){
            throw exception;
        }

        return true;
    }

    public void updateElementExplorerTable(final Context context, final String email){

        setAction(false);

        final ElementExplorerRequest elementExplorerRequest = new ElementExplorerRequest(email);
        elementExplorerRequest.requestRetrieveElements(context, new ElementExplorerRequest.Callback() {
            @Override
            public void callbackResponse(boolean response) {
            }

            @Override
            public void callbackResponse(List<Element> elements) {
                ElementDAO database = new ElementDAO(context);
                database.deleteAllElementsFromElementExplorer(database.getWritableDatabase());
                for(int i = 0; i < elements.size(); i++){
                    Element element = elements.get(i);
                    int resposta = database.insertElementExplorer(element.getIdElement(), email,
                            element.getCatchDate(), "");
                    Log.d("Resposta", String.valueOf(resposta));
                }

                setAction(true);
            }
        });
    }

    public void sendElementsExplorerTable(final Context context, final String email){
        ElementExplorerRequest elementExplorerRequest = new ElementExplorerRequest(email);
        elementExplorerRequest.sendElementsExplorer(context);
    }
}
