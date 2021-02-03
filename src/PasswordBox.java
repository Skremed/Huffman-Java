import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.io.Serializable;

public class PasswordBox implements Serializable {

    public transient static String key;

    public transient Button addNew;
    public transient Button edit;
    public byte[] PWnameString;
    public byte[] PWString;
    public transient TextField PWname;
    public transient TextField PW;

    public PasswordBox(Button addNew, TextField PWname, TextField PW, Button edit) {
        this.addNew = addNew;
        this.PWname = PWname;
        this.PW = PW;
        this.edit = edit;
    }
}
