package edu.akwak.heliosassistance;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import edu.akwak.heliosassistance.logic.RequestSender;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class SendRequestToServerTest {



    @Test
    public void shouldReturnRequest() throws Exception {
        String response = RequestSender.sendGet();
        assertEquals(response, "home");
    }
}
