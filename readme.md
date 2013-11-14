Hibernate one-to-many example
=============================

Shows two examples of a one-to-many relationship between entities:

1. Unordered one-to-many between Customer and Address.
2. Ordered one-to-many between Customer and PaymentCard.

Note that in the normal, unordered association, the address object owns the relationship and does the database updates. By constrast, in the ordered association, the customer must take responsibility for updating the database columns for the association, since only the customer knows the ordering of the payment cards in their list. In the address association, the Address class has the mapping, and the Customer uses the mappedBy attribute to say that the Address class owns the relationship. In the payment card association, because you cannot use the "mappedBy" attribute on the "many" side of a one-to-many association, in the PaymentCard object, you have to specify insertable = false, updatable = false on the mapping, so that the Customer object can take care of the database updates.

The code uses HSQLDB as the database and TestNG as the test framework. It is built using maven, so you can run the examples with:

    mvn test
