package me.pdouble

import geb.spock.GebReportingSpec;
import me.pdouble.page.HomePage;

class HomeFuncSpec extends GebReportingSpec {

	void 'home page displays'() {
		when:'home page is visited'
		to HomePage
		
		then:'home page is shown'
		at HomePage
	}
}
