package MyApplication.demo.controllers;

import MyApplication.demo.models.Film;

import MyApplication.demo.models.FilmSale;
import MyApplication.demo.models.User;
import MyApplication.demo.repos.FilmRepository;
import MyApplication.demo.repos.FilmSaleRepository;
import MyApplication.demo.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.swing.text.html.Option;
import java.util.*;

@Controller
public class LibraryController {

    @Autowired
    private FilmRepository filmRepository;
    @Autowired
    private FilmSaleRepository filmSaleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserController userController;


    @GetMapping("/home")
    public String home(Model model) {
        if(userController.checkActiveUser() == null) return "login";
        userController.checkActiveUser(model);
        return "home";
    }
    @GetMapping("/myFilms")
    public String myFilms(Model model) {
        if(userController.checkActiveUser() == null) return "login";
        User user = userController.checkActiveUser();

        Iterator<FilmSale> iteratorFilmSale = filmSaleRepository.findAll().iterator();
        List<Film> films = new ArrayList<>();
        while (iteratorFilmSale.hasNext())
        {
            FilmSale filmSale = iteratorFilmSale.next();
            if(filmSale.getId_user().equals(user.getId())){
                Long Id_film = filmSale.getId_film();

                if(filmRepository.existsById(Id_film)){
                    Film film = filmRepository.findById(Id_film).get();
                    films.add(film);
                }
                else{
                    Film film = new Film("Фильм удалён");
                }

            }
        }

        model.addAttribute("films", films);
        userController.checkActiveUser(model);
        return "myFilms";
    }

    @GetMapping("/library")
    public String library(Model model){
        if(userController.checkActiveUser() == null) return "login";

        Iterator<Film> iteratorFilms = filmRepository.findAll().iterator();
        List<Film> films = new ArrayList<>();
        List<Film> films0 = new ArrayList<>();
        List<Film> films1 = new ArrayList<>();
        while(iteratorFilms.hasNext()){
            Film film = iteratorFilms.next();
            films.add(film);
        }
        for(int i = 0; i<films.size(); i++){
            if(i%2==0) films0.add(films.get(i));
            else films1.add(films.get(i));
        }

        model.addAttribute("films0", films0);
        model.addAttribute("films1", films1);

        userController.checkActiveUser(model);
        return "library";
    }
    @GetMapping("/library/add")
    public String libraryAdd(Model model){
        if(userController.checkActiveUser() == null) return "login";
        if(userController.checkActiveUser().getStatus() == 0){
            model.addAttribute("message", "Для этого войдите как администратор");
            return "login";
        }
        userController.checkActiveUser(model);
        return "library_add";
    }

    @PostMapping("/library/add")
    public String libraryAddFilm(@RequestParam String title,@RequestParam String anons, @RequestParam String producer,@RequestParam String genre, @RequestParam String full_info,@RequestParam String poster_url, @RequestParam String video_url, Model model){
        Film film = new Film(title, anons, producer, genre, full_info, poster_url, video_url);
        filmRepository.save(film);
        userController.checkActiveUser(model);
        model.addAttribute("genres", getUniqGenres());
        return "redirect:/library";
    }

    @GetMapping("/library/{id}")
    public String filmPage(@PathVariable(value = "id") long id, Model model){
        if(userController.checkActiveUser() == null) return "login";

        if(!filmRepository.existsById(id)){
            model.addAttribute("genres", getUniqGenres());
            return "redirect:/library";
        }
        Optional<Film> optionalFilm = filmRepository.findById(id);
        Film film = optionalFilm.get();
        film.setViews(film.getViews() + 1);
        filmRepository.save(film);
        model.addAttribute("film", film);

        User user = userController.checkActiveUser();
        userController.checkActiveUser(model);
        Iterator<FilmSale> iteratorFilmSale = filmSaleRepository.findAll().iterator();

        while (iteratorFilmSale.hasNext())
        {
            FilmSale filmSale = iteratorFilmSale.next();
            if(filmSale.getId_user().equals(user.getId()) && filmSale.getId_film().equals(id)){
                model.addAttribute("message", "Уже в избранном :)");
                model.addAttribute("isMyFilm", true);
                return "film_page";
            }
        }

        model.addAttribute("message", "В избранное");
        return "film_page";
    }

    @GetMapping("/library/{id}/edit")
    public String filmEdit(@PathVariable(value = "id") long id, Model model){

        if(userController.checkActiveUser() == null) return "login";

        if(!filmRepository.existsById(id)){
            model.addAttribute("genres", getUniqGenres());
            return "redirect:/library";
        }
        Optional<Film> optionalFilm = filmRepository.findById(id);
        Film film = optionalFilm.get();

        model.addAttribute("film", film);
        userController.checkActiveUser(model);
        return "film_edit";
    }

    @PostMapping("/library/{id}/edit")
    public String filmUpdate(@PathVariable(value = "id") long id,@RequestParam String title,@RequestParam String anons, @RequestParam String producer, @RequestParam String genre,@RequestParam String full_info,@RequestParam String poster_url, @RequestParam String video_url, Model model){

        Optional<Film> optionalFilm = filmRepository.findById(id);
        Film film = optionalFilm.get();

        film.setTitle(title);
        film.setAnons(anons);
        film.setProducer(producer);
        film.setFull_info(full_info);
        film.setGenre(genre);
        film.setPoster_url(poster_url);
        film.setVideo_url(video_url);
        filmRepository.save(film);
        userController.checkActiveUser(model);
        model.addAttribute("genres", getUniqGenres());
        return "redirect:/library";
    }

    @PostMapping("/library/{id}/delete")
    public String filmDelete(@PathVariable(value = "id") long id, Model model){

        Optional<Film> optionalFilm = filmRepository.findById(id);
        Film film = optionalFilm.get();
        filmRepository.delete(film);
        userController.checkActiveUser(model);
        model.addAttribute("genres", getUniqGenres());
        return "redirect:/library";
    }



    @PostMapping("/library/{id}/toMyFilms")
    public String filmAddToMyFilms(@PathVariable(value = "id") long id, Model model){

        Optional<Film> optionalFilm = filmRepository.findById(id);
        Film film = optionalFilm.get();
        model.addAttribute("film", film);
        User user = userController.checkActiveUser();
        if(user.getBalance() >= 5){
            Iterator<FilmSale> iteratorFilmSale = filmSaleRepository.findAll().iterator();

            boolean isNewFilmSale = true;

            while (iteratorFilmSale.hasNext())
            {
                FilmSale filmSale = iteratorFilmSale.next();

                if(filmSale.getId_user().equals(user.getId()) && filmSale.getId_film().equals(id)){
                    model.addAttribute("message", "Уже в избранном :)");
                    isNewFilmSale = false;
                    break;
                }
            }

            if(isNewFilmSale){
                FilmSale filmSale = new FilmSale(user.getId(), film.getId());
                user.setBalance(user.getBalance() - 5);
                filmSaleRepository.save(filmSale);
                userRepository.save(user);
                model.addAttribute("message", "В избранном :)");
            }
            isNewFilmSale = true;
        }else{
            model.addAttribute("message", "Недостаточно коинов");
        }



        userController.checkActiveUser(model);
        return "film_page";
    }


    @PostMapping("library/filter")
    public String libraryFilter(Model model, @RequestParam Option genreFilter){
        model.addAttribute("genres", getUniqGenres());

        Iterator<Film> iteratorFilms = filmRepository.findAll().iterator();
        List<Film> films = new ArrayList<>();
        List<Film> films0 = new ArrayList<>();
        List<Film> films1 = new ArrayList<>();
        while(iteratorFilms.hasNext()){
            Film film = iteratorFilms.next();
            films.add(film);
        }
        films.stream().filter(film->film.getGenre().equals(genreFilter.getValue()));
        for(int i = 0; i<films.size(); i++){
            if(i%2==0) films0.add(films.get(i));
            else films1.add(films.get(i));
        }

        model.addAttribute("films0", films0);
        model.addAttribute("films1", films1);
        userController.checkActiveUser(model);

        return "library";
    }

    public String[] getUniqGenres(){
        Iterator<Film> iteratorFilms = filmRepository.findAll().iterator();
        List<String> genres = new ArrayList<>();

        while(iteratorFilms.hasNext()){
            Film film = iteratorFilms.next();
            if(!genres.contains(film.getGenre())) genres.add(film.getGenre());
        }
        String[] strings = new String[genres.size()];
        for(int i =0; i<genres.size(); i++){
            strings[i] = genres.get(i).toString();
        }
        
        return  strings;
    }

}
