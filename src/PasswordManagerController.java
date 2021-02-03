import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.ResourceBundle;


public class PasswordManagerController implements Initializable {
    public String MasterPW;

    @FXML
    public TextField passwordName;
    public TextField password;
    public Button add;
    public Button edit;
    @FXML
    public TextField passwordName1;
    public TextField password1;
    public Button add1;
    public Button edit1;
    @FXML
    public TextField passwordName2;
    public TextField password2;
    public Button add2;
    public Button edit2;
    @FXML
    public TextField passwordName3;
    public TextField password3;
    public Button add3;
    public Button edit3;
    @FXML
    public TextField passwordName4;
    public TextField password4;
    public Button add4;
    public Button edit4;
    @FXML
    public TextField passwordName5;
    public TextField password5;
    public Button add5;
    public Button edit5;
    @FXML
    public TextField passwordName6;
    public TextField password6;
    public Button add6;
    public Button edit6;
    @FXML
    public TextField passwordName7;
    public TextField password7;
    public Button add7;
    public Button edit7;
    @FXML
    public TextField passwordName8;
    public TextField password8;
    public Button add8;
    public Button edit8;
    @FXML
    public TextField passwordName9;
    public TextField password9;
    public Button add9;
    public Button edit9;
    @FXML
    public TextField passwordName10;
    public TextField password10;
    public Button add10;
    public Button edit10;
    @FXML
    public TextField passwordName11;
    public TextField password11;
    public Button add11;
    public Button edit11;

    private Dictionary<String,Object> Boxmap;
    private PasswordBox[] passwordBoxes;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Boxmap = new Hashtable<>();
        populateDic();

        try {
            File passwords = new File("passwords.fe");
            FileInputStream fileInputStream = new FileInputStream(passwords);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            boolean cont = true;
            try {
                while (cont) {
                    Object obj = objectInputStream.readObject();
                    if (obj != null) {
                        this.passwordBoxes = (PasswordBox[]) obj;
                    } else
                        cont = false;
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < passwordBoxes.length; i++) {
            TextField tpwname = (TextField) Boxmap.get("PW_Name" + i);
            TextField tpw = (TextField) Boxmap.get("PW" + i);
            Button tadd = (Button) Boxmap.get("add" + i);
            Button tedit = (Button) Boxmap.get("edit" + i);
            if (passwordBoxes[i].PWnameString == null && passwordBoxes[i].PWString == null) {
                tpwname.setVisible(false);
                tpw.setVisible(false);
                tedit.setVisible(false);
                tadd.setVisible(true);
            } else {
                tpwname.setVisible(true);
                tpw.setVisible(true);
                tedit.setVisible(true);
                tadd.setVisible(false);
                try {
                    if(passwordBoxes[i].PWnameString != null) {
                        String decpwName = HashString.decrypt(new String(passwordBoxes[i].PWnameString),MainPageController.MasterPW);
                        System.out.println(decpwName);
                        tpwname.setText(decpwName);
                    }
                    if(passwordBoxes[i].PWString != null) {
                        String decpw = HashString.decrypt(new String(passwordBoxes[i].PWString),MainPageController.MasterPW);
                        System.out.println(decpw);
                        tpw.setText(decpw);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

        }
    }


    private void populateDic() {
        Boxmap.put("PW_Name0", passwordName);
        Boxmap.put("PW0",password);
        Boxmap.put("add0",add);
        Boxmap.put("edit0",edit);

        Boxmap.put("PW_Name1", passwordName1);
        Boxmap.put("PW1",password1);
        Boxmap.put("add1",add1);
        Boxmap.put("edit1",edit1);

        Boxmap.put("PW_Name2", passwordName2);
        Boxmap.put("PW2",password2);
        Boxmap.put("add2",add2);
        Boxmap.put("edit2",edit2);

        Boxmap.put("PW_Name3", passwordName3);
        Boxmap.put("PW3",password3);
        Boxmap.put("add3",add3);
        Boxmap.put("edit3",edit3);

        Boxmap.put("PW_Name4", passwordName4);
        Boxmap.put("PW4",password4);
        Boxmap.put("add4",add4);
        Boxmap.put("edit4",edit4);

        Boxmap.put("PW_Name5", passwordName5);
        Boxmap.put("PW5",password5);
        Boxmap.put("add5",add5);
        Boxmap.put("edit5",edit5);

        Boxmap.put("PW_Name6", passwordName6);
        Boxmap.put("PW6",password6);
        Boxmap.put("add6",add6);
        Boxmap.put("edit6",edit6);

        Boxmap.put("PW_Name7", passwordName7);
        Boxmap.put("PW7",password7);
        Boxmap.put("add7",add7);
        Boxmap.put("edit7",edit7);

        Boxmap.put("PW_Name8", passwordName8);
        Boxmap.put("PW8",password8);
        Boxmap.put("add8",add8);
        Boxmap.put("edit8",edit8);

        Boxmap.put("PW_Name9", passwordName9);
        Boxmap.put("PW9",password9);
        Boxmap.put("add9",add9);
        Boxmap.put("edit9",edit9);

        Boxmap.put("PW_Name10", passwordName10);
        Boxmap.put("PW10",password10);
        Boxmap.put("add10",add10);
        Boxmap.put("edit10",edit10);

        Boxmap.put("PW_Name11", passwordName11);
        Boxmap.put("PW11",password11);
        Boxmap.put("add11",add11);
        Boxmap.put("edit11",edit11);
    }


    public void addPW(ActionEvent event){
        Button e = (Button) event.getSource();
        int index = -1;
        for (int i = 0; i< 12; i++){
            if(e == Boxmap.get("add"+i)){
                index = i;
                break;
            }
        }
        TextField tpwname = (TextField) Boxmap.get("PW_Name" + index);
        TextField tpw = (TextField) Boxmap.get("PW" + index);
        Button tadd = (Button) Boxmap.get("add" + index);
        Button tedit = (Button) Boxmap.get("edit" + index);


        tpwname.setVisible(true);
        tpwname.setEditable(true);

        tpw.setVisible(true);
        tpwname.setEditable(true);

        tedit.setVisible(true);
        tadd.setVisible(false);

        passwordBoxes[index].PWname = tpwname;
        passwordBoxes[index].PW = tpw;
        passwordBoxes[index].edit = tedit;
        passwordBoxes[index].addNew = tadd;
        WriteChanges();

    }


    public void enterPressed(KeyEvent e){
        if (e.getCode() == KeyCode.ENTER) {
            System.out.println(e.getSource());
            TextField tmp = (TextField) e.getSource();
            int index = -1;
            for (int i = 0; i < 12; i++) {
                if (tmp == Boxmap.get("PW_Name" + i) || tmp == Boxmap.get("PW" + i)) {
                    index = i;
                    break;
                }
            }
            TextField tpw = (TextField) Boxmap.get("PW" + index);
            TextField tpwName = (TextField) Boxmap.get("PW_Name"+ index);
            try {
                byte[] encpw = HashString.encrypt(tpw.getText(),MainPageController.MasterPW);
                byte[] encpwName = HashString.encrypt(tpwName.getText(),MainPageController.MasterPW);
                passwordBoxes[index].PWString = encpw;
                passwordBoxes[index].PWnameString = encpwName;
            } catch (Exception ex){
                ex.printStackTrace();
            }
            WriteChanges();
        }
    }


    public void editPW(ActionEvent event){
        Button e = (Button) event.getSource();
        int index = -1;
        for (int i = 0; i< 12; i++){
            if(e == Boxmap.get("edit"+i)){
                index = i;
                break;
            }
        }
        TextField tpw = (TextField) Boxmap.get("PW" + index);
        tpw.setEditable(true);
        passwordBoxes[index].PWString = " ".getBytes();
        WriteChanges();

    }


    public void WriteChanges(){
        try {
        File file = new File("passwords.fe");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
        objectOutputStream.writeObject(passwordBoxes);
        objectOutputStream.flush();
        objectOutputStream.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }


}
