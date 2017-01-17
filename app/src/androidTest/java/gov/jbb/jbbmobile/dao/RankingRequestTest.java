package gov.jbb.jbbmobile.dao;

import gov.jbb.jbbmobile.dao.RankingRequest;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;


public class RankingRequestTest {

    @Test
    public void testIfRequestIsCreated(){
        RankingRequest rankingRequest = new RankingRequest("nickname");
        assertEquals("nickname", rankingRequest.getNickname());
    }

}
