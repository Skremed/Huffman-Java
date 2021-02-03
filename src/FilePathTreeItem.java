//import com.sun.source.tree.Tree;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class FilePathTreeItem extends TreeItem<String> {
    private boolean isLeaf;
    private boolean isFirstTimeChildren=true;
    private boolean isFirstTimeLeaf=true;
    private final File file;
    public File getFile(){return(this.file);}
    private final String absolutePath;
    public String getAbsolutePath(){return(this.absolutePath);}
    private final boolean isDirectory;
    public boolean isDirectory(){return(this.isDirectory);}


    public FilePathTreeItem(File file){
        super(FileSystemView.getFileSystemView().getSystemDisplayName(file));
        this.file=file;
        this.absolutePath=file.getAbsolutePath();
        this.isDirectory=file.isDirectory();
        Icon icon = FileSystemView.getFileSystemView().getSystemIcon(file);
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
        if(this.isDirectory){
            this.setGraphic(new ImageView(image));
            //add event handlers
            this.addEventHandler(TreeItem.branchCollapsedEvent(),new EventHandler(){
                @Override
                public void handle(Event e){
                    FilePathTreeItem source=(FilePathTreeItem)e.getSource();
                    if(!source.isExpanded()){
                        //ImageView iv=(ImageView)source.getGraphic();
                        //iv.setImage(image);
                    }
                }
            } );
            this.addEventHandler(TreeItem.branchExpandedEvent(),new EventHandler(){
                @Override
                public void handle(Event e){
                    FilePathTreeItem source=(FilePathTreeItem)e.getSource();
                    if(source.isExpanded()){
                        //ImageView iv=(ImageView)source.getGraphic();
                        //iv.setImage(image);
                    }
                }
            } );
        }else{
            this.setGraphic(new ImageView(image));
        }
        //set the value (which is what is displayed in the tree)
        String fullPath=file.getAbsolutePath();
        if(!fullPath.endsWith(File.separator)){
            String value=file.toString();
            int indexOf=value.lastIndexOf(File.separator);
            if(indexOf>0){
                this.setValue(value.substring(indexOf+1));
            }else{
                this.setValue(value);
            }
        }
    }

    @Override
    public ObservableList<TreeItem<String>> getChildren(){
        if(isFirstTimeChildren){
            isFirstTimeChildren=false;
            super.getChildren().setAll(buildChildren(this));
        }
        return(super.getChildren());
    }

    @Override
    public boolean isLeaf(){
        if(isFirstTimeLeaf){
            isFirstTimeLeaf=false;
            isLeaf=this.file.isFile();
        }
        return(isLeaf);
    }

    public ObservableList<FilePathTreeItem> buildChildren(FilePathTreeItem treeItem){
        File f=treeItem.getFile();
        if((f!=null)&&(f.isDirectory())){
            File[] files=f.listFiles();
            if (files!=null){
                ObservableList<FilePathTreeItem> children=FXCollections.observableArrayList();
                for(File childFile:files){
                    children.add(new FilePathTreeItem(childFile));
                }
                return(children);
            }
        }
        return FXCollections.emptyObservableList();
    }


}
