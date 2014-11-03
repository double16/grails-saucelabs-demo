package me.pdouble.page

import geb.Page

class BookDetails extends Page {
	static at = { title == 'Show Book' }
	static content = {
		bookTitle { $('ol.book span.property-value', 0).text() }
		author { $('ol.book span.property-value', 1).text() }
		pages { $('ol.book span.property-value', 2).text() }
	}
}
