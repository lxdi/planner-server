package services;

public class BitUtils {

    public static int setBit(int num, int pos){
        int sample = 1 << pos;
        return num|sample;
    }

    public static int unsetBit(int num, int pos){
        int sample = 1 << pos;
        sample = ~sample;
        return num&sample;
    }

    public static int getBit(int n, int k) {
        return (n >> k) & 1;
    }

    public static int getLastBit(int num){
        if(getBit(num, 0)==0){
            return -1;
        }
        for(int i = 1; i<32; i++){
            int bit = getBit(num, i);
            if(bit==0){
                if(getBit(num, i-1)==1){
                    return i-1;
                } else {
                    throw new RuntimeException("No way");
                }
            }
        }
        return -1;
    }

}
