package com.example.jeudelavie;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.net.URL;

public class GrilleJeuDeLaVie extends AppCompatActivity {

    private GridLayout grille;
    private int[][] matriceCelluleFinal;
    private TextView info;
    private Button button;
    private SeekBar bar;

    private int valTaille;
    private float valFrequ;
    private int vitesse;
    private int score;


    private HandlerThread handlerThread;
    private Handler backgroundHandler;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grille_jeu_de_la_vie);

        this.grille = findViewById(R.id.layoutgrille);
        this.info = findViewById(R.id.informations);
        this.button = findViewById(R.id.resetButton);
        this.bar = findViewById(R.id.bar);

        this.matriceCelluleFinal = new int[valTaille + 2][valTaille + 2];
        this.vitesse = 100;

        Intent intent = getIntent();

        this.valTaille = intent.getIntExtra("valTaille", 0);
        this.valFrequ = intent.getFloatExtra("valFrequence", 0);

        this.bar.setProgress(10);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.bar.setMin(50);
        }
        this.bar.setMax(3000);

        demarrerJeu(this.grille);

        this.bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                GrilleJeuDeLaVie.this.vitesse = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    public void initialiserTabCellule()
    {
        this.matriceCelluleFinal = new int[valTaille + 2][valTaille + 2];
        int size = (int)((this.valTaille * this.valTaille) * this.valFrequ);

        for (int i = 1; i < this.matriceCelluleFinal.length - 1; i++)
        {
            for (int j = 1; j < this.matriceCelluleFinal.length - 1; j++)
            {
                if((i * this.matriceCelluleFinal[0].length - 2) + j >= size)
                {
                    this.matriceCelluleFinal[i][j] = 0;
                }
                else
                {
                    this.matriceCelluleFinal[i][j] = 1;
                }
            }
        }
        GrilleJeuDeLaVie.shuffleArray(this.matriceCelluleFinal);
    }

    public void actualiserGrille(View view)
    {
        try {
            GrilleJeuDeLaVie.this.grille.removeAllViews();
            GrilleJeuDeLaVie.this.grille.setColumnCount(GrilleJeuDeLaVie.this.valTaille);
            GrilleJeuDeLaVie.this.grille.setRowCount(GrilleJeuDeLaVie.this.valTaille);

            for (int i = 1; i < GrilleJeuDeLaVie.this.matriceCelluleFinal.length - 1; i++)
            {
                for (int j = 1; j < GrilleJeuDeLaVie.this.matriceCelluleFinal.length - 1; j++)
                {
                    View tempView = this.buildView(view);

                    if (GrilleJeuDeLaVie.this.matriceCelluleFinal[i][j] == 1) {
                        tempView.setBackgroundColor(Color.BLACK);
                    } else {
                        tempView.setBackgroundColor(Color.WHITE);
                    }

                    GrilleJeuDeLaVie.this.grille.addView(tempView);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public View buildView(View view)
    {
        View tempView = new TextView(view.getContext());

        int size = GrilleJeuDeLaVie.this.grille.getWidth() / GrilleJeuDeLaVie.this.valTaille;
        ViewGroup.LayoutParams param = new ViewGroup.LayoutParams(size, size);
        tempView.setLayoutParams(param);

        return tempView;
    }


    public void changerGrille(View view) {
        backgroundHandler.post(new Runnable() {
            int nbIterations = 0;

            @Override
            public void run() {
                int size = GrilleJeuDeLaVie.this.matriceCelluleFinal.length;

                int[][] tabTemp = new int[size][size];
                for (int i = 0; i < size; i++)
                {
                    System.arraycopy(GrilleJeuDeLaVie.this.matriceCelluleFinal[i], 0, tabTemp[i], 0, size);
                }

                int[][] tabFuture = new int[size][size];

                for (int i = 1; i < size - 1; i++)
                {
                    for (int j = 1; j < size - 1; j++)
                    {
                        int nbVoisine = GrilleJeuDeLaVie.this.getNbVoisins(i, j);

                        if(GrilleJeuDeLaVie.this.matriceCelluleFinal[i][j] == 1)
                        {
                            if (nbVoisine == 2 || nbVoisine == 3)
                            {
                                tabFuture[i][j] = 1;
                            }
                            else
                            {
                                tabFuture[i][j] = 0;
                            }
                        }
                        else
                        {
                            if (nbVoisine == 3)
                            {
                                tabFuture[i][j] = 1;
                            }
                            else
                            {
                                tabFuture[i][j] = 0;
                            }
                        }
                    }
                }
                for (int i = 0; i < size; i++) {
                    System.arraycopy(tabFuture[i], 0, GrilleJeuDeLaVie.this.matriceCelluleFinal[i], 0, size);
                }

                if(GrilleJeuDeLaVie.isTabEqual(GrilleJeuDeLaVie.this.matriceCelluleFinal, tabTemp) || nbIterations >= 1000)
                {
                    mainHandler.post(() -> finIterations(nbIterations));
                    return;
                }
                else
                {
                    mainHandler.post(() -> updateInfo(nbIterations));
                }


                mainHandler.post(() -> actualiserGrille(view));

                nbIterations++;

                backgroundHandler.postDelayed(this, GrilleJeuDeLaVie.this.vitesse);
            }
        });
    }

    public void updateInfo(int iteration)
    {
        this.score = iteration;
        this.info.setText(Integer.toString(iteration));
    }

    public void finIterations(int iteration)
    {
        this.score = iteration;
        this.info.setTextColor(Color.GREEN);
        this.info.setText("Itérations terminés");
        this.button.setText("Relancer");
    }

    public void retourMenu(View view)
    {
        this.handlerThread.quitSafely();
        Intent intent = new Intent(this, com.example.jeudelavie.MainActivity.class);
        intent.putExtra("score", this.score);
        startActivity(intent);
    }

    public void demarrerJeu(View view)
    {
        this.info.setTextColor(Color.BLACK);
        this.button.setText("Reset");

        if(this.handlerThread != null)
        {
            if(this.handlerThread.isAlive())
            {
                this.handlerThread.quitSafely();
            }
        }

        this.handlerThread = new HandlerThread("BackgroundThread");
        this.handlerThread.start();
        this.backgroundHandler = new Handler(this.handlerThread.getLooper());

        this.initialiserTabCellule();
        this.actualiserGrille(this.grille);
        this.changerGrille(this.grille);
    }

    public int getNbVoisins(int lig, int col)
    {
        int count = 0;

        if (this.matriceCelluleFinal[lig + 1][col + 1] == 1)
        {
            count++;
        }

        if (this.matriceCelluleFinal[lig - 1][col - 1] == 1)
        {
            count++;
        }

        if (this.matriceCelluleFinal[lig + 1][col - 1] == 1)
        {
            count++;
        }

        if (this.matriceCelluleFinal[lig - 1][col + 1] == 1)
        {
            count++;
        }

        if (this.matriceCelluleFinal[lig][col + 1] == 1)
        {
            count++;
        }

        if (this.matriceCelluleFinal[lig][col - 1] == 1)
        {
            count++;
        }

        if (this.matriceCelluleFinal[lig + 1][col] == 1)
        {
            count++;
        }

        if (this.matriceCelluleFinal[lig - 1][col] == 1)
        {
            count++;
        }

        return count;
    }

    public static boolean isTabEqual(int[][] tab1, int[][] tab2)
    {
        for (int i = 0; i < tab1.length; i++)
        {
            for (int j = 0; j < tab2.length; j++)
            {
                if(tab1[i][j] != tab2[i][j])
                {
                    return false;
                }
            }
        }
        return true;
    }

    public static void shuffleArray(int[][] array) {
        for (int i = 1; i < array.length - 1; i++)
        {
            for (int j = 1; j < array.length - 1; j++)
            {
                int randI = 1 + (int)(Math.random() * ((array.length - 1) - 1));
                int randJ = 1 + (int)(Math.random() * ((array.length - 1) - 1));

                int temp = array[i][j];
                array[i][j] = array[randI][randJ];
                array[randI][randJ] = temp;
            }
        }
    }
}