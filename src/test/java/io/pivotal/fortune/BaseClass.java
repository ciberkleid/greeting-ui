package io.pivotal.fortune;

import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.Before;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

public class BaseClass {

    @Before
    public void setup() {
        FortuneService service = BDDMockito.mock(FortuneService.class);
        BDDMockito.given(service.getFortune()).willReturn("foo fortune");
        RestAssuredMockMvc.standaloneSetup(new FortuneController(service));
    }
}
