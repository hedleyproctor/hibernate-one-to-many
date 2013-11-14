package com.hedleyproctor;

import com.hedleyproctor.domain.Address;
import com.hedleyproctor.domain.Customer;
import com.hedleyproctor.domain.PaymentCard;

import org.apache.commons.dbcp.BasicDataSource;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class HibernateOneToMany {

    private BasicDataSource dataSource;
    private SessionFactory sessionFactory;

    @BeforeClass
    public void setUp() throws SQLException {
        dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
        dataSource.setUrl("jdbc:hsqldb:mem:hibernateDB");
        dataSource.setUsername("sa");
        dataSource.setDefaultAutoCommit(false);
        dataSource.setPassword("sa");
        dataSource.setAccessToUnderlyingConnectionAllowed(true);

        sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    @Test
    public void oneToMany() throws SQLException {
        Customer customer = new Customer();
        customer.setForename("Joe");
        customer.setSurname("Bloggs");

        Address homeAddress = new Address();
        homeAddress.setLine1("10 Park Avenue");
        homeAddress.setTown("London");
        homeAddress.setPostcode("SW12RT");
        customer.addAddress(homeAddress);

        Address workAddress = new Address();
        workAddress.setLine1("15 Maple Road");
        workAddress.setTown("Sheffield");
        workAddress.setPostcode("SH142YU");
        customer.addAddress(workAddress);

        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.save(customer);
        tx.commit();
        session.close();

        Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("select count(*) from customer");
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        assertEquals(resultSet.getInt(1), 1, "Should be 1 customer added");

        statement = connection.prepareStatement("select forename,surname from customer");
        resultSet = statement.executeQuery();
        resultSet.next();
        assertEquals(resultSet.getString("forename"),"Joe");
        assertEquals(resultSet.getString("surname"), "Bloggs");

        statement = connection.prepareStatement("select count(*) from address");
        resultSet = statement.executeQuery();
        resultSet.next();
        assertEquals(resultSet.getInt(1), 2, "Should be two addresses added");

        connection.close();
    }
    
    @Test
    public void orderedOneToMany() throws SQLException {
    	Customer customer = new Customer();
        customer.setForename("Joe");
        customer.setSurname("Bloggs");

        PaymentCard visa = new PaymentCard();
        visa.setType("VISA");
        visa.setCardNumber("1234567812345678");
        visa.setNameOnCard("Mr J Bloggs");
        visa.setExpiryDate("0614");
        
        PaymentCard masterCard = new PaymentCard();
        masterCard.setType("MASTERCARD");
        masterCard.setCardNumber("8765432187654321");
        masterCard.setNameOnCard("Mr Joe Bloggs");
        masterCard.setExpiryDate("0714");
        
        PaymentCard amex = new PaymentCard();
        amex.setType("AMEX");
        amex.setCardNumber("1122334455667788");
        amex.setNameOnCard("Joe Bloggs");
        amex.setExpiryDate("0814");
        
        customer.addPaymentCard(visa);
        customer.addPaymentCard(masterCard);
        // insert the final card in a specified position
        // the list is indexed from 0 so 1 is in the middle
        customer.addPaymentCard(amex,1);
        
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        Long customerId = (Long) session.save(customer);
        tx.commit();
        session.close();
        
        // now retrieve
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
        Customer retrievedCustomer = (Customer) session.get(Customer.class, customerId);
        List<PaymentCard> paymentCards = retrievedCustomer.getPaymentCards();
        
        assertEquals(paymentCards.size(),3);
        assertEquals(paymentCards.get(0).getType(),"VISA");
        assertEquals(paymentCards.get(1).getType(),"AMEX");
        assertEquals(paymentCards.get(2).getType(),"MASTERCARD");
        
        tx.commit();
        session.close();
        
    }



}
