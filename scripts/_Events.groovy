import com.saucelabs.ci.sauceconnect.SauceConnectTwoManager
import com.saucelabs.ci.sauceconnect.SauceTunnelManager

SauceTunnelManager sauceConnectManager = null

eventTestPhaseStart = { phase ->
  if (phase == 'functional') {

    if (!sauceConnectManager) {

      def username = System.getenv('SAUCE_LABS_USER')
      def accessKey = System.getenv('SAUCE_LABS_ACCESS_PASSWORD')
      def port = 4445

      if (username && accessKey) {
        try {
          SauceTunnelManager manager = new SauceConnectTwoManager();
          File sauceConnectJar = null // it will find the JAR in the classpath
          String options = null
          String httpsProtocol = null
          manager.openConnection(username, accessKey, port, sauceConnectJar, options, httpsProtocol, System.out, false /*verbose logging*/);
          sauceConnectManager = manager
        } catch (IOException e) {
          println "Error generated when launching Sauce Connect: ${e.message}"
        }
      } else {
        println "sauce-connect requires environment variables SAUCE_LABS_USER and SAUCE_LABS_ACCESS_PASSWORD (API key)"
      }
    }

  }
}

eventTestPhaseEnd = { phase ->
  if (phase == 'functional') {

    if (sauceConnectManager) {
      def username = System.getenv('SAUCE_LABS_USER')
      sauceConnectManager.closeTunnelsForPlan(username, null, System.out);
    }

  }
}