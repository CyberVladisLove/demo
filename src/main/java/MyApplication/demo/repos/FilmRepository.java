package MyApplication.demo.repos;

import MyApplication.demo.models.Film;
import org.springframework.data.repository.CrudRepository;

public interface FilmRepository extends CrudRepository<Film, Long> {

}
