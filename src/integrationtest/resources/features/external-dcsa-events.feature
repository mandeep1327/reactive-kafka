Feature: dcsa-events

  Here goes the description.

  Scenario Outline: Sending different files to Kafka
    Given set a message on kafka with "<dataFileName>"
    Then the EMPv2 topic should produce a message
    Examples:
      | dataFileName         |
      | gems_data_event.json |
      | Issue_Verify_Cpoy_of_TPDoc_Closed.json |
      | Shipment_ETA.json |
      | Shipment_ETA_With_Parties.json |
