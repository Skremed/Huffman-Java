import java.io.Serializable;
import java.util.Comparator;


class HuffmanNode implements Serializable {
    public int freq;
    public char c;

    HuffmanNode left;
    HuffmanNode right;
}

class NodeComparator implements Comparator<HuffmanNode> {
    public int compare(HuffmanNode x, HuffmanNode y) {
        return x.freq - y.freq;
    }
}

public class HuffmanTree {

    public static void printCode(HuffmanNode root, String s) {
        if (root.left == null && root.right == null && Character.isLetter(root.c)) {
            System.out.println(root.c + ":" + s);
            return;
        }
        printCode(root.left, s + "0");
        printCode(root.right, s + "1");
    }

}