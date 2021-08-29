package com.bslota.optimisticapi.holding.infrastructure.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bslota.optimisticapi.holding.aggregate.StaleStateIdentified;
import com.bslota.optimisticapi.holding.domain.Book;
import com.bslota.optimisticapi.holding.domain.BookId;
import com.bslota.optimisticapi.holding.domain.BookRepository;

@Component

class JpaBasedBookRepository implements BookRepository {

	private final JpaBookRepository jpaBookRepository;

	JpaBasedBookRepository(JpaBookRepository jpaBookRepository) {
		this.jpaBookRepository = jpaBookRepository;
	}

	@Override
	public Optional<Book> findBy(BookId bookId) {
		return jpaBookRepository.findById(bookId.getValue()).map(BookEntity::toDomainModel);
	}

	@Override
	@Transactional
	public void save(Book book) {
		try {
			BookEntity entity = BookEntity.from(book);
			jpaBookRepository.save(entity);
		} catch (OptimisticLockingFailureException ex) {
			throw StaleStateIdentified.forAggregateWith(book.id().getValue());
		}
	}
}

@org.springframework.stereotype.Repository
interface JpaBookRepository extends Repository<BookEntity, UUID> {

	@org.springframework.data.jpa.repository.QueryHints({
			@javax.persistence.QueryHint(name = "org.hibernate.cacheable", value = "true") })
	Optional<BookEntity> findById(UUID id);

	void save(BookEntity bookEntity);
}
