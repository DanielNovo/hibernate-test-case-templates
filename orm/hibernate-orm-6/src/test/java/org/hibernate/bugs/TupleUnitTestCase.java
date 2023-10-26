package org.hibernate.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using the Java Persistence API.
 */
public class TupleUnitTestCase {

	private EntityManagerFactory entityManagerFactory;

	@Before
	public void init() {
		entityManagerFactory = Persistence.createEntityManagerFactory( "templatePU" );
	}

	@After
	public void destroy() {
		entityManagerFactory.close();
	}

	// Entities are auto-discovered, so just add them anywhere on class-path
	// Add your tests, using standard JUnit.
	@Test
	public void hhh16534Test() throws Exception {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		
		BookEntity bookOne = new BookEntity("1", "NOVEL");
		BookEntity bookTwo = new BookEntity("2", "CRIME");
		BookEntity bookThree = new BookEntity("3", "NOVEL");
		
		entityManager.persist(bookOne);
		entityManager.persist(bookTwo);
		entityManager.persist(bookThree);
		
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);

		Root<BookEntity> root = cq.from(BookEntity.class);

		cq.select(cb.construct(Tuple.class, root.get("type").alias("type"),
				cb.count(root.get("type")).alias("count")))
				.groupBy(root.get("type"));

		TypedQuery<Tuple> tq = entityManager.createQuery(cq);

		Map<String, Long> map = tq.getResultList().stream().collect(Collectors.toMap(tuple -> ((String) tuple.get("type")), tuple -> ((Long) tuple.get("count"))));

		System.out.println(map);
		
		assertEquals(map.size(), 2);
		
		entityManager.getTransaction().commit();
		entityManager.close();
	}
}
