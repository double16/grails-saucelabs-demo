package me.pdouble

class Book {
	String title
	String author
	int pages
	
  static constraints = {
		title nullable: false
		author nullable: false
		pages min: 1
  }

  String toString() {
    return "${title} - ${author}"
  }
}
