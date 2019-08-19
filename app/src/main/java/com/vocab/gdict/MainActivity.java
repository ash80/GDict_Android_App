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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ListActivity implements OnInitListener {
    EditText searchBox;
    Button clearB, searchB;
    WebView webV;
    ListView lView;
    ProgressBar loadingBar;
//    String xmlText;
    String searchQ;
    DictEntriesAdapter mAdapter;
    int openCounter;
    // int pageStartCtr=0;
    // int pageReloadCtr=0;
    // int pageFinishCtr=0;
    boolean isRedirected;
    private TextToSpeech myTTS;
    boolean IsFirstTime;
    int activeTable;
    private SQLiteDatabase mDB = null;
    private DatabaseOpenHelper mDbHelper;
    private SimpleCursorAdapter mCursonAdapter;
    private SharedPreferences prefs;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("Customizes",MODE_PRIVATE);
        IsFirstTime = prefs.getBoolean("IsFirstTime", true);
        mDbHelper = new DatabaseOpenHelper(this);
        if(IsFirstTime) {
            mDbHelper.createDataBase();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("Status_0", "Saved Words");
            editor.putInt("currentNumberOfTables", 1);
            editor.putInt("currentlyActiveTable", 0);
            editor.putBoolean("resetListStatus_0", true);
            editor.putBoolean("IsFirstTime", false);
            editor.apply();
        }
        activeTable = prefs.getInt("currentlyActiveTable", 0);
        isRedirected = false;
        searchQ = getIntent().getStringExtra("EXTRA_QUERY");
        if (searchQ != null) {
            setTheme(android.R.style.Theme_Holo_Dialog_NoActionBar_MinWidth);
        } else {
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
                                editor.apply();
                            }
                        }).show();
            }
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("openCounter", openCounter);
            editor.apply();
        }
        setContentView(R.layout.activity_main);
        myTTS = new TextToSpeech(this, this);
        searchBox = findViewById(R.id.word_search);
        clearB = findViewById(R.id.clear_button);
        searchB = findViewById(R.id.search_button);
        mDB = mDbHelper.getWritableDatabase();
        Cursor localWordsCursor = readLocalWords(null);
        mCursonAdapter = new SimpleCursorAdapter(this, R.layout.mean_sent_syn_ant_local,
                localWordsCursor,
                new String[] { DatabaseOpenHelper.columns[1], DatabaseOpenHelper.columns[2],
                               DatabaseOpenHelper.columns[3], DatabaseOpenHelper.columns[4],
                               DatabaseOpenHelper.columns[5] },
                new int[] { R.id.local_word, R.id.local_pos, R.id.local_meaning,
                R.id.local_sentence, R.id.local_synonyms },
                0);
        lView = (ListView) findViewById(R.id.second_list_view);

        webV = (WebView) findViewById(R.id.webView1);
        loadingBar = (ProgressBar) findViewById(R.id.loading_bar);
        loadingBar.setVisibility(View.INVISIBLE);
        webV.setVisibility(View.INVISIBLE);
        webV.getSettings().setJavaScriptEnabled(true);
        //CookieManager.getInstance().setAcceptCookie(true);
        mAdapter = new DictEntriesAdapter(this, myTTS, prefs.getInt("currentlyActiveTable", 0));
        lView.setAdapter(mAdapter);
        lView.setVisibility(View.INVISIBLE);
        getListView().setAdapter(mCursonAdapter);
        mCursonAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence constraint) {
                return readLocalWords(constraint);
            }
        });
//        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
//                new AlertDialog.Builder(MainActivity.this)
//                        .setIcon(android.R.drawable.ic_dialog_alert)
//                        .setTitle("Delete:")
//                        .setMessage("Are you sure you want to remove this word")
//                        .setPositiveButton("Yes",
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog,
//                                                        int which) {
//                                        setAs("Delete");
//                                        updateLocalWordList();
//                                    }
//
//                                }).setNegativeButton("No", null).show();
//                return false;
//            }
//        });
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
                getListView().setVisibility(View.VISIBLE);
                lView.setVisibility(View.INVISIBLE);
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

        searchBox.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //updateStoryData();
            }
//
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                getListView().setVisibility(View.VISIBLE);
                lView.setVisibility(View.INVISIBLE);
                mCursonAdapter.getFilter().filter(s.toString());
            }
        });

        searchB.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getListView().setVisibility(View.INVISIBLE);
                lView.setVisibility(View.VISIBLE);
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

    public void updateWordList()
    {
        Cursor c = readLocalWords(null);
        mCursonAdapter.changeCursor(c);
    }

    private Cursor readLocalWords(CharSequence constraint) {
        if (constraint == null  ||  constraint.length () == 0)  {
            //  Return the full list
            return mDB.query(DatabaseOpenHelper.TABLE_NAME
                            +prefs.getInt("currentlyActiveTable", 0),
                    null,
                    null, null, null, null,
                    DatabaseOpenHelper.WORD);
        }  else  {
            String value = "%"+constraint.toString()+"%";

            return mDB.query(DatabaseOpenHelper.TABLE_NAME
                            +prefs.getInt("currentlyActiveTable",0),
                    null, "word like ? UNION SELECT * FROM "
                            +DatabaseOpenHelper.TABLE_NAME
                            +prefs.getInt("currentlyActiveTable",0)+
                            " WHERE synonym like ?",
                    new String[] {value, value}, null, null, null);
        }


    }

//    private void deleteThisWord(int int id) {
//        AddWordsToDatabase deleteWord = new AddWordsToDatabase(MainActivity.this);
//        deleteWord.deleteOneRow(choice, id, getApplicationContext());
//    }

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
        mDB.close();
        super.onDestroy();
    }


}
