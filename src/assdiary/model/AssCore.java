package assdiary.model;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AssCore implements Serializable {

    // максимальная дистанция между словами
    private static int maxParseDistance = 10;

    private HashMap<String, HashMap<String, HashMap<Integer, Integer>>> asscoreMap = new HashMap<>();
    private HashMap<String, Integer> freqMap = new HashMap<>();

    void fillAssCore(ArrayList<String> list) {
        if (list==null||list.size()==0) return;

        HashMap<String, HashMap<Integer, Integer>> assMap;
        HashMap<Integer, Integer> assFreqMap;

        String keyWord;
        String assWord;
        int dist;

        for (int i=0; i<list.size(); i++) {
            keyWord = list.get(i);
            if (keyWord.equals("")||keyWord==null) continue;
            if (!freqMap.containsKey(keyWord))
                freqMap.put(keyWord,1);
            else
                freqMap.put(keyWord,freqMap.get(keyWord)+1);
            for (int j=0; j<list.size(); j++) {
                assWord = list.get(j);
                if (!keyWord.equals(assWord)) {
                    dist = Math.abs(i-j);
                    if (dist>maxParseDistance) continue;
                    if (!asscoreMap.containsKey(keyWord)) {
                        assMap = new HashMap<>();
                        assFreqMap = new HashMap<>();
                        assFreqMap.put(dist, 1);
                        assMap.put(assWord, assFreqMap);
                        asscoreMap.put(keyWord, assMap);
                    } else {
                        assMap = asscoreMap.get(keyWord);
                        if (!assMap.containsKey(assWord)) {
                            assFreqMap = new HashMap<>();
                            assFreqMap.put(dist, 1);
                            assMap.put(assWord, assFreqMap);
                        } else {
                            assFreqMap = asscoreMap.get(keyWord).get(assWord);
                            if (!assFreqMap.containsKey(dist)) {
                                assFreqMap.put(dist, 1);
                            } else {
                                assFreqMap.put(dist, assFreqMap.get(dist)+1);
                            }
                        }
                    }
                }
            }
        }
    }

    public ObservableList<String> getKeywords() {
        return FXCollections.observableArrayList(asscoreMap.keySet());
    }
    public ObservableList<String> getAssotiations(String keyword) {
        if (asscoreMap.containsKey(keyword)) {
            //return asscoreMap.get(keyword).keySet();
            return FXCollections.observableArrayList(asscoreMap.get(keyword).keySet());
        } else
            return null;
    }
    public ObservableList<String> getFrequencies(String key, String ass) {
        ArrayList<String> list = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : asscoreMap.get(key).get(ass).entrySet()) {
            list.add(entry.getKey().toString() + "\t\t\t" + entry.getValue().toString());
        }
        return FXCollections.observableArrayList(list);

    }

    public boolean isExist(String key) {
        return asscoreMap.containsKey(key);
    }
    public boolean isExist(String key, String ass) {
        if (asscoreMap.containsKey(key))
            return asscoreMap.get(key).containsKey(ass);
        else return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, HashMap<String, HashMap<Integer, Integer>>> asscoreEntry : asscoreMap.entrySet()) {
            sb.append(asscoreEntry.getKey())
                    .append(";;;\r\n");
            for (Map.Entry<String, HashMap<Integer, Integer>> assEntry : asscoreEntry.getValue().entrySet()) {
                sb.append(";")
                        .append(assEntry.getKey())
                        .append(";;\r\n");
                for (Map.Entry<Integer, Integer> assFreqEntry : assEntry.getValue().entrySet()) {
                    sb.append(";;")
                            .append(assFreqEntry.getKey())
                            .append(";")
                            .append(assFreqEntry.getValue())
                            .append("\r\n");
                }
            }
        }
        return sb.toString();
    }

    // операция создания унифицированного представления ассоциативной карты без учета дистанций между словами
    // используется для создания файла AssFinder
    public HashMap<String, HashMap<String, Integer>> createAssWeightMap() {
        HashMap<String, HashMap<String, Integer>> map = new HashMap<>();
        HashMap<String, Integer> assmap;
        for (Map.Entry<String, HashMap<String, HashMap<Integer, Integer>>> entryKeys : asscoreMap.entrySet()) {
            assmap = new HashMap<>();
            for (Map.Entry<String, HashMap<Integer, Integer>> entryAssz : entryKeys.getValue().entrySet()) {
                assmap.put(entryAssz.getKey(), getIntFromFreqMap(entryAssz.getValue()));
            }
            map.put(entryKeys.getKey(), assmap);
        }
        return map;
    }

    public HashMap<String, Integer> getFreqMap() {
        return freqMap;
    }

    private int getIntFromFreqMap(HashMap<Integer, Integer> freqmap) {
        int freq = 0;
        for (Map.Entry<Integer, Integer> entry : freqmap.entrySet()) {
            freq += entry.getValue();
        }
        return freq;
    }

}
