package games.stendhal.server.entity.npc.condition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.npc.parser.ConversationParser;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.SpeakerNPCTestHelper;

public class QuestSmallerThanConditionTest {
	private static final String QUESTNAME = "questname";

	@BeforeClass
	public static void setUpClass() throws Exception {
		Log4J.init();
		MockStendlRPWorld.get();
		MockStendhalRPRuleProcessor.get();
	}

	@Test
	public final void testFire() {
		final Player bob = PlayerTestHelper.createPlayer("player");
		final int value = 2009;

		assertFalse(new QuestSmallerThanCondition(QUESTNAME, value).fire(
				bob,
				ConversationParser.parse("testQuestSmallerThanCondition"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		bob.setQuest(QUESTNAME, "invalid");
		assertFalse(new QuestSmallerThanCondition(QUESTNAME, value).fire(bob,
				ConversationParser.parse("testQuestSmallerThanCondition"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		bob.setQuest(QUESTNAME, "");
		assertFalse(new QuestSmallerThanCondition(QUESTNAME, value).fire(bob,
				ConversationParser.parse("testQuestSmallerThanCondition"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		bob.setQuest(QUESTNAME, "2010");
		assertFalse(new QuestSmallerThanCondition(QUESTNAME, value).fire(bob,
				ConversationParser.parse("testQuestSmallerThanCondition"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		bob.setQuest(QUESTNAME, "2009");
		assertFalse(new QuestSmallerThanCondition(QUESTNAME, value).fire(bob,
				ConversationParser.parse("testQuestSmallerThanCondition"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		bob.setQuest(QUESTNAME, "2008");
		assertTrue(new QuestSmallerThanCondition(QUESTNAME, value).fire(bob,
				ConversationParser.parse("testQuestSmallerThanCondition"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

	}

	@Test
	public final void testToString() {
		assertEquals("QuestSmallerThan <questname[-1] = 2009>",
				new QuestSmallerThanCondition(QUESTNAME, 2009).toString());
	}

	@Test
	public void testEquals() throws Throwable {
		final int value = 2009;
		assertFalse(new QuestSmallerThanCondition(QUESTNAME, value).equals(null));

		final QuestSmallerThanCondition obj = new QuestSmallerThanCondition(QUESTNAME, value);
		assertTrue(obj.equals(obj));

		assertTrue(new QuestSmallerThanCondition(QUESTNAME, value).equals(new QuestSmallerThanCondition(QUESTNAME, value)));
		assertTrue(new QuestSmallerThanCondition(null, value).equals(new QuestSmallerThanCondition(null, value)));

		assertFalse(new QuestSmallerThanCondition(QUESTNAME, value).equals(new Object()));

		assertFalse(new QuestSmallerThanCondition(null, value).equals(new QuestSmallerThanCondition(QUESTNAME, value)));
		assertFalse(new QuestSmallerThanCondition(QUESTNAME, 2008).equals(new QuestSmallerThanCondition(QUESTNAME, value)));
		assertFalse(new QuestSmallerThanCondition(QUESTNAME, 2008).equals(new QuestSmallerThanCondition(null, value)));
		assertFalse(new QuestSmallerThanCondition(QUESTNAME, value).equals(new QuestSmallerThanCondition(QUESTNAME, value + 2)));

		assertTrue(new QuestSmallerThanCondition(QUESTNAME, value).equals(new QuestSmallerThanCondition(QUESTNAME, value) {
			// this is an anonymous sub class
		}));
	}

	@Test
	public void testHashCode() throws Throwable {

		final QuestSmallerThanCondition obj = new QuestSmallerThanCondition(QUESTNAME, 2009);
		assertEquals(obj.hashCode(), obj.hashCode());

		assertEquals(
				new QuestSmallerThanCondition("questname", 2009).hashCode(),
				new QuestSmallerThanCondition("questname", 2009).hashCode());
		assertEquals(
				new QuestSmallerThanCondition(null, 2009).hashCode(),
				new QuestSmallerThanCondition(null, 2009).hashCode());
	}

}
