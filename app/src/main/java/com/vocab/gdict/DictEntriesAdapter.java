package com.vocab.gdict;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
//import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DictEntriesAdapter extends BaseAdapter {

    private final List<DictEntry> mItems = new ArrayList<DictEntry>();
    private final Context mContext;
    private final TextToSpeech myTTS;

    DictEntriesAdapter(Context ctx, TextToSpeech tts) {
        mContext = ctx;
        myTTS = tts;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public int getViewTypeCount() {
        return 9;
    }

    @Override
    public int getItemViewType(int position) {
        switch (mItems.get(position).getEntryType()) {
            case DictEntry.THE_WORD:
                return 0;
            case DictEntry.FOS:
                return 1;
            case DictEntry.ONLY_MEAN:
                return 2;
            case DictEntry.MEAN_SYN:
                return 3;
            case DictEntry.MEAN_SYN_ANT:
                return 4;
            case DictEntry.MEAN_SENT:
                return 5;
            case DictEntry.MEAN_SENT_SYN:
                return 6;
            case DictEntry.MEAN_SENT_SYN_ANT:
                return 7;
            case DictEntry.ENCYCLOPEDIA:
                return 8;
            default:
                return 0;
        }
    }

    @Override
    public Object getItem(int pos) {
        return mItems.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    void add(DictEntry anItem) {
        mItems.add(anItem);
        notifyDataSetChanged();
    }

    void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    @SuppressWarnings("deprecation")
    private void speakWords(String speech) {

        //speak straight away
        myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final DictEntry anItem = (DictEntry) getItem(position);
        int viewType = getItemViewType(position);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout itemLayout;
        final TextView word, fos, mean, sent, syn, ant;
        Button addB;
        switch (viewType) {
            case DictEntry.THE_WORD:
                itemLayout = (LinearLayout) inflater.inflate(R.layout.the_word, null);
                word = (TextView) itemLayout.findViewById(R.id.the_word);
                addB = (Button) itemLayout.findViewById(R.id.speak_button);
                //fos = (TextView) itemLayout.findViewById(R.id.pronunciation);
                word.setText(anItem.getWord());
                addB.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String lWord = anItem.getWord();
                        speakWords(lWord.toLowerCase(Locale.US));
                    }
                });
                //fos.setText(anItem.getFosFull());
                //MediaPlayer mAudio = new MediaPlayer();
                //MediaController mC = new MediaController(mContext);
                //mC.setMediaPlayer(mContext);
                return itemLayout;

            case DictEntry.FOS:
                itemLayout = (LinearLayout) inflater.inflate(R.layout.verb_noun_view, null);
                fos = (TextView) itemLayout.findViewById(R.id.verb_noun);
                word = (TextView) itemLayout.findViewById(R.id.v_n_desc);
                if (anItem.getFosFull().equals("Dictionary Not Found")) {
                    ((View) parent.getParent()).findViewById(R.id.webView1).setVisibility(View.VISIBLE);
                    //parent.findViewById(R.id.list_items).setVisibility(View.INVISIBLE);
                } else {
                    ((View) parent.getParent()).findViewById(R.id.webView1).setVisibility(View.INVISIBLE);
                    //parent.findViewById(R.id.list_items).setVisibility(View.VISIBLE);
                }
                fos.setText(anItem.getFosFull());
                word.setText(anItem.getFosDesc());
                return itemLayout;

            case DictEntry.ONLY_MEAN:
                itemLayout = (LinearLayout) inflater.inflate(R.layout.mean, null);
                mean = (TextView) itemLayout.findViewById(R.id.meaning);
                fos = (TextView) itemLayout.findViewById(R.id.adjunct);
//                addB = (Button) itemLayout.findViewById(R.id.add_button);
                mean.setText(anItem.getMeaning());
                fos.setText(anItem.getFosBrief());

                return itemLayout;

            case DictEntry.MEAN_SYN:
                itemLayout = (LinearLayout) inflater.inflate(R.layout.mean_syn, null);
                mean = (TextView) itemLayout.findViewById(R.id.meaning);
                fos = (TextView) itemLayout.findViewById(R.id.adjunct);
                syn = (TextView) itemLayout.findViewById(R.id.synonyms);
//                addB = (Button) itemLayout.findViewById(R.id.add_button);
                mean.setText(anItem.getMeaning());
                fos.setText(anItem.getFosBrief());
                syn.setText(anItem.getSynonyms());

                return itemLayout;

            case DictEntry.MEAN_SYN_ANT:
                itemLayout = (LinearLayout) inflater.inflate(R.layout.mean_syn_ant, null);
                mean = (TextView) itemLayout.findViewById(R.id.meaning);
                fos = (TextView) itemLayout.findViewById(R.id.adjunct);
                syn = (TextView) itemLayout.findViewById(R.id.synonyms);
                ant = (TextView) itemLayout.findViewById(R.id.antonyms);
//                addB = (Button) itemLayout.findViewById(R.id.add_button);
                mean.setText(anItem.getMeaning());
                fos.setText(anItem.getFosBrief());
                syn.setText(anItem.getSynonyms());
                ant.setText(anItem.getAntonyms());

                return itemLayout;

            case DictEntry.MEAN_SENT:
                itemLayout = (LinearLayout) inflater.inflate(R.layout.mean_sent, null);
                mean = (TextView) itemLayout.findViewById(R.id.meaning);
                fos = (TextView) itemLayout.findViewById(R.id.adjunct);
                sent = (TextView) itemLayout.findViewById(R.id.sentence);
//                addB = (Button) itemLayout.findViewById(R.id.add_button);
                mean.setText(anItem.getMeaning());
                fos.setText(anItem.getFosBrief());
                sent.setText(anItem.getSentence());

                return itemLayout;

            case DictEntry.MEAN_SENT_SYN:
                itemLayout = (LinearLayout) inflater.inflate(R.layout.mean_sent_syn, null);
                mean = (TextView) itemLayout.findViewById(R.id.meaning);
                fos = (TextView) itemLayout.findViewById(R.id.adjunct);
                syn = (TextView) itemLayout.findViewById(R.id.synonyms);
                sent = (TextView) itemLayout.findViewById(R.id.sentence);
//                addB = (Button) itemLayout.findViewById(R.id.add_button);
                mean.setText(anItem.getMeaning());
                fos.setText(anItem.getFosBrief());
                syn.setText(anItem.getSynonyms());
                sent.setText(anItem.getSentence());

                return itemLayout;

            case DictEntry.MEAN_SENT_SYN_ANT:
                itemLayout = (LinearLayout) inflater.inflate(R.layout.mean_sent_syn_ant, null);
                mean = (TextView) itemLayout.findViewById(R.id.meaning);
                fos = (TextView) itemLayout.findViewById(R.id.adjunct);
                syn = (TextView) itemLayout.findViewById(R.id.synonyms);
                ant = (TextView) itemLayout.findViewById(R.id.antonyms);
                sent = (TextView) itemLayout.findViewById(R.id.sentence);
//                addB = (Button) itemLayout.findViewById(R.id.add_button);
                mean.setText(anItem.getMeaning());
                fos.setText(anItem.getFosBrief());
                syn.setText(anItem.getSynonyms());
                sent.setText(anItem.getSentence());
                ant.setText(anItem.getAntonyms());

			/*((ListActivity) mContext).runOnUiThread(new Runnable() {
			     @Override
			     public void run() {
					mean.setText(anItem.getMeaning());
					fos.setText(anItem.getFosBrief());
					syn.setText(anItem.getSynonyms());
					sent.setText(anItem.getSentence());
					ant.setText(anItem.getAntonyms());
			    }
			});*/
                return itemLayout;

            case DictEntry.ENCYCLOPEDIA:
                itemLayout = (LinearLayout) inflater.inflate(R.layout.encyclopedia, null);
                word = (TextView) itemLayout.findViewById(R.id.search_title);
                mean = (TextView) itemLayout.findViewById(R.id.brief_desc);
                sent = (TextView) itemLayout.findViewById(R.id.summary);
                word.setText(anItem.getWord());
                mean.setText(anItem.getMeaning());
                sent.setText(anItem.getSentence());
                return itemLayout;

            default:
                return null;
        }
    }
/*
	@Override
	public void onInit(int initStatus) {
	     
        //check for successful instantiation
    if (initStatus == TextToSpeech.SUCCESS) {
        if(myTTS.isLanguageAvailable(Locale.US)==TextToSpeech.LANG_AVAILABLE)
            myTTS.setLanguage(Locale.US);
    }
    else if (initStatus == TextToSpeech.ERROR) {
        Toast.makeText(mContext, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
    }
}
	*/
}
