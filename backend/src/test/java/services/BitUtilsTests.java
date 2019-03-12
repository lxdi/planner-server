package services;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class BitUtilsTests {

    @Test
    public void setBitTest(){
        assertTrue(Integer.toBinaryString(BitUtils.setBit(0, 0)).equals("1"));
        assertTrue(Integer.toBinaryString(BitUtils.setBit(1, 1)).equals("11"));
        assertTrue(Integer.toBinaryString(BitUtils.setBit(3, 2)).equals("111"));
        assertTrue(Integer.toBinaryString(BitUtils.setBit(3, 0)).equals("11"));
        assertTrue(Integer.toBinaryString(BitUtils.setBit(1, 0)).equals("1"));
        assertTrue(Integer.toBinaryString(BitUtils.setBit(0, 31)).equals("10000000000000000000000000000000"));
    }

    @Test
    public void getBitTest(){
        assertTrue(BitUtils.getBit(BitUtils.setBit(3, 2), 0)==1);
        assertTrue(BitUtils.getBit(BitUtils.setBit(3, 2), 1)==1);
        assertTrue(BitUtils.getBit(BitUtils.setBit(3, 2), 2)==1);
        assertTrue(BitUtils.getBit(BitUtils.setBit(3, 2), 3)==0);
        assertTrue(BitUtils.getBit(BitUtils.setBit(0, 31), 31)==1);
        assertTrue(BitUtils.getBit(BitUtils.setBit(0, 31), 30)==0);
        assertTrue(BitUtils.getBit(0, 0)==0);
    }

    @Test
    public void unsetBitTest(){
        assertTrue(Integer.toBinaryString(BitUtils.unsetBit(0, 0)).equals("0"));
        assertTrue(Integer.toBinaryString(BitUtils.unsetBit(1, 0)).equals("0"));
        assertTrue(Integer.toBinaryString(BitUtils.unsetBit(1, 1)).equals("1"));
        assertTrue(Integer.toBinaryString(BitUtils.unsetBit(1, 2)).equals("1"));
    }

    @Test
    public void getLastBit(){
        assertTrue(BitUtils.getLastBit(3)==1);
        assertTrue(BitUtils.getLastBit(0)==-1);
        assertTrue(BitUtils.getLastBit(1)==0);
        assertTrue(BitUtils.getLastBit(BitUtils.setBit(3, 2))==2);
    }

}
