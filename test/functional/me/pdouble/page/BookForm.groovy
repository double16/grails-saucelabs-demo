package me.pdouble.page

import geb.Page
import groovy.lang.MetaClass;

abstract class BookForm extends Page {
	static content = {
		bookTitle { $('#title') }
		author { $('#author') }
		pages { $('#pages') }
		submit { $('input', type: 'submit') }
	}
}
