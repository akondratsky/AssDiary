package assdiary.controller;

import assdiary.Main;
import assdiary.model.DiaryModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;

import java.io.*;

public class Controller {

    @FXML   TextField keyFilter;
    @FXML   TextField assFilter;
    @FXML   ListView<String> keyListView;
    @FXML   ListView<String> assListView;
    @FXML   ListView<String> freqListView;
    @FXML   TextArea textArea;
    @FXML   Button btnHandle;
    @FXML   Button btnCurrentSave;

    private DiaryModel diaryModel;
    private String lastString = "";
    private File saveFile = new File("afsave.bin");
    private File initialDir;
    private File fileForDB = null;

    @FXML
    public void onSaveButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить...");
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Файл сохранения ASS", "*.ass");
        fileChooser.getExtensionFilters().add(extensionFilter);
        if (initialDir!=null) fileChooser.setInitialDirectory(initialDir);
        File file = fileChooser.showSaveDialog(Main.getMainStage());
        if (file!=null) {
            initialDir = file.getParentFile();
            saveConfig();
            diaryModel.save(file);
        }
        fileForDB = file;
        btnCurrentSave.setDisable(false);
    }

    @FXML
    public void saveForAssFinder() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить файл для AssFinder...");
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Файл анализатора AssFinder", "*.aff");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showSaveDialog(Main.getMainStage());
        if (file!=null) {
            diaryModel.saveForAssFinder(file);
        }
    }

    @FXML
    public void onLoadButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Загрузить...");
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Файл сохранения ASS", "*.ass");
        fileChooser.getExtensionFilters().add(extensionFilter);
        if (initialDir!=null) fileChooser.setInitialDirectory(initialDir);
        File file = fileChooser.showOpenDialog(Main.getMainStage());
        if (file!=null) {
            initialDir = file.getParentFile();
            saveConfig();
            diaryModel.load(file);
        }
        fileForDB = file;
        btnCurrentSave.setDisable(false);
    }

    @FXML
    public void onHandleButton() {
        String str = textArea.getText();
        if (!(str.equals(lastString)||str.equals(""))) {
            diaryModel.handleText(str);
            lastString = str;
            textArea.setText("");
        }
    }

    @FXML
    public void onCurrentSave() {
        if (fileForDB!=null) {
            diaryModel.save(fileForDB);
        }
    }

    private void saveConfig() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFile));
            oos.writeObject(initialDir);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        if (saveFile.exists()) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFile));
                initialDir = (File) ois.readObject();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        diaryModel = new DiaryModel();
        btnHandle.disableProperty().bind(diaryModel.inWork);
        keyListView.itemsProperty().bind(diaryModel.keyWords);
        assListView.itemsProperty().bind(diaryModel.assWords);
        freqListView.itemsProperty().bind(diaryModel.freqWords);
        keyListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)->{
            Platform.runLater(()-> {
                    assListView.getSelectionModel().select(-1);
                    diaryModel.changeAssList((String) newValue);
                    keyFilter.setText((String)newValue);
            });
        });
        assListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)->{
            Platform.runLater(()->{
                assFilter.setText((String) newValue);
                if (keyListView.getSelectionModel().getSelectedItem()!=null&&assListView.getSelectionModel().getSelectedItem()!=null)
                    diaryModel.changeFreqList((String) keyListView.getSelectionModel().getSelectedItem(), (String) assListView.getSelectionModel().getSelectedItem());
            });
        });
        keyFilter.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (diaryModel.tryChangeAssList(keyFilter.getText()))
                keyListView.getSelectionModel().select(keyFilter.getText());
        }));
        assFilter.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (diaryModel.tryChangeFreqList(keyFilter.getText(), assFilter.getText()))
                assListView.getSelectionModel().select(assFilter.getText());
        }));
        textArea.setOnKeyPressed(event -> {
            if (event.isControlDown()&&event.getCode().equals(KeyCode.ENTER))
                onHandleButton();
        });
    }
}