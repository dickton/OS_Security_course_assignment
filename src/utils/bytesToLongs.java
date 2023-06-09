package utils;

import java.nio.charset.StandardCharsets;

public class bytesToLongs {

    static public long bytes2long(byte[] bytes){
        long l=0;
        for(int i=0;i<bytes.length;i++){
            l|=(long)(bytes[i]&0xff)<<(8*i);
            if(i==7){
                break;
            }
        }
        return l;
    }

    static public long[] bytes2longArr(byte[] bytes){

        //Notice: both start & end in params are the mark of bytes
        int start=0;
        int end= bytes.length-1;

        boolean divBy8= (end - start + 1) % 8 == 0;
        long[] arrLong=new long[(end-start+1)/8+(divBy8?0:1)];

        int count=0;

        while(end-start>=7){
            arrLong[count]=bytes[start]&0xff|
                    (bytes[start+1]&0xff)<<8|
                    (bytes[start+2]&0xff)<<16|
                    (long) (bytes[start + 3] & 0xff) <<24|
                    (long) (bytes[start + 4] & 0xff) <<32|
                    (long) (bytes[start + 5] & 0xff) <<40|
                    (long) (bytes[start + 6] & 0xff) <<48|
                    (long) (bytes[start + 7] & 0xff) <<56;
            count++;
            start+=8;
        }
        if(!divBy8){
            long temp=0;
            for(int i=end-start;i>=0;i--){
                temp|= (long) (bytes[i+start] & 0xff) <<(8*i);
            }
            arrLong[count]=temp;
            count++;
        }
        return arrLong;
    }

    //Todo l2b Algorithm
    static public byte[] longArr2Bytes(long[] arrLong){
        int start=0;
        int end= arrLong.length-1;
        byte[] bytes=new byte[(end-start)*8];
        int longCount=0;
        while(end>start){
            if(arrLong[longCount]!=0){
                for(int i=0;i<8;i++){
                    bytes[longCount*8+i]=(byte)((arrLong[longCount] >> (8*i)) & 0xff);
                }
            }else{
                for(int i=0;i<8;i++){
                    bytes[longCount*8+i]=0;
                }
            }

            start++;
            longCount++;
        }


        int i;
        byte[] temp=new byte[8];
        for (i=0;i<8;i++){
            temp[i]=(byte)((arrLong[longCount] >> (8*i)) & 0xff);
        }
        boolean flag=true;
        for(i=7;i>=0;i--) {
            if (temp[i] != 0) {
                flag=false;
                break;
            }
        }
        byte[] bytes1=new byte[longCount* 8 +(flag?8:i+1)];
        System.arraycopy(bytes,0,bytes1,0,8*longCount);
        System.arraycopy(temp, 0, bytes1, longCount*8, i + 1);




        return bytes1;
    }

    static public byte[] long2Bytes(long l){
        byte[] bytes=new byte[8];
        for (int i=0;i<8;i++){
            bytes[i]=(byte) ((l >> (i * 8)) & 0xff);
        }
        int i;
        for(i=7;i>=0;i--){
            if(bytes[i]!=0){
                break;
            }
        }
        byte[] bytes1=new byte[i+1];
        System.arraycopy(bytes, 0, bytes1, 0, i + 1);
        return bytes1;
    }


    //Algorithm test
    public static void main(String[] args){
        byte[] bytes="IamDESKey".getBytes();
        for(byte b:bytes){
            System.out.print(b);
            System.out.print('\t');
        }
        System.out.println();

        long[] longs=bytesToLongs.bytes2longArr(bytes);
        for(long i:longs){
            System.out.println(i);
        }
        System.out.println();


        byte[] bytes1=bytesToLongs.longArr2Bytes(longs);
        for(byte b:bytes1){
            System.out.print(b);
            System.out.print('\t');
        }


        System.out.println();
        System.out.println(new String(bytes1));
        System.out.println();
        long l=bytes2long(bytes);
        System.out.println(l);
        System.out.println(new String(long2Bytes(l)));

        long[] arr={0,0,0};
        byte[] bytes2=longArr2Bytes(arr);
        for(byte b:bytes2){
            System.out.print(b);
            System.out.print('\t');
        }
        System.out.println();
    }
}
