package com.tequila.database;

import java.io.IOException;

import com.shatam.utils.U;

public class Test {
	public static void main(String[] args) {
		new Thread(() -> {
			Runtime run = Runtime.getRuntime();
			try {
				//\"https://meet.jit.si/demoLecShatamClassroom#jitsi_meet_external_api_id=0&config.prejoinPageEnabled=false&config.remoteVideoMenu=%7B%22disableKick%22%3Atrue%7D&config.disableRemoteMute=true&interfaceConfig.HIDE_INVITE_MORE_HEADER=true&interfaceConfig.DEFAULT_LOGO_URL=%22logo.png%22&interfaceConfig.DEFAULT_WELCOME_PAGE_LOGO_URL=%22logo.png%22&interfaceConfig.TOOLBAR_BUTTONS=%5B%22microphone%22%2C%22camera%22%2C%22embedmeeting%22%2C%22fodeviceselection%22%2C%22profile%22%2C%22chat%22%2C%22recording%22%2C%22etherpad%22%2C%22sharedvideo%22%2C%22raisehand%22%2C%22videoquality%22%2C%22filmstrip%22%2C%22stats%22%2C%22shortcuts%22%2C%22tileview%22%5D&interfaceConfig.HIDE_KICK_BUTTON_FOR_GUESTS=true&interfaceConfig.SHOW_CHROME_EXTENSION_BANNER=false&userInfo.displayName=%22ShatamTestStudent%22\"
//				Process pr = run.exec(new String[]{"bash", "-c", "google-chrome --app=\"https://meet.jit.si/demoLecShatamClassroom#jitsi_meet_external_api_id=0&config.prejoinPageEnabled=false&config.remoteVideoMenu=%7B%22disableKick%22%3Atrue%7D&config.disableRemoteMute=true&interfaceConfig.HIDE_INVITE_MORE_HEADER=true&interfaceConfig.DEFAULT_LOGO_URL=%22logo.png%22&interfaceConfig.DEFAULT_WELCOME_PAGE_LOGO_URL=%22logo.png%22&interfaceConfig.TOOLBAR_BUTTONS=%5B%22microphone%22%2C%22camera%22%2C%22embedmeeting%22%2C%22fodeviceselection%22%2C%22profile%22%2C%22chat%22%2C%22recording%22%2C%22etherpad%22%2C%22sharedvideo%22%2C%22raisehand%22%2C%22videoquality%22%2C%22filmstrip%22%2C%22stats%22%2C%22shortcuts%22%2C%22tileview%22%5D&interfaceConfig.HIDE_KICK_BUTTON_FOR_GUESTS=true&interfaceConfig.SHOW_CHROME_EXTENSION_BANNER=false&userInfo.displayName=%22ShatamTestStudent%22\""});
				Process pr = run.exec(new String[]{"bash", "-c", "chromium-browser --app=\"https://meet.jit.si/demoLecShatamClassroom#jitsi_meet_external_api_id=0&config.prejoinPageEnabled=false&config.remoteVideoMenu=%7B%22disableKick%22%3Atrue%7D&config.disableRemoteMute=true&interfaceConfig.HIDE_INVITE_MORE_HEADER=true&interfaceConfig.DEFAULT_LOGO_URL=%22logo.png%22&interfaceConfig.DEFAULT_WELCOME_PAGE_LOGO_URL=%22logo.png%22&interfaceConfig.TOOLBAR_BUTTONS=%5B%22microphone%22%2C%22camera%22%2C%22embedmeeting%22%2C%22fodeviceselection%22%2C%22profile%22%2C%22chat%22%2C%22recording%22%2C%22etherpad%22%2C%22sharedvideo%22%2C%22raisehand%22%2C%22videoquality%22%2C%22filmstrip%22%2C%22stats%22%2C%22shortcuts%22%2C%22tileview%22%5D&interfaceConfig.HIDE_KICK_BUTTON_FOR_GUESTS=true&interfaceConfig.SHOW_CHROME_EXTENSION_BANNER=false&userInfo.displayName=%22ShatamTestStudent%22\""});
				U.log(pr.getErrorStream());
				pr.waitFor();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}
}
