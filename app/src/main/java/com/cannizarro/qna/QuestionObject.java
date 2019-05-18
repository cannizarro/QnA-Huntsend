package com.cannizarro.qna;

import java.util.List;

public class QuestionObject {

    private String ques, name;

    QuestionObject()
    {
    }

    QuestionObject(String title, String name)
    {
        this.ques=title;
        this.name=name;
    }

    public String getQues()
    {
        return ques;
    }
    public String getName()
    {
        return name;
    }

}
