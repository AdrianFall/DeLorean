package com.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Adrian on 02/09/2015.
 */
public class DeLorean {

    HashMap<Integer, Record> recordMap = new HashMap<>();

    public void processCommand(String command) {


        if (command.startsWith("GET")) {
            boolean isValidGetCommand = false;

            String[] splitCommand = command.split(" ");

            if (splitCommand.length == 3) {
                // Valid GET command
                isValidGetCommand = true;

                // Ensure valid data was inserted
                try {
                    int id = Integer.parseInt(splitCommand[1]);
                    int timestamp = Integer.parseInt(splitCommand[2]);

                    // Get the observation
                    Observation obtainedObservation = get(id, timestamp);
                    if (obtainedObservation != null)
                        System.out.println("OK " + obtainedObservation.getData());
                    else
                        System.out.println("ERR GET couldn't obtain observation at the given id and timestamp");
                } catch (NumberFormatException nfe) {
                    System.out.println("ERR the id or timestamp was not a valid number");
                }
            }

            if (!isValidGetCommand)
                System.out.println("ERR GET command should match the pattern of GET <id> <timestamp>");
        }

        else if (command.startsWith("LATEST")) {
            boolean isValidLatestPattern = false;
            String[] splitCommand = command.split(" ");
            if (splitCommand.length == 2) {
                // Valid latest pattern
                isValidLatestPattern = true;

                // Ensure valid data was inserted
                try {
                    int id = Integer.parseInt(splitCommand[1]);
                    // Get the latest
                    Observation latestObservation = latest(id);
                    if (latestObservation != null)
                        System.out.println("OK " + latestObservation.getTimestamp() + " " + latestObservation.getData());
                    else
                        System.out.println("ERR No latest observation found for the identifier" + id);
                } catch (NumberFormatException nfe) {
                    System.out.println("ERR the id was not a valid number");
                }
            }


            if (!isValidLatestPattern) {
                System.out.println("ERR LATEST command should match the pattern of \"LATEST <id>\"");
            }
        }
        else if (command.startsWith("DELETE")) {
            boolean isValidDeletePattern = false;
            String[] splitCommand = command.split(" ");
            if (splitCommand.length == 2) { // delete(id)
                // Valid delete pattern
                isValidDeletePattern = true;

                // Ensure valid data was inserted
                try {
                    int id = Integer.parseInt(splitCommand[1]);

                    // Attempt to delete
                    Observation deletedObservationWithGreatestTimestamp = delete(id);

                    if (deletedObservationWithGreatestTimestamp != null)
                        System.out.println("OK " + deletedObservationWithGreatestTimestamp.getData());
                    else
                        System.out.println("ERR No history can be deleted for the identifier.");
                } catch (NumberFormatException nfe) {
                    System.out.println("ERR the id was not a valid number.");
                }
            } else if (splitCommand.length == 3) { // delete(id, timestamp)
                // Valid delete pattern
                isValidDeletePattern = true;

                // Ensure valid data was inserted
                try {
                    int id = Integer.parseInt(splitCommand[1]);
                    int timestamp = Integer.parseInt(splitCommand[2]);

                    // Attempt to delete
                    Observation deletedObservation = delete(id, timestamp);

                    if (deletedObservation != null)
                        System.out.println("OK " + deletedObservation.getData());
                    else
                        System.out.println("ERR No history can be found for given id and timestamp");

                } catch (NumberFormatException nfe) {
                    System.out.println("ERR the id or timestamp was not a valid number.");
                }
            }
            if (!isValidDeletePattern) {
                System.out.println("ERR DELETE command should match the pattern of \"DELETE <id>\" OR \"DELETE <id> <timestamp>\"");
            }
        } // END command startsWith DELETE

        else if (command.startsWith("UPDATE")) {
            boolean isValidUpdatePattern = false;

            // Extract command and ensure it matches pattern of "CREATE <id> <timestamp> <data>"

            if (command.contains(" ")) { // If contains at least one empty char
                String[] splitCommand = command.split(" ");
                if (splitCommand.length == 4) { // If spliting by empty char produces an array of 4 elements
                    // Valid update pattern
                    isValidUpdatePattern = true;

                    // Ensure valid data was inserted
                    try {
                        int id = Integer.parseInt(splitCommand[1]);
                        int timestamp = Integer.parseInt(splitCommand[2]);
                        String data = splitCommand[3];

                        // TODO update the record
                        Observation priorObservation = update(id, timestamp, data);
                        if (priorObservation != null) {
                            System.out.println("OK " + priorObservation.getData());
                        } else
                            System.out.println("ERR Couldn't update the history.");

                    } catch (NumberFormatException nfe) {
                        System.out.println("ERR the id or timestamp were not a valid number.");
                        //nfe.printStackTrace();
                    }

                }
            }

            if (!isValidUpdatePattern)
                System.out.println("ERR UPDATE command should match the pattern of \"UPDATE <id> <timestamp> <data>\"");
        } // END command.startsWith("UPDATE")

        else if (command.startsWith("CREATE")) {
            boolean isValidCreatePattern = false;
            // Extract command and ensure it matches pattern of "CREATE <id> <timestamp> <data>"

            if (command.contains(" ")) { // If contains at least one empty char
                String[] splitCommand = command.split(" ");
                if (splitCommand.length == 4) { // If spliting by empty char produces an array of 4 elements
                    // Valid create pattern
                    isValidCreatePattern = true;

                    // Ensure valid data was inserted
                    try {
                        int id = Integer.parseInt(splitCommand[1]);
                        int timestamp = Integer.parseInt(splitCommand[2]);
                        String data = splitCommand[3];

                        // Create new record
                        Record insertedRecord = create(id, timestamp, data);
                        if (insertedRecord == null)
                            System.out.println("ERR A record already exists for identifier " + id);
                        else
                            System.out.println("OK " + insertedRecord.getLatestObservation(timestamp).getData());


                    } catch (NumberFormatException nfe) {
                        System.out.println("ERR the id or timestamp were not a valid number.");
                        //nfe.printStackTrace();
                    }

                }
            }

            if (!isValidCreatePattern)
                System.out.println("ERR Create command should match the pattern of \"CREATE <id> <timestamp> <data>\"");

        } // END command.startsWith("CREATE")
        else { // Not recognised command
            System.out.println("ERR Not recognised command.");
        }
    }
    /**
     * Creates a new history for the given identifier, if there is no existing history. Adds an observation to the newly created
     * history for the given timestamp and data. CREATE should not be executed if the provided identifier
     * already has a history.
     * @param id identifier of the record
     * @param timestamp a given point in time of the observation
     * @param data a piece of data which is associated
     * @return Returns a null value if not created. Otherwise returns the data which was inserted, to confirm insertion.
     */
    public Record create(int id, int timestamp, String data) {
        Record insertedRecord = null;

        if (recordMap.get(id) == null) { // Ensure the identifier doesn't exist
            // System.out.println("Identifier (" + id + ") doesn't exist yet");
            // Create new observation for the record's history
            Observation observation = new Observation(data, timestamp);
            List<Observation> history = new ArrayList<>();
            history.add(observation);

            insertedRecord = new Record(id, history);
            recordMap.put(insertedRecord.getIdentifier(), insertedRecord);
        }

        return insertedRecord;

    }
    /**
     * Inserts an observation for the given identifier, timestamp and data.
     * @param id an existing identifier of the record
     * @param timestamp an existing timestamp of the observation
     * @param data the piece of data to be updated
     * @return Returns null if id or timestamp do not match current record. Otherwise returns the data from the prior observation for that timestamp or the data from the latest (prior to update) observation (in case of new timestamp).
     * */
    public Observation update(int id, int timestamp, String data) {

        Record existingRecord = recordMap.get(id);
        if (existingRecord != null) { // Ensure record exists

            return existingRecord.updateObservation(timestamp, data);


           /* Observation currentObservation = existingRecord.getObservationByTimestamp(timestamp);
            if (currentObservation != null) {
                // Save the prior observation
                Observation priorObservation = new Observation(currentObservation.getData(), currentObservation.getTimestamp());
                // update
                if (existingRecord.updateObservation(timestamp, data) != null)
                    return priorObservation;
            }*/
        }

        return null;
    }

    /**
     * Deletes the history for the given identifier. The record will still exist in the recordMap, but of course without history.
     * @param id an existing identifier of the record
     * @return null if record does not exist. Otherwise returns the observation with the greatest timestamp from the history which has been deleted.
     * */
    public Observation delete(int id) {
        Observation observationWithGreatestTimestampFromHistory = null;

        Record existingRecord = recordMap.get(id);
        if (existingRecord != null) { // Ensure record exists
            observationWithGreatestTimestampFromHistory = existingRecord.deleteHistory();
        }
        return observationWithGreatestTimestampFromHistory;
    }
    /**
     * Deletes all observation for the given identifier from that timestamp forward.
     * @param id an existing identifier of the record
     * @param timestamp an existing timestamp of the observation
     * @return null if there is no available observation. Otherwise returns the current observation at the given timestamp which has been deleted from the history.
     * */
    public Observation delete(int id, int timestamp) {
        Observation observation = null;

        Record existingRecord = recordMap.get(id);
        if (existingRecord != null) { // Ensure record exists
            observation = existingRecord.deleteHistoryFromTimestampOnwards(timestamp);
        }
        return observation;
    }

    public Observation latest(int id) {
        Observation latestObservation = null;

        Record existingRecord = recordMap.get(id);
        if (existingRecord != null)
            latestObservation = existingRecord.getLatestObservation();


        return latestObservation;
    }

    public Observation get(int id, int timestamp) {
        Observation observation = null;

        Record existingRecord = recordMap.get(id);
        if (existingRecord != null)
            observation = existingRecord.getObservationByTimestamp(timestamp);


        return observation;
    }
}
