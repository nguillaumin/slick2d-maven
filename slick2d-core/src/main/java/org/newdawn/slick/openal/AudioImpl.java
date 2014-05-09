package org.newdawn.slick.openal;

import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

/**
 * A sound that can be played through OpenAL
 * 
 * @author Kevin Glass
 * @author Nathan Sweet <misc@n4te.com>
 */
public class AudioImpl implements Audio {
	/** The store from which this sound was loaded */
	protected SoundStore store;
	/** The buffer containing the sound */
	protected int buffer;
	/** The index of the source being used to play this sound */
	protected int index = -1;
	
	/** The length of the audio */
	protected float length;
	
	/**
	 * Create a new sound
	 * 
	 * @param store The sound store from which the sound was created
	 * @param buffer The buffer containing the sound data
	 */
	AudioImpl(SoundStore store, int buffer) {
		this.store = store;
		this.buffer = buffer;
		
		int bytes = AL10.alGetBufferi(buffer, AL10.AL_SIZE);
		int bits = AL10.alGetBufferi(buffer, AL10.AL_BITS);
		int channels = AL10.alGetBufferi(buffer, AL10.AL_CHANNELS);
		int freq = AL10.alGetBufferi(buffer, AL10.AL_FREQUENCY);
		
		int samples = bytes / (bits / 8);
		length = (samples / (float) freq) / channels;
	}

	/**
	 * Calls stop() and releases this buffer from memory. For music, this will
	 * stop the source, remove any queued buffers, and close the stream.
	 * For sound, this will stop the source and release the buffer contained by
	 * the Sound.
	 */
	public void release() {
		int oldIndex = index;
		stop();
		if (oldIndex!=-1) 
			//detach buffer from source
			AL10.alSourcei(SoundStore.get().getSource(oldIndex), AL10.AL_BUFFER, 0);
		//delete buffer
		if (buffer!=0)
			AL10.alDeleteBuffers(buffer);
		index = -1;
		buffer = 0;
	}
	
	/**
	 * Get the ID of the OpenAL buffer holding this data (if any). This method
	 * is not valid with streaming resources.
	 * 
	 * If the source has been released, this will return zero.
	 * 
	 * @return The ID of the OpenAL buffer holding this data 
	 */
	public int getBufferID() {
		return buffer;
	}
	
	/**
	 * Returns the index of the source found in the SoundStore;
	 * the source ID can then be retrieved with SoundStore.getSource().
	 * This may be -1 if the sound is not attached to a source.
	 * @return the last attached source
	 */
	protected int getSourceIndex() {
		return index;
	}
	
	/**
	 *
	 */
	protected AudioImpl() {
		
	}
	
	/**
	 * @see org.newdawn.slick.openal.Audio#stop()
	 */
	public void stop() {			
		if (index != -1) {
			store.stopSource(index);
			index = -1;
		}
	}
	
	/**
	 * @see org.newdawn.slick.openal.Audio#isPlaying()
	 */
	public boolean isPlaying() {
		if (index != -1) {
			return SoundStore.get().isPlaying(index);
		}
		
		return false;
	}
	
	/**
	 * Returns true if this audio has a source attached and that source
	 * is currently paused.
	 * @return true if paused
	 */
	public boolean isPaused() {
		if (index != -1)
			return SoundStore.get().isPaused(index);
		return false;
	}
	
	/**
	 * @see org.newdawn.slick.openal.Audio#playAsSoundEffect(float, float, boolean)
	 */
	public int playAsSoundEffect(float pitch, float gain, boolean loop) {
		if (buffer==0)
			return 0;
		index = store.playAsSound(buffer, pitch, gain, loop);
		return store.getSource(index);
	}


	/**
	 * @see org.newdawn.slick.openal.Audio#playAsSoundEffect(float, float, boolean, float, float, float)
	 */
	public int playAsSoundEffect(float pitch, float gain, boolean loop, float x, float y, float z) {
		if (buffer==0)
			return 0;
		index = store.playAsSoundAt(buffer, pitch, gain, loop, x, y, z);
		return store.getSource(index);
	}
	
	/**
	 * @see org.newdawn.slick.openal.Audio#playAsMusic(float, float, boolean)
	 */
	public int playAsMusic(float pitch, float gain, boolean loop) {
		if (buffer==0)
			return 0;
		store.playAsMusic(buffer, pitch, gain, loop);
		index = 0;
		return store.getSource(0);
	}
	
	/**
	 * Pause the music currently being played
	 */
	public static void pauseMusic() {
		SoundStore.get().pauseLoop();
	}

	/**
	 * Restart the music currently being paused
	 */
	public static void restartMusic() {
		SoundStore.get().restartLoop();
	}
	
	/**
	 * @see org.newdawn.slick.openal.Audio#setPosition(float)
	 */
	public boolean setPosition(float position) {
		position = position % length;
		
		AL10.alSourcef(store.getSource(index), AL11.AL_SEC_OFFSET, position);
		if (AL10.alGetError() != 0) {
			return false;
		}
		return true;
	}

	/**
	 * @see org.newdawn.slick.openal.Audio#getPosition()
	 */
	public float getPosition() {
		return AL10.alGetSourcef(store.getSource(index), AL11.AL_SEC_OFFSET);
	}
}
