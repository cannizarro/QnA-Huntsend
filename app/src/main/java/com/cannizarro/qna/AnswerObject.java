package com.cannizarro.qna;

public class AnswerObject {

    private String ans,name;

    AnswerObject()
    {

    }

    AnswerObject(String answer, String name)
    {
        this.ans=answer;
        this.name=name;
    }

    public String getAns()
    {
        return ans;
    }
    public String getName()
    {
        return name;
    }

}
