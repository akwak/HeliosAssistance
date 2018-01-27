package edu.akwak.heliosassistance.utils;


import android.widget.TextView;

public class TextviewUpdater implements Runnable{
    private final TextView textView;
    String textToUpdate;
    public TextviewUpdater(String n, TextView textView) {
        this.textView = textView;
        textToUpdate=n;
    }
    @Override
    public void run() {
        textView.setText(textToUpdate);
    }
}
