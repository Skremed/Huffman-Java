import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FilesListView extends ListCell<File> {

    @FXML
    private ImageView fileIcon;
    @FXML
    private Label fileName;
    @FXML
    private HBox hbox;

    private FXMLLoader Loader;


    public FilesListView() {
        loadFXML();
    }

    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CellList.fxml"));
            loader.setController(this);
            loader.setRoot(new HBox());
            loader.load();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void updateItem(File item, boolean empty){
        super.updateItem(item, empty);

        if(empty || item == null){
            setText(null);
            setGraphic(null);
        } else {

            System.out.println("In");
            Icon icon = FileSystemView.getFileSystemView().getSystemIcon(item);
            ImageIcon swingImageIcon = (ImageIcon) icon;
            java.awt.Image awtImage = swingImageIcon.getImage();
            BufferedImage bImg ;
            if (awtImage instanceof BufferedImage) {
                bImg = (BufferedImage) awtImage ;
            } else {
                bImg = new BufferedImage(awtImage.getWidth(null), awtImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                Graphics2D graphics = bImg.createGraphics();
                graphics.drawImage(awtImage, 0, 0, null);
                graphics.dispose();
            }
            Image image = SwingFXUtils.toFXImage(bImg, null);
            fileName.setText(FileSystemView.getFileSystemView().getSystemDisplayName(item));
            System.out.println((FileSystemView.getFileSystemView().getSystemDisplayName(item)));
            fileIcon.setImage(image);
            //setText(null);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            setGraphic(hbox);
        }

    }
}
