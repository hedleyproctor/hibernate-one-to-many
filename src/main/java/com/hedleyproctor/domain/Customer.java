package com.hedleyproctor.domain;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Customer {

    private Long id;
    private String forename;
    private String surname;
    private Set<Address> addresses = new HashSet<Address>();
    private List<PaymentCard> paymentCards = new ArrayList<PaymentCard>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    public Set<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(Set<Address> addresses) {
        this.addresses = addresses;
    }
    
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    @OrderColumn(name = "CARD_INDEX")
    // Order column wasn't in JPA 1, so you'd previously have had: @org.hibernate.annotations.IndexColumn(name = "CARD_INDEX")
    public List<PaymentCard> getPaymentCards() {
		return paymentCards;
	}

	public void setPaymentCards(List<PaymentCard> paymentCards) {
		this.paymentCards = paymentCards;
	}

	public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void addAddress(Address address) {
        getAddresses().add(address);
        address.setCustomer(this);
    }
    
    public void addPaymentCard(PaymentCard paymentCard) {
    	getPaymentCards().add(paymentCard);
    	paymentCard.setCustomer(this);
    }
    
    public void addPaymentCard(PaymentCard paymentCard, int index) {
    	getPaymentCards().add(index, paymentCard);
    	paymentCard.setCustomer(this);
    }
}
