
import CS.Client;
import CS.Server;
import utils.BlockCalThread;
import utils.FileIOThread;
import utils.bytesToLongs;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;
import java.util.concurrent.CountDownLatch;


public class test {

    /**
     * @param plainTextFileName: The file name of the plaintext.
     * @param encryptedFileName: The file name of the encrypted file.
     *      Tip: The absolute file path should be included in the fileName Strings.
     * @param key:               the Key for the DES algorithm.
     * @param iv:                the initial vector for the CBC mode.
     **/
    public static void encryptTest(String plainTextFileName, String encryptedFileName, String key, String iv) throws InterruptedException {
        File plain = new File(plainTextFileName);
        File cipher = new File(encryptedFileName);

        if (plain.exists()) {
            long startTime = System.currentTimeMillis();
            Hashtable<Long, long[]> hashtable = new Hashtable<>();
            long fileLen = plain.length();
            long blockNum = fileLen / FileIOThread.BLOCK_SIZE + (fileLen % FileIOThread.BLOCK_SIZE == 0 ? 0 : 1);
            long[] iv_arr = new long[1];
            iv_arr[0] = bytesToLongs.bytes2long(iv.getBytes());
            hashtable.put((long) -1, iv_arr);


            FileIOThread plainInputThread_1 = new FileIOThread(plainTextFileName, hashtable, 0, blockNum / 2 * FileIOThread.BLOCK_SIZE, FileIOThread.INPUT_MODE);
            FileIOThread plainInputThread_2 = new FileIOThread(plainTextFileName, hashtable, blockNum / 2 * FileIOThread.BLOCK_SIZE, fileLen, FileIOThread.INPUT_MODE);
            plainInputThread_1.start();
            plainInputThread_2.start();
            plainInputThread_1.join();

            BlockCalThread blockCalThread = new BlockCalThread(hashtable, 0, blockNum, key.getBytes());
            blockCalThread.start();
            blockCalThread.join();

            FileIOThread encryptedOut1 = new FileIOThread(encryptedFileName, hashtable, 0, fileLen, FileIOThread.OUTPUT_MODE);
            //FileIOThread encryptedOut2=new FileIOThread(outputFileName1,hashtable,blockLen,fileLen,FileIOThread.OUTPUT_MODE);
            encryptedOut1.start();
            //encryptedOut2.start();
            encryptedOut1.join();
            //encryptedOut2.join();

            long endTime = System.currentTimeMillis();

            System.out.println(String.format("Encryption operation cost: %d millisecond\n", endTime - startTime));

        }
    }


    /**

     * @param encryptedFileName: The file name of the encrypted file.
     * @param decryptedFileName: The file name of the decrypted file.
     *      Tip: The absolute file path should be included in the fileName Strings.
     * @param key:               the Key for the DES algorithm.
     * @param iv:                the initial vector for the CBC mode.
     **/
    public static void decryptTest(String encryptedFileName, String decryptedFileName, String key, String iv) throws InterruptedException {
        File decrypted = new File(decryptedFileName);
        File cipher = new File(encryptedFileName);

        if (cipher.exists()) {
            long startTime = System.currentTimeMillis();

            Hashtable<Long, long[]> hashtable = new Hashtable<>();
            Hashtable<Long, long[]> decryptedTable = new Hashtable<>();

            long fileLen = cipher.length();
            long blockNum = fileLen / FileIOThread.BLOCK_SIZE + (fileLen % FileIOThread.BLOCK_SIZE == 0 ? 0 : 1);
            long[] iv_arr = new long[1];
            iv_arr[0] = bytesToLongs.bytes2long(iv.getBytes());
            hashtable.put((long) -1, iv_arr);


            FileIOThread plainInputThread_1 = new FileIOThread(encryptedFileName, hashtable, 0, blockNum / 2 * FileIOThread.BLOCK_SIZE, FileIOThread.INPUT_MODE);
            FileIOThread plainInputThread_2 = new FileIOThread(encryptedFileName, hashtable, blockNum / 2 * FileIOThread.BLOCK_SIZE, fileLen, FileIOThread.INPUT_MODE);
            plainInputThread_1.start();
            plainInputThread_2.start();
            plainInputThread_1.join();

            BlockCalThread de1 = new BlockCalThread(hashtable, decryptedTable, 0, blockNum / 2, key.getBytes());
            BlockCalThread de2 = new BlockCalThread(hashtable, decryptedTable, blockNum / 2, blockNum, key.getBytes());
            de1.start();
            de2.start();

            de1.join();
            de2.join();

            FileIOThread deOut1 = new FileIOThread(decryptedFileName, decryptedTable, 0, fileLen, FileIOThread.OUTPUT_MODE);
            //FileIOThread deOut2= new FileIOThread(outputFileName2,decryptedTable,blockLen,fileLen,FileIOThread.OUTPUT_MODE);

            deOut1.start();
            //deOut2.start();
            deOut1.join();
            //deOut2.join();

            long endTime = System.currentTimeMillis();

            System.out.println(String.format("Decryption operation cost: %d millisecond\n", endTime - startTime));

        }
    }

    /**
    * This method is used for testing transmission function for the project.
     **/
    public static void transmissionTest(){
        try {
            Server server=new Server(10086,"cam6.pdf","C:\\temp\\");
            server.start();     // Start the server thread
            Client client=new Client("127.0.0.1",10086);
            client.start();     // Start the client thread
            server.join();
            client.join();
            System.out.println("Transmission finished");
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public static void main(String[] args) throws InterruptedException {
//        long startTime = System.currentTimeMillis();
//
        //Encryption & Decryption Instance
//        String inputFileName = "C:\\temp\\2023.pdf";
//        String outputFileName1 = "C:\\temp\\2023_en.pdf";
//        String outputFileName2 = "C:\\temp\\2023_de.pdf";
//
//        encryptTest(inputFileName,outputFileName1,"IamDESKey","IamCBCIV");
//        decryptTest(outputFileName1,outputFileName2,"IamDESKey","IamCBCIV");
//
//        long endTime = System.currentTimeMillis();
//
//        System.out.println(String.format("Total process time: %d millisecond\n", endTime - startTime));
        //Transmission test
        transmissionTest();

    }
}
