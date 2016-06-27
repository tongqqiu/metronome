package dcos.metronome.integration

import dcos.metronome.integration.setup.SingleAppIntegrationTest
import play.api.test.FakeRequest
import play.api.test.Helpers._

class PingIntegrationTest extends SingleAppIntegrationTest {

  "ping" ignore {
    "send a pong" in {
      val ping = route(app, FakeRequest(GET, "/ping")).value
      status(ping) mustBe OK
      contentType(ping) mustBe Some("text/plain")
      contentAsString(ping) must include("pong")
    }
  }

}
