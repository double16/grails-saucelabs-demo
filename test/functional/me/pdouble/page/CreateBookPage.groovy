package me.pdouble.page

import groovy.lang.MetaClass

class CreateBookPage extends BookForm {
	static url = 'book/create'
	static at = { title == 'Create Book' }
}
