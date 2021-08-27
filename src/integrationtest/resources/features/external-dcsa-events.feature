Feature: dcsa-events

  Here goes the description.

  Scenario Outline: Sending different files to Kafka
    Given set a message on kafka with "<dataFileName>"
    Then the EMPv2 topic should produce a message
    And it should be of type "<eventType>"
    And the event id should be "<eventId>"
    And the eventClassifierCode should be "<eventClassifierCode>"
    Examples:
      | dataFileName                            | eventType  | eventId  | eventClassifierCode |
      | gems_data_event.json                    | TRANSPORT  | 96483854 | EST                 |
      | Issue_Verify_Cpoy_of_TPDoc_Closed.json  | SHIPMENT   | 96655021 | ACT                 |
      | Shipment_ETA.json                       | TRANSPORT  | 96654975 | EST                 |
      | Shipment_ETA_With_Parties.json          | TRANSPORT  | 96657859 | EST                 |
