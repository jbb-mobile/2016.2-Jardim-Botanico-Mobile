package gov.jbb.jbbmobile.controller;

import android.graphics.drawable.Drawable;

import gov.jbb.jbbmobile.view.ProfessorFragment;

import java.util.ArrayList;

public class ProfessorController {
    ProfessorFragment professorFragment;

    public ProfessorFragment createProfessorFragment(ArrayList<String> dialogs, ArrayList<Drawable> drawables){
        if(dialogs.size() == 0) {
            throw new IllegalArgumentException("Dialogs Array can't be empty");
        }
        if(drawables.size() == 0){
            throw new IllegalArgumentException("Drawable Array can't be empty");
        }

        professorFragment = new ProfessorFragment();
        professorFragment.setDialogs(dialogs);
        professorFragment.setDrawables(drawables);

        return professorFragment;
    }

    public ProfessorFragment createProfessorFragment(ArrayList<String> dialogs, Drawable drawable){
        ArrayList<Drawable> drawables = new ArrayList<>();
        drawables.add(drawable);
        ProfessorFragment professorFragment = createProfessorFragment(dialogs, drawables);

        return professorFragment;
    }
}
