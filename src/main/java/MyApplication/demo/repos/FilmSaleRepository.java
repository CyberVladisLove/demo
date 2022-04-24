package MyApplication.demo.repos;

import MyApplication.demo.models.Film;
import MyApplication.demo.models.FilmSale;
import org.springframework.data.repository.CrudRepository;

public interface FilmSaleRepository extends CrudRepository<FilmSale, Long> {
}
