/***************************************************************************
 *                    Copyright © 2003-2022 - Stendhal                     *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { ConfigManager } from "./ConfigManager";
import { WeatherRenderer } from "./WeatherRenderer";

import { EmojiStore } from "../data/EmojiStore";

import { KeyHandler } from "../ui/KeyHandler";
import { LoopedSoundSourceManager } from "../ui/LoopedSoundSourceManager";
import { SoundManager } from "../ui/SoundManager";


export class SingletonRepo {

	static getConfigManager(): ConfigManager {
		return ConfigManager.get();
	}

	static getEmojiStore(): EmojiStore {
		return EmojiStore.get();
	}

	static getKeyHandler(): KeyHandler {
		return KeyHandler;
	}

	static getLoopedSoundSourceManager(): LoopedSoundSourceManager {
		return LoopedSoundSourceManager.get();
	}

	static getSoundManager(): SoundManager {
		return SoundManager.get();
	}

	static getWeatherRenderer(): WeatherRenderer {
		return WeatherRenderer.get();
	}
}

export { SingletonRepo as default };