package com.example.lab1;

import static com.example.lab1.R.drawable.*;

import static java.lang.Thread.sleep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;
import java.util.Vector;

public class MainActivity2 extends AppCompatActivity {
    Chronometer Chrono;
    MediaPlayer mediaPlayer;
    boolean NewRecord;
    Dialog dialog;
    ImageView Effect;
    TextView RecordString;
    SharedPreferences RecordTime;
    Animation animScale;
    Animation animRotation;
    Animation animSleepRotation;
    Animation animEffect;
    private int size;
    private int width;
    private int height;
    Vector<Integer> BeginVector = new Vector<>();
    Vector<Integer> EndVector = new Vector<>();
    boolean ChosenFlag = false;
    Stack<ImageButton> stack = new Stack<ImageButton>();
    Stack<ImageButton> Allstack = new Stack<ImageButton>();
    Stack<TableRow> TableRowStack = new Stack<TableRow>();
    boolean ChronoisActivated;
    TableLayout TL;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowInsetsControllerCompat windowInsetsController = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars());
        windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        getWindow().setStatusBarColor(Color.TRANSPARENT);

        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (controller != null) {
            controller.setAppearanceLightStatusBars(true);
        }

        setContentView(R.layout.activity_main2);
        RecordString = (TextView)findViewById(R.id.textView3);
        animScale = AnimationUtils.loadAnimation(this, R.anim.scale);
        animRotation = AnimationUtils.loadAnimation(this, R.anim.rotation);
        animSleepRotation = AnimationUtils.loadAnimation(this, R.anim.sleeprotation);
        animEffect = AnimationUtils.loadAnimation(this, R.anim.effects);
        size = getIntent().getIntExtra("size", 2);
        Chrono = (Chronometer)findViewById(R.id.chronometer);
        Effect = (ImageView)findViewById(R.id.imageView4);
        width = getWindowManager().getDefaultDisplay().getWidth();
        height = getWindowManager().getDefaultDisplay().getHeight();
        TL = (TableLayout)findViewById(R.id.tableLayout);
        mediaPlayer = MediaPlayer.create(this, R.raw.music2);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        Game();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer.isPlaying()) mediaPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mediaPlayer.isPlaying()){
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }

    private void Game()
    {
        ChronoisActivated = false;
        NewRecord = false;
        GetRecord();
        for (int i = 0, k = (int)(Math.random()*4 + 1); i < size*size; i++, k++)
        {
            BeginVector.add(k);
            if (i < 8)
            {
                i++;
                BeginVector.add(k);
            }
            if (k == 4) k = 0;
        }

        while (BeginVector.size() > 0)
        {
            int index = (int) (Math.random()*(BeginVector.size()));
            EndVector.add(BeginVector.get(index));
            BeginVector.remove(index);
        }

        for (int i = 0; i < size; i++)
        {
            TableRow TR = new TableRow(this);
            TableRow.LayoutParams params = new TableRow.LayoutParams(
                    width,
                    height - 100
            );
            TableRow.LayoutParams ButtonParams = new TableRow.LayoutParams(
                    width / size ,
                    height/size - height/size/3
            );
            ButtonParams.setMargins(0,0,5,0);
            TR.setLayoutParams(params);
            for (int j = 0; j < size; j++)
            {
                ImageButton b = new ImageButton(this);
                b.setLayoutParams(ButtonParams);
                b.setId(i*size+j);
                b.setImageResource(cover);
                b.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                b.setBackgroundColor(Color.TRANSPARENT);
                Allstack.add(b);
                TR.addView(b);
                b.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        ImageButton IB = (ImageButton) view;
                        if (IB.getDrawable().getConstantState().equals(getResources().getDrawable(cover).getConstantState())) {
                            int id = IB.getId();
                            switch (EndVector.get(id)) {
                                case 1:
                                    IB.setImageResource(leonardo);
                                    break;
                                case 2:
                                    IB.setImageResource(raphael);
                                    break;
                                case 3:
                                    IB.setImageResource(donatello);
                                    break;
                                case 4:
                                    IB.setImageResource(mickey);
                                    break;
                            }
                            IB.startAnimation(animRotation);
                            stack.add(IB);
                            if (ChosenFlag)
                            {
                                if (CardsAreSame()) {
                                    if (!CardIsStillOnTheTable(EndVector.get(IB.getId()))) {
                                        while (!stack.isEmpty()) {
                                            stack.peek().startAnimation(animScale);
                                            stack.peek().setVisibility(ImageButton.INVISIBLE);
                                            EndVector.set(stack.peek().getId(), 0);
                                            stack.pop();
                                            ChosenFlag = false;
                                        }
                                        ShowEffect();
                                        if (CheckWin())
                                        {
                                            Chrono.stop();
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ShowDialog();
                                                }
                                            }, 1000);
                                            return;
                                        }
                                    }
                                }
                                else{
                                   new Handler().postDelayed(new Runnable() {
                                       @Override
                                        public void run() {
                                            ClearCards();
                                        }
                                        }, 500);
                                    ChosenFlag = false;
                                }
                            }
                            else {
                                if (!ChronoisActivated)
                                {
                                    Chrono.setBase(SystemClock.elapsedRealtime());
                                    Chrono.start();
                                    ChronoisActivated = true;
                                }
                                ChosenFlag = true;
                            }
                        }
                    }
                });
            }
            TL.addView(TR);
            TableRowStack.add(TR);
        }
        ShowCards();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HideCards();
            }
       }, 250 * size);
    }

    private void ShowCards()
    {
        for (int i = 0; i < Allstack.size(); i++)
        {
            Allstack.elementAt(i).startAnimation(animRotation);
            switch (EndVector.get(i)) {
                case 1:
                    Allstack.elementAt(i).setImageResource(leonardo);
                    break;
                case 2:
                    Allstack.elementAt(i).setImageResource(raphael);
                    break;
                case 3:
                    Allstack.elementAt(i).setImageResource(donatello);
                    break;
                case 4:
                    Allstack.elementAt(i).setImageResource(mickey);
                    break;
            }
        }
    }

    private void HideCards()
    {
        for (int i = 0; i < Allstack.size(); i++)
        {
            Allstack.elementAt(i).setImageResource(cover);
            Allstack.elementAt(i).startAnimation(animRotation);
        }
    }

    private void ClearCards()
    {
        final Animation animRotation = AnimationUtils.loadAnimation(this, R.anim.rotation);
        while (!stack.isEmpty())
        {
            stack.peek().setImageResource(cover);
            stack.peek().startAnimation(animRotation);
            stack.pop();
        }
    }
    private boolean CardsAreSame()
    {
        int card = EndVector.get(stack.elementAt(0).getId());
        for (int i = 1; i < stack.size(); i++)
        {
            if(EndVector.get(stack.elementAt(i).getId()) != card) return false;
        }
        return true;
    }

    private boolean CheckIndexes(int value, int[] indexes)
    {
        for (int i = 0; i < indexes.length; i++)
        {
            if (value == indexes[i]) return true;
        }
        return false;
    }

    private boolean CardIsStillOnTheTable(int card)
    {
        int StackSize = stack.size();
        int[] indexes = new int[StackSize];
        for (int i = 0; i < StackSize; i++)
        {
            indexes[i] = stack.elementAt(i).getId();
        }
        for (int i = 0; i < size*size; i++)
        {
            if (!CheckIndexes(i, indexes))
            {
                if (EndVector.get(i) == card) return true;
            }
        }
        return false;
    }

    private boolean CheckWin()
    {
        for (int i = 0; i < EndVector.size(); i++)
        {
            if (EndVector.get(i) != 0) return false;
        }
        CheckNewRecord();
        return true;
    }

    private void GetRecord()
    {
        String value = "";
        switch (size)
        {
            case 2:
            {
                RecordTime = getSharedPreferences("RecordTime2x2", MODE_PRIVATE);
                value = RecordTime.getString("RecordTime2x2", "00:00");
            } break;
            case 3:{
                RecordTime = getSharedPreferences("RecordTime3x3", MODE_PRIVATE);
                value = RecordTime.getString("RecordTime3x3", "00:00");
            }; break;
            case 4:{
                RecordTime = getSharedPreferences("RecordTime4x4", MODE_PRIVATE);
                value = RecordTime.getString("RecordTime4x4", "00:00");
            }; break;
            case 5: {
                RecordTime = getSharedPreferences("RecordTime5x5", MODE_PRIVATE);
                value = RecordTime.getString("RecordTime5x5", "00:00");
            }; break;
        }
        RecordString.setText(value);
    }

    private void CheckNewRecord() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
            Date Time1 = sdf.parse(RecordString.getText().toString());
            Date Time2 = sdf.parse(Chrono.getText().toString());
            String Time1String = RecordString.getText().toString();
            String Time2String = Chrono.getText().toString();
            if (Time2.compareTo(Time1) < 0 || Time1String == "00:00") {
                NewRecord = true;
                SharedPreferences.Editor editor = RecordTime.edit();
                switch (size) {
                    case 2:
                        editor.putString("RecordTime2x2", Time2String);
                        break;
                    case 3:
                        editor.putString("RecordTime3x3", Time2String);
                        break;
                    case 4:
                        editor.putString("RecordTime4x4", Time2String);
                        break;
                    case 5:
                        editor.putString("RecordTime5x5", Time2String);
                        break;
                }
                editor.apply();
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private void ShowEffect()
    {
        switch ((int) (Math.random() * 3))
        {
            case 0: Effect.setImageResource(poooow); break;
            case 1: Effect.setImageResource(boooomnew); break;
            case 2: Effect.setImageResource(woooownew); break;
        }
        Effect.setVisibility(View.VISIBLE);
        Effect.startAnimation(animEffect);
        Effect.setVisibility(View.INVISIBLE);
    }

    private void ShowDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("You won!");
        builder.setIcon(android.R.drawable.btn_star);
        String string = "";
        if (NewRecord) string += "New Best Time!\n";
        string += "Try again?";
        builder.setMessage(string);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ResetGame();
                Chrono.setBase(SystemClock.elapsedRealtime());
                Game();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void ResetGame()
    {
        EndVector.clear();
        while (!Allstack.isEmpty())
        {
            Allstack.peek().setVisibility(View.GONE);
            Allstack.pop();
        }
        while (!TableRowStack.isEmpty())
        {
            TableRowStack.peek().setVisibility(View.GONE);
            TableRowStack.pop();
        }
    }
}