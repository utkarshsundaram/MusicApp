// IMediaPlaybackService.aidl
package com.bluesky.exoplayerexample.PlayerService;

// Declare any non-default types here with import statements
import com.bluesky.exoplayerexample.Model.MusicObject;
interface IMediaPlaybackService {
        void openList(in List<MusicObject> listSong, int position);
    	void openSongAt(int position);
    	void openListFromFragment(in List<MusicObject> listSong, int position , String fromFragment);
    	String getTagFragment();
    	long getDuration();
    	long getPosition();
        MusicObject getCurrentSong();
        MusicObject getLastSong();
        int getIndexCurrentSong();
        List<MusicObject> getCurrentListPlay();
        void setShufferMode(int shufferMode);
        void setRepeateMode(int repeateMode);
        int getShufferMode();
        int getRepeateMode();
        boolean isPlaying();
        void stop();
        void pause();
        void play();
        void prev();
        void next();
        void seek(long pos);
        void addToPlaylist(in MusicObject songAdd, int position);
        void removeFromPlayList(int position);
        void setListSong(in List<MusicObject> listSong);
        boolean insertToNowPlaying(in MusicObject song);
        boolean removeFromNowPlaying(int position);
        boolean insertListSongToNowPlaying(in List<MusicObject> listSong);
        boolean playerIsInit();
        void setTimeOffMusic(int time); // Time in second
        int getTimeBeforeMusicOff(); // Time in second
        //CollectionRadio getCurrentCollectionPlay();
        int setAndGetNextRepeatMode();
        int setAndGetNextShufferMode();
        void setInterruptPlay();
        boolean canNext();
        boolean canBack();
        void swap(int from , int to);
}
