package com.cannizarro.qna;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int DEFAULT_QUES_LENGTH_LIMIT = 500;
    public static final int RC_SIGN_IN= 1;


    ListView listView;
    Button ask;
    EditText questionEditText;

    ArrayList<QuestionObject> questionList;
    ArrayList<String> questionIds;
    QuestionAdapter adapter;


    static String name="Author";
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    ChildEventListener mChildEventListener;
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=findViewById(R.id.questions);
        ask=findViewById(R.id.askButton);
        questionEditText=findViewById(R.id.answerEdit);
        ask.setEnabled(false);

        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mDatabaseReference=mFirebaseDatabase.getReference("question");
        mFirebaseAuth=FirebaseAuth.getInstance();
        questionList=new ArrayList<>();
        questionIds=new ArrayList<>();
        adapter=new QuestionAdapter(this,android.R.layout.simple_list_item_1,questionList);
        listView.setAdapter(adapter);

        questionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    ask.setEnabled(true);
                } else {
                    ask.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        questionEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_QUES_LENGTH_LIMIT)});

        ask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                QuestionObject questionObject=new QuestionObject(questionEditText.getText().toString(), name);
                mDatabaseReference.push().setValue(questionObject);

                questionEditText.setText("");
            }
        });



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(MainActivity.this,Answers.class);
                intent.putExtra("id",questionIds.get(position));
                intent.putExtra("ques",questionList.get(position).getQues());
                Log.i("ffff",questionList.toString());
                startActivity(intent);
            }
        });

        mAuthStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user != null)
                {
                    //signed in
                    onSignedInInitialize(user.getDisplayName());
                }
                else
                {
                    //signed out
                    onSignedOutCleanup();
                    // Choose authentication providers
                    List<AuthUI.IdpConfig> providers = Arrays.asList(
                            new AuthUI.IdpConfig.EmailBuilder().build(),
                            new AuthUI.IdpConfig.GoogleBuilder().build());

// Create and launch sign-in intent
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(providers)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN)
        {
            if(resultCode == RESULT_OK)
            {
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            }
            else if(resultCode == RESULT_CANCELED)
            {
                Toast.makeText(this, "Cancelled.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mAuthStateListener != null)
        {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        detachReadListener();
        adapter.clear();
    }

    private void onSignedInInitialize(String username)
    {
        name=username;
        attachReadListener();
    }

    private void onSignedOutCleanup()
    {
        name="Author";
        adapter.clear();
        detachReadListener();

    }

    private void attachReadListener()
    {
        if(mChildEventListener == null)
        {

            mChildEventListener=new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    QuestionObject question= dataSnapshot.getValue(QuestionObject.class);
                    questionList.add(question);
                    adapter.notifyDataSetChanged();
                    String[] ref=dataSnapshot.getRef().toString().split("/-");
                    questionIds.add(ref[ref.length - 1]);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            };
            mDatabaseReference.addChildEventListener(mChildEventListener);

        }
    }

    private void detachReadListener()
    {
        if(mChildEventListener != null)
        {
            mDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener=null;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_context_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.sign_out:
                AuthUI.getInstance().signOut(getApplicationContext());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


}
