package fr.noop.subtitle;

/**
 * Test
 */
public class Test {

    public static void main(String[] args) throws Exception {
        byte[] koko = {-6};
        String bite = "œ";
        String charset = "ISO-6937-2";
        byte[] grosseBite = bite.getBytes(charset);
        String kiki = new String(koko, charset);
        System.out.println(kiki);
    }
}
