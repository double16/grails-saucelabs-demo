import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.Dimension
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.ie.InternetExplorerDriver
import geb.driver.SauceLabsDriverFactory
import java.io.File
import org.apache.commons.lang.SystemUtils

/*
  Tips for cross-browser test:
  1. Start the test on a different page than tests end. The browser may not reload the page is the URL is the same.
  2. Firefox is picky about selectors for non-strict HTML pages. For example, spring-security-core uses single quote deliminators.
  3. Add 'ng-cloak' class to AngularJS elements to hide before AngularJS inits.
  4. AngularJS actions may need a 'ready' class on the element to know when the controller is ready. Or the identifying class can be set using an ng-class attribute so it's not available until the controlller is ready.
  5. 'at' checkers should look for content on the page, not only the title. The title is commonly available before the page content, such as in firefox.
*/

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

def sauceDriver = { String sauceBrowser ->
    def username = System.getenv("SAUCE_LABS_USER")
    assert username
    def accessKey = System.getenv("SAUCE_LABS_ACCESS_PASSWORD")
    assert accessKey
    driver = {
       new SauceLabsDriverFactory().create(sauceBrowser, username, accessKey)
    }    
}

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
      } else if (SystemUtils.IS_OS_LINUX) {
          if (SystemUtils.OS_ARCH?.contains("64")) {
            downloadDriver(chromeDriver, "http://chromedriver.storage.googleapis.com/2.10/chromedriver_linux64.zip")      
          } else {
            downloadDriver(chromeDriver, "http://chromedriver.storage.googleapis.com/2.10/chromedriver_linux32.zip")      
          }
      } else if (SystemUtils.IS_OS_MAC) {
        downloadDriver(chromeDriver, "http://chromedriver.storage.googleapis.com/2.10/chromedriver_mac32.zip")      
      }
      System.setProperty('webdriver.chrome.driver', chromeDriver.absolutePath)
    
      driver = { new ChromeDriver() }
  }
  // grails -Dgeb.env=firefox test-app functional:
  'firefox' { sauceDriver('firefox:Windows 7:31') }
  // grails -Dgeb.env=chrome test-app functional:
  'chrome' { sauceDriver('chrome:Windows 7:36') }
  // grails -Dgeb.env=safari test-app functional:
  'safari' { sauceDriver('safari:OS X 10.9:7') }
  // grails -Dgeb.env=ie9 test-app functional:
  'ie9' { sauceDriver('internetExplorer:Windows 7:9') }
  // grails -Dgeb.env=ie10 test-app functional:
  'ie10' { sauceDriver('internetExplorer:Windows 7:10') }
  // grails -Dgeb.env=ie11 test-app functional:
  'ie11' { sauceDriver('internetExplorer:Windows 7:11') }
}
