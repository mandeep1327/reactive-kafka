Feature: dcsa-events

  Here goes the description.

  Scenario Outline: Sending different files to Kafka
    Given set a message on kafka with "<dataFileName>"
    And I wait external-dcsa-events-processor consumes the message
    Then the EMPv2 topic should produce a message
    Examples:
      | dataFileName         |
      | gems_data_event.json |

    Scenario:
      Given Nothing given
      Then Nothing