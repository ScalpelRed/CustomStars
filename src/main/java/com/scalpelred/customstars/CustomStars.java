package com.scalpelred.customstars;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomStars implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("customstars");

	@Override
	public void onInitialize() {

		{
			String spl = "Cosmic stuff!";
			if (Math.random() < 0.95) spl = "Yowza! " + spl;
			else spl = "oOwOo " + spl;
            LOGGER.info(spl);
		}
	}
}