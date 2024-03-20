package org.generator.util.ran;

import org.generator.util.net.ID;
import org.generator.util.net.IP;
import org.generator.util.net.IPRange;

import java.util.List;
import java.util.Random;

public class ranHelper {
    /**
     * random in [l, r]
     * @param l
     * @param r
     * @return
     */
    static private final Random random = new Random();
    public static int randomInt(int l, int r){
        return (int) randomLong(l, r);
    }

    public static long randomLong(long l, long r){
        return l + (random.nextLong() % (r - l + 1));
    }

    static public ID randomID(){
        return ID.of(randomLong(0, 0xffffffffL));
    }

    static public IP randomIP(){
        return IP.of(randomLong(0, 0xffffffffL), randomInt(1, 32));
    }

    static public IPRange randomIpRange(){
        return IPRange.of(randomLong(0,0xffffffffL), randomInt(1, 32));
    }

    private static String getRandomString2(int length){
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(3);
            long result=0;
            switch(number){
                case 0:
                    result=Math.round(Math.random()*25+65);
                    sb.append(String.valueOf((char)result));
                    break;
                case 1:
                    result=Math.round(Math.random()*25+97);
                    sb.append(String.valueOf((char)result));
                    break;
                case 2:
                    sb.append(String.valueOf(new Random().nextInt(10)));
                    break;
            }

        }
        return sb.toString();
    }
    public static String randomStr(){
        int len = randomInt(1, 100);
        return getRandomString2(len);
    }

    /**
     * if list size is 0, return null
     * @param elems
     * @return
     * @param <T>
     */
     public static <T> T randomElemOfList(List<T> elems){
        if (elems.isEmpty()) return null;
        return elems.get(ranHelper.randomInt(0, elems.size() - 1));
     }
}
