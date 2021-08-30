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
      | issue_verify_copy_of_tpdoc_closed.json  | SHIPMENT   | 96655021 | ACT                 |
      | shipment_eta.json                       | TRANSPORT  | 96654975 | EST                 |
      | shipment_eta_with_parties.json          | TRANSPORT  | 96657859 | EST                 |
