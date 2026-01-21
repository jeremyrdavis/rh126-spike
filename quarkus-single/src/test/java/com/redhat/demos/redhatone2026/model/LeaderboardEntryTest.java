package com.redhat.demos.redhatone2026.model;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for LeaderboardEntry record.
 * Validates record creation, field accessors, and JSON serialization.
 */
@QuarkusTest
class LeaderboardEntryTest {

    @Test
    void testRecordCreation() {
        LeaderboardEntry entry = new LeaderboardEntry("alice", 15, 1, 15);

        assertNotNull(entry);
        assertEquals("alice", entry.username());
        assertEquals(15, entry.score());
        assertEquals(1, entry.rank());
        assertEquals(15, entry.questionsAnsweredCorrectly());
    }

    @Test
    void testRecordEquality() {
        LeaderboardEntry entry1 = new LeaderboardEntry("bob", 10, 2, 10);
        LeaderboardEntry entry2 = new LeaderboardEntry("bob", 10, 2, 10);
        LeaderboardEntry entry3 = new LeaderboardEntry("alice", 10, 2, 10);

        assertEquals(entry1, entry2);
        assertNotEquals(entry1, entry3);
    }

    @Test
    void testJsonSerialization() throws Exception {
        LeaderboardEntry entry = new LeaderboardEntry("charlie", 8, 3, 8);
        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(entry);

        assertTrue(json.contains("\"username\":\"charlie\""));
        assertTrue(json.contains("\"score\":8"));
        assertTrue(json.contains("\"rank\":3"));
        assertTrue(json.contains("\"questionsAnsweredCorrectly\":8"));
    }

    @Test
    void testJsonDeserialization() throws Exception {
        String json = "{\"username\":\"dave\",\"score\":5,\"rank\":4,\"questionsAnsweredCorrectly\":5}";
        ObjectMapper mapper = new ObjectMapper();

        LeaderboardEntry entry = mapper.readValue(json, LeaderboardEntry.class);

        assertEquals("dave", entry.username());
        assertEquals(5, entry.score());
        assertEquals(4, entry.rank());
        assertEquals(5, entry.questionsAnsweredCorrectly());
    }

    @Test
    void testRecordWithZeroValues() {
        LeaderboardEntry entry = new LeaderboardEntry("newuser", 0, 10, 0);

        assertEquals("newuser", entry.username());
        assertEquals(0, entry.score());
        assertEquals(10, entry.rank());
        assertEquals(0, entry.questionsAnsweredCorrectly());
    }
}
