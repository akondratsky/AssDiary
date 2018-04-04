package assdiary.model;

import javafx.application.Platform;
import javafx.beans.property.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class DiaryModel {

    public final BooleanProperty inWork = new SimpleBooleanProperty();
    public final ListProperty<String> keyWords = new SimpleListProperty<>();
    public final ListProperty<String> assWords = new SimpleListProperty<>();
    public final ListProperty<String> freqWords = new SimpleListProperty<>();

    private AssCore asscore;
    private Lemmatizator lemmatizator = new Lemmatizator();

    public DiaryModel() {
        asscore = new AssCore();
        inWork.setValue(false);
    }

    public void handleText(String str) {
        inWork.setValue(true);
        new Thread(()->{
            ArrayList<String> kwList = lemmatizator.lemmatizate(str);
            asscore.fillAssCore(kwList);
            Platform.runLater(()-> {
                keyWords.set(asscore.getKeywords());
                keyWords.sort(Comparator.naturalOrder());
            });
            inWork.setValue(false);
        }).start();
    }

    public boolean tryChangeAssList(String str) {
        if (asscore.isExist(str)) {
            changeAssList(str);
            return true;
        } else
            return false;
    }
    public void changeAssList(String str) {
        assWords.setValue(asscore.getAssotiations(str));
        assWords.sort(Comparator.naturalOrder());
    }

    public boolean tryChangeFreqList(String key, String ass) {
        if (asscore.isExist(key, ass)) {
            changeFreqList(key, ass);
            return true;
        } else
            return false;
    }
    public void changeFreqList(String key, String ass) {
        freqWords.setValue(asscore.getFrequencies(key, ass));
    }

    public void load(File file) {
        ObjectInputStream ois;
        try {
            ois = new ObjectInputStream(new FileInputStream(file));
            asscore = (AssCore) ois.readObject();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        keyWords.set(asscore.getKeywords());
        keyWords.sort(Comparator.naturalOrder());
    }
    public void save(File file) {
        ObjectOutputStream  oos;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(asscore);
            oos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void saveForAssFinder(File file) {
        HashMap<String, HashMap<String, Integer>> map = asscore.createAssWeightMap();
        HashMap<String, Integer> freqmap = asscore.getFreqMap();
        ObjectOutputStream  oos;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(freqmap);
            oos.writeObject(map);
            oos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
