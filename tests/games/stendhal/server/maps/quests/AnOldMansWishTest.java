/***************************************************************************
 *                    Copyright © 2003-2022 - Arianne                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReplies;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.Before;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rp.StendhalQuestSystem;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.deniran.cityinterior.brelandhouse.OldManNPC;
import games.stendhal.server.maps.deniran.cityoutside.LittleGirlNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;


public class AnOldMansWishTest extends QuestHelper {

	private static final StendhalQuestSystem quests = StendhalQuestSystem.get();

	private static final String QUEST_SLOT = AnOldMansWish.QUEST_SLOT;

	private Player player;
	private SpeakerNPC elias;
	private SpeakerNPC marianne;


	@Before
	public void setup() {
		final StendhalRPZone zone = new StendhalRPZone("test_zone");
		new OldManNPC().configureZone(zone, null);
		new LittleGirlNPC().configureZone(zone, null);
		player = PlayerTestHelper.createPlayer("player");
		elias = SingletonRepository.getNPCList().get("Elias Breland");
		marianne = SingletonRepository.getNPCList().get("Marianne");
	}

	@Test
	public void init() {
		checkEntities();
		checkBeforeQuest();
		checkEliasStep();
		checkMarianneStep();
		checkFindApothecaryStep();
		checkAfterQuest();
	}

	private void checkEntities() {
		assertNotNull(player);
		assertNotNull(elias);
		assertNotNull(marianne);
		assertFalse(player.hasQuest(QUEST_SLOT));
	}

	private void checkBeforeQuest() {
		// quest not added to world
		Engine en = elias.getEngine();
		en.step(player, "hi");
		en.step(player, "quest");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"There is something that weighs heavy on me. But I am not ready"
				+ " for help. Perhaps you could come back later.",
			getReply(elias));
		en.step(player, "bye");

		// Marianne should not respond before quest is started
		en = marianne.getEngine();
		en.step(player, "hi");
		marianne.clearEvents(); // clear reply to "hi"
		en.step(player, "Niall");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertNull(getReply(marianne));
		en.step(player, "bye");

		// clear replies
		elias.clearEvents();
		marianne.clearEvents();
	}

	private void checkEliasStep() {
		player.setLevel(99);
		assertEquals(99, player.getLevel());

		final Engine en = elias.getEngine();
		en.step(player, "hello");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Hello young one.", getReply(elias));

		// add quest to world
		final AnOldMansWish quest = new AnOldMansWish();
		assertFalse(quests.isLoaded(quest));
		quests.loadQuest(quest);
		assertTrue(quests.isLoaded(quest));

		// level too low to start quest
		en.step(player, "quest");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"My grandson disappeared over a year ago. But I need help from a"
				+ " more experienced adventurer.",
			getReply(elias));

		player.setLevel(100);
		assertEquals(100, player.getLevel());

		en.step(player, "quest");
		assertEquals(ConversationStates.QUEST_OFFERED, en.getCurrentState());
		assertEquals(
			"My grandson disappeared over a year ago. I fear the worst and"
				+ " have nearly given up all hope. What I would give to just"
				+ " know what happened to him! If you learn anything will"
				+ " you bring me the news?",
			getReply(elias));

		en.step(player, "no");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Alas! What has become of my grandson!?", getReply(elias));
		assertEquals("rejected", player.getQuest(QUEST_SLOT, 0));

		en.step(player, "quest");
		en.step(player, "yes");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Oh thank you! My grandson's name is #Niall. You could talk to"
				+ " #Marianne. They used to play together.",
			getReply(elias));
		assertEquals("investigate", player.getQuest(QUEST_SLOT, 0));

		// quest already started
		en.step(player, "quest");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Thank you for accepting my plea for help. Please tell me if"
				+ " you hear any news about what has become of my grandson."
				+ " He used to play with a little girl named #Marianne.",
			getReply(elias));

		en.step(player, "Niall");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Niall is my grandson. I am so distraught over his disappearance."
				+ " Ask the girl #Marianne. The often played together.",
			getReply(elias));

		en.step(player, "Marianne");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Marianne lives here in Deniran. Ask her about #Niall.",
			getReply(elias));

		en.step(player, "bye");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("Goodbye.", getReply(elias));
	}

	private void checkMarianneStep() {
		final Engine en = marianne.getEngine();

		en.step(player, "hi");
		marianne.clearEvents(); // clear reply to "hi"
		en.step(player, "Niall");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Oh! My friend Niall! I haven't seen him in a long time. Every"
				+ " time I go to his grandfather's house to #play, he is not"
				+ " home.",
			getReplies(marianne).get(0));

		en.step(player, "play");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Not only was he fun to play with, but he was also very helpful."
				+ " He used to help me gather chicken eggs whenever I was too"
				+ " #afraid to do it myself.",
			getReplies(marianne).get(0));

		en.step(player, "afraid");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Know what he told me once? He said he wanted to go all the way"
				+ " to Semos to see the #graveyard there. Nuh uh! No way! That"
				+ " sounds more scary than chickens.",
			getReplies(marianne).get(0));
		assertEquals("find_myling:start", player.getQuest(QUEST_SLOT, 1));

		en.step(player, "graveyard");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"I hope he didn't go to that scary graveyard. Who knows what kind"
				+ " of monsters are there.",
			getReply(marianne));

		en.step(player, "bye");
	}

	private void checkFindApothecaryStep() {
		final Engine en = elias.getEngine();

		// TODO: set in quest action
		player.setQuest(QUEST_SLOT, 1, "find_myling:done");

		en.step(player, "hi");
		en.step(player, "myling");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Oh no! My dear grandson! If only there were a way to #change"
					+ " him back.",
			getReply(elias));

		en.step(player, "change");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Wait! I heard there was an apothecary that lives somewhere"
					+ " near Semos. Please, go to him and plead for help.",
			getReply(elias));
		assertEquals("find_apothecary:start", player.getQuest(QUEST_SLOT, 2));

		en.step(player, "apothecary");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Please! Find the apothecary. Maybe he can do something to"
					+ " help my grandson.",
			getReply(elias));

		en.step(player, "bye");
	}

	private void checkAfterQuest() {
		// TODO: complete quest
		player.setQuest(QUEST_SLOT, 0, "done");

		Engine en = elias.getEngine();

		en.step(player, "hi");
		en.step(player, "quest");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Thank you for returning my grandson to me. I am overfilled"
				+ " with joy!",
			getReply(elias));

		en.step(player, "bye");

		en = marianne.getEngine();

		en.step(player, "hi");
		marianne.clearEvents(); // clear reply to "hi"
		en.step(player, "Niall");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"I heard that Niall came home! He sure was gone for a long time."
				+ " I am glad he is home safe.",
			getReplies(marianne).get(0));

		en.step(player, "bye");
	}
}