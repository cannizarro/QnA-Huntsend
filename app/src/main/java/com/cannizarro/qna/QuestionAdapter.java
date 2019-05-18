package com.cannizarro.qna;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class QuestionAdapter extends ArrayAdapter<QuestionObject> {

    public QuestionAdapter(Context context, int resource, List<QuestionObject> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.question_item, parent, false);
        }

        TextView question = convertView.findViewById(R.id.question);
        TextView author=convertView.findViewById(R.id.author);

        QuestionObject questionObject = getItem(position);
            question.setText("Q: " + questionObject.getQues());
            author.setText("-" + questionObject.getName());

        return convertView;
    }

}
