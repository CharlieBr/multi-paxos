Feature: Working of multi paxos protocol

  Scenario: Save a single value on all nodes
    When client sends key A with value 1 to node 3
    Then node 1 has saved key A with value 1
    Then node 2 has saved key A with value 1
    Then node 3 has saved key A with value 1

  Scenario: Remove single key
    When client sends remove request with key A to node 3
    Then node 1 has removed key A
    Then node 2 has removed key A
    Then node 3 has removed key A
