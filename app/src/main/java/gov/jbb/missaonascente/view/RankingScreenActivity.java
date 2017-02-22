package gov.jbb.missaonascente.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import gov.jbb.missaonascente.R;
import gov.jbb.missaonascente.controller.MainController;
import gov.jbb.missaonascente.controller.RankingController;

public class RankingScreenActivity extends AppCompatActivity {
    protected RankingController controller;
    protected MainController mainController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking_screen);
        setContentView(R.layout.ranking_adapter);
        controller = new RankingController();
        mainController = new MainController();
        controller.updateRanking(this);

        if(mainController.checkIfUserHasInternet(this)) {
            new RankingWebService().execute();
        }else{
            Toast.makeText(this,R.string.rankingNoInternet, Toast.LENGTH_SHORT).show();
        }
    }

    private class RankingWebService extends AsyncTask<Void, Void, Boolean>{
        @Override
        protected Boolean doInBackground(Void... params) {
            while (!controller.isAction());
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            RankingAdapter adapter = new RankingAdapter(RankingScreenActivity.this, controller.getExplorers());
            ListView rankingListView = (ListView)findViewById(R.id.ranking);
            rankingListView.setAdapter(adapter);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mainScreenIntent = new Intent(RankingScreenActivity.this, MainScreenActivity.class);
        RankingScreenActivity.this.startActivity(mainScreenIntent);
        finish();
    }
}