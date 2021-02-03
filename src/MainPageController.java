import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainPageController implements Initializable {
    static String MasterPW;

    @FXML
    private ListView<File> FilesList;
    @FXML
    private TreeView<String> fileExplorer;
    @FXML
    private Button dontHavePassword;
    @FXML
    private Button createMP;
    @FXML
    private Label error;
    @FXML
    public Label fileName;
    @FXML
    public Label fileSize;
    @FXML
    public Label filePath;
    @FXML
    public Label fileCreated;
    @FXML
    public TextField initMasterPasswordText;
    @FXML
    public TextField MasterPasswordText;
    @FXML
    public ImageView MPicon;
    @FXML
    private Label PWerror;
    @FXML
    private Label enterMasterPW;
    @FXML
    private Button enterPW;

    private TreeItem<String> root;
    private boolean PasswordManagerState = false;

    public static String getFileName(File file){
        return FileSystemView.getFileSystemView().getSystemDisplayName(file);
    }


    public void initialize(URL location, ResourceBundle resources){
        File[] files = File.listRoots();
        Image directoryIcon = new Image(ClassLoader.getSystemResourceAsStream("Icons/BootCamp.png"),22,22,false,false);
        TreeItem<String> rootItem = new TreeItem<>("My Computer",new ImageView(directoryIcon));
        root = rootItem;
        for (File name : files){
            FilePathTreeItem childItem = new FilePathTreeItem(name);
            rootItem.getChildren().add(childItem);

        }
        fileExplorer.getSelectionModel().selectedItemProperty().addListener((ChangeListener) (observable, oldValue, newValue) -> {
            FilePathTreeItem selectedItem = (FilePathTreeItem) newValue;
            this.fileName.setText(selectedItem.getValue());
            Double size = (double) selectedItem.getFile().length();
            String text = String.format("%.2f Bytes", size);
            if (size >= 1024){ //kilobyte
                size = size / 1024;
                text = String.format("%.2f KB", size);
                if (size >= 1024){ //megabyte
                    size = size / 1024;
                    text = String.format("%.2f MB", size);
                    if(size >= 1024){ //gigabyte
                        size = size / 1024;
                        text = String.format("%.2f GB", size);
                        if(size >= 1024){ //terabyte
                            size = size / 1024;
                            text = String.format("%.2f TB", size);
                        }
                    }
                }
            }
            this.fileSize.setText(text);
            this.filePath.setText(selectedItem.getFile().getAbsolutePath());
            Path path = Paths.get(selectedItem.getFile().getPath());
            BasicFileAttributeView basicfile = Files.getFileAttributeView(path, BasicFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);
            try {
                BasicFileAttributes attr = basicfile.readAttributes();
                long date = attr.creationTime().toMillis();
                Instant instant = Instant.ofEpochMilli(date);
                fileCreated.setText(String.valueOf(LocalDateTime.ofInstant(instant, ZoneId.systemDefault())).replace("T"," "));
            } catch (IOException e){
                e.printStackTrace();
            }
        });
        fileExplorer.setRoot(rootItem);
        //FilesList.setCellFactory(new FileCellFactory());
    }


    public void compressFile(){
        FilePathTreeItem selected = (FilePathTreeItem) fileExplorer.getSelectionModel().getSelectedItem();
        if(selected == null){
            error.setTextFill(Color.RED);
            error.setText("Error: Select File to compress first");
            return;
        }
        File toCompress = selected.getFile();
        HuffmanEncode encoder = new HuffmanEncode(HuffmanEncode.MODE_COMPRESS,toCompress);
        System.out.println(toCompress);
        try {
            Thread ex = new Thread(encoder);
            encoder.run();
            //File compressed = encoder.Encode(toCompress);
            error.setText("File Successfully compressed. File name:" + encoder.returnFile);
        } catch (Exception e){
            error.setText("Unexpected error happened, try again.");
            error.setTextFill(Color.RED);
        }
        refreshFiles(selected);
    }


    public void extractFile(){
        FilePathTreeItem selected = (FilePathTreeItem) fileExplorer.getSelectionModel().getSelectedItem();
        if(selected == null){
            error.setTextFill(Color.RED);
            error.setText("Error: Select compressed file to extract first");
            return;
        }
        File toExtract = selected.getFile();
        HuffmanEncode decoder = new HuffmanEncode(HuffmanEncode.MODE_EXTRACT,toExtract);
        File extracted = null;
        try {
            Thread ex = new Thread(decoder);
            decoder.run();
            //extracted = decoder.Decode(toExtract);
        } catch (Exception e){
            e.printStackTrace();
            error.setText("Unexpected error happened, try again.");
            error.setTextFill(Color.RED);
        }
        if (decoder.returnFile == null){
            error.setText("File is not Compressed with Huffman Algorithm");
            error.setTextFill(Color.RED);
        } else{
            error.setText("File Successfully Extracted. File name:" + decoder.returnFile);
        }
        refreshFiles(selected);
    }


    public void renameFile(){
        FilePathTreeItem selected = (FilePathTreeItem) fileExplorer.getSelectionModel().getSelectedItem();
        if(selected == null){
            error.setTextFill(Color.RED);
            error.setText("Error: Select something to rename first!");
            return;
        }
        File toRename = selected.getFile();
        try {
            //Load
            Stage renameBox = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RenamePage.fxml"));
            Parent layout = loader.load();
            Scene RenamePage = new Scene(layout);
            RenamePageController cont = loader.getController();
            cont.label.setText("New name for " + toRename.getName());
            renameBox.setScene(RenamePage);
            renameBox.initModality(Modality.APPLICATION_MODAL);
            renameBox.setTitle("Renaming " + toRename.getName());
            renameBox.setResizable(false);
            renameBox.showAndWait();
            //Load end
            if (cont.textString != null) {
                String newName = cont.textString;
                String fullName = toRename.getAbsolutePath().replace(FilenameUtils.removeExtension(toRename.getName()), newName);
                File newFile = new File(fullName);
                boolean t = toRename.renameTo(newFile);
                if (t) {
                    error.setTextFill(Color.GREEN);
                    error.setText("File successfully Renamed to " + newName);
                }
            } else{
                error.setTextFill(Color.RED);
                error.setText("Renaming Canceled");
            }
        } catch (SecurityException se){
            error.setTextFill(Color.RED);
            error.setText("File is protected.");
        }
        catch (Exception e){
            e.printStackTrace();
            error.setTextFill(Color.RED);
            error.setText("An unexpected error happened. Please try again.");
        }
        refreshFiles(selected);
    }


    public void deleteFile(){
        FilePathTreeItem selected = (FilePathTreeItem) fileExplorer.getSelectionModel().getSelectedItem();
        if(selected == null){
            error.setTextFill(Color.RED);
            error.setText("Error: Select something to delete first!");
            return;
        }
        File toDelete = selected.getFile();
        try {
            //Load
            Stage confirmBox = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Confirm.fxml"));
            Parent layout = loader.load();
            Scene RenamePage = new Scene(layout);
            ConfirmController cont = loader.getController();
            cont.text.setText("Permanently Delete " + toDelete.getName() + "?");
            confirmBox.setScene(RenamePage);
            confirmBox.initModality(Modality.APPLICATION_MODAL);
            confirmBox.setTitle("Confirm Delete " + toDelete.getName());
            confirmBox.setResizable(false);
            confirmBox.showAndWait();
            //Load end
            if (cont.answer) {
                if (toDelete.delete()) {
                    error.setTextFill(Color.GREEN);
                    error.setText("Permanently deleted " + toDelete.getName());
                    refreshFiles(selected);
                } else {
                    error.setTextFill(Color.RED);
                    error.setText("Couldn't Delete file.");
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            error.setText("An unexpected error happened. Please try again.");
        }



    }


    public void encryptFile(){
        FilePathTreeItem selected = (FilePathTreeItem) fileExplorer.getSelectionModel().getSelectedItem();
        if(selected == null){
            error.setTextFill(Color.RED);
            error.setText("Error: Select File to encrypt first");
            return;
        }
        Encryptor encryptor = new Encryptor();
        File toEncrypt = selected.getFile();
        try {
            //Load
            Stage renameBox = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RenamePage.fxml"));
            Parent layout = loader.load();
            Scene RenamePage = new Scene(layout);
            RenamePageController cont = loader.getController();
            cont.label.setText("Enter password for " + toEncrypt.getName());
            renameBox.setScene(RenamePage);
            renameBox.initModality(Modality.APPLICATION_MODAL);
            renameBox.setTitle("Encrypting " + toEncrypt.getName());
            renameBox.setResizable(false);
            renameBox.showAndWait();
            //Load end
            if (cont.textString != null) {
                String key = cont.textString;
                boolean t = encryptor.Encrypt(key,toEncrypt);
                if (t) {
                    error.setTextFill(Color.GREEN);
                    error.setText("File successfully Encrypted.");
                }
            } else{
                error.setTextFill(Color.RED);
                error.setText("Encrypting Canceled");
            }
        } catch (SecurityException se){
            error.setTextFill(Color.RED);
            error.setText("File is protected.");
        } catch (Exception e){
            e.printStackTrace();
            error.setTextFill(Color.RED);
            error.setText("An unexpected error happened. Please try again.");
        }
        refreshFiles(selected);
    }


    public void decryptFile(){
        FilePathTreeItem selected = (FilePathTreeItem) fileExplorer.getSelectionModel().getSelectedItem();
        if(selected == null){
            error.setTextFill(Color.RED);
            error.setText("Error: Select File to encrypt first");
            return;
        }
        Encryptor decryptor = new Encryptor();
        File toDecrypt = selected.getFile();
        try {
            //Load
            Stage renameBox = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RenamePage.fxml"));
            Parent layout = loader.load();
            Scene RenamePage = new Scene(layout);
            RenamePageController cont = loader.getController();
            cont.label.setText("Enter password for " + toDecrypt.getName());
            renameBox.setScene(RenamePage);
            renameBox.initModality(Modality.APPLICATION_MODAL);
            renameBox.setTitle("Decrypting " + toDecrypt.getName());
            renameBox.setResizable(false);
            renameBox.showAndWait();
            //Load end
            if (cont.textString != null) {
                String key = cont.textString;
                int t = decryptor.Decrypt(key,toDecrypt);
                if (t == 1) {
                    error.setTextFill(Color.GREEN);
                    error.setText("File successfully Decrypted.");
                } else if (t == -1){
                    error.setTextFill(Color.RED);
                    error.setText("Wrong Password for " + toDecrypt.getName());
                }
            } else{
                error.setTextFill(Color.RED);
                error.setText("Decrypting Canceled");
            }
        } catch (SecurityException se){
            error.setTextFill(Color.RED);
            error.setText("File is protected.");
        } catch (Exception e){
            e.printStackTrace();
            error.setTextFill(Color.RED);
            error.setText("An unexpected error happened. Please try again.");
        }
        refreshFiles(selected);
    }


    public void refresh(){
        ObservableList<TreeItem<String>> children = root.getChildren();
        System.out.println(children.size());
        for (int i = 0; i < children.size() ; i++){
            FilePathTreeItem t = (FilePathTreeItem) children.get(i);
            t.getChildren().setAll(t.buildChildren(t));
        }
    }


    private void refreshFiles(FilePathTreeItem child){
        FilePathTreeItem parent = (FilePathTreeItem) child.getParent();
        if (parent.isExpanded()){
            parent.setExpanded(false);
            parent.getChildren().setAll(parent.buildChildren(parent));
            parent.setExpanded(true);
        }
    }


    public void createMasterPassword(){
        initMasterPasswordText.setDisable(false);
        initMasterPasswordText.setVisible(true);
        createMP.setDisable(false);
        createMP.setVisible(true);
    }


    public void initMasterPassword(){
        try {
            String masterP = initMasterPasswordText.getText();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] keyHash = digest.digest(masterP.getBytes(StandardCharsets.UTF_8));
            File masterPWFile = new File("MP.fe");
            FileOutputStream fileOutputStream = new FileOutputStream(masterPWFile);
            fileOutputStream.write(keyHash);
            fileOutputStream.close();
            masterPWFile.setReadOnly();
            File passwords = new File("passwords.fe");
            fileOutputStream = new FileOutputStream(passwords);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            PasswordBox[] passwordBoxes = new PasswordBox[12];
            for (int i = 0; i<12 ; i++){
                passwordBoxes[i] = new PasswordBox(null,null,null, null);
            }
            objectOutputStream.writeObject(passwordBoxes);
            objectOutputStream.flush();
            objectOutputStream.close();
            fileOutputStream.close();
            PWerror.setTextFill(Color.GREEN);
            PWerror.setText("Password Created Successfully.");
            initMasterPasswordText.setDisable(true);
            initMasterPasswordText.setVisible(false);
            createMP.setDisable(true);
            createMP.setVisible(false);
        } catch (FileNotFoundException e){
            PWerror.setTextFill(Color.RED);
            PWerror.setText("You already have Master Password.");
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public void enterMasterPassword() throws Exception{
        if(!PasswordManagerState) {
            File masterPWFile = new File("MP.fe");
            if(masterPWFile.exists()){
                String masterP = MasterPasswordText.getText();
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] keyHash = digest.digest(masterP.getBytes(StandardCharsets.UTF_8));
                byte[] fileByte = new byte[(int) masterPWFile.length()];
                FileInputStream fileInputStream = new FileInputStream(masterPWFile);
                fileInputStream.read(fileByte);
                System.out.println(Arrays.toString(fileByte));
                System.out.println(Arrays.toString(keyHash));
                if(Arrays.equals(keyHash,fileByte)){
                        PasswordManagerState = true;
                        MasterPW = masterP;
                        Stage PasswordStage = new Stage();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("PasswordManager.fxml"));
                        Parent layout = loader.load();
                        Scene PasswordManager = new Scene(layout);
                        PasswordManagerController cont = loader.getController();
                        PasswordStage.setScene(PasswordManager);
                        PasswordStage.setTitle("Password Manager");
                        PasswordStage.show();
                } else {
                    PWerror.setTextFill(Color.RED);
                    PWerror.setText("Wrong Master Password.");
                }
            } else {
                PWerror.setTextFill(Color.RED);
                PWerror.setText("You don't have Master Password. Create one.");
            }
        } else {
            PWerror.setTextFill(Color.RED);
            PWerror.setText("Password Manager is Already Running");
        }

    }





}
