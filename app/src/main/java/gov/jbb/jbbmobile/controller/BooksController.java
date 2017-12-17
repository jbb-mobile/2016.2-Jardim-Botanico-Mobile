package gov.jbb.jbbmobile.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.SQLException;

import gov.jbb.jbbmobile.R;
import gov.jbb.jbbmobile.dao.BookDAO;
import gov.jbb.jbbmobile.dao.ElementDAO;
import gov.jbb.jbbmobile.model.Book;

import java.text.SimpleDateFormat;

public class BooksController {
    private Book[] book;
    private LoginController loginController;
    private int currentPeriod;

    public void insertBooks(Context context){
        //Instantiating three books in the books vector
        book = new Book[]{new Book(), new Book(), new Book()};

        getBook(0).setNameBook(context.getString(R.string.firstBook));
        getBook(0).setIdBook(1);
        getBook(1).setNameBook(context.getString(R.string.secondBook));
        getBook(1).setIdBook(2);
        getBook(2).setNameBook(context.getString(R.string.thirdBook));
        getBook(2).setIdBook(3);

        //Inserting books in the book database
        try {
            BookDAO bookDAO = new BookDAO(context);
            bookDAO.insertBook(getBook(0));
            bookDAO.insertBook(getBook(1));
            bookDAO.insertBook(getBook(2));
            bookDAO.close();
        } catch (SQLException e){
            e.getMessage();
        }
    }

    public BooksController() {}

    public BooksController(Context context) {
        getAllBooksData(context);
        getElementsFromDatabase(context);
    }

    public BooksController(SharedPreferences settings, Context context) {
        if (settings.getBoolean("mainScreenFirstTime", true)) {
            insertBooks(context);
            settings.edit().putBoolean("mainScreenFirstTime", false).apply();
        }

        getAllBooksData(context);
        getElementsFromDatabase(context);
    }

    public void getAllBooksData(Context context){
        //Initialize three books
        book = new Book[]{new Book(), new Book(), new Book()};
        findBooks(context);
    }

    public void getElementsFromDatabase(Context context) {
        //Initialize database
        ElementDAO elementDAO = new ElementDAO(context);
        findBooks(context);

        //Get all element from one book and sets them in List<Element>
        findExplorerLogged(context);
        getBook(0).setElements(elementDAO.findElementsExplorerBook(book[0].getIdBook(),loginController.getExplorer().getEmail()));
        getBook(1).setElements(elementDAO.findElementsExplorerBook(book[1].getIdBook(),loginController.getExplorer().getEmail()));
        getBook(2).setElements(elementDAO.findElementsExplorerBook(book[2].getIdBook(),loginController.getExplorer().getEmail()));

        elementDAO.close();
    }

    public String[] getElementsForBook(int idBook){
        String[] names = new String[getBook(idBook).getElements().size()];

        for(int i=0;i<getBook(idBook).getElements().size();i++){
            names[i] = "";
            names[i] = getBook(idBook).getElements().get(i).getNameElement();
        }

        return names;
    }

    public int[] getElementsId(int idBook){
        int[] idElements = new int[getBook(idBook).getElements().size()];

        for(int i = 0 ; i < getBook(idBook).getElements().size() ; i++){
            idElements[i] = getBook(idBook).getElements().get(i).getIdElement();
        }

        return idElements;
    }

    public int[] getElementsImage(Context context, int idBook){
        int[] elementsImage = new int[getBook(idBook).getElements().size()];

        for(int i = 0 ; i < getBook(idBook).getElements().size() ; i++){
            elementsImage[i] = context.getResources().getIdentifier(getBook(idBook).getElements().get(i).getDefaultImage(),"drawable",context.getPackageName());
        }

        return elementsImage;
    }

    public int[] getElementsHistory(int idBook){
        int[] historyElements = new int[getBook(idBook).getElements().size()];

        for(int i = 0 ; i < getBook(idBook).getElements().size() ; i++){
            historyElements[i] = getBook(idBook).getElements().get(i).getHistory();
        }

        return historyElements;
    }

    public Book getBook(int id) {
        return book[id];
    }

    public void setBook(Book book, int id) {
        this.book[id] = book;
    }

    public void findBooks(Context context){
        BookDAO bookDAO = new BookDAO(context);
        this.book[0] = bookDAO.findBook(1);
        this.book[1] = bookDAO.findBook(2);
        this.book[2] = bookDAO.findBook(3);
        bookDAO.close();
    }

    public void findExplorerLogged(Context context){
        loginController = new LoginController();
        loginController.loadFile(context);
    }

    public void currentPeriod(){
        int systemMonth = 0;
        long date;
        date = System.currentTimeMillis();

        SimpleDateFormat monthSimpleDateFormat = new SimpleDateFormat("MM");
        String month = monthSimpleDateFormat.format(date);
        systemMonth = Integer.valueOf(month);

        if(systemMonth > 10 || systemMonth < 3){
            setCurrentPeriod(3);
        }else if(systemMonth > 2 && systemMonth < 6){
            setCurrentPeriod(2);
        }else{
            setCurrentPeriod(1);
        }
    }

    public Book[] getAllBooks(){
        return book;
    }

    public int getCurrentPeriod() {
        return currentPeriod;
    }

    public void setCurrentPeriod(int currentPeriod) {
        this.currentPeriod = currentPeriod;
    }
}