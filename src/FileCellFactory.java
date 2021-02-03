import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.io.File;

public class FileCellFactory implements Callback<ListView<File>, ListCell<File>> {

    @Override
    public ListCell<File> call(ListView<File> param) {
        return new FilesListView();
    }
}