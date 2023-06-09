package utils;

import com.sun.istack.internal.Nullable;
import des.DES;

import java.util.Hashtable;

public class BlockCalThread extends Thread {


    private Hashtable<Long, long[]> hashtable;

    private long start_block_num;
    private long end_block_num;

    // Set the mode of thread class
    private final boolean mode;
    public final static boolean ENCRYPT_MODE = true;
    public final static boolean DECRYPT_MODE = false;

    // DES key
    private final long key;

    // decryptedTable is used for decryption mode
    private Hashtable<Long, long[]> decryptedTable;


    //Constructor for encryption mode
    public BlockCalThread(Hashtable<Long, long[]> hashtable, long start, long end, byte[] key) {
        this.hashtable = hashtable;
        this.start_block_num = start;
        this.end_block_num = end;
        this.mode = ENCRYPT_MODE;
        this.key = bytesToLongs.bytes2long(key);
    }

    //Constructor for decryption mode
    public BlockCalThread(Hashtable<Long, long[]> encryptedTable, Hashtable<Long, long[]> decryptedTable,
                          long start, long end, byte[] key) {
        this.mode = DECRYPT_MODE;
        this.hashtable = encryptedTable;
        this.decryptedTable = decryptedTable;
        this.start_block_num = start;
        this.end_block_num = end;
        this.key = bytesToLongs.bytes2long(key);
    }

    // CBC encryption must be serialized
    // while CBC decryption could be done by multi thread work
    // So we need two different constructor

    public void run() {
        int errFlag = 0;
        if (mode == DECRYPT_MODE) {
            DES des = new DES();

            long iv = 0;
            long[] longArr;
            if (start_block_num == 0) {
                // Put <-1, iv> into the hashtable
                while (true) {
                    if (hashtable.containsKey((long) -1) && hashtable.containsKey((long) 0)) {
                        iv = hashtable.get((long) -1)[0];
                        longArr = hashtable.get((long) 0);
                        decryptedTable.put(start_block_num, des.CBCDecrypt(longArr, key, iv));
                        iv = longArr[longArr.length - 1];
                        start_block_num++;
                        errFlag = 0;
                        break;
                    } else {
                        try {
                            errFlag++;
                            sleep(errFlag * 200L);
                            if (errFlag >= 5) {
                                throw new RuntimeException();
                            }


                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            } else {
                while (true) {
                    if (hashtable.containsKey(start_block_num - 1)) {
                        long[] temp = hashtable.get(start_block_num - 1);
                        iv = temp[temp.length - 1];     // set the new iv for the next block
                        errFlag = 0;
                        break;
                    } else {
                        errFlag++;
                        if (errFlag >= 5) {
                            throw new RuntimeException();
                        }
                        try {
                            sleep(errFlag * 200L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }

            errFlag = 0;
            while (start_block_num < end_block_num) {

                if (hashtable.containsKey(start_block_num)) {

                    longArr = hashtable.get(start_block_num);
                    decryptedTable.put(start_block_num, des.CBCDecrypt(longArr, key, iv));
                    iv = longArr[longArr.length - 1];    // set the new iv
                    start_block_num++;
                    errFlag = 0;
                } else {
                    try {
                        errFlag++;
                        sleep(errFlag * 200L);
                        if (errFlag >= 5) {
                            throw new RuntimeException();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        } else {
            // do DES encryption work
            // Do the CBC encryption work in the main thread after multi threads work being finished
            long iv = 0;
            DES des = new DES();
            try {
                iv = hashtable.get((long) -1)[0];
            } catch (Exception e) {
                e.printStackTrace();
            }
            long[] arr;
            while (start_block_num < end_block_num) {
                if (hashtable.containsKey(start_block_num)) {
                    arr = hashtable.get(start_block_num);
                    arr = des.CBCEncrypt(arr, key, iv);
                    hashtable.replace(start_block_num, arr);
                    iv = arr[arr.length - 1];
                    start_block_num++;
                    errFlag = 0;
                } else {
                    try {
                        errFlag++;
                        sleep(errFlag * 200L);
                        if (errFlag >= 5) {
                            throw new RuntimeException();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
}
