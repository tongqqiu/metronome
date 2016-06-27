package dcos.metronome.integration

import dcos.metronome.api.v1.models._
import dcos.metronome.integration.setup.SingleAppIntegrationTest
import dcos.metronome.model._
import org.scalatest.concurrent.ScalaFutures
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._

class JobSpecIntegrationTest extends SingleAppIntegrationTest with ScalaFutures {

  "The JobSpec API" should {
    val f = new Fixture

    "POST a JobSpec" in {
      // TODO figure out how to wait for the application to elect a leader and startup
      Thread.sleep(5000L)

      val json = Json.toJson(f.simpleJobSpec)
      val response = route(app, FakeRequest(POST, "/v1/jobs"), json).value
      status(response) mustBe CREATED
      contentType(response).value mustBe JSON
      contentAsJson(response).as[JobSpec] mustEqual f.simpleJobSpec
    }

    "GET the created spec" in {
      val response = route(app, FakeRequest(GET, s"/v1/jobs/${f.simpleJobSpec.id}")).value
      status(response) mustBe OK
      contentType(response).value mustBe JSON
      contentAsJson(response).as[JobSpec] mustEqual f.simpleJobSpec
    }

    "fail to GET an unknown JobSpecId" in {
      val response = route(app, FakeRequest(GET, s"/v1/jobs/unknown")).value
      status(response) mustBe NOT_FOUND
    }

    "PUT a JobSpec" in {
      val response = route(app, FakeRequest(PUT, s"/v1/jobs/${f.simpleJobSpec.id}"), Json.toJson(f.simpleJobSpecUpdated)).value
      status(response) mustBe OK
      contentType(response).value mustBe JSON
      contentAsJson(response).as[JobSpec] mustEqual f.simpleJobSpecUpdated
    }

    "fail to PUT an unknown JobSpecId" in {
      val response = route(app, FakeRequest(PUT, s"/v1/jobs/unknown"), Json.toJson(f.simpleJobSpecUpdated)).value
      status(response) mustBe NOT_FOUND
    }

    "DELETE a JobSpec" in {
      val response = route(app, FakeRequest(DELETE, s"/v1/jobs/${f.simpleJobSpec.id}")).value
      status(response) mustBe OK
      contentType(response).value mustBe JSON
      contentAsJson(response).as[JobSpec] mustEqual f.simpleJobSpecUpdated
    }

    "fail to DELETE an unknown JobSpecId" in {
      val response = route(app, FakeRequest(DELETE, s"/v1/jobs/unknown")).value
      status(response) mustBe NOT_FOUND
    }

  }

  class Fixture {
    lazy val simpleJobSpec: JobSpec = JobSpec(id = JobId("test"))
    lazy val simpleJobSpecUpdated: JobSpec = JobSpec(id = JobId("test"), run = JobRunSpec(cpus = 4.2))
  }

}
