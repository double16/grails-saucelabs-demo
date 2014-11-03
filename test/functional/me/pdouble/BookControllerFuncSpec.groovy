package me.pdouble

import me.pdouble.page.BookDetails;
import me.pdouble.page.BookListPage;
import me.pdouble.page.CreateBookPage;
import geb.spock.GebReportingSpec

class BookControllerFuncSpec extends GebReportingSpec {

	void 'book list page'() {
		when:'book list is shown'
		to BookListPage
		then:'book list page is shown'
		at BookListPage
	}
	
	void 'create new book'() {
		given:'at book list page'
		to BookListPage
		
		when:'new book button is clicked'
		newBookButton.click()
		then:'book create form is displayed'
		at CreateBookPage
		
		when:'form is submitted'
		bookTitle = 'New Book'
		author = 'John Doe'
		pages = '25'
		submit.click()
		then:'book details are displayed'
		at BookDetails
		bookTitle == 'New Book'
		author == 'John Doe'
		pages == '25'
		
		when:'book list is visited'
		to BookListPage
		then:'new book is in the list'
		bookTitleAt(0) == 'New Book'
		authorAt(0) == 'John Doe'
		pagesAt(0) == '25'
	}
}
