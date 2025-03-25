package com.example.jeudelavie;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_param);

        TextView viewScore = findViewById(R.id.resultatScore);

        Intent partieIntent = getIntent();
        if(partieIntent != null)
        {
            int newScore = partieIntent.getIntExtra("score", 0);
            System.out.println(newScore);

            if(newScore > this.score)
            {
                this.score = newScore;
            }
        }
        viewScore.setText(Integer.toString(this.score));

    }

    public void buttonActivated(View view)
    {
        EditText taille = findViewById(R.id.editTaille);
        EditText frequence = findViewById(R.id.editFrequence);
        TextView erreur = findViewById(R.id.erreur);
        erreur.setText("");

        if( taille.getText().toString().matches("^(\\d*)$") &&
            frequence.getText().toString().matches("^(\\d*\\.?\\d*)$") &&
            Float.parseFloat(frequence.getText().toString()) <= 1 &&
            Float.parseFloat(frequence.getText().toString()) > 0)
        {
            float valFrequ = Float.parseFloat(frequence.getText().toString());
            int valTaille = Integer.parseInt(taille.getText().toString());

            Intent intent = new Intent(this, com.example.jeudelavie.GrilleJeuDeLaVie.class);
            intent.putExtra("valFrequence", valFrequ);
            intent.putExtra("valTaille", valTaille);
            startActivity(intent);
        }
        else
        {
            erreur.setText("Valeur invalide");
        }
    }
}