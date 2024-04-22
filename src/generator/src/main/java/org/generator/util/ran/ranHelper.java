package org.generator.util.ran;

import org.generator.util.collections.Pair;
import org.generator.util.net.ID;
import org.generator.util.net.IP;
import org.generator.util.net.IPRange;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Integer.min;

public class ranHelper {
    /**
     * random in [l, r]
     * @param l
     * @param r
     * @return
     */
    static private final Random random = new Random();
    public static int randomInt(int l, int r){
        int k = Math.abs(random.nextInt());
        return l + (k % (r - l + 1));
    }

    public static long randomLong(long l, long r){
        return l + (Math.abs(random.nextLong()) % (r - l + 1));
    }

    static public ID randomID(){
        return ID.of(randomLong(0, 0xE0000000L));
    }

        //< 254.0.0.0
    static public IP randomIP(){
        return IP.of(randomLong(0, 0xE0000000L), randomInt(1, 31));
    }

    static public IPRange randomIpRange(){
        return IPRange.of(randomLong(0,0xffffffffL), randomInt(1, 31));
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

     public static <T> List<T> randomElemsOfList(List<T> elems){
         return randomElemsOfList(elems, elems.size());
     }

    public static <T> List<T> randomElemsOfList(List<T> elems, int bound){
        assert !elems.isEmpty();
         if (elems.isEmpty()) return null;
        var tmp = new ArrayList<>(elems);
        Collections.shuffle(tmp);
        return new ArrayList<>(tmp.subList(0, min(elems.size(), ranHelper.randomInt(1, bound))));
    }
    private static String getRanNoName(Map<String, Object> argRange, String field){
        return ranHelper.randomStr();
    }

    public static <T> List<List<T>> randomSplitElemsCanEmpty(List<T> elems, int splitPart){
         //TODO random split
         //assert  elems.size() >= splitPart;
         int n = elems.size();
         List<Integer> split_points = new ArrayList<>();
         for(int i = 0; i < splitPart - 1; i++) split_points.add(ranHelper.randomInt(0, n));
         split_points.add(n);
         Collections.sort(split_points);
         List<List<T>> res = new ArrayList<>();
         int start = 0;
         for(int i = 0; i < splitPart; i++){
             res.add(elems.subList(start, split_points.get(i)));
             start = split_points.get(i);
         }
         return res;
    }
}
