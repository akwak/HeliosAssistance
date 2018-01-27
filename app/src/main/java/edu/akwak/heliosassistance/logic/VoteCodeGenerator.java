package edu.akwak.heliosassistance.logic;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VoteCodeGenerator {
    private final char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private Random random = new Random();
    private int defaultNum = 3;
    private StringBuilder stringBuilder = new StringBuilder();

    public String[] generateCodes(int amount, int length) {
        List<String> generatedCodes = new ArrayList<>();


        while(generatedCodes.size() < amount) {
            stringBuilder.setLength(0);
            for(int i = 0; i < length; i++) {
                int randomInt = random.nextInt(alphabet.length);
                if(randomInt < alphabet.length) {
                    stringBuilder.append(alphabet[randomInt]);
                }
            }
            String nextCode = stringBuilder.toString();
            if(!generatedCodes.contains(nextCode)) {
                generatedCodes.add(nextCode);
            }
        }
        return generatedCodes.toArray(new String[amount]);
    }

    public String[] generateCodes(int amount) {
        return generateCodes(amount, defaultNum);
    }

}
