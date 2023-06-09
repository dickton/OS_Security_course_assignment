package utils;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.Hashtable;
import java.util.concurrent.Callable;

public class FileIOThread extends Thread {
    private RandomAccessFile randomAccessFile;
    private long start;
    private long end;
    private boolean mode;

    private Hashtable<Long,long[]> hashtable;

    static final public boolean INPUT_MODE=true;
    static final public boolean OUTPUT_MODE=false;

    static final public int BLOCK_SIZE =1024*512;

    public FileIOThread(String fileName, Hashtable<Long, long[]> hashtable, long start, long end, boolean mode){
        this.start=start;
        this.end=end;
        this.mode=mode;
        this.hashtable=hashtable;
        try{
            this.randomAccessFile=new RandomAccessFile(fileName,"rw");
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    public void run(){
        try {
            if(mode==INPUT_MODE){
                //Input mode code
                int hasRead=0;
                long current_block=start/BLOCK_SIZE;
                byte[] buffer=new byte[BLOCK_SIZE];
                while((hasRead= randomAccessFile.read(buffer))!=-1&&start<end){
                    hashtable.put(start/BLOCK_SIZE,bytesToLongs.bytes2longArr(buffer));

                    //Output the Input info on the terminal
                    //System.out.println(String.format("%d\n",start/BLOCK_SIZE)+ new String(buffer));

                    start+=hasRead;
                    //current_block++;
                }

            }else {
                //Output mode code
                int hasWritten=0;
                long current_block=this.start/BLOCK_SIZE;;

                while (start<end){

                    if(hashtable.containsKey(current_block)){
                        byte[] buffer=bytesToLongs.longArr2Bytes(hashtable.get(current_block));
                        synchronized (this){
                            randomAccessFile.write(buffer);
                        }

                        current_block++;
                        start+=BLOCK_SIZE;
                    }else{
                        break;
                    }
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
