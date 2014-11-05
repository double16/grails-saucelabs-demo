package me.pdouble

import com.saucelabs.ci.sauceconnect.SauceConnectTwoManager
import com.saucelabs.ci.sauceconnect.SauceTunnelManager
import com.saucelabs.common.Utils
import com.saucelabs.saucerest.SauceREST
import geb.driver.SauceLabsDriverFactory
import grails.build.GrailsBuildListener
import org.codehaus.groovy.grails.cli.support.GrailsBuildEventListener
import org.openqa.selenium.remote.RemoteWebDriver

/**
 * Manages Sauce Connect and updating of the test results. This class assumes Geb tests, which
 * assumes one session per test invocation. This class will not work if multiple sessions (driver instances)
 * are used in the same JVM. Sauce Connect v2 (Java based) will be started at the beginning of the test
 * phase and closed at the end. Test results of pass or fail will be updated at the end of the test phase.
 */
class SauceGebTestService implements GrailsBuildListener {
  /** username from configuration. */
  String username
  /** accessKey from configuration. */
  String accessKey
  /** port from configuration. */
  int port = 4445
  /** whether to use sauce connect from configuration. */
  boolean useSauceConnect = true
  boolean verboseMode = true

  private boolean testsFailed = false
  private String sessionId
  private SauceREST sauceREST
  private SauceTunnelManager sauceConnectManager

  void init(ConfigSlurper config, GrailsBuildEventListener eventListener) {
    if (config) {
      username = config.username
      accessKey = config.accessKey
      port = (config.port ?: 4445) as int
      useSauceConnect = config.connect ?: true
    }

    if (username && accessKey) {
      sauceREST = new SauceREST(username, accessKey)
      eventListener.addGrailsBuildListener(this)
      addSessionIdRetriever()
    } else {
      println "SauceLabs integration requires username and access key"
    }
  }

  /**
   * Open the connection to Sauce Connect.
   * @return true if the connection was established
   */
  boolean openSauceConnect() {
    boolean connected = false
    if (useSauceConnect) {
      try {
        SauceTunnelManager manager = new SauceConnectTwoManager();
        File sauceConnectJar = null // it will find the JAR in the classpath
        String options = null
        String httpsProtocol = null
        manager.openConnection(username, accessKey, port, sauceConnectJar, options, httpsProtocol, System.out, false /*verbose logging*/);
        sauceConnectManager = manager
        connected = true
      } catch (IOException e) {
        println "Error generated when launching Sauce Connect: ${e.message}"
      }
    }
    connected
  }

  /**
   * Closes the Sauce Connect connection. This is safe to call whether a connection has been successfully
   * established or not.
   */
  void closeSauceConnect() {
    if (sauceConnectManager) {
      sauceConnectManager.closeTunnelsForPlan(username, null, System.out);
    }
  }

  void addSessionIdRetriever() {
    SauceLabsDriverFactory.metaClass.invokeMethod = { String name, args ->
      def metaMethod = SauceLabsDriverFactory.metaClass.getMetaMethod(name, args)
      def result
      if(metaMethod) {
        result = metaMethod.invoke(delegate, args)
      }
      if (name == 'create' && result instanceof RemoteWebDriver) {
        // get the info we need from the RemoteWebDriver
        RemoteWebDriver driver = result
        sessionId = driver.sessionId
        if (verboseMode) {
          println "sessionId = ${sessionId}"
        }
      }
      result
    }
  }

  void updateTestResults() {
    if (sessionId != null) {
      Map<String, Object> updates = new HashMap<String, Object>();
      updates.put("passed", !testsFailed);
      Utils.addBuildNumberToUpdate(updates);
      sauceREST.updateJobInfo(sessionId, updates);

      if (verboseMode) {
        String authLink = sauceREST.getPublicJobLink(sessionId);
        println("Job link: " + authLink);
      }
    }
  }

  @Override
  void receiveGrailsBuildEvent(String name, Object... args) {
    switch (name) {
      case 'TestPhaseStart':
        if (args[0] == 'functional') {
          startTestPhase()
        }
        break
      case 'TestPhaseEnd':
        if (args[0] == 'functional') {
          endTestPhase()
        }
        break
      case 'TestFailure':
        testFailure(args[0], args[1], args[2])
        break
    }
  }

  void startTestPhase() {
    openSauceConnect()
  }

  void endTestPhase() {
    updateTestResults()
    closeSauceConnect()
  }

  void testFailure(String name, def failure, boolean isError) {
    testsFailed = true
  }
}
