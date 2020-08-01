package eu.kartoffelquadrat.polyglot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * REST controller for the Polyglot backend. All REST entry points around cards are defined here.
 *
 * @Author Maximilian Schiedermeier, Github: kartoffelquadrat
 */
@RestController
@RequestMapping(path = "/polyglot") // This means URL's start with /dbdemo
public class CardController {

    @Autowired
    private CardRepository cardRepository;

    /**
     * Retrieve meta-info for all cards
     */
    @GetMapping(path = "/")
    public @ResponseBody
    int[] getFillState() {

        // Look up how many words are stored per box. The DB has five boxes, starting at index 0.
        int[] fillState = new int[5];
        for (int i = 0; i < fillState.length; i++) {
            fillState[i] = cardRepository.countByBox(i);
        }
        return fillState;
    }

    /**
     * Retrieve all saved cards from DB: curl -X GET http://127.0.0.1:8080/polyglot/cards
     * <p>
     * Retrieve all saved cards, having a specific substring, from the DB ---NOT YET ENABLED---
     */
    @GetMapping(path = "/cards")
    public @ResponseBody
    Iterable<Card> getAllCards() {

        return cardRepository.findAll();
    }


    /**
     * Card id is generated -> not an idempotent resource. That means cards should be created with a POST on the parent
     * collection rather than with a PUT on the card-id.
     *
     * curl -H 'Content-type:application/json' -X POST http://127.0.0.1:8080/polyglot/cards --data '{"french":"La grenouille","german":"Der Frosch"}'
     */
    @PostMapping(path = "/cards", consumes = "application/json; charset=utf-8")
    public void addCard(@RequestBody CardStub cardStub) {

        // New card has to created, for consistent ID to be generated.
        Card card = new Card();
        card.setFrench(cardStub.getFrench());
        card.setGerman(cardStub.getGerman());
        card.setBox(0);
        cardRepository.save(card);
    }

    /**
     * Get details of a random card
     */
    @GetMapping("/cards/random")
    public ResponseEntity<Object> getRandomCard() {
        if (cardRepository.count() == 0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No cards available.");

        // Minor flaw: Cards of almost empty boxes are retrieved with higher probability, compared to fuller boxes.
        List<Integer> cardIdsInBox = cardRepository.findRandomInBox(getRandomNonEmptyBox());
        int randomCardIdOfRandomBox = randomListElement(cardIdsInBox);
        return ResponseEntity.ok().body(cardRepository.findById(randomCardIdOfRandomBox));
    }

    /**
     * Get details of a specific card, identified by id.
     *
     * curl -X GET http://127.0.0.1:8080/polyglot/cards/42
     */
    @GetMapping("/cards/{cardId}")
    public ResponseEntity<Object> getCard(@PathVariable int cardId) {
        if (!cardRepository.existsById(cardId))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Requested card not found.");

        return ResponseEntity.ok().body(cardRepository.findById(cardId));
    }

    /**
     * Update details of a specific card, identified by id.
     *
     * curl -H 'Content-type:application/json' -X POST http://127.0.0.1:8080/polyglot/cards/3 --data '{"id":3,"french":"Lamour (f)","german":"Die Liebe","box":3}'
     */
    @PostMapping(path="/cards/{cardId}", consumes = "application/json; charset=utf-8")
    public ResponseEntity<Object> updateCard(@PathVariable int cardId, @RequestBody Card card) {
        if (!cardRepository.existsById(cardId))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Requested card not found.");

        if(cardId != card.getId())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Card id mismatch. Modifications rejected.");

        cardRepository.save(card);
        return ResponseEntity.ok().body("");
    }

    /**
     * Get details of a specific card, identified by id.
     *
     * curl -X DELETE http://127.0.0.1:8080/polyglot/cards/42
     */
    @DeleteMapping("/cards/{cardId}")
    public ResponseEntity<Object> deleteCard(@PathVariable int cardId) {
        if (!cardRepository.existsById(cardId))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Requested card not found.");

        cardRepository.deleteById(cardId);
        return ResponseEntity.ok().body("");
    }


    /**
     * Returns the index of a random, non-empty box.
     *
     * @return
     */
    private int getRandomNonEmptyBox() {

        if (cardRepository.count() == 0)
            throw new RuntimeException("Cannot retrieve random non-empty box, when all boxes are empty");

        // Build a collection with the indexes of all non-empty boxes.
        List<Integer> nonEmptyBoxes = new LinkedList<>();
        int[] fillState = getFillState();
        for (int i = 0; i < fillState.length; i++) {
            if (fillState[i] > 0)
                nonEmptyBoxes.add(i);
        }

        // Select a random element, out of these non-empty boxed.
        return randomListElement(nonEmptyBoxes);
    }

    private int randomListElement(List<Integer> list) {
        return list.get(new Random().nextInt(list.size()));
    }


}