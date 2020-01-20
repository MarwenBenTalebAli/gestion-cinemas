package com.mycompany.cinema.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mycompany.cinema.dao.CategorieRepository;
import com.mycompany.cinema.dao.CinemaRepository;
import com.mycompany.cinema.dao.FilmRepository;
import com.mycompany.cinema.dao.PlaceRepository;
import com.mycompany.cinema.dao.ProjectionRepository;
import com.mycompany.cinema.dao.SalleRepository;
import com.mycompany.cinema.dao.SeanceRepository;
import com.mycompany.cinema.dao.TicketRepository;
import com.mycompany.cinema.dao.VilleRepository;
import com.mycompany.cinema.entities.Categorie;
import com.mycompany.cinema.entities.Cinema;
import com.mycompany.cinema.entities.Film;
import com.mycompany.cinema.entities.Place;
import com.mycompany.cinema.entities.ProjectionFilm;
import com.mycompany.cinema.entities.Salle;
import com.mycompany.cinema.entities.Seance;
import com.mycompany.cinema.entities.Ticket;
import com.mycompany.cinema.entities.Ville;

@Service
@Transactional
public class CinemaInitServiceImpl implements ICinemaInitService {

	@Autowired
	private VilleRepository villeRepository;

	@Autowired
	private CinemaRepository cinemaRepository;

	@Autowired
	private SalleRepository salleRepository;

	@Autowired
	private PlaceRepository placeRepository;

	@Autowired
	private SeanceRepository seanceRepository;

	@Autowired
	private CategorieRepository categorieRepository;

	@Autowired
	private FilmRepository filmRepository;

	@Autowired
	private ProjectionRepository projectionRepository;

	@Autowired
	private TicketRepository ticketRepository;

	@Override
	public void initVilles() {
		Stream.of("Casablanca", "Marrakech", "Rabat", "Tanger").forEach(nameVille -> {
			Ville ville = new Ville();
			ville.setName(nameVille);
			villeRepository.save(ville);
		});
	}

	@Override
	public void initCinemas() {
		villeRepository.findAll().forEach(ville -> {
			Stream.of("MagaRama", "IMAX", "FOUNOUN", "CHAHRAZAD", "DAOULIZ").forEach(nameCinema -> {
				Cinema cinema = new Cinema();
				cinema.setName(nameCinema);
				cinema.setNombreSalles(3 + (int) (Math.random() * 7));
				cinema.setVille(ville);
				cinemaRepository.save(cinema);
			});
		});
	}

	@Override
	public void initSalles() {
		cinemaRepository.findAll().forEach(cinema -> {
			for (int i = 0; i < cinema.getNombreSalles(); i++) {
				Salle salle = new Salle();
				salle.setName("Salle " + (i + 1));
				salle.setCinema(cinema);
				salle.setNombrePlaces(15 + (int) (Math.random() * 20));
				salleRepository.save(salle);
			}
		});
	}

	@Override
	public void initPlaces() {
		salleRepository.findAll().forEach(salle -> {
			for (int i = 0; i < salle.getNombrePlaces(); i++) {
				Place place = new Place();
				place.setNumero(i+1);
				place.setSalle(salle);
				placeRepository.save(place);
			}
		});
	}

	@Override
	public void initSeances() {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm");
		Stream.of("12:00", "15:00", "17:00", "19:00", "21:00").forEach(heure -> {
			Seance seance = new Seance();
			try {
				seance.setHeureDebut(dateFormat.parse(heure));
				seanceRepository.save(seance);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public void initCategories() {
		Stream.of("Histoire", "Actions", "Fiction", "Drama").forEach(name -> {
			Categorie categorie = new Categorie();
			categorie.setName(name);
			categorieRepository.save(categorie);
		});
	}

	@Override
	public void initFilms() {
		double[] durees = new double[] { 1, 1.5, 2, 2.5, 3 };
		List<Categorie> categories = categorieRepository.findAll();
		Stream.of("Sans Un Bruit 2", "Conjuring 3", "Underwater", "Escape Room 2", "The Turning")
				.forEach(titreFilm -> {
					Film film = new Film();
					film.setTitre(titreFilm);
					film.setDuree(durees[new Random().nextInt(durees.length)]);
					film.setDescription(titreFilm);
					film.setPhoto(titreFilm.replaceAll(" ", "")+".jpg");
					film.setCategorie(categories.get(new Random().nextInt(categories.size())));
					filmRepository.save(film);
				});
	}

	@Override
	public void initProjections() {
		double[] prices = new double[] { 30, 50, 60, 70, 90, 100 };
		List<Film> films = filmRepository.findAll();
		villeRepository.findAll().forEach(ville -> {
			ville.getCinemas().forEach(cinema -> {
				cinema.getSalles().forEach(salle -> {
					int index = new Random().nextInt(films.size());
					Film film = films.get(index);
					seanceRepository.findAll().forEach(seance -> {
						ProjectionFilm projectionFilm = new ProjectionFilm();
						projectionFilm.setDateProjection(new Date());
						projectionFilm.setFilm(film);
						projectionFilm.setPrix(prices[new Random().nextInt(prices.length)]);
						projectionFilm.setSalle(salle);
						projectionFilm.setSeance(seance);
						projectionRepository.save(projectionFilm);
					});
				});
			});
		});
	}

	@Override
	public void initTickets() {
		projectionRepository.findAll().forEach(projection -> {
			projection.getSalle().getPlaces().forEach(place -> {
				Ticket ticket = new Ticket();
				ticket.setPlace(place);
				ticket.setPrix(projection.getPrix());
				ticket.setProjection(projection);
				ticket.setReserve(false);
				ticket.setCodePayement(null);
				ticketRepository.save(ticket);
			});
		});
	}
}
