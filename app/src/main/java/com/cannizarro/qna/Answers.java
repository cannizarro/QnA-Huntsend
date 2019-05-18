package com.cannizarro.qna;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static java.security.AccessController.getContext;

public class Answers extends AppCompatActivity {

    final int DEFAULT_ANS_LENGTH_LIMIT=1000;

    ListView listView;
    Button ansButton;
    EditText answerEditText;

    ArrayList<AnswerObject> answerList;
    AnswerAdapter adapter;
    String ques,quesID;

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    ChildEventListener mChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answers);

        listView=findViewById(R.id.listview);
        ansButton=findViewById(R.id.answerButton);
        answerEditText=findViewById(R.id.answerEdit);
        ansButton.setEnabled(false);

        Intent intent=getIntent();
        quesID=intent.getStringExtra("id");
        ques=intent.getStringExtra("ques");

        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mDatabaseReference=mFirebaseDatabase.getReference("answers/" + quesID);
        answerList=new ArrayList<>();
        adapter=new AnswerAdapter(this,android.R.layout.simple_list_item_1,answerList);
        listView.setAdapter(adapter);
        AnswerObject answerObject=new AnswerObject("Q. " + ques, MainActivity.name);
        answerList.add(answerObject);


        answerEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    ansButton.setEnabled(true);
                } else {
                    ansButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        answerEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_ANS_LENGTH_LIMIT)});

        ansButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AnswerObject answerObject=new AnswerObject(answerEditText.getText().toString(), MainActivity.name);
                mDatabaseReference.push().setValue(answerObject);

                answerEditText.setText("");
            }
        });

        mChildEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                AnswerObject answer= dataSnapshot.getValue(AnswerObject.class);
                answerList.add(answer);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabaseReference.addChildEventListener(mChildEventListener);

    }
}
