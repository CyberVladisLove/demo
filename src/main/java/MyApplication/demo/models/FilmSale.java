package MyApplication.demo.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class FilmSale {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long Id_user;
    private Long Id_film;

    public FilmSale() {
    }
    public FilmSale(Long id_user, Long id_film) {
        Id_user = id_user;
        Id_film = id_film;
    }

    public Long getId() {
        return id;
    }

    public Long getId_user() {
        return Id_user;
    }

    public void setId_user(Long id_user) {
        Id_user = id_user;
    }

    public Long getId_film() {
        return Id_film;
    }

    public void setId_film(Long id_film) {
        Id_film = id_film;
    }
}
