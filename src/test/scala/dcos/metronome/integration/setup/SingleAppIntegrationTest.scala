package dcos.metronome.integration.setup

import com.typesafe.config.ConfigFactory
import dcos.metronome.JobComponents
import org.scalatest.{ Args, Status }
import org.scalatestplus.play.{ OneAppPerSuite, PlaySpec }
import org.slf4j.LoggerFactory
import play.api.{ Application, ApplicationLoader, Configuration, Environment, Mode }

import scala.concurrent.Future

abstract class SingleAppIntegrationTest extends PlaySpec with OneAppPerSuite {
  val log = LoggerFactory.getLogger(getClass)

  override def run(testName: Option[String], args: Args): Status = {
    // setup zk and mesos before starting the app
    beforeRun()
    super.run(testName, args)
  }

  override implicit lazy val app: Application = {
    val components = new JobComponents(context) {
      override lazy val configuration: Configuration = {
        val testConfig = ConfigFactory.load("test.conf")
        Configuration(testConfig)
      }
    }
    lazy val schedulerService = components.schedulerService

    Future {
      schedulerService.run()
    }(scala.concurrent.ExecutionContext.global)

    components.applicationLifecycle.addStopHook { () =>
      log.info(">>> Application stopped")
      schedulerService.shutdown()
      Future.successful(())
    }

    components.application
  }

  private[this] def beforeRun(): Unit = {
    //make sure last test cleared everything
    ProcessKeeper.shutdown()

    log.info("Setting up zk, mesos infrastructure...")
    startZooKeeperProcess()
    startMesos()
    //      cleanMarathonState()
    //      waitForCleanSlateInMesos()

    log.info("Setting up zk, mesos infrastructure: done.")

    //    startCallbackEndpoint(config.httpPort, config.cwd)

    // TODO: we need to wait for services to come up ...
  }

  private[this] def startZooKeeperProcess(
    port:        Int     = 2183,
    workDir:     String  = "/tmp/foo/single",
    wipeWorkDir: Boolean = true
  ): Unit = {
    ProcessKeeper.startZooKeeper(
      port, workDir, wipeWorkDir, superCreds = Some("super:secret")
    )
  }

  private[this] def startMesos(): Unit = ProcessKeeper.startMesosLocal()

  /**
    * @return a context to use to create the application.
    */
  private[this] def context: ApplicationLoader.Context = {
    val classLoader = ApplicationLoader.getClass.getClassLoader
    val env = new Environment(new java.io.File("."), classLoader, Mode.Test)
    ApplicationLoader.createContext(env)
  }

}
