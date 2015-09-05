package com.test;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * Created by Adrian on 02/09/2015.
 */
public class Record {
    private int identifier;
    private List<Observation> history;

    public static Predicate<Observation> isHistoryContainingLowerOrEqualTimestampValue(int timestamp) {
        return p ->
                p.getTimestamp() > timestamp || p.getTimestamp() == timestamp;
    }

    public static Predicate<Observation> isHistoryContainingEqualTimestampValue(int timestamp) {
        return p ->
                p.getTimestamp() == timestamp;
    }




    /**
     * The latest observation for an identifier at a timestamp is found by searching in the identifier's
     * history for the first observation whose timestamp is less than, or equal to, the sought timestamp.
     * @param timestamp timestamp of the particular observation
     * @return null if no observation at lower or the same timestamp value exists. Otherwise returns an Observation object.
     * */
    public Observation getLatestObservation(int timestamp) {
        Observation latestObservation = null;

        Optional<Observation> optionalObservation = history.stream()
                .filter(isHistoryContainingLowerOrEqualTimestampValue(timestamp))
                .findFirst();

        if (optionalObservation.isPresent())
            latestObservation = optionalObservation.get();
        else
            System.out.println("ERR Optional Observation is not present for id " + identifier + " and timestamp less or equal to " + timestamp);
        return latestObservation;
    }

    /**
     * The latest observation for an identifier.
     * @return null if no observation exists. Otherwise returns the latest Observation opbject in the history.
     * */
    public Observation getLatestObservation() {
        Observation latestObservation = null;

        try {
            latestObservation = history.stream()
                    .max(Comparator.comparing(e -> e.getTimestamp()))
                    .get();
        } catch (NoSuchElementException nsee) {}

        return latestObservation;
    }

    /**
     * Updates the particular observation at a timestamp, with the new data. Otherwise if the timestamp does not already exist, it creates a new Observation with the data.
     * @param timestamp timestamp of the particular observation
     * @param data the new data
     * @return null if observation was not updated. Otherwise if the timestamp already existed it will return it's previous value, or in case that there was no existing timestamp then will return the latest (prior to update) observation.
     */
    public Observation updateObservation(int timestamp, String data) {
        Observation observation = null;

        Optional<Observation> optionalExistingObservation = history.stream()
                .filter(isHistoryContainingEqualTimestampValue(timestamp))
                .findFirst();

        if (optionalExistingObservation.isPresent()) {
            Observation existingObservation = optionalExistingObservation.get();
            Observation previousObservation = new Observation(existingObservation.getData(), existingObservation.getTimestamp());
            // Override the data for existing timestamp
            existingObservation.setData(data);
            observation = previousObservation;
            return observation;
        }
        // Get latest observation prior to the update
        observation = getLatestObservation();
        // Add new Observation to the history
        Observation newObservation = new Observation(data, timestamp);
        history.add(newObservation);

        return observation;
    }

    /**
     * Obtains the particular observation at a timestamp.
     * @param timestamp timestamp of the particular observation
     * @return null if no observation at the timestamp value exists. Otherwise returns the particular Observation.
     * */
    public Observation getObservationByTimestamp(int timestamp) {
        Observation observation = null;

        Optional<Observation> optionalObservation = history.stream()
                .filter(isHistoryContainingEqualTimestampValue(timestamp))
                .findFirst();

        if (optionalObservation.isPresent())
            observation = optionalObservation.get();

        return observation;
    }


    /**
     * Deletes the entire history of the record.
     * @return null if no history is deleted. Otherwise returns Observation with the greatest timestamp from the deleted history.
     * */
    public Observation deleteHistory() {
        Observation observationWithGreatestTimestampFromHistory = null;

        try {
            observationWithGreatestTimestampFromHistory = history.stream()
                    .max(Comparator.comparing(e -> e.getTimestamp()))
                    .get();
        } catch (NoSuchElementException nsee) {}
        if (observationWithGreatestTimestampFromHistory != null) { // Check if any observation exists
            System.out.println("REMOVING HISTORY");
            history = new ArrayList<>();
        }

        return observationWithGreatestTimestampFromHistory;
    }

    /**
     * Deletes the history from the given timestamp onwards.
     * @param timestamp an existing timestamp
     * @return null if no observation on given timestamp exists. Otherwise returns the Observation with the given timestamp from the deleted history.
     */
    public Observation deleteHistoryFromTimestampOnwards(int timestamp) {
        Observation deletedObservation = null;

        Observation queryObservation = getObservationByTimestamp(timestamp);
        if (queryObservation != null) { // Ensure it exists
            deletedObservation = new Observation(queryObservation.getData(), queryObservation.getTimestamp());

            // Delete all with >= timestamp
            IntStream.range(0, history.size())
                    .filter(e -> history.get(e).getTimestamp() >= timestamp)
                    .forEach(e -> history.remove(e));
        }

        return deletedObservation;
    }


    public Record(int identifier, List<Observation> history) {
        this.identifier = identifier;
        this.history = history;
    }

    public Record() {

    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public List<Observation> getHistory() {
        return history;
    }

    public void setHistory(List<Observation> history) {
        this.history = history;
    }
}
