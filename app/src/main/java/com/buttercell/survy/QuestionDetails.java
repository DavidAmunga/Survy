package com.buttercell.survy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buttercell.survy.model.Answer;
import com.buttercell.survy.model.Question;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuestionDetails extends AppCompatActivity {

    private static final String TAG = "QuestionDetails";

    CollectionReference firestore;
    @BindView(R.id.question)
    TextView txtQuestion;
    @BindView(R.id.answersList)
    RecyclerView answersList;

    Question question;

    FirestoreRecyclerAdapter<Answer, AnswerViewHolder> adapter;
    @BindView(R.id.answer_count)
    TextView answerCount;


    private List<String> listAnswers = new ArrayList<>();
    @BindView(R.id.answerDistribution)
    LinearLayout answerDistribution;
    private List<String> totalListAnswers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_details);
        ButterKnife.bind(this);


        answersList.setLayoutManager(new LinearLayoutManager(this));
        answersList.setHasFixedSize(true);

        if (getIntent().getExtras() != null) {
            question = (Question) getIntent().getExtras().getSerializable("question");
            String choice = getIntent().getExtras().getString("choice");
            String surveyKey = getIntent().getExtras().getString("surveyKey");
            String questionKey = getIntent().getExtras().getString("questionKey");


            txtQuestion.setText(question.getQuestion());

            Query query = FirebaseFirestore.getInstance().collection("Surveys")
                    .document(surveyKey)
                    .collection(choice)
                    .document(questionKey)
                    .collection("answers").orderBy("timestamp");


            FirestoreRecyclerOptions<Answer> options = new FirestoreRecyclerOptions.Builder<Answer>()
                    .setQuery(query, Answer.class)
                    .build();

            adapter = new FirestoreRecyclerAdapter<Answer, AnswerViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull AnswerViewHolder holder, int position, @NonNull Answer model) {
                    holder.setAnswer(model.getAnswer());
                    holder.setUserName(model.getUsername());

                    String answerNo = String.valueOf(adapter.getItemCount());
                    if (adapter.getItemCount() > 1) {
                        answerCount.setText(String.valueOf(answerNo + " answers"));
                    } else {
                        answerCount.setText(String.valueOf(answerNo + " answer"));
                    }

                    if (!listAnswers.contains(model.getAnswer())) {
                        listAnswers.add(model.getAnswer());
                    }

                }

                @Override
                public AnswerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_layout, parent, false);
                    return new AnswerViewHolder(view);
                }

            };

            answersList.setAdapter(adapter);
            adapter.notifyDataSetChanged();


        }

    }

    public static class AnswerViewHolder extends RecyclerView.ViewHolder {

        public AnswerViewHolder(View itemView) {
            super(itemView);
        }

        public void setAnswer(String answer) {
            TextView txtAnswer = itemView.findViewById(R.id.txtAnswer);
            txtAnswer.setText(answer);
        }

        public void setUserName(String name) {
            TextView txtName = itemView.findViewById(R.id.txtName);
            txtName.setText(name);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
