package de.hka.projekt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author Lukas Heupel
 * @author Simon Schreckenberg
 * @author Alexander Beck
 * @author Cosimo Schostok
 * @author Kilian Langlinderer
 * @version 1.0
 */
public class ScoreActivity extends AppCompatActivity {

    private boolean detailsShown;


    /**
     * Layout definieren; Views einbinden; Buttons mit Aktionen versehen
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        TextView txtScoreFuß = this.findViewById(R.id.scoreFoot);
        TextView txtScoreÖV = this.findViewById(R.id.scorePT);
        TextView txtScoreRad = this.findViewById(R.id.scoreBike);
        TextView txtScoreNB = this.findViewById(R.id.scoreNextbike);
        TextView txtScoreMIV = this.findViewById(R.id.scoreMIV);
        TextView textScore = this.findViewById(R.id.textScore);
        TextView numberScore = this.findViewById(R.id.numberScore);
        RelativeLayout detailedScore = this.findViewById(R.id.detailedScore);
        LinearLayout showScore = this.findViewById(R.id.showScore);
        ImageButton fab = findViewById(R.id.fab);
        detailsShown = false;
        Intent intent = this.getIntent();
        double[] scoreList = intent.getDoubleArrayExtra("scoreList");
        String[] nameList = intent.getStringArrayExtra("nameList");
        String[] reqList = intent.getStringArrayExtra("reqs");
        detailedScore.setVisibility(View.GONE);
        double[] scores = calculateFinalScore(scoreList, reqList);
        int score = (int) scores[0];
        numberScore.setText(String.valueOf(score));
        Drawable d = showScore.getBackground();
        Drawable c = fab.getBackground();

        if (score < 17) {
            d.setTint(getResources().getColor(R.color.score6));
            c.setTint(getResources().getColor(R.color.score6));
            textScore.setText("ungenügend");
        } else if (score < 34) {
            d.setTint(getResources().getColor(R.color.score5));
            c.setTint(getResources().getColor(R.color.score5));
            textScore.setText("mangelhaft");
        } else if (score < 51) {
            d.setTint(getResources().getColor(R.color.score4));
            c.setTint(getResources().getColor(R.color.score4));
            textScore.setText("ausreichend");
        } else if (score < 68) {
            d.setTint(getResources().getColor(R.color.score3));
            c.setTint(getResources().getColor(R.color.score3));
            textScore.setText("befriedigend");
        } else if (score < 85) {
            d.setTint(getResources().getColor(R.color.score2));
            c.setTint(getResources().getColor(R.color.score2));
            textScore.setText("gut");
        } else if (score < 101) {
            d.setTint(getResources().getColor(R.color.score1));
            c.setTint(getResources().getColor(R.color.score1));
            textScore.setText("sehr gut");
        }

        txtScoreFuß.setText((double) Math.round(40 * scores[3]) / 10 + " von 4");
        txtScoreÖV.setText((double) Math.round(40 * scores[1]) / 10 + " von 34");
        txtScoreMIV.setText((double) Math.round(40 * scores[2]) / 10 + " von 36");
        txtScoreRad.setText((double) Math.round(40 * scores[5]) / 10 + " von 16");
        txtScoreNB.setText((double) Math.round(40 * scores[4]) / 10 + " von 10");

        fab.setOnClickListener(view -> {
            if (detailsShown) {
                detailedScore.setVisibility(View.GONE);
                detailsShown = false;
                fab.setImageDrawable(getResources().getDrawable(R.drawable.baseline_keyboard_arrow_down_24));
            } else {
                detailedScore.setVisibility(View.VISIBLE);
                detailsShown = true;
                fab.setImageDrawable(getResources().getDrawable(R.drawable.baseline_keyboard_arrow_up_24));
            }
        });
    }


    /**
     * Berechnet den finalen Mobilitätsscore
     * @param scores die Daten der einzelnen Verkehrsmittel
     * @param reqs die angegebenen Mobilitätsvoraussetzungen
     * @return die einzelnen Scores der Verkehrsmittel sowie den Gesamtscore
     */
    public double[] calculateFinalScore(@NonNull double[] scores, @NonNull String[] reqs) {
        double factor = 1.5;
        double scoreFoot = scores[0] / 100;
        double scoreBike;
        if (reqs[4].equals("-1")) {
            scoreBike = 500.0;
        } else {
            scoreBike = (double) Integer.valueOf(reqs[4]);
        }
        if (scoreBike > 500.0) {
            scoreBike = 500.0;
        }
        if (scoreBike < 0.0) {
            scoreBike = 0.0;
        }
        scoreBike = 4 * ((500.0 - scoreBike) / 500.0);
        if (!reqs[3].equals("1")) {
            scoreBike = 0.0;
        }
        double scoreMIV;
        if (reqs[2].equals("-1")) {
            scoreMIV = 500.0;
        } else {
            scoreMIV = Double.valueOf(reqs[2]);
        }
        if (scoreMIV > 500.0) {
            scoreMIV = 500.0;
        }
        if (scoreMIV < 0.0) {
            scoreMIV = 0.0;
        }
        scoreMIV = 9 * ((500.0 - scoreMIV) / 500.0);
        if (!reqs[0].equals("1") || !reqs[1].equals("1")) {
            scoreMIV = 0.0;
        }
        double scoreBikeSharing = scores[2];
        if (scoreBikeSharing > 500.0) {
            scoreBikeSharing = 500.0;
        }
        if (scoreBikeSharing < 0.0) {
            scoreBikeSharing = 0.0;
        }
        scoreBikeSharing = 2.5 * ((500.0 - scoreBikeSharing) / 500.0);
        double scorePT = scores[1];
        if (scorePT > 500.0) {
            scorePT = 500.0;
        }
        if (scorePT < 0.0) {
            scorePT = 0.0;
        }
        scorePT = 8.5 * (((500.0 - scorePT) / 500.0) * 0.9 + (scores[3] / 8) * 0.1);
        if (reqs[5].equals("1")) {
            scorePT *= factor;
        }
        switch (reqs[6]) {
            case "MIV":
                scoreMIV *= factor;
                break;
            case "ÖV":
                scorePT *= factor;
                break;
            case "Fahrrad":
                scoreBike *= factor;
                scoreBikeSharing *= factor;
                break;
            case "Fuß":
                scoreFoot *= factor;
                break;
            default:
                break;
        }
        if (scorePT > 8.5) {
            scorePT = 8.5;
        }
        if (scoreMIV > 9.0) {
            scoreMIV = 9.0;
        }
        if (scoreBike > 4.0) {
            scoreBike = 4.0;
        }
        if (scoreBikeSharing > 2.5) {
            scoreBikeSharing = 2.5;
        }
        if (scoreFoot > 1.0) {
            scoreFoot = 1.0;
        }
        double finalScore = scorePT + scoreMIV + scoreFoot + scoreBikeSharing + scoreBike;
        double resultScore = (4 * finalScore);
        double[] resultArray = new double[6];
        resultArray[0] = resultScore;
        resultArray[1] = scorePT;
        resultArray[2] = scoreMIV;
        resultArray[3] = scoreFoot;
        resultArray[4] = scoreBikeSharing;
        resultArray[5] = scoreBike;
        return resultArray;
    }

}