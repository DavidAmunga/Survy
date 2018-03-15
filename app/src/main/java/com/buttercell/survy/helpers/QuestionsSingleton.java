package com.buttercell.survy.helpers;

import com.buttercell.survy.model.Question;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amush on 17-Feb-18.
 */

public class QuestionsSingleton {
    private static QuestionsSingleton questionsSingleton = null;


    private QuestionsSingleton() {

    }

    public static QuestionsSingleton getInstance() {
        if (questionsSingleton == null) {
            questionsSingleton = new QuestionsSingleton();

        }
        return questionsSingleton;
    }

    public List<Question> singleChoiceList = new ArrayList<>();
    public List<Question> multiChoiceList = new ArrayList<>();
    public List<Question> openChoiceList = new ArrayList<>();


    public List<Question> getSingleChoiceList() {
        return singleChoiceList;
    }

    public void setSingleChoiceList(List<Question> singleChoiceList) {
        this.singleChoiceList = singleChoiceList;
    }

    public List<Question> getMultiChoiceList() {
        return multiChoiceList;
    }

    public void setMultiChoiceList(List<Question> multiChoiceList) {
        this.multiChoiceList = multiChoiceList;
    }

    public List<Question> getOpenChoiceList() {
        return openChoiceList;
    }

    public void setOpenChoiceList(List<Question> openChoiceList) {
        this.openChoiceList = openChoiceList;
    }
}
