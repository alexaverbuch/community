/**
 * Copyright (c) 2002-2012 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.cypher.docgen

import org.junit.Test
import org.neo4j.cypher.StatisticsChecker

class RelateTest extends DocumentingTestBase with StatisticsChecker {
  def graphDescription = List(
    "root X A",
    "root X B",
    "root X C",
    "A KNOWS C"
  )

  def section = "Relate"

  @Test def create_relationship_when_missing() {
    testQuery(
      title = "Create relationship if it is missing",
      text = "+RELATE+ is used to describe the pattern that should be found or created.",
      queryText = "start left=node(%A%), right=node(%B%,%C%) relate left-[r:KNOWS]->right return r",
      returns = "The left node is matched agains the two right nodes. One relationship already exists and can be " +
        "matched, and the other relationship is created before it is returned.",
      assertions = (p) => assertStats(p, relationshipsCreated = 1))
  }

  @Test def create_node_if_it_is_missing() {
    testQuery(
      title = "Create node if missing",
      text = "If the pattern described needs a node, and it can't be matched, a new node will be created.",
      queryText = "start root=node(%root%) relate root-[:LOVES]-someone return someone",
      returns = "The root node doesn't have any LOVES relationships, and so a node is created, and also a relationship " +
        "to that node.",
      assertions = (p) => assertStats(p, relationshipsCreated = 1, nodesCreated = 1))
  }

  @Test def create_node_with_values() {
    testQuery(
      title = "Create nodes with values",
      text = "The pattern described can also contain values on the node. These are given using the JSON-like prop : <expression> syntax.",
      queryText = "start root=node(%root%) relate root-[:X]-(leaf {name:'D'} ) return leaf",
      returns = "Node node connected with the root node has the name 'D', and so a new node needs to be created to " +
        "match the pattern.",
      assertions = (p) => assertStats(p, relationshipsCreated = 1, nodesCreated = 1, propertiesSet = 1))
  }

  @Test def create_relationship_with_values() {
    testQuery(
      title = "Create relationship with values",
      text = "Relationships created can also be matched on values.",
      queryText = "start root=node(%root%) relate root-[r:X {since:'forever'}]-() return r",
      returns = "In this example, we want the relationship to have a value, and since no such relationship can be found," +
        " a new node and relationship are created. Note that since we are not interested in the created node, we don't " +
        "name it.",
      assertions = (p) => assertStats(p, relationshipsCreated = 1, nodesCreated = 1, propertiesSet = 1))
  }
}