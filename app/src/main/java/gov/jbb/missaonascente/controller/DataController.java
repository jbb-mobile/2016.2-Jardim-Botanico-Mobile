package gov.jbb.missaonascente.controller;

import android.content.Context;
import android.content.res.Resources;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import gov.jbb.missaonascente.dao.AlternativeDAO;
import gov.jbb.missaonascente.dao.QuestionDAO;
import gov.jbb.missaonascente.model.Alternative;
import gov.jbb.missaonascente.model.Question;

public class DataController {

    private QuestionDAO questionDAO;
    private AlternativeDAO alternativeDAO;

    public boolean isLoaded(Context context){
        questionDAO = new QuestionDAO(context);

        if (questionDAO.countAllQuestions() > 0)
            return true;
        else
            return false;
    }

    public boolean loadData (Context context){
        if (!isLoaded(context))
            return false;

        alternativeDAO = new AlternativeDAO(context);

        getQuestions(context, context.getResources().getIdentifier("tabela_questoes", "raw", context.getPackageName()));

        return true;
    }

    public void getQuestions(Context context, int fileId){

        InputStream tsv = readFile(context, fileId);

        ArrayList<Question> questions = new ArrayList<>();
        Scanner scanner = new Scanner(tsv);
        scanner.useDelimiter("\t|\n");

        String[] alternativeLetters = new String[] {"a", "b", "c", "d"};

        while(scanner.hasNext()){
            String idQuestion = scanner.next().trim();
            String description = scanner.next().trim();
            String a = scanner.next().trim();
            String b = scanner.next().trim();
            String c = scanner.next().trim();
            String d = scanner.next().trim();
            String correctAnswer = scanner.next().trim();

            String[] alternatives = new String[] {a, b, c, d};

            questionDAO.insertQuestion(new Question(Integer.parseInt(idQuestion), description, correctAnswer, 4));

            for (int i = 0; i < alternativeLetters.length; ++i)
                alternativeDAO.insertAlternative(new Alternative((i * Integer.parseInt(idQuestion)), alternativeLetters[i], alternatives[i], Integer.parseInt(idQuestion)));
        }
    }

    private static InputStream readFile(Context context, int fileId){
        Resources resources = context.getResources();
        InputStream file;
        file = resources.openRawResource(fileId);

        return file;
    }

}
