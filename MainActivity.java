package com.example.testprank;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    private LinearLayout rootLayout;
    private Button startButton;
    private TextView statusText;
    private ToneGenerator toneGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Создаем главный экран программно (без XML)
        rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setGravity(Gravity.CENTER);
        rootLayout.setBackgroundColor(Color.parseColor("#1E1E2E"));

        // 2. Создаем кнопку запуска
        startButton = new Button(this);
        startButton.setText("АКТИВИРОВАТЬ ЧИТ 2026");
        startButton.setTextSize(20);
        startButton.setBackgroundColor(Color.parseColor("#4CAF50"));
        startButton.setTextColor(Color.WHITE);
        startButton.setPadding(50, 30, 50, 30);

        // 3. Создаем финальную надпись (пока скрыта)
        statusText = new TextView(this);
        statusText.setText("ПОПАЛСЯ, ХАКЕР!\n----------------");
        statusText.setTextColor(Color.RED);
        statusText.setTextSize(32);
        statusText.setTypeface(null, Typeface.BOLD);
        statusText.setGravity(Gravity.CENTER);
        statusText.setVisibility(View.GONE);

        // Добавляем элементы на экран
        rootLayout.addView(startButton);
        rootLayout.addView(statusText);
        setContentView(rootLayout);

        // Нажатие на кнопку
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runPrankSequence();
            }
        });
    }

    private void runPrankSequence() {
        // Прячем кнопку и делаем экран полностью черным на 5 секунд
        startButton.setVisibility(View.GONE);
        rootLayout.setBackgroundColor(Color.BLACK);

        // Прячем верхнюю панель телефона
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );

        // Поднимаем громкость на максимум и запускаем встроенный звуковой сигнал на 5 секунд
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0);
        }

        toneGenerator = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK, 5000);

        // Ждем ровно 5 секунд (5000 мс), затем показываем финальную надпись
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                statusText.setVisibility(View.VISIBLE);
            }
        }, 5000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (toneGenerator != null) {
            toneGenerator.release();
            toneGenerator = null;
        }
    }
}
