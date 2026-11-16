package project;

public class Util {

    public static byte sign_a_byte(int value){
        value %= 256;
        if(value>0x7f){
            value -= 256;
        }
        return (byte)value;
    }
    public static byte sab(int v){ return sign_a_byte(v);}

    public static String byte_as_hex(byte b){
        int i=b;
        if(i<0) i+=0x100;
        return Integer.toString(i, 16).toUpperCase();
    }
    public static String short_as_hex(short s){
        int i=s;
        if(i<0) i+=0x10000;
        return Integer.toString(i, 16).toUpperCase();
    }

    public static short bytes_as_short(byte hi, byte lo){
        return (short)((hi<<8) | lo);
    }
    public static short b2s(byte hi, byte lo){
        return bytes_as_short(hi, lo);
    }

    /// converts a byte to an int, without sign extension
    /// because default java casting uses sign extension
    public static int byte_to_int_unsigned(byte value){
        return value&0xFF;
    }
    /// byte to int unsigned
    public static int b2iu(byte value){
        return byte_to_int_unsigned(value);
    }
}
