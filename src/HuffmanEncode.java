import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class HuffmanEncode implements Runnable{
    public final static String MODE_COMPRESS = "compress";
    public final static String MODE_EXTRACT = "extract";

    private String[] encodedTable;
    private String[] decodedTable;
    private File file;
    private String mode;
    public File returnFile;


    HuffmanEncode(String mode, File file) {
        this.mode = mode;
        this.file = file;
    }

    @Override
    public void run() {
        if(this.mode.equals(MODE_COMPRESS)){
            try {
                this.returnFile = Encode(this.file);
            } catch (Exception e){
                e.printStackTrace();
            }
        } else if(this.mode.equals(MODE_EXTRACT)){
            try {
                this.returnFile = Decode(this.file);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public File Encode(File file) throws IOException {
        // We read ints from File and calculate frequency of each number read
        // Its 8 bit so the number read cant go above 255
        BufferedInputStream source = new BufferedInputStream(new FileInputStream(file));
        int[] freq = new int[256];
        int read;
        while((read = source.read()) != -1) {
            //char debug = (char) read;
            freq[read]++;
        }
        PriorityQueue<HuffmanNode> q = queueCreator(freq);
        // Create a HuffmanNode for each int in freq array, convert the index to character
        //Merging Nodes based on Huffman Algorithm
        HuffmanNode root = null;
        while(q.size() > 1){
            HuffmanNode l = q.poll();
            HuffmanNode r = q.poll();
            HuffmanNode parent = new HuffmanNode();
            parent.c = '-';
            parent.left = l;
            parent.right = r;
            parent.freq = l.freq + r.freq;
            root = parent;
            q.add(parent);
        }
        String[] codes = new String[256];
        getCodes(root, "", codes);
        encodedTable = codes;
        String path = file.getParent() + file.getName()+"enc";
        File targetFile = new File(path);
        FileOutputStream targetOutPut = new FileOutputStream(targetFile);
        BufferedOutputStream target = new BufferedOutputStream(targetOutPut);
        source.close();

        source = new BufferedInputStream(new FileInputStream(file));
        int count = 0;
        StringBuilder toWrite = new StringBuilder();
        long startTime = System.nanoTime();
        while((read = source.read()) != -1){
            String readCode = codes[read];
            for(int i=0; i<readCode.length(); i++) {
                // Remember why you did this instead of creating a long string
                // and then writing 8 char at a time
                // To preserve Memory on Large Files
                toWrite.append(readCode.charAt(i));
                count++;
                if (count == 8){
                    int byteToWrite = Integer.parseInt(toWrite.toString(),2);
                    target.write(byteToWrite);
                    count = 0;
                    toWrite.setLength(0);
                }
            }
        }
        long endTime = System.nanoTime();
        long seconds = TimeUnit.NANOSECONDS.toSeconds(endTime - startTime);
        System.out.println("Writing the compressed code took :" + Long.toString(seconds));

        startTime = System.nanoTime();
        if(count != 0){
            String toAdd = ByteFiller(8-count,root);
            toWrite.append(toAdd);
            int byteToWrite = Integer.parseInt(toWrite.toString(),2);
            target.write(byteToWrite);
        }
        endTime = System.nanoTime();
        seconds = TimeUnit.NANOSECONDS.toSeconds(endTime - startTime);
        System.out.println("Writing and finding the Extra bits took:" + Long.toString(seconds));


        LinkedList<Object> fileHeader = new LinkedList<>();
        // We turn the file into Bytes and add it to Linked List
        target.close();
        byte[] barr = Files.readAllBytes(targetFile.toPath());
        System.out.println(barr.length);
        fileHeader.add(root);
        fileHeader.add(barr);
        target = new BufferedOutputStream(new FileOutputStream(targetFile));
        ObjectOutputStream oos = new ObjectOutputStream(target);
        oos.writeObject(fileHeader);
        oos.flush();
        oos.close();
        source.close();
        return targetFile;
    }


    public File Decode(File file) throws IOException{
        System.out.println("Decoding "+ file.getName());
        FileInputStream source = new FileInputStream(file);
        LinkedList<Object> header = new LinkedList<>();
        boolean cont = true;
        try {
            ObjectInputStream input = new ObjectInputStream(source);
            while (cont){
                Object obj = input.readObject();
                if(obj != null){
                    header.add(obj);
                }
                else
                    cont = false;
            }
        } catch (Exception e){
            //e.printStackTrace();
        }
        LinkedList<Object> t = (LinkedList<Object>) header.get(0);
        HuffmanNode root = (HuffmanNode) t.get(0);
        String[] codes = new String[256];
        getCodes(root,"",codes);
        decodedTable = codes;
        byte[] targetFileByte = (byte[]) t.get(1);
        System.out.println(targetFileByte.length);
        String fileName = file.getName();
        fileName = fileName.replace("enc","");
        System.out.println(fileName);
        String path = file.getParent() + fileName;
        System.out.println(path);
        File decodedFile = new File(path);
        BufferedOutputStream decodedFileStream = new BufferedOutputStream(new FileOutputStream(decodedFile));
        HuffmanNode currentNode = root;
        /*int read = 0;
        File fake = new File("temp");
        BufferedOutputStream fakeBuffer = new BufferedOutputStream(new FileOutputStream(fake));
        fakeBuffer.write(targetFileByte);
        fakeBuffer.close();
        BufferedInputStream fakeInput = new BufferedInputStream(new FileInputStream(fake));
        while ((read = fakeInput.read()) != -1){
            String dir = String.format("%8s", Integer.toBinaryString(read & 0xFF)).replace(' ', '0');
            for(int i = 0; i<dir.length(); i++){
                if(dir.charAt(i) == '0'){
                    currentNode = currentNode.left;
                    if (currentNode.left == null && currentNode.right == null){
                        decodedFileStream.write(currentNode.c);
                        currentNode = root;
                    }
                } else if (dir.charAt(i) == '1'){
                    currentNode = currentNode.right;
                    if (currentNode.left == null && currentNode.right == null){
                        decodedFileStream.write(currentNode.c);
                        currentNode = root;
                    }
                }
            }
        }*/
        for(int ind = 0; ind < targetFileByte.length; ind++) {
            String dir = String.format("%8s", Integer.toBinaryString(targetFileByte[ind] & 0xFF)).replace(' ', '0');
            for(int i = 0; i<dir.length(); i++){
                if(dir.charAt(i) == '0'){
                    currentNode = currentNode.left;
                    if (currentNode.left == null && currentNode.right == null){
                        decodedFileStream.write(currentNode.c);
                        currentNode = root;
                    }
                } else if (dir.charAt(i) == '1'){
                    currentNode = currentNode.right;
                    if (currentNode.left == null && currentNode.right == null){
                        decodedFileStream.write(currentNode.c);
                        currentNode = root;
                    }
                }
            }
        }
        decodedFileStream.close();
        return decodedFile;
    }

    private static void getCodes(HuffmanNode root, String s, String[] codes) {
        if (root.left == null && root.right == null) {
            //System.out.println(root.c + ":" + s);
            codes[(int) root.c] = s;
            return;
        }
        getCodes(root.left, s + "0", codes);
        getCodes(root.right, s + "1", codes);
    }

    private PriorityQueue<HuffmanNode> queueCreator(int[] freq){
        PriorityQueue<HuffmanNode> q = new PriorityQueue<>(new NodeComparator());
        for(int i = 0; i<freq.length; i++){
            if (freq[i] > 0) {
                HuffmanNode node = new HuffmanNode();
                node.c = (char) i;
                node.freq = freq[i];
                node.left = null;
                node.right = null;
                q.add(node);
            }
        }
        return q;
    }


    private boolean checkIntegrity() throws IOException{
        for(int i = 0; i<encodedTable.length; i++){
            if((encodedTable[i] == null && decodedTable[i] != null)
                    || decodedTable[i] == null && encodedTable[i] != null){
                return false;
            }
            if (encodedTable[i] == null && decodedTable[i] == null)
                continue;
            if(!encodedTable[i].equals(decodedTable[i])) {
                System.out.println("Found wrong codes at :"+i);
                System.out.println(encodedTable[i] + " VS " + decodedTable[i]);
                return false;
            }
        }
        return true;
    }


    private String ByteFiller(int fill,HuffmanNode root){
        Random rand = new Random();
        HuffmanNode currentNode = root;
        String way = "";
        boolean cont = true;
        int direction = rand.nextInt(2);
        int failed = 0;
        while (cont) {
            if(way.length() >= fill){
                cont = false;
                break;
            }
            if (direction == 0) {
                HuffmanNode t = currentNode.left;
                if(t.left == null && t.right == null){
                    failed++;
                    if(failed == 2){
                        direction = rand.nextInt(2);
                        failed = 0;
                        way = "";
                        currentNode = root;
                        continue;
                    }
                    direction = 1;
                    continue;
                }
                currentNode = t;
                way += "0";
            } else {
                HuffmanNode t = currentNode.right;
                if(t.left == null && t.right == null){
                    failed++;
                    if(failed == 2){
                        direction = rand.nextInt(2);
                        failed = 0;
                        way = "";
                        currentNode = root;
                        continue;
                    }
                    direction = 0;
                    continue;
                }
                currentNode = t;
                way += "1";
            }
            direction = rand.nextInt(2);
        }
        return way;
    }

}


