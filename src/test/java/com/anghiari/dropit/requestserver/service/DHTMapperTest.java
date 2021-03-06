package com.anghiari.dropit.requestserver.service;

import com.anghiari.dropit.commons.KeyId;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author gayashan
 */
public class DHTMapperTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testGenerateKeyId() throws Exception {
        KeyId keyId = DHTUtils.generateKeyId("dropit.png");
        assertEquals(1870626650L, keyId.getHashId());
    }
}
