package org.hibernate.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using the Java Persistence API.
 */
public class JPAByteArrayUnitTestCase {

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
	public void hhh16606() throws Exception {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		
		byte[] result = "myphoto".getBytes();
		
		MyEntity myEntity = new MyEntity("1", result);
		entityManager.persist(myEntity);
		
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<byte[]> cq = cb.createQuery(byte[].class);

		Root<MyEntity> root = cq.from(MyEntity.class);

		cq.select(root.get("photo"))
				.where(cb.equal(root.get("id"), "1"));

		TypedQuery<byte[]> q = entityManager.createQuery(cq);

		byte[] bytes = q.getSingleResult();
		
		assertEquals(result, bytes);
		
		entityManager.getTransaction().commit();
		entityManager.close();
	}
}
