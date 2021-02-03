import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedList;

public class Encryptor {
    private byte[] keyHash;


    public boolean Encrypt(String key, File file){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            keyHash = digest.digest(key.getBytes(StandardCharsets.UTF_8));
            byte[] fileByte = Files.readAllBytes(file.toPath());
            LinkedList<Object> fileHeader = new LinkedList<>();
            fileHeader.add(keyHash);
            fileHeader.add(fileByte);
            String path = file.getParent() + file.getName()+"sh";
            File targetFile = new File(path);
            BufferedOutputStream targetStream = new BufferedOutputStream(new FileOutputStream(targetFile));
            ObjectOutputStream targetObjectStream = new ObjectOutputStream(targetStream);
            targetObjectStream.writeObject(fileHeader);
            targetObjectStream.flush();
            targetObjectStream.close();
            targetStream.close();
        } catch (NoSuchAlgorithmException | IOException e){
            return false;
        }
        return true;
    }

    public int Decrypt(String key, File file){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            keyHash = digest.digest(key.getBytes(StandardCharsets.UTF_8));
            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            LinkedList<Object> header = new LinkedList<>();
            boolean cont = true;
            try {
                while (cont){
                    Object obj = objectInputStream.readObject();
                    if(obj != null){
                        header = (LinkedList<Object>) obj;
                    }
                    else
                        cont = false;
                }
            } catch (Exception e){
                //e.printStackTrace();
            }
            byte[] encKey = (byte[]) header.get(0);
            if (Arrays.equals(encKey, keyHash)){
                String fileName = file.getName().replace("sh","");
                String path = file.getParent() + fileName;
                File targetFile = new File(path);
                byte[] encrypted = (byte[]) header.get(1);
                System.out.println(path);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(targetFile));
                bufferedOutputStream.write(encrypted);
                bufferedOutputStream.close();
            } else
                return -1;
        } catch (Exception e){
            return 0;
        }
        return 1;
    }
}
