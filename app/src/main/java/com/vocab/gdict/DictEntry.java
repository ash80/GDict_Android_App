package com.vocab.gdict;

class DictEntry {
    static final int THE_WORD = 0;
    static final int FOS = 1;
    static final int ONLY_MEAN = 2;
    static final int MEAN_SYN = 3;
    static final int MEAN_SYN_ANT = 4;
    static final int MEAN_SENT = 5;
    static final int MEAN_SENT_SYN = 6;
    static final int MEAN_SENT_SYN_ANT = 7;
    static final int ENCYCLOPEDIA = 8;

    private int entryType;
    private String word;
    private String fosFull;
    private String audioURL;
    private String fos;
    private String fosDesc;
    private String sentence;
    private String meaning;
    private String synonyms;
    private String antonyms;

    DictEntry(String w, String fosFull, String mean, String sent, String syn, String ant) {
        this.setEntryType(DictEntry.MEAN_SENT_SYN_ANT);
        setTheValues(w, null, mean, sent, fosFull, null, syn, ant);
    }

    DictEntry(String fosF, String fosD) {
        entryType = DictEntry.FOS;
        setTheValues(null, null, null, null, fosF, fosD, null, null);
    }

    DictEntry(String searchTitle, String briefDesc, String summary) {
        entryType = DictEntry.ENCYCLOPEDIA;
        setTheValues(searchTitle, null, briefDesc, summary, null, null, null, null);
    }

    DictEntry(int entT, String w, String audio) {
        this.setEntryType(entT);
        if (entT == THE_WORD)
            setTheValues(w, audio, null, null, null, null, null, null);
    }

    DictEntry(int entT, String w, String arg1, String arg2) {
        this.setEntryType(entT);
        if (entT == ONLY_MEAN)
            setTheValues(w, null, arg2, null, arg1, null, null, null);
    }

    DictEntry(int entT, String w, String arg1, String arg2, String arg3) {
        this.setEntryType(entT);
        if (entT == DictEntry.MEAN_SYN)
            setTheValues(w, null, arg2, null, arg1, null, arg3, null);
        else if (entT == DictEntry.MEAN_SENT)
            setTheValues(w, null, arg2, arg3, arg1, null, null, null);
    }

    DictEntry(int entT, String w, String arg1, String arg2, String arg3, String arg4) {
        this.setEntryType(entT);
        if (entT == DictEntry.MEAN_SYN_ANT)
            setTheValues(w, null, arg2, null, arg1, null, arg3, arg4);
        else if (entT == DictEntry.MEAN_SENT_SYN)
            setTheValues(w, null, arg2, arg3, arg1, null, arg4, null);
    }


    private void setTheValues(String w, String audio, String mean, String sent, String fosFull, String fosDesc, String syn, String ant) {
        switch (entryType) {
            case THE_WORD:
                this.setWord(w);
                this.setAudioURL(audio);
                break;
            case FOS:
                this.fosFull = fosFull;
                this.fosDesc = fosDesc;
                break;
            case ONLY_MEAN:
                this.setWord(w);
                this.fosFull = fosFull;
                this.meaning = mean;
                this.fos = getFosBrief();
                break;
            case MEAN_SYN:
                this.setWord(w);
                this.fosFull = fosFull;
                this.meaning = mean;
                this.synonyms = syn;
                this.fos = getFosBrief();
                break;
            case MEAN_SYN_ANT:
                this.setWord(w);
                this.fosFull = fosFull;
                this.meaning = mean;
                this.synonyms = syn;
                this.antonyms = ant;
                this.fos = getFosBrief();
                break;
            case MEAN_SENT:
                this.setWord(w);
                this.fosFull = fosFull;
                this.meaning = mean;
                this.sentence = sent;
                this.fos = getFosBrief();
                break;
            case MEAN_SENT_SYN:
                this.setWord(w);
                this.fosFull = fosFull;
                this.meaning = mean;
                this.synonyms = syn;
                this.sentence = sent;
                this.fos = getFosBrief();
                break;
            case MEAN_SENT_SYN_ANT:
                this.setWord(w);
                this.fosFull = fosFull;
                this.meaning = mean;
                this.synonyms = syn;
                this.sentence = sent;
                this.antonyms = ant;
                this.fos = getFosBrief();
                break;
            case ENCYCLOPEDIA:
                this.setWord(w);
                this.setMeaning(mean);
                this.setSentence(sent);
                break;
        }
    }

    String getFosBrief() {
        if (fosFull.equalsIgnoreCase("verb")) {
            return "(v)";
        } else if (fosFull.equalsIgnoreCase("noun")) {
            return "(n)";
        } else if (fosFull.equalsIgnoreCase("adjective")) {
            return "(adj)";
        } else if (fosFull.equalsIgnoreCase("adverb")) {
            return "(adv)";
        } else {
            return "(" + fosFull + ")";
        }
    }

    int getEntryType() {
        return entryType;
    }

    private void setEntryType(int entryType) {
        this.entryType = entryType;
    }

    String getWord() {
        return word;
    }

    private void setWord(String word) {
        this.word = word;
    }

//    public String getAudioURL() {
//        return audioURL;
//    }

    private void setAudioURL(String audio) {
        this.audioURL = audio;
    }

    String getFosFull() {
        return fosFull;
    }

//    public void setFosFull(String fosFull) {
//        this.fosFull = fosFull;
//    }

//    public String getFos() {
//        return fos;
//    }

//    public void setFos(String fos) {
//        this.fos = fos;
//    }

    String getFosDesc() {
        return fosDesc;
    }

//    public void setFosDesc(String fosDesc) {
//        this.fosDesc = fosDesc;
//    }

    String getSentence() {
        return sentence;
    }

    private void setSentence(String sentence) {
        this.sentence = sentence;
    }

    String getMeaning() {
        return meaning;
    }

    private void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    String getSynonyms() {
        return synonyms;
    }

//    public void setSynonyms(String synonyms) {
//        this.synonyms = synonyms;
//    }

    String getAntonyms() {
        return antonyms;
    }

//    public void setAntonyms(String antonyms) {
//        this.antonyms = antonyms;
//    }

}
