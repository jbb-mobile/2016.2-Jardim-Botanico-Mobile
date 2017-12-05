package gov.jbb.missaonascente.controller;

import android.content.Context;
import android.content.res.Resources;
import android.database.SQLException;

import java.io.InputStream;
import java.util.Scanner;

import gov.jbb.missaonascente.dao.AchievementDAO;
import gov.jbb.missaonascente.dao.AlternativeDAO;
import gov.jbb.missaonascente.dao.ElementDAO;
import gov.jbb.missaonascente.dao.QuestionDAO;
import gov.jbb.missaonascente.model.Achievement;
import gov.jbb.missaonascente.model.Alternative;
import gov.jbb.missaonascente.model.Element;
import gov.jbb.missaonascente.model.Question;

public class DataController {

    private QuestionDAO questionDAO;
    private AlternativeDAO alternativeDAO;
    private AchievementDAO achievementDAO;
    private ElementDAO elementDAO;

    public boolean isLoaded(Context context){
        questionDAO = new QuestionDAO(context);

        try {
            if (questionDAO.countAllQuestions() > 0)
                return true;
        }catch (SQLException e){
            return false;
        }

        return false;
    }

    public boolean loadData (Context context){
        if (isLoaded(context))
            return false;

        alternativeDAO = new AlternativeDAO(context);
        achievementDAO = new AchievementDAO(context);
        elementDAO = new ElementDAO(context);

        getQuestionsAndAlternatives(context, context.getResources().getIdentifier("tabela_questoes", "raw", context.getPackageName()));

        getAchievements(context, context.getResources().getIdentifier("tabela_conquistas", "raw", context.getPackageName()));

        getElements(context, context.getResources().getIdentifier("tabela_elementos", "raw", context.getPackageName()));

        return true;
    }

    public void getQuestionsAndAlternatives(Context context, int fileId){

        InputStream tsv = readFile(context, fileId);

        Scanner scanner = new Scanner(tsv);
        scanner.useDelimiter("\t|\n");

        String[] alternativeLetters = new String[] {"a", "b", "c", "d"};

        while(scanner.hasNext()){
            Integer idQuestion = Integer.parseInt(scanner.next().trim());
            String description = scanner.next().trim();
            String a = scanner.next().trim();
            String b = scanner.next().trim();
            String c = scanner.next().trim();
            String d = scanner.next().trim();
            String correctAnswer = scanner.next().trim();

            String[] alternatives = new String[] {a, b, c, d};

            questionDAO.insertQuestion(new Question(idQuestion, description, correctAnswer, 4));

            for (int i = 1; i <= alternativeLetters.length; ++i) {
                Alternative alternative = new Alternative((i * idQuestion), alternativeLetters[i-1], alternatives[i-1], idQuestion);
                alternativeDAO.insertAlternative(alternative);
            }
        }
    }

    public void getAchievements(Context context, int fileId) {

        InputStream tsv = readFile(context, fileId);

        Scanner scanner = new Scanner(tsv);
        scanner.useDelimiter("\t|\n");

        while(scanner.hasNext()) {
            int idAchievement = Integer.parseInt(scanner.next().trim());
            String name = scanner.next().trim();
            String description = scanner.next().trim();
            int quantity = Integer.parseInt(scanner.next().trim());
            int keys = Integer.parseInt(scanner.next().trim());

            achievementDAO.insertAchievement(new Achievement(idAchievement, name, description, quantity, keys));
        }
    }

    public void getElements(Context context, int fileId) {

        InputStream tsv = readFile(context, fileId);

        Scanner scanner = new Scanner(tsv);
        scanner.useDelimiter("\t|\n");

        while(scanner.hasNext()) {
            int idElement = Integer.parseInt(scanner.next().trim());
            int energeticValue = Integer.parseInt(scanner.next().trim());
            int qrCodeNumber = Integer.parseInt(scanner.next().trim());
            int elementScore = Integer.parseInt(scanner.next().trim());
            String defaultImage	= scanner.next().trim();
            int idBook = Integer.parseInt(scanner.next().trim());
            String nameElement = scanner.next().trim();
            int x = Integer.parseInt(scanner.next().trim());
            int y = Integer.parseInt(scanner.next().trim());
            String textDescription = scanner.next().trim();
            int history = Integer.parseInt(scanner.next().trim());
            String historyMessage  = scanner.next().trim();

            elementDAO.insertElement(new Element(idElement, qrCodeNumber, elementScore, defaultImage, nameElement, idBook, textDescription, x, y, energeticValue, history, historyMessage));
        }
    }

    private static InputStream readFile(Context context, int fileId){
        Resources resources = context.getResources();
        InputStream file;
        file = resources.openRawResource(fileId);

        return file;
    }

}
