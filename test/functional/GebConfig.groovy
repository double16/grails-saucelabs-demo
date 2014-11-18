import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.Dimension
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.ie.InternetExplorerDriver
import geb.driver.SauceLabsDriverFactory
import java.io.File
import org.apache.commons.lang.SystemUtils

waiting {
  timeout = 10
  retryInterval = 0.5
  presets {
        slow {
            timeout = 20
            retryInterval = 1
        }
        quick {
            timeout = 5
        }
    }
}

atCheckWaiting = true
baseNavigatorWaiting = true

def downloadDriver = { File file, String path ->
    if (!file.exists()) {
        def ant = new AntBuilder()
        ant.get(src: path, dest: 'driver.zip')
        ant.unzip(src: 'driver.zip', dest: file.parent)
        ant.delete(file: 'driver.zip')
        ant.chmod(file: file, perm: '700')
    }
}

driver = {
    def d = new PhantomJSDriver(new DesiredCapabilities())
    d.manage().window().setSize(new Dimension(1024, 768))
    d
}

def sauceDriver = { def browserCaps ->
    def username = System.getenv("SAUCE_LABS_USER")
    assert username
    def accessKey = System.getenv("SAUCE_LABS_ACCESS_PASSWORD")
    assert accessKey
    def caps = [:]
    caps << browserCaps
    caps.put('name', 'grails-saucelabs-demo')
    caps.put('build', "git rev-parse HEAD".execute().text)
    driver = {
       new SauceLabsDriverFactory().create(username, accessKey, caps)
    }
}

// This property comes from the gradle geb-saucelabs plugin
def sauceLabsBrowser = System.getProperty("geb.saucelabs.browser")
if (sauceLabsBrowser) {
  def browserCaps = new Properties()
  browserCaps.load(new StringReader(sauceLabsBrowser.replaceAll(',','\n')))
  sauceDriver(browserCaps)
} else {

  // This comes from the 'geb.env' system property supported by Geb
  environments {
    // grails -Dgeb.env=firefox-local test-app functional:
    'firefox-local' {
      driver = { new FirefoxDriver() }
    }
    // grails -Dgeb.env=chrome-local test-app functional:
    'chrome-local' {
      def chromeDriver = new File('target/webdrivers/chrome/chromedriver')
      if (SystemUtils.IS_OS_WINDOWS) {
        downloadDriver(chromeDriver, "http://chromedriver.storage.googleapis.com/2.10/chromedriver_win32.zip")
      } else if (SystemUtils.IS_OS_LINUX
      ) {
          if (SystemUtils.OS_ARCH?.
              contains("64")) {
            downloadDriver(chromeDriver,
                "http://chromedriver.storage.googleapis.com/2.10/chromedriver_linux64.zip")
          } else {
            downloadDriver(chromeDriver,
                "http://chromedriver.storage.googleapis.com/2.10/chromedriver_linux32.zip")
          }
      } else if (SystemUtils.IS_OS_MAC) {
        downloadDriver(chromeDriver,
            "http://chromedriver.storage.googleapis.com/2.10/chromedriver_mac32.zip")
      }
      System.setProperty(

          'webdriver.chrome.driver', chromeDriver.absolutePath)
      driver = { new ChromeDriver() }
  } // grails -Dgeb.env=firefox test-app functional:
    'firefox' {

      sauceDriver(browserName: 'firefox', platform:'Windows 7', version: '31') }
    'firefox-yosemite' {

      sauceDriver(browserName: 'firefox', platform:
          'OS X 10.10', version:'33') } // grails -Dgeb.env=chrome test-app functional:
    'chrome' {

      sauceDriver(browserName: 'chrome', platform:
          'Windows 7', version:'36') } // grails -Dgeb.env=safari test-app functional:
    'safari' {

      sauceDriver(browserName: 'safari', platform:
          'OS X 10.9', version:'7') } // grails -Dgeb.env=ie8 test-app functional:
  'ie8' {
    sauceDriver(

        browserName: 'internet explorer', platform:
        'Windows 7', version:'8') } // grails -Dgeb.env=ie9 test-app functional:
  'ie9' {
    sauceDriver(

        browserName: 'internet explorer', platform:
        'Windows 7', version:'9') } // grails -Dgeb.env=ie10 test-app functional:
  'ie10' {
    sauceDriver(

        browserName: 'internet explorer', platform:
        'Windows 7', version:'10') } // grails -Dgeb.env=ie11 test-app functional:
  'ie11' {
    sauceDriver(
        browserName: 'internet explorer', platform: 'Windows 8.1', version:'11') }

  'ios' { sauceDriver(browserName:
      'iPhone'
      ,

      platform:'OS X 10.9', version:'7.1', 'device-orientation': 'portrait') }
}

}
