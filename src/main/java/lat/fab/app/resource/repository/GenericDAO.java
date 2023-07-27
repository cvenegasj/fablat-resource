package lat.fab.app.resource.repository;

import java.io.Serializable;
import java.util.List;

public interface GenericDAO<T, ID extends Serializable> {

	T findById(ID id);

	List<T> findAll();

	T makePersistent(T entity);

	void makeTransient(T entity);

}