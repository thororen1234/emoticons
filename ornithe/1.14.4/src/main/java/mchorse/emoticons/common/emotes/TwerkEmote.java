package mchorse.emoticons.common.emotes;

public final class TwerkEmote
		extends Emote {
	TwerkEmote(String string, int n, boolean bl) {
		super(string, n, bl);
	}

	@Override
	public boolean shouldStopOnMove() {
		return true;
	}
}
