package com.vocab.gdict;


import java.io.IOException;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.ListActivity;
import android.content.Context;
import android.util.Xml;


class XMLResponseHandler {

    private static final String divTag = "div";
    private static final String spanTag = "span";
//    private static final String boldTag = "b";
    private static final String italicTag = "i";
//    private static final String olTag = "ol";
//    private static final String liTag = "li";
    private static final String tableTag = "table";
    private static final String trTag = "tr";

    private static final String dctEnter = "VpH2eb";
    private static final String dctEnter_old = "lr_dct_ent";
    private static final String searchTitle = "kno-ecr-pt kno-fb-ctx";
//    private static final String wikiSearch = "kp-blk";
    private static final String wikiSearch = "a1MHvb"; //"kp-blk _Z7 _RJe";
    private static final String wordName = "dDoNo"; // "vk_ans";
    private static final String wordName1 = "GgmXif"; // "dDoNo";

    private static final String phonetic = "S23sjd";
    private static final String phonetic_old = "lr_dct_ent_ph";

//    private static final String phonetic_span = "lr_dct_ph";
//    private static final String speaker = "lr_dct_spkr lr_dct_spkr_off";
    private static final String figSpeech = "pgRvse"; // "lr_dct_sf_h";
    private static final String figSpeech_old = "lr_dct_sf_h";

    private static final String fsDesc = "xpdxpnd vk_gy";

    private static final String sen = "thODed";
    private static final String subsen = "csWlI";
    private static final String sen_old = "lr_dct_sf_sen";
    private static final String subsen_old = "lr_dct_sf_subsen";

    private static final String infoInfoMean = "mQo3nc hsL7ld"; // "lr_dct_lbl_blk lr_dct_lbl_box";
    private static final String infoMean = "dfn";
    private static final String display_inline = "display:inline";
    private static final String infoMean_old = "lr_dct_lbl_blk lr_dct_lbl_box"; // "lr_dct_lbl_blk vk_gy";

    private static final String sentence = "vk_gy";

//    private static final String simiOppo = "q3q3Oc";
    private static final String similar_class = "pdpvld";
    private static final String opposite_class = "hVpeib";
//    private static final String simiOppoWords = "lLE0jd";

    private static final String briefDesc = "_CLb kno-fb-ctx";
    //private static final String briefDesc = "kno-fb-ctx";
//    private static final String briefDescAlt = "_gdf kno-fb-ctx";
    private static final String briefDescAlt = "_gdf";
    private static final String summary = "kno-rdesc";
    private static final String onlySummaryDesc = "viOShc"; // "_oDd";

    //private String mLat, mLng, mMag;
    //private boolean mIsParsingLat, mIsParsingLng, mIsParsingMag;
    //private final List<String> mResults = new ArrayList<String>();

    private XmlPullParser xpp;
    private int eventType;
    private boolean onlySummary = false;
    private Context ctx;
    private DictEntriesAdapter mAdapter;

    XMLResponseHandler(Context context, DictEntriesAdapter adpt) {
        ctx = context;
        mAdapter = adpt;
        //((ListActivity) ctx).getListView().setAdapter(mAdapter);
    }

    private void runDictNotFound() {
        ((ListActivity) ctx).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.add(new DictEntry("Dictionary Not Found",
                        "Either dictionary is not available for this query,\n" +
                                "or the device is not connected to the internet"));
            }
        });
    }

    //@Override
    //public List<String> handleResponse(HttpResponse response)
    void handleResponse(String response, int idx)
            throws IOException {
        try {
            XmlPullParserFactory factory;
            // Create the Pull Parser
            factory = XmlPullParserFactory.newInstance();
            factory.setValidating(false);
            factory.setFeature(Xml.FEATURE_RELAXED, true);
            factory.setNamespaceAware(true);
            xpp = factory.newPullParser();
            // Set the Parser's input to be the XML document in the HTTP Response
            //xpp.setInput(new InputStreamReader(IOUtils.toInputStream(response)));
            xpp.setInput(new StringReader(response));
            int idx1 = idx;
            // Get the first Parser event and start iterating over the XML document
            eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && (hasAttr(divTag, "class", dctEnter) ||
                        hasAttr(divTag, "class", dctEnter_old)) && ! (hasAttr(divTag, "class", phonetic) ||
                        hasAttr(divTag, "class", phonetic_old))) {
                    idx1--;
                    if (idx1 != 0)
                        eventType = xpp.next();
                } else {
                    eventType = xpp.next();
                }
                if (idx1 == 0 && eventType == XmlPullParser.START_TAG && (hasAttr(divTag, "class", dctEnter) ||
                        hasAttr(divTag, "class", dctEnter_old)))
                    break;
            }

            if (eventType == XmlPullParser.END_DOCUMENT && idx == 1) { // Checking if Wiki Info Exists
                xpp.setInput(new StringReader(response));
                eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG && hasAttr(divTag, "class", searchTitle))
                        break;
                    else if (eventType == XmlPullParser.START_TAG && hasAttr(divTag, "class", wikiSearch)) {
                        onlySummary = true;
                        break;
                    } else {
                        eventType = xpp.next();
                    }
                }
                if (eventType == XmlPullParser.END_DOCUMENT) {
                    runDictNotFound();
                }
                else {
                    String sT = "", bD = "", smry = "";

                    if (!onlySummary) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT)
                            sT = xpp.getText();
                        eventType = xpp.next();
                        while (!(eventType == XmlPullParser.START_TAG && (hasAttr(divTag, "class", briefDesc) || hasAttr(divTag, "class", briefDescAlt)))) {
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.END_DOCUMENT) {
                                runDictNotFound();
                                break;
                            }
                        }
                        while (eventType != XmlPullParser.TEXT)
                            eventType = xpp.next();
                        bD = xpp.getText();
                        eventType = xpp.next();
                        while (!((eventType == XmlPullParser.START_TAG && hasAttr(divTag, "class", summary)) ||
                                eventType == XmlPullParser.END_DOCUMENT)) {
                            eventType = xpp.next();
                        }
                        if (eventType != XmlPullParser.END_DOCUMENT) {
                            while (eventType != XmlPullParser.TEXT)
                                eventType = xpp.next();
                            smry = xpp.getText();
                        }
                    }
                    else {
                        while (!((eventType == XmlPullParser.START_TAG && hasAttr(divTag, "class", onlySummaryDesc)) ||
                                eventType == XmlPullParser.END_DOCUMENT)) {
                            eventType = xpp.next();
                        }
                        if (eventType != XmlPullParser.END_DOCUMENT) {
                            while (!(eventType == XmlPullParser.END_TAG && xpp.getName().equals(divTag))) {
                                if (eventType == XmlPullParser.TEXT)
                                    smry = smry + xpp.getText();
                                eventType = xpp.next();
                            }
                        } else {
                            runDictNotFound();
                        }
                    }


                    final String sT1 = sT;
                    final String bD1 = bD;
                    final String smry1 = smry;
                    ((ListActivity) ctx).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.add(new DictEntry(sT1, bD1, smry1));
                        }
                    });
                }
            }
            else {
                final String word = getWord();
                eventType = xpp.next(); //enter empty divTag
                eventType = xpp.next(); //enter divTag with lr_dct_ent_ph or another empty <div>
                String fos, fosDesc = "";
                fos = "";
                boolean j = false; //fos not ready
                boolean k = false; // fosDesc not ready
                boolean phV = false; // is Phrasal verb?
                String pronunciation = "";
                if (eventType == XmlPullParser.START_TAG && (hasAttr(divTag, "class", phonetic) ||
                        hasAttr(divTag, "class", phonetic_old)))
                    pronunciation = getAudioLink(); //exits at the closing divTag of Audio
                final String final_pronun = pronunciation;
                if (eventType == XmlPullParser.START_TAG && hasAttr(divTag, "class", sentence)) {
                    while (!(eventType == XmlPullParser.END_TAG && xpp.getName().equals(divTag))) {
                        //fosDesc="";
                        if (eventType == XmlPullParser.TEXT) {
                            if (!phV && xpp.getText().trim().equalsIgnoreCase("phrasal verb of"))
                                fos = "verb";
                            else if (!phV)
                                fos = "noun";
                            phV = true;
                            fosDesc = fosDesc + xpp.getText();
                            j = true;
                            k = true;
                        }
                        eventType = xpp.next();
                    }
                }
                else
                ((ListActivity) ctx).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.add(new DictEntry(DictEntry.THE_WORD, word, final_pronun));
                    }
                });
                while (!(eventType == XmlPullParser.END_DOCUMENT || (
                        eventType == XmlPullParser.START_TAG &&
                                (hasAttr(divTag, "class", dctEnter) ||
                        hasAttr(divTag, "class", dctEnter_old))))) {
                    eventType = xpp.next();
                    if (!phV && (eventType == XmlPullParser.START_TAG &&
                            (hasAttr(divTag, "class", figSpeech) ||
                            hasAttr(divTag, "class", figSpeech_old)))) {
                        fos = getFos();
                        j = true; //fos ready
                        System.out.println(fos);
                        //eventType = xpp.next();
                    }
                    if (!phV && eventType == XmlPullParser.START_TAG && (hasAttr(divTag, "class", fsDesc) || hasAttr(divTag, "class", sentence))) {
                        fosDesc = getFosDesc();

                        k = true; //fosDesc ready
                        System.out.println(fosDesc);

                        //seventType = xpp.next();
                    }

                    if (j && k) {
                        final String fos1 = fos;
                        final String fosDesc1 = fosDesc;
                        ((ListActivity) ctx).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.add(new DictEntry(fos1, fosDesc1));
                            }
                        });
                        j = false;
                        k = false;
                    }

                    if (eventType == XmlPullParser.START_TAG && (hasAttr(divTag, "class", sen) ||
                            hasAttr(divTag, "class", sen_old))) {
                        printRests(word, final_pronun, fos);
                    }
                    if (eventType == XmlPullParser.START_TAG && (hasAttr(divTag, "class", subsen) ||
                            hasAttr(divTag, "class", subsen_old))) {
                        printRests(word, final_pronun, fos);
                    }
                }
                // TODO: Code for additional dictionary
                int nextIdx = idx;
//                eventType = xpp.next();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG && (hasAttr(divTag, "class", dctEnter) ||
                            hasAttr(divTag, "class", dctEnter_old))) {
                        nextIdx++;
                        break;
                    } else {
                        eventType = xpp.next();
                    }

                }
                if (nextIdx != idx) {
                    handleResponse(response, nextIdx);
                }

            }
        } catch (XmlPullParserException e) {
        }
        //return null;
    }

    private String getWord() throws XmlPullParserException, IOException {
        while (!(eventType == XmlPullParser.START_TAG && (hasAttr(divTag, "class", wordName) || hasAttr(divTag, "class", wordName1)))) {
            eventType = xpp.next();
        }
        //eventType = xpp.next(); // next to start of <div>
        String wordNameTemp = "";
        while (!(eventType == XmlPullParser.END_TAG && xpp.getName().equals(divTag))) {
            if (eventType == XmlPullParser.TEXT && wordNameTemp.equals(""))
                // wordNameTemp = wordNameTemp + xpp.getText();
                 wordNameTemp = xpp.getText();

            if (eventType == XmlPullParser.START_TAG && xpp.getName().equals("sup")) {
                while (!(eventType == XmlPullParser.END_TAG && xpp.getName().equals("sup")))
                    eventType = xpp.next();
            }
            eventType = xpp.next(); //exit wordName divTag
        }
        return wordNameTemp;
    }


    private String getAudioLink() throws XmlPullParserException, IOException {
        int i = 1;
        String pronunciation="";

        while (!(eventType == XmlPullParser.END_TAG && xpp.getName().equals(divTag))) {
            eventType = xpp.next();
            if (eventType == XmlPullParser.TEXT)
                pronunciation += xpp.getText();
            if (eventType == XmlPullParser.START_TAG && xpp.getName().equals(divTag))
                i++;
            if (eventType == XmlPullParser.END_TAG && xpp.getName().equals(divTag))
                i--;
            if (i == 0)
                break;
        }
        return pronunciation;
    }

    private String getFos() throws XmlPullParserException, IOException {
        while (eventType != XmlPullParser.TEXT) {
            eventType = xpp.next();
        }
        String fosTemp = xpp.getText();
        xpp.next(); //exiting italicTag
        eventType = xpp.next(); //exiting divTag
        if (eventType == XmlPullParser.START_TAG && xpp.getName().equals(divTag)) {
            while (!(eventType == XmlPullParser.END_TAG && xpp.getName().equals(divTag)))
                eventType = xpp.next(); // at optional exit divTag
            eventType = xpp.next(); // at main exit divTag
        }
        return fosTemp;
    }

    private String getFosDesc() throws XmlPullParserException, IOException {
        String fosDesTemp = "";
        while (!(eventType == XmlPullParser.END_TAG && xpp.getName().equals(divTag))) {
            eventType = xpp.next();
            if (eventType == XmlPullParser.TEXT)
                fosDesTemp += xpp.getText();
        }
        return fosDesTemp;
    }

    private void printRests(String w, String pronunciation, String fos) throws XmlPullParserException, IOException {
        boolean isSubsen = false;
        if (eventType == XmlPullParser.START_TAG && (hasAttr(divTag, "class", subsen) ||
                hasAttr(divTag, "class", subsen_old))) {
            isSubsen = true;
        }
        //List<String> Meaning;
		/*while(!(eventType == XmlPullParser.START_TAG && xpp.getAttributeValue(null,"class").equals(sen))) {
			eventType = xpp.next();
			continue;
		}*/
        String mean = "";
        String sent = "";
        String syn = "";
        String ant = "";
        boolean bothDone = false;

        int i = 1;
        while (!(eventType == XmlPullParser.END_TAG && xpp.getName().equals(divTag))) {
            eventType = xpp.next();
            if (eventType == XmlPullParser.START_TAG && hasAttr(divTag, "style", "float:left")) {
                while (!(eventType == XmlPullParser.END_TAG && xpp.getName().equals(divTag)))
                    eventType = xpp.next();
                eventType = xpp.next(); //exit of optional divTag
            }
            if (eventType == XmlPullParser.START_TAG
                    && ((hasAttr(divTag, "data-dobid", infoMean)
                    && hasAttr(divTag, "style", display_inline))
                    || hasAttr(spanTag, "class", infoInfoMean))) {
                if(xpp.getName().equals(spanTag)) {
                    while (!(eventType == XmlPullParser.START_TAG && xpp.getName().equals(divTag))) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT)
                            if (mean.equals(""))
                                mean = xpp.getText(); //Getting meaning attribute
                            else
                                mean += xpp.getText();
                    }
                }
                if (!mean.equals(""))
                    mean = "(" + mean + ") ";
                int meanIdx = 1;
                while (meanIdx != 0) {
                    eventType = xpp.next();
                    if ((eventType == XmlPullParser.END_TAG && xpp.getName().equals(divTag)))
                        meanIdx--;
                    else if ((eventType == XmlPullParser.START_TAG && xpp.getName().equals(divTag)))
                        meanIdx++;
                    else if (eventType == XmlPullParser.TEXT)
                        mean += xpp.getText();
                }
//                eventType = xpp.next(); //Getting out div that sourrounded the span tag
            }
//            if (eventType == XmlPullParser.TEXT) {
//                if (!mean.equals(""))
//                    mean = "(" + mean + ") ";
//                mean = mean + xpp.getText(); //getting meaning
//            }
        } // meaning received exiting at divTag
        while (i != 0) {
            eventType = xpp.next(); //i=1 first time
            if (eventType == XmlPullParser.START_TAG && xpp.getName().equals(divTag))
                i++; //i =2 when sentence/table found
            if (eventType == XmlPullParser.START_TAG && hasAttr(divTag, "class", sentence)) {
                String SentenceTemp = "";
                while (!(eventType == XmlPullParser.END_TAG && xpp.getName().equals(divTag))) {
                    eventType = xpp.next();
                    if (eventType == XmlPullParser.TEXT)
                        SentenceTemp += xpp.getText();
                } //exits at the divTag end of sentence
                // System.out.println(SentenceTemp);
                sent = SentenceTemp;
            }

            // Similar:
            if (eventType == XmlPullParser.START_TAG && hasAttr(divTag, "class", similar_class)) {
                String similarWords = "";
                int synIdx = 2;
                while (true) {
                    eventType = xpp.next();
                    if (eventType == XmlPullParser.START_TAG && hasAttr(divTag, "role", "listitem")) {
                        break;
                    }
                }
                synIdx = 1;
                while (true) {
                    while (synIdx > 0) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.END_TAG && xpp.getName().equals(divTag)) {
                            synIdx--;
                        }
                        else if (eventType == XmlPullParser.START_TAG && xpp.getName().equals(divTag)) {
                            synIdx++;
                        }
                        else if (eventType == XmlPullParser.TEXT)
                            similarWords += xpp.getText();

                    }
                    eventType = xpp.next();
                    if (eventType == XmlPullParser.START_TAG && hasAttr(divTag, "role", "listitem")) {
                        similarWords += ", ";
                        synIdx = 1;
                    }
                    else {
                        break;
                    }
                }
                syn = similarWords;
                synIdx = 1; // checking if opposite exists
                while (synIdx < 3 && synIdx >= 0) {
                    eventType = xpp.next();
                    if (eventType == XmlPullParser.END_TAG && xpp.getName().equals(divTag)) {
                        synIdx--;
                    }
                    else if (eventType == XmlPullParser.START_TAG && xpp.getName().equals(divTag)) {
                        synIdx++;
                    }
                }
                if (! hasAttr(divTag, "class", opposite_class)) {
                    bothDone = true;
                }
            }

            // Opposite:
            if (eventType == XmlPullParser.START_TAG && hasAttr(divTag, "class", opposite_class)) {
                String oppositeWords = "";
                int oppIdx = 2;

                while (true) {
                    eventType = xpp.next();
                    if (eventType == XmlPullParser.START_TAG && hasAttr(divTag, "role", "listitem")) {
                        break;
                    }
                }

                oppIdx = 1;
                while (true) {
                    while (oppIdx > 0) {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.END_TAG && xpp.getName().equals(divTag)) {
                            oppIdx--;
                        }
                        else if (eventType == XmlPullParser.START_TAG && xpp.getName().equals(divTag)) {
                            oppIdx++;
                        }
                        else if (eventType == XmlPullParser.TEXT)
                            oppositeWords += xpp.getText();
                    }
                    eventType = xpp.next();
                    if (eventType == XmlPullParser.START_TAG && hasAttr(divTag, "role", "listitem")) {
                        oppositeWords += ", ";
                        oppIdx = 1;
                    }
                    else {
                        break;
                    }
                }
                ant = oppositeWords;
                bothDone = true;
            }

//            if (eventType == XmlPullParser.START_TAG && hasAttr(tableTag, "class", synAntEnt)) {
//                String NymsTemp = "";
//                while (!(eventType == XmlPullParser.END_TAG && xpp.getName().equals(trTag))) {
//                    eventType = xpp.next();
//
//                    //Detecting italic informal/archaic
//                    if (eventType == XmlPullParser.START_TAG && xpp.getName().equals(italicTag))
//                        while (!(eventType == XmlPullParser.END_TAG && xpp.getName().equals(italicTag)))
//                            eventType = xpp.next();
//
//                    //Removing More
//                    if (eventType == XmlPullParser.START_TAG && hasAttr(spanTag, "class", synAntMore)) {
//                        eventType = xpp.next(); //at more
//                        eventType = xpp.next(); //at exit spanTag
//                    }
//
//                    //Removing sentences
//                    if (eventType == XmlPullParser.START_TAG && (hasAttr(divTag, "class", "_iYd xpdxpnd xpdnoxpnd vk_gy") || hasAttr(divTag, "class", sentence))) {
//                        while (!(eventType == XmlPullParser.END_TAG && xpp.getName().equals(divTag)))
//                            eventType = xpp.next();
//                    }
//
//                    if (eventType == XmlPullParser.TEXT)
//                        NymsTemp += xpp.getText();
//                }
//                System.out.println(NymsTemp); //print out Syn/Ant
//                if (NymsTemp.charAt(0) == 'a' || NymsTemp.charAt(0) == 'A') {
//                    ant = NymsTemp;
//                }
//                else
//                    syn = NymsTemp;
//            }

            if ((eventType == XmlPullParser.END_TAG && xpp.getName().equals(divTag)))
                i--; //i=1 if sentence/table was there; i=0 nothing is left
            if (bothDone) {
                break;
            }
        }
        if (sent.equals("") && syn.equals("")) { //ONLY_MEAN
            final String fos1 = fos;
            final String mean1 = mean;
            final String w1 = w;
            final String pronunciation1 = pronunciation;
            ((ListActivity) ctx).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.add(new DictEntry(DictEntry.ONLY_MEAN, w1, pronunciation1, fos1, mean1));
                }
            });
        }
        else if (sent.equals("") && ant.equals("")) {
            final String fos1 = fos;
            final String mean1 = mean;
            final String syn1 = syn;
            final String w1 = w;
            final String pronunciation1 = pronunciation;
            ((ListActivity) ctx).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.add(new DictEntry(DictEntry.MEAN_SYN, w1, pronunciation1, fos1, mean1, syn1));
                }
            });
        }
        else if (sent.equals("")) {
            final String fos1 = fos;
            final String mean1 = mean;
            final String syn1 = syn;
            final String ant1 = ant;
            final String w1 = w;
            final String pronunciation1 = pronunciation;
            ((ListActivity) ctx).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.add(new DictEntry(DictEntry.MEAN_SYN_ANT, w1, pronunciation1, fos1, mean1, syn1, ant1));
                }
            });
        }
        else if (syn.equals("")) {
            final String fos1 = fos;
            final String mean1 = mean;
            final String sent1 = sent;
            final String w1 = w;
            final String pronunciation1 = pronunciation;
            ((ListActivity) ctx).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.add(new DictEntry(DictEntry.MEAN_SENT, w1, pronunciation1, fos1, mean1, sent1));
                }
            });
        }
        else if (ant.equals("")) {
            final String fos1 = fos;
            final String mean1 = mean;
            final String sent1 = sent;
            final String syn1 = syn;
            final String w1 = w;
            final String pronunciation1 = pronunciation;
            ((ListActivity) ctx).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.add(new DictEntry(DictEntry.MEAN_SENT_SYN, w1, pronunciation1, fos1, mean1, sent1, syn1));
                }
            });
        }
        else {
            final String fos1 = fos;
            final String mean1 = mean;
            final String sent1 = sent;
            final String syn1 = syn;
            final String ant1 = ant;
            final String w1 = w;
            final String pronunciation1 = pronunciation;
            ((ListActivity) ctx).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.add(new DictEntry(w1, pronunciation1, fos1, mean1, sent1, syn1, ant1));
                }
            });
        }
        if (isSubsen) {
            eventType = xpp.next(); //exiting at the div matching with subsen
        }
    }

    private boolean hasAttr(String tag, String attr, String val) {
        if (xpp.getName().equals(tag)) {
            if (xpp.getAttributeValue(null, attr) != null) {
                //if(xpp.getAttributeValue(null,attr).equals(val))
                return xpp.getAttributeValue(null, attr).contains(val);
            }
        }
        return false;
    }

}