package info.xonix.zlo.web.ws;

import javax.jws.WebService;

/**
 * User: Vovan
 * Date: 12.12.10
 * Time: 1:02
 */
@WebService(endpointInterface = "info.xonix.zlo.web.ws.TestWS")
public class TestWSImpl implements TestWS {
    @Override
    public String hello() {
        return "Hello world";
    }
}
