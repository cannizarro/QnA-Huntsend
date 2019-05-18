package com.cannizarro.qna;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class AnswerAdapter extends ArrayAdapter<AnswerObject> {
    Context mcontext;

    public AnswerAdapter(Context context, int resource, List<AnswerObject> objects) {
        super(context, resource, objects);
        mcontext=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {



        if(position == 0)
        {
            if (convertView == null) {
                convertView = ((Answers) getContext()).getLayoutInflater().inflate(R.layout.list_item_layout, parent, false);
            }
            TextView answer = convertView.findViewById(R.id.question);

            AnswerObject answerObject = getItem(position);

            convertView.setBackgroundColor(Color.parseColor("#157EFB"));
            answer.setTextColor(Color.parseColor("#fb9215"));
            convertView.setPadding(32,32,32,32);
            answer.setText(answerObject.getAns());
        }
        else
        {
            if (convertView == null) {
                convertView = ((Answers) getContext()).getLayoutInflater().inflate(R.layout.question_item, parent, false);
            }
            TextView answer = convertView.findViewById(R.id.question);
            TextView name=convertView.findViewById(R.id.author);

            AnswerObject answerObject = getItem(position);

            answer.setTextAppearance(mcontext,android.R.style.TextAppearance_Material_Medium);
            answer.setTextSize(20);
            answer.setText("Ans: " + answerObject.getAns());
            name.setText("-" + answerObject.getName());
        }


        return convertView;
    }
}
