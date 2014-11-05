import me.pdouble.SauceGebTestService

def service = new SauceGebTestService()
service.username = System.getenv('SAUCE_LABS_USER')
service.accessKey = System.getenv('SAUCE_LABS_ACCESS_PASSWORD')
service.init(null, eventListener)
