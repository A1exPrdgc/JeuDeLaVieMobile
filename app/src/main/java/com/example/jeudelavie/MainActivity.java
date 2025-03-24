package com.example.jeudelavie;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_param);
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