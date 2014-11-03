package me.pdouble.page

import geb.Page

class BookListPage extends Page {
	static url = 'book/index'
	static at = { title == 'Book List' }
	static content = {
		newBookButton(to: CreateBookPage) { $('a.create') }
		books { $('#list-book tbody tr') }
		bookAt { index -> $('#list-book tbody tr', index) }
		bookTitleAt { index -> bookAt(index).find('td', 0).find('a').text() }
		authorAt { index -> bookAt(index).find('td', 1).text() }
		pagesAt { index -> bookAt(index).find('td', 2).text() }
	}
}
