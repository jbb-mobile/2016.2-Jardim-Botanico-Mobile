package gov.jbb.missaonascente.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import gov.jbb.missaonascente.R;
import gov.jbb.missaonascente.controller.BooksController;
import gov.jbb.missaonascente.controller.EnergyController;
import gov.jbb.missaonascente.controller.HistoryController;
import gov.jbb.missaonascente.controller.LoginController;
import gov.jbb.missaonascente.controller.MainController;
import gov.jbb.missaonascente.controller.NotificationController;
import gov.jbb.missaonascente.controller.ProfessorController;
import gov.jbb.missaonascente.controller.QuestionController;
import gov.jbb.missaonascente.controller.RegisterElementController;
import gov.jbb.missaonascente.controller.RegisterExplorerController;
import gov.jbb.missaonascente.model.Achievement;
import gov.jbb.missaonascente.model.Element;


public class MainScreenActivity extends AppCompatActivity implements View.OnClickListener, QuestionFragment.OnFragmentInteractionListener {

    private LoginController loginController;
    private ImageButton menuMoreButton;
    private ImageButton almanacButton;
    private ImageButton historyButton;
    private ImageView readQrCodeButton;
    private PopupMenu popupMenu;
    private TextView scoreViewText;
    private MainController mainController;
    private QuestionController questionController;
    private RegisterElementFragment registerElementFragment;
    private QuestionFragment questionFragment;
    private ProfessorFragment professorFragment;
    private RelativeLayout relativeLayoutUp;
    private ProgressBar energyBar;
    private EnergyController energyController;
    private Thread energyThread;
    private HistoryController historyController;

    @SuppressWarnings("unused")
    private static final String TAG = "MainScreenActivity";

    private void createPopup(View v){
        Log.d(TAG, "Criando popup");
        Context layout = new ContextThemeWrapper(getContext(), R.style.popupMenuStyle);
        popupMenu = new PopupMenu(layout, v);
        popupMenu.inflate(R.menu.settings_menu);

        MainController mainController = new MainController();
        mainController.forceImageIcons(popupMenu);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.achievements:
                        //call achievement activity
                        Intent achievementIntent = new Intent(MainScreenActivity.this, AchievementsScreenActivity.class);
                        MainScreenActivity.this.startActivity(achievementIntent);
                        finish();
                        return true;
                    case R.id.rankingIcon:
                        Intent rankingIntent = new Intent(MainScreenActivity.this, RankingScreenActivity.class);
                        MainScreenActivity.this.startActivity(rankingIntent);
                        finish();
                        return true;
                    case R.id.preferenceIcon:
                        goToPreferenceScreen();
                        return true;
                    case R.id.tutorialIcon:
                        tutorialChoice();
                        return true;
                    case R.id.aboutIcon:
                        Intent aboutIntent = new Intent(MainScreenActivity.this, AboutActivity.class);
                        MainScreenActivity.this.startActivity(aboutIntent);
                        return true;
                    case R.id.mapIcon:
                        Intent mapIntent = new Intent(MainScreenActivity.this, MapActivity.class);
                        MainScreenActivity.this.startActivity(mapIntent);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void showPopup(View v) {
        if(popupMenu == null){
            createPopup(v);
        }
        popupMenu.show();
        Log.d(TAG, "Mostrou popup");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        if (savedInstanceState == null) {
            historyController = new HistoryController(this);
        }

        if(registerElementFragment == null){
            registerElementFragment = new RegisterElementFragment();
        }

        MainController mainController = new MainController();
        String APP_FIRST_TIME = "appFirstTime";
        mainController.downloadDataFirstTime(this, getSharedPreferences(APP_FIRST_TIME, MODE_PRIVATE));

        NotificationController notificationController = new NotificationController(this);
        notificationController.notificationByPeriod();

        initViews();


        this.loginController = new LoginController();
        this.loginController.loadFile(this.getApplicationContext());
        registerElementFragment.createRegisterElementController(this.loginController);

        this.energyController = new EnergyController(this.getApplicationContext());
        this.mainController = new MainController();
        this.questionController = new QuestionController();

        BooksController booksController = new BooksController(this);
        booksController.currentPeriod();

        RegisterExplorerController registerExplorerController = new RegisterExplorerController();
        SharedPreferences preferencesRegister = this.getSharedPreferences(registerExplorerController.FIRST_EXPLORER_LOGIN, 0);

        if(preferencesRegister.getBoolean(registerExplorerController.EXPLORER_REGISTER, false)){
            registerExplorerController.updateExplorerSharedPreference(this);
            tutorialChoice();
        }

        createPopup(findViewById(R.id.menuMoreButton));
    }

    @Override
    protected void onStart() {
        super.onStart();
        setScore();
    }

    @Override
    protected void onResume() {
        super.onResume();

        processQRCode();
        ArrayList<Achievement> newAchievements =
                mainController.checkForNewElementAchievements(this, historyController, loginController.getExplorer());

        for(Achievement newAchievement : newAchievements){
            createAchievementToast(newAchievement);
        }

        Log.d("Initial MainScreen", String.valueOf(energyController.getExplorer().getEnergy()));

        /*if(mainController.checkIfUserHasInternet(getContext()) )
            energyController.synchronizeEnergy(getContext());*/
        energyController.calculateElapsedEnergyTime(this);
        questionController.calculateElapsedQuestionTime(this);

        energyThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (energyController.getExplorer().getEnergy() < energyController.getMAX_ENERGY()) {
                        updateEnergyProgress();
                        sleep(60);
                        energyController.setExplorerEnergyInDataBase(energyController.getExplorer().getEnergy(), energyController.INCREMENT_FOR_TIME);
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                } finally {
                    // Highlight bar!
                    updateEnergyProgress();
                    energyController.setExplorerEnergyInDataBase(energyController.getExplorer().getEnergy(), energyController.INCREMENT_FOR_TIME);
                }
            }
        };
        energyThread.start();

        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        energyThread.interrupt();
        energyController.addEnergyTimeOnPreferencesTime();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        destroyViews();

        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "Entrou onClick");
        switch (v.getId()) {
            case R.id.almanacButton:
                goToAlmanacScreen();

                break;
            case R.id.menuMoreButton:
                Log.d(TAG, "Achou botao");
                showPopup(findViewById(R.id.menuMoreButton));

                break;
            case R.id.readQrCodeButton:

                if (mainController != null) {
                    mainController = null;
                }

                if (energyController.DECREASE_ENERGY <= energyController.getExplorer().getEnergy()) {
                    if(energyController.getExplorer().getEnergy() == energyController.getMAX_ENERGY()) {
                        questionController.setElapsedQuestionTime(questionController.MIN_TIME);
                    }
                    mainController = new MainController(MainScreenActivity.this);
                } else {

                        questionController.calculateElapsedQuestionTime(this);

                    if(questionController.getElapsedQuestionTime() >= questionController.MIN_TIME) {
                        callQuestion();
                        callProfessor(getString(R.string.withoutEnergyMessage),getString(R.string.withoutEnergyMessage2));
                        questionController.addQuestionTimeOnPreferencesTime(this);
                    }else {
                        callProfessor("Aguarde " + questionController.getRemainingTimeInMinutes() + " minutos para responder!");
                    }
                }

                break;
            case R.id.historyButton:
                Intent mapIntent = new Intent(MainScreenActivity.this, MapActivity.class);
                MainScreenActivity.this.startActivity(mapIntent);
                break;

        }
    }

    public void callQuestion(){
        relativeLayoutUp = (RelativeLayout) findViewById(R.id.mainScreenUp);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        questionFragment = new QuestionFragment();
        ft.add(R.id.question_fragment, questionFragment, "QuestionFragment");
        ft.commitNow();
        findViewById(R.id.question_fragment).setVisibility(View.VISIBLE);
        findViewById(R.id.question_fragment).requestLayout();

        menuMoreButton.setClickable(false);
        almanacButton.setClickable(false);
        readQrCodeButton.setClickable(false);

        relativeLayoutUp.setBackgroundColor(0x4D000000);
    }

    protected void processQRCode(){
        int elementEnergy;

        if(mainController == null){
            mainController = new MainController(this);
        }

        String code = mainController.getCode();
        if(code != null) code = code.replaceAll("\\D+", "");

        if(registerElementFragment != null){
            RegisterElementController registerElementController = registerElementFragment.getController();
            if(code != null) {
                mainController.setCode(null);
                Element element;
                try {
                    registerElementController.associateElementByQrCode(code, getContext());
                    elementEnergy = registerElementController.getElement().getEnergeticValue();
                    energyController.checkEnergeticValueElement(elementEnergy);
                    modifyEnergy();

                    element = registerElementController.getElement();
                    setFragmentComponents(element, true);

                } catch (SQLException exception) {
                    element = registerElementController.getElement();
                    elementEnergy = registerElementController.getElement().getEnergeticValue();
                    energyController.calculateElapsedElementTime(this, elementEnergy);
                    modifyEnergy();
                    //Toast.makeText(this,"Elemento já registrado!", Toast.LENGTH_SHORT).show();
                    String existedElement = getString(R.string.existedElement);
                    if (elementEnergy < 0) {
                        callProfessor(existedElement);
                    }
                    setFragmentComponents(element, false);
                } catch (IllegalArgumentException exception) {
                    //Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    String aux = exception.getMessage();
                    exception.printStackTrace();
                    callProfessor(aux);
                    return;
                }
            }

        }
        mainController.setCode(null);
    }

    public void createAchievementToast(Achievement achievement){
        Log.d("TOAST", "Toast para newAchievement");
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.new_achievement_layout,
                (ViewGroup) findViewById(R.id.new_achievement_relative_layout));

        Integer imageId = AchievementAdapter.getIds(achievement.getKeys()).get(1);

        ((TextView) view.findViewById(R.id.new_medal_text)).
                setText(getString(R.string.new_unlocked_achievement) + achievement.getNameAchievement());
        ((ImageView) view.findViewById(R.id.new_medal_image)).setImageResource(imageId);
        Toast toast = new Toast(getContext());
        toast.setView(view);
        toast.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            mainController.setCode(result.getContents());
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void callProfessor(String message) {
        ProfessorController professorController = new ProfessorController();
        BooksController book = new BooksController();
        ArrayList<String> s = new ArrayList<>();
        s.add(message);

        book.currentPeriod();
        int period = book.getCurrentPeriod();

        switch (period) {
            case 1:
                Drawable drawable1 = ResourcesCompat.getDrawable(getResources(), R.drawable.professor_1, null);
                professorFragment = professorController.createProfessorFragment(s, drawable1);
                break;
            case 2:
                Drawable drawable2 = ResourcesCompat.getDrawable(getResources(), R.drawable.professor_2, null);
                professorFragment = professorController.createProfessorFragment(s, drawable2);
                break;
            case 3:
                Drawable drawable3 = ResourcesCompat.getDrawable(getResources(), R.drawable.professor_3, null);
                professorFragment = professorController.createProfessorFragment(s, drawable3);
                break;
            default:
                break;
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.professor_fragment, professorFragment, "ProfessorFragment");
        fragmentTransaction.commitNow();
    }

    public void callProfessor(String message, String message2) {
        ProfessorController professorController = new ProfessorController();
        BooksController book = new BooksController();
        ArrayList<String> s = new ArrayList<>();
        s.add(message);
        s.add(message2);

        book.currentPeriod();
        int period = book.getCurrentPeriod();

        switch (period) {
            case 1:
                Drawable drawable1 = ResourcesCompat.getDrawable(getResources(), R.drawable.professor_1, null);
                professorFragment = professorController.createProfessorFragment(s, drawable1);
                break;
            case 2:
                Drawable drawable2 = ResourcesCompat.getDrawable(getResources(), R.drawable.professor_2, null);
                professorFragment = professorController.createProfessorFragment(s, drawable2);
                break;
            case 3:
                Drawable drawable3 = ResourcesCompat.getDrawable(getResources(), R.drawable.professor_3, null);
                professorFragment = professorController.createProfessorFragment(s, drawable3);
                break;
            default:
                break;
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.professor_fragment, professorFragment, "ProfessorFragment");
        fragmentTransaction.commitNow();
    }

    private void initViews() {
        this.menuMoreButton = (ImageButton) findViewById(R.id.menuMoreButton);
        this.almanacButton = (ImageButton) findViewById(R.id.almanacButton);
        this.historyButton = (ImageButton) findViewById(R.id.historyButton);
        this.readQrCodeButton = (ImageView) findViewById(R.id.readQrCodeButton);
        this.scoreViewText = (TextView) findViewById(R.id.explorerScore);
        this.energyBar = (ProgressBar) findViewById(R.id.energyBar);

        this.menuMoreButton.setOnClickListener(this);
        this.almanacButton.setOnClickListener(this);
        this.readQrCodeButton.setOnClickListener(this);
        this.historyButton.setOnClickListener(this);
    }

    private void destroyViews(){
        this.menuMoreButton = null;
        this.almanacButton = null;
        this.historyButton = null;
        this.readQrCodeButton = null;
        this.scoreViewText = null;
        this.energyBar = null;
        this.popupMenu = null;
    }

    public void setScore() {
        //Change score leaft color if explorer has any story element
        historyController = new HistoryController(this);
        int history = historyController.getCurrentElement();
        if (history != 1){
            //Show history button
            historyButton.setVisibility(View.VISIBLE);
        }
        scoreViewText.setText("");
        scoreViewText.setText(String.valueOf(loginController.getExplorer().getScore()));
    }


    private void goToPreferenceScreen() {
        Intent registerIntent = new Intent(MainScreenActivity.this, PreferenceScreenActivity.class);
        MainScreenActivity.this.startActivity(registerIntent);
        finish();
    }

    private void goToAlmanacScreen() {
        Intent almanacIntent = new Intent(MainScreenActivity.this, AlmanacScreenActivity.class);
        MainScreenActivity.this.startActivity(almanacIntent);
        finish();
    }

    private Context getContext() {
        return this;
    }

    public void updateEnergyProgress() {
        if (energyBar != null) {
            int progress = energyController.energyProgress(energyBar.getMax());
            energyBar.setProgress(progress);
        }
    }

    public void modifyEnergy() {
        energyThread.interrupt();
        RegisterElementController registerElementController = registerElementFragment.getController();

        Integer elementEnergy = registerElementController.getElement().getEnergeticValue();
        Integer elementsEnergyType = energyController.checkElementsEnergyType(elementEnergy);

        if (elementsEnergyType == energyController.JUST_DECREASE_ENERGY) {
            String energyNegativeMessenger = Integer.toString(elementEnergy) + " de energia!";
            Toast.makeText(this, energyNegativeMessenger, Toast.LENGTH_SHORT).show();
        } else if (elementsEnergyType == energyController.JUST_INCREASE_ENERGY) {
            String energyPositiveMessenger = "+" + Integer.toString(elementEnergy)+ " de energia!";
            Toast.makeText(this, energyPositiveMessenger, Toast.LENGTH_SHORT).show();
        } else {
            String message = getString(R.string.existedElement);
            String num = energyController.getRemainingTimeInMinutes();
            String message2 = "Aguarde " + num + " minutos para ganhar energia novamente com este elemento";
            callProfessor(message , message2);
        }

        updateEnergyProgress();

        //energyController.sendEnergy(this);
    }

    public void questionEnergy(){
        int explorerEnergy;
        int explorerEnergyText;

        explorerEnergy = energyController.getExplorer().getEnergy();

        int QUESTION_ENERGY = 20;
        explorerEnergyText = explorerEnergy + QUESTION_ENERGY;
        energyController.getExplorer().setEnergy(explorerEnergyText);

         Log.i("QUEST ENERGY explorer",""+explorerEnergy);
         Log.i("QUEST ENERGY explorer2",""+energyController.getExplorer().getEnergy());
         Log.i("QUEST ENERGY exp+quest",""+explorerEnergyText);
         Log.i("QUEST ENERGY EXP",energyController.getExplorer().getEmail());

        updateEnergyProgress();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    public void onBackPressed() {
        if (questionFragment != null && questionFragment.isVisible()) {
            this.getSupportFragmentManager().beginTransaction().remove(questionFragment).commit();
            relativeLayoutUp.setBackgroundColor(0x00000000);
            menuMoreButton.setClickable(true);
            almanacButton.setClickable(true);
            readQrCodeButton.setClickable(true);
        } else {
            super.onBackPressed();
        }
    }

    public Element verifyHistoryElement(Element element){
        historyController.getElementsHistory();

        historyController.loadSave();
        boolean sequence = historyController.sequenceElement(element.getHistory(), loginController.getExplorer());
        if(sequence){
            element.setElementScore(element.getElementScore()*2);
            //Toast.makeText(this,element.getHistoryMessage(), Toast.LENGTH_SHORT).show();
            callProfessor(element.getHistoryMessage());
        }

        changeColorElementHistory(element,sequence);

        return  element;
    }

    private void changeColorElementHistory(Element element, boolean sequence){
        int colorBackground = R.drawable.background_catched_element;
        int colorButton = R.color.colorGreen;
        if(sequence){
            colorBackground = R.drawable.background_catched_element_history;
            colorButton = R.color.colorPrimaryText;

            //Toast.makeText(this,element.getHistoryMessage(), Toast.LENGTH_SHORT).show();
            callProfessor(element.getHistoryMessage());
        }

        findViewById(R.id.fragment_element).setBackground(ContextCompat.getDrawable(this, colorBackground));
        findViewById(R.id.name_text).getBackground().setColorFilter(ContextCompat.getColor(this, colorButton), PorterDuff.Mode.SRC_ATOP);
        findViewById(R.id.close_button).getBackground().setColorFilter(ContextCompat.getColor(this, colorButton), PorterDuff.Mode.SRC_ATOP);
        findViewById(R.id.camera_button).getBackground().setColorFilter(ContextCompat.getColor(this, colorButton), PorterDuff.Mode.SRC_ATOP);
        findViewById(R.id.show_element_button).getBackground().setColorFilter(ContextCompat.getColor(this, colorButton), PorterDuff.Mode.SRC_ATOP);
        RoundedImageView imageView = (RoundedImageView)findViewById(R.id.element_image);
        imageView.setBorderColor(ContextCompat.getColor(this,colorButton));
    }

    private void addRegisterFragment(){
        findViewById(R.id.readQrCodeButton).setVisibility(View.INVISIBLE);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.register_fragment, registerElementFragment).commitNow();
        findViewById(R.id.register_fragment).requestLayout();
    }

    private void setFragmentComponents(Element element, boolean showScoreInFirstRegister){
        addRegisterFragment();
        element = verifyHistoryElement(element);
        registerElementFragment.showElement(element, showScoreInFirstRegister);
        setScore();
    }

    private void tutorialChoice() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.tutorial);
        alert.setMessage(R.string.textForTutorialChoice);
        alert.setPositiveButton(R.string.yesMessage, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent tutorialIntent = new Intent(MainScreenActivity.this, TutorialScreenActivity.class);
                MainScreenActivity.this.startActivity(tutorialIntent);
                finish();
            }
        });

        alert.setNegativeButton(R.string.noMessage, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        alert.show();
    }
}