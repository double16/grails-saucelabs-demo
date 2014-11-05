package me.pdouble

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification

@TestMixin(GrailsUnitTestMixin)
class BookSpec extends Specification {

  void "toString"() {
    when:'book has title and author'
    def book = new Book(title:'title', author:'author', pages:1)
    then:'toString includes title and author'
    book.toString() == 'title - author'
  }
}
