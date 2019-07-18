package com.vocab.gdict;

import java.io.IOException;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
//import android.graphics.Bitmap;


public class MainActivity extends ListActivity implements OnInitListener {
    EditText searchBox;
    Button clearB, searchB;
    WebView webV;
    ListView lView;
    ProgressBar loadingBar;
    String xmlText;
    String searchQ;
    DictEntriesAdapter mAdapter;
    int openCounter;
    // int pageStartCtr=0;
    // int pageReloadCtr=0;
    // int pageFinishCtr=0;
    boolean isRedirected;
    private TextToSpeech myTTS;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isRedirected = false;
        searchQ = getIntent().getStringExtra("EXTRA_QUERY");
        if (searchQ != null) {
            setTheme(android.R.style.Theme_Holo_Dialog_NoActionBar_MinWidth);
        } else {
            final SharedPreferences prefs = getSharedPreferences("Customizes", MODE_PRIVATE);
            openCounter = prefs.getInt("openCounter", 0);
            openCounter++;
            if (openCounter == 15) {
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_menu_info_details)
                        .setTitle("Like It??")
                        .setMessage("Please take a moment to provide your feedback")
                        .setPositiveButton("Rate It",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        Intent openPlay = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.vocab.gdict"));
                                        startActivity(openPlay);
                                    }
                                }).setNegativeButton("Later",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                openCounter = 0;
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putInt("openCounter", openCounter);
                                editor.commit();
                            }
                        }).show();
            }
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("openCounter", openCounter);
            editor.commit();
        }
        setContentView(R.layout.activity_main);
        myTTS = new TextToSpeech(this, this);
        searchBox = (EditText) findViewById(R.id.word_search);
        clearB = (Button) findViewById(R.id.clear_button);
        searchB = (Button) findViewById(R.id.search_button);
        //lView = (ListView) findViewById(R.id.list_items);
        webV = (WebView) findViewById(R.id.webView1);
        loadingBar = (ProgressBar) findViewById(R.id.loading_bar);
        loadingBar.setVisibility(View.INVISIBLE);
        webV.setVisibility(View.INVISIBLE);
        webV.getSettings().setJavaScriptEnabled(true);
        //CookieManager.getInstance().setAcceptCookie(true);
        mAdapter = new DictEntriesAdapter(this, myTTS);
        getListView().setAdapter(mAdapter);
        webV.addJavascriptInterface(new MyJavaScriptInterface(this, mAdapter), "HtmlViewer");

        webV.setWebViewClient(new WebViewClient() {
			/*
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {

				isRedirected = !isRedirected;
                // pageStartCtr++;
			}
			*/
			/*@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				  // public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                pageReloadCtr++;
                view.loadUrl(url);
                isRedirected = true;
				return true;
			}*/


            @Override
            public void onPageFinished(WebView view, String url) {
                // http://stackoverflow.com/questions/2376471/how-do-i-get-the-web-page-contents-from-a-webview
                // pageFinishCtr++;
                if (!isRedirected) {
                    webV.loadUrl("javascript:window.HtmlViewer.showHTML" +
                            "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                    isRedirected = false;
                } else {
                    isRedirected = false;
                }

                loadingBar.setIndeterminate(false);
                loadingBar.setVisibility(View.INVISIBLE);
                // onlyOnce = false;
                //loadingBar.setProgress(0);
            }
        });
        clearB.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                searchBox.setText("");
                searchBox.requestFocus();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            }
        });

        searchBox.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on Enter key press
                    // onlyOnce=true;
                    searchB.performClick();
                    return true;
                }
                return false;
            }
        });

        searchB.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                loadingBar.setVisibility(View.VISIBLE);
                loadingBar.setIndeterminate(true);
                isRedirected = false;
                //loadingBar.getProgressDrawable().setColorFilter(Color.GREEN, Mode.SRC_IN);
                String query = searchBox.getText().toString();
                mAdapter.clear();
                hideSoftKeyboard(MainActivity.this, v);
                // webV.loadUrl("https://www.google.com/search?q=define%3A" + query + "&num=1");
                webV.loadUrl("https://www.google.com/search?q=define+" + query + "&num=1");
                // webV.removeJavascriptInterface("HtmlViewer");
                // webV.addJavascriptInterface(new MyJavaScriptInterface(getApplicationContext(),mAdapter), "HtmlViewer");
            }
        });

        if (searchQ != null || savedInstanceState != null) {
            if (searchQ != null) {
                //hideSoftKeyboard(this, getWindow().getDecorView().findViewById(android.R.id.content)); doesn't work
                searchBox.setText(searchQ);
            } else
                searchBox.setText(savedInstanceState.getString("searchString"));
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); //works like charm
            searchB.performClick();
            searchQ = null;
        }
    }

    class MyJavaScriptInterface {

        // http://stackoverflow.com/questions/2376471/how-do-i-get-the-web-page-contents-from-a-webview
        private Context ctx;
        private DictEntriesAdapter cAdapter;
        // private boolean onlyOnce=true;

        MyJavaScriptInterface(Context ctx, DictEntriesAdapter mAdapter) {
            this.ctx = ctx;
            this.cAdapter = mAdapter;
            // this.onlyOnce = true;
        }

        @JavascriptInterface
        public void showHTML(String html) {

            XMLResponseHandler a = new XMLResponseHandler(ctx, cAdapter);
            //Log.i("htmlpage",html);
            //new AlertDialog.Builder(ctx).setTitle("HTML").setMessage(html)
            //.setPositiveButton(android.R.string.ok, null).setCancelable(false).create().show();
            //if(onlyOnce) {
            try {
                a.handleResponse(html, 1);
                // onlyOnce = true;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //}
        }

    }

    public static void hideSoftKeyboard(ListActivity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final TextView message = new TextView(this);
        message.setText("The developer would like to express his gratitude to Google for the built-in Dictionary in Google Search Engine");
        message.setMovementMethod(LinkMovementMethod.getInstance());
        switch (item.getItemId()) {
            case R.id.action_share:
                Intent shareInt = new Intent(android.content.Intent.ACTION_SEND);
                shareInt.setType("text/plain");
                shareInt.putExtra(android.content.Intent.EXTRA_SUBJECT, "Google Dictionary https://play.google.com/store/apps/details?id=com.vocab.gdict");
                shareInt.putExtra(android.content.Intent.EXTRA_TEXT, "Check out \"GDict - Google Dictionary\" app"
                        + " here: https://play.google.com/store/apps/details?id=com.vocab.gdict");
                startActivity(Intent.createChooser(shareInt, "Share using:"));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onSaveInstanceState(Bundle savedState) {

        super.onSaveInstanceState(savedState);
        searchQ = null;
        String sS = searchBox.getText().toString();
        savedState.putString("searchString", sS);
    }

    @Override
    public void onInit(int initStatus) {

        //check for successful instantiation
        if (initStatus == TextToSpeech.SUCCESS) {
            if (myTTS.isLanguageAvailable(Locale.US) == TextToSpeech.LANG_AVAILABLE)
                myTTS.setLanguage(Locale.US);
        } else if (initStatus == TextToSpeech.ERROR) {
            Toast.makeText(MainActivity.this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {


        //Close the Text to Speech Library
        if (myTTS != null) {

            myTTS.stop();
            myTTS.shutdown();
            //Log.d(TAG, "TTS Destroyed");
        }
        super.onDestroy();
    }


}
