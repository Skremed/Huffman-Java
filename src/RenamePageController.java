import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RenamePageController {

    @FXML
    public Label label;
    @FXML
    public TextField text;
    @FXML
    public Button done;
    @FXML
    public Button cancel;

    public String textString = null;


    public void doneClicked(){
        textString = text.getText();
        ((Stage)(done.getScene().getWindow())).close();
    }

    public void cancelClicked(){
        textString = null;
        ((Stage)(done.getScene().getWindow())).close();
    }
}
