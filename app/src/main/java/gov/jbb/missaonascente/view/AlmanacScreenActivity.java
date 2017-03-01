package gov.jbb.missaonascente.view;

import android.content.Intent;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import gov.jbb.missaonascente.R;
import gov.jbb.missaonascente.controller.BooksController;

public class AlmanacScreenActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageButton orangeBook;
    private ImageButton greenBook;
    private ImageButton blueBook;
    private GridView gridView;
    private BooksController booksController;
    private TextView zeroElements;

    protected void onCreate(Bundle savedInstanceState) {
        int currentPeriod;

        booksController = new BooksController(this);
        booksController.currentPeriod();

        currentPeriod = booksController.getCurrentPeriod();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_almanac_screen);
        initViews();

        CustomAdapter adapter;

        switch (currentPeriod){
            case 1:
                adapter = new CustomAdapter(this,booksController, 0);
                gridView.setAdapter(adapter);
                zeroElements.setText(adapter.getCount() == 0 ? getResources().getString(R.string.zero_elements) : "");

                setDefaultBooks();
                orangeBook.setImageResource(R.drawable.book_icon_open_orange);
                orangeBook.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_grid_background, null));
                break;
            case 2:
                adapter = new CustomAdapter(this,booksController, 1);
                gridView.setAdapter(adapter);
                zeroElements.setText(adapter.getCount() == 0 ? getResources().getString(R.string.zero_elements) : "");

                setDefaultBooks();
                greenBook.setImageResource(R.drawable.book_icon_open_green);
                greenBook.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_grid_background, null));

                break;
            case 3:
                adapter = new CustomAdapter(this,booksController, 2);
                gridView.setAdapter(adapter);
                zeroElements.setText(adapter.getCount() == 0 ? getResources().getString(R.string.zero_elements) : "");

                setDefaultBooks();
                blueBook.setImageResource(R.drawable.book_icon_open_blue);
                blueBook.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_grid_background, null));
                break;
        }
    }

    private void initViews(){
        gridView = (GridView) findViewById(R.id.gridView);

        zeroElements = (TextView) findViewById(R.id.zeroElements);

        orangeBook=(ImageButton) findViewById(R.id.orangeBook);
        orangeBook.setOnClickListener(this);

        greenBook=(ImageButton) findViewById(R.id.greenBook);
        greenBook.setOnClickListener(this);

        blueBook=(ImageButton) findViewById(R.id.blueBook);
        blueBook.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mainScreenIntent = new Intent(AlmanacScreenActivity.this, MainScreenActivity.class);
        AlmanacScreenActivity.this.startActivity(mainScreenIntent);
        finish();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        gridView = null;
        orangeBook = null;
        blueBook = null;
        greenBook = null;
        zeroElements = null;
    }

    public void onClick(View v) {
        CustomAdapter adapter;
        switch (v.getId()) {
            case R.id.orangeBook:
                adapter = new CustomAdapter(this,booksController, 0);
                gridView.setAdapter(adapter);
                zeroElements.setText(adapter.getCount() == 0 ? getResources().getString(R.string.zero_elements) : "");

                setDefaultBooks();
                orangeBook.setImageResource(R.drawable.book_icon_open_orange);
                orangeBook.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_grid_background, null));
                break;
            case R.id.greenBook:
                adapter = new CustomAdapter(this,booksController, 1);
                gridView.setAdapter(adapter);
                zeroElements.setText(adapter.getCount() == 0 ? getResources().getString(R.string.zero_elements) : "");

                setDefaultBooks();
                greenBook.setImageResource(R.drawable.book_icon_open_green);
                greenBook.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_grid_background, null));
                break;
            case R.id.blueBook:
                adapter = new CustomAdapter(this,booksController, 2);
                gridView.setAdapter(adapter);
                zeroElements.setText(adapter.getCount() == 0 ? getResources().getString(R.string.zero_elements) : "");

                setDefaultBooks();
                blueBook.setImageResource(R.drawable.book_icon_open_blue);
                blueBook.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_grid_background, null));
                break;
        }
    }

    public void setDefaultBooks(){
        blueBook.setImageResource(R.drawable.book_blue);
        blueBook.setBackground(ResourcesCompat.getDrawable(getResources(), R.color.colorTransparent, null));

        orangeBook.setImageResource(R.drawable.book_orange);
        orangeBook.setBackground(ResourcesCompat.getDrawable(getResources(), R.color.colorTransparent, null));

        greenBook.setImageResource(R.drawable.book_green);
        greenBook.setBackground(ResourcesCompat.getDrawable(getResources(), R.color.colorTransparent, null));
    }
}
