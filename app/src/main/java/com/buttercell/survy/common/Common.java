package com.buttercell.survy.common;

import com.buttercell.survy.model.Question;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amush on 12-Feb-18.
 */

public class Common {
    public static String profileImg = "";
    public static String courseName = "";
    public static String courseKey = "";
    public static String survey_name="";

    public static String surveyKey = "";

    public static String surveyName = "";
    public static int questionNo;


    public static List<Question> singleChoiceList=new ArrayList<>();
    public static List<Question> multiChoiceList=new ArrayList<>();
    public static List<Question> openChoiceList=new ArrayList<>();
    public static List<Question> likertChoiceList=new ArrayList<>();


    public static ArrayList<String> surveyDates=new ArrayList<>();

}
