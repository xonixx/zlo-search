package info.xonix.zlo.web.ws.tst;

import javax.jws.WebService;

/**
 * User: Vovan
 * Date: 12.12.10
 * Time: 1:02
 */
@WebService(endpointInterface = "info.xonix.zlo.web.ws.tst.TestWS")
public class TestWSImpl implements TestWS {
    @Override
    public String hello() {
        return "Hello world";
    }
}
