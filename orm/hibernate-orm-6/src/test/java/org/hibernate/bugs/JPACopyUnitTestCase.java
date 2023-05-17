package org.hibernate.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM,
 * using the Java Persistence API.
 */
public class JPACopyUnitTestCase {

	private EntityManagerFactory entityManagerFactory;

	@Before
	public void init() {
		entityManagerFactory = Persistence.createEntityManagerFactory("templatePU");
	}

	@After
	public void destroy() {
		entityManagerFactory.close();
	}

	// Entities are auto-discovered, so just add them anywhere on class-path
	// Add your tests, using standard JUnit.
	@Test
	public void hhh16622() throws Exception {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		byte[] result = "myphoto".getBytes();

		MyEntity myEntity = new MyEntity("1", result);
		entityManager.persist(myEntity);

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		CriteriaQuery<MyEntity> cq = cb.createQuery(MyEntity.class);
		Root<MyEntity> root = cq.from(MyEntity.class);

		List<Predicate> predicates = getPredicates(cb, cq, root, "1");

		cq.select(root).where(predicates.toArray(Predicate[]::new));

		TypedQuery<MyEntity> q = entityManager.createQuery(cq);

		long count = count(entityManager, predicates);

		List<MyEntity> myEntityList = q.getResultList();

		assertEquals(count, myEntityList.size());

		entityManager.getTransaction().commit();
		entityManager.close();
	}

	private long count(EntityManager entityManager, List<Predicate> predicates) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<MyEntity> root = cq.from(MyEntity.class);

		cq.select(cb.count(root)).where(predicates.toArray(Predicate[]::new));

		TypedQuery<Long> q = entityManager.createQuery(cq);

		long count = q.getSingleResult();

		return count;
	}

	private List<Predicate> getPredicates(CriteriaBuilder cb, CriteriaQuery<MyEntity> cq, Root<MyEntity> root,
			String id) {
		List<Predicate> predicates = new ArrayList<>();
		predicates.add(cb.equal(root.get("id"), id));

		Subquery<Long> subQuery = cq.subquery(Long.class);
		subQuery.from(MyEntity.class);
		Predicate pred = cb.equal(root.get("id"), "1");
		subQuery.select(cb.literal(1L)).where(pred);

		predicates.add(cb.exists(subQuery));

		return predicates;
	}

}
