package com.example.jeudelavie;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
{

    private int scoreActuel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_param);

        TextView viewScore = findViewById(R.id.resultatScore);

        Intent partieIntent = getIntent();
        if(partieIntent != null)
        {
            this.scoreActuel = partieIntent.getIntExtra("score", 0);
        }

        viewScore.setText(Integer.toString(this.scoreActuel));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("score", this.scoreActuel);
    }

    public void buttonActivated(View view)
    {
        EditText taille = findViewById(R.id.editTaille);
        EditText frequence = findViewById(R.id.editFrequence);
        TextView erreur = findViewById(R.id.erreur);
        erreur.setText("");

        if (!taille.getText().toString().isEmpty() &&
            !frequence.getText().toString().isEmpty())
        {
            if( taille.getText().toString().matches("^(\\d*)$") &&
                    frequence.getText().toString().matches("^(\\d*\\.?\\d*)$") &&
                    Float.parseFloat(frequence.getText().toString()) <= 1 &&
                    Float.parseFloat(frequence.getText().toString()) > 0 &&
                    Integer.parseInt(taille.getText().toString()) > 4 &&
                    Integer.parseInt(taille.getText().toString()) <= 200)
            {
                float valFrequ = Float.parseFloat(frequence.getText().toString());
                int valTaille = Integer.parseInt(taille.getText().toString());

                Intent intent = new Intent(this, com.example.jeudelavie.GrilleJeuDeLaVie.class);
                intent.putExtra("valFrequence", valFrequ);
                intent.putExtra("valTaille", valTaille);
                intent.putExtra("oldscore", this.scoreActuel);
                startActivity(intent);
            }
            else
            {
                erreur.setText("Valeur invalide (]4; 200] et ]0; 1])");
            }
        }
        else
        {
            erreur.setText("Valeur invalide (valeur vide)");
        }
    }
}