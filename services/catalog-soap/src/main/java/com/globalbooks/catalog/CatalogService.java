package com.globalbooks.catalog;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import java.util.ArrayList;
import java.util.List;

/**
 * SOAP Web Service for Catalog operations.
 * Implements operations to retrieve book details.
 */
@WebService(serviceName = "CatalogService", targetNamespace = "http://globalbooks.com/catalog")
public class CatalogService {

    // In-memory catalog for demo purposes
    private static List<Book> catalog = new ArrayList<>();

    static {
        catalog.add(new Book(1, "Java Programming", "John Doe", 29.99, "Programming"));
        catalog.add(new Book(2, "SOA Essentials", "Jane Smith", 39.99, "Technology"));
        catalog.add(new Book(3, "Microservices Guide", "Bob Johnson", 49.99, "Technology"));
    }

    /**
     * Retrieves a book by its ID.
     * @param id The book ID
     * @return Book object or null if not found
     */
    @WebMethod(operationName = "getBookById")
    public Book getBookById(@WebParam(name = "id") int id) {
        return catalog.stream().filter(book -> book.getId() == id).findFirst().orElse(null);
    }

    /**
     * Retrieves books by category.
     * @param category The book category
     * @return List of books in the category
     */
    @WebMethod(operationName = "getBooksByCategory")
    public List<Book> getBooksByCategory(@WebParam(name = "category") String category) {
        List<Book> result = new ArrayList<>();
        for (Book book : catalog) {
            if (book.getCategory().equalsIgnoreCase(category)) {
                result.add(book);
            }
        }
        return result;
    }
}