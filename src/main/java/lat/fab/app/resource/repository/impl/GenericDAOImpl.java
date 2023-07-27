package lat.fab.app.resource.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lat.fab.app.resource.repository.GenericDAO;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

//@Repository
//@Transactional
public abstract class GenericDAOImpl<T, ID extends Serializable> implements GenericDAO<T, ID> {

	@PersistenceContext
	protected EntityManager entityManager;
	private Class<T> domainClass;

	private Class<T> getDomainClass() {
		if (domainClass == null) {
			ParameterizedType thisType = (ParameterizedType) getClass().getGenericSuperclass();
			this.domainClass = (Class<T>) thisType.getActualTypeArguments()[0];
		}
		return domainClass;
	}

	public String getDomainClassName() {
		return getDomainClass().getName();
	}

//	@Transactional
	public T findById(ID id) {
		T entity;
		entity = (T) entityManager.find(getDomainClass(), id);
		return entity;
	}

//	@Transactional
	public List<T> findAll() {
		List<T> entities;
		entities = (List<T>) entityManager.createQuery("from " + getDomainClassName() + " x").getResultList();
		return entities;
	}

//	@Transactional
	public T makePersistent(T entity) {
		entityManager.persist(entity);
		return entity;
	}

//	@Transactional
	public void makeTransient(T entity) {
		entityManager.remove(entity);
	}

}
