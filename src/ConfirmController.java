import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ConfirmController {

    @FXML
    public Label text;
    @FXML
    public Button yes;
    @FXML
    public Button cancel;

    public boolean answer = false;

    public void Yes(){
        answer = true;
        ((Stage)(yes.getScene().getWindow())).close();
    }

    public void No(){
        answer = false;
        ((Stage)(cancel.getScene().getWindow())).close();
    }
}
