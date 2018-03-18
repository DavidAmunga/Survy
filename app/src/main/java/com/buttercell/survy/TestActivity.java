package com.buttercell.survy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.feeeei.circleseekbar.CircleSeekBar;

public class TestActivity extends AppCompatActivity {

    private static final String TAG = "TestActivity";

    @BindView(R.id.txtQuestion)
    TextView txtQuestion;
    @BindView(R.id.txtChoice)
    TextView txtChoice;
    @BindView(R.id.likert_scale)
    CircleSeekBar likertScale;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);

        likertScale.setOnSeekBarChangeListener(new CircleSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onChanged(CircleSeekBar circleSeekBar, int i) {
                txtChoice.setText(String.valueOf(i));
            }
        });


    }
}
