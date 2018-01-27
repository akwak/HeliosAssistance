package edu.akwak.heliosassistance.test;

import org.junit.Test;

import edu.akwak.heliosassistance.logic.VoteCodeGenerator;
import static org.junit.Assert.assertTrue;
public class VoteCodeGeneratorTest {

    @Test
    public void shouldGenerateThreeCodes() {
        VoteCodeGenerator voteCodeGenerator = new VoteCodeGenerator();
        String[] codes = voteCodeGenerator.generateCodes(3);
        System.out.println(codes);
        assertTrue(codes.length == 3);
    }
}
