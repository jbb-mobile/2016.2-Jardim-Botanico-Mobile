package gov.jbb.jbbmobile.controller;

import android.content.Context;

import gov.jbb.jbbmobile.dao.RankingRequest;
import gov.jbb.jbbmobile.model.Explorer;

import java.util.List;

public class RankingController {

    private List<Explorer> explorers;
    private boolean action = false;

    public List<Explorer> updateRanking(Context context){
        LoginController explorer = new LoginController();
        explorer.loadFile(context);

        RankingRequest rankingRequest = new RankingRequest(explorer.getExplorer().getNickname());
        rankingRequest.request(context, new RankingRequest.Callback() {
            @Override
            public void callbackResponse(List<Explorer> explorers) {
                setExplorers(explorers);
                setAction(true);
            }
        });

        return explorers;
    }

    public List<Explorer> getExplorers() {
        return explorers;
    }

    public void setExplorers(List<Explorer> explorers) {
        this.explorers = explorers;
    }

    public boolean isAction() {
        return action;
    }

    public void setAction(boolean action) {
        this.action = action;
    }

}
