Demo Videos

1. BottomNavigationView
a. Fragments Screenshots
![image](https://github.com/caesiummm/MusicPlaybackApp/assets/129256652/8dcbf3d6-b1ef-4292-9bcd-9894c2133754)

[Image]
[Image]

b. Bottom Navigation Bar Layout Design
- BottomNavigationView: Material design bottom navigation layout
[Image]

- Menu Creation: Populates bar contents using menu resource files. Defines menu item title and icon.
[Image]
[Image]

- Selector: To switch between active & inactive state of menu item
[Image]

- Switch between SongsFragment, LibraryFragment, and ProfileFragment in MainActivity
[Image]

  - FragmentTransaction: Obtain fragment manager instance to start a new fragment transaction (in this case, replacing fragments) whenever an item in the bottom navigation bar is clicked.
  - commit() - execute the transaction
[Image]


2. Deezer API
a. Retrofit
- A library for making network requests in Android applications.
- 简单来说: It helps connect Android applications to Internet
- Retrofit is built on top of OkHttp client library
  - Save time freeing the developer from the setup needed to run a request and focus on the functionality that is specified in the app
[Image]

- retrofitBuilder(): Creates a Retrofit instance
[Image]

b. API Interface
- @Headers: API key and host for authentication purpose
- @GET: Specifies the endpoint of the Deezer API
- getData(): takes a query parameter (q) representing the artist name as input and returns a Call object containing the response data of type Tracks.
[Image]

c. Fetching music tracks
1. After creating a Retrofit instance, the getData() method of the ApiInterface is invoked with the artist name ("eminem") as the query parameter.
2. This initiates a network request to the Deezer API's search endpoint with the specified query parameter.
3. The Deezer API processes the request and returns a response containing a list of music tracks related to the artist "Eminem".
4. Once the network request is made, Retrofit handles the communication with the Deezer API asynchronously.
5. When the response is received, Retrofit converts the JSON response into Kotlin objects based on the defined data model (in this case, Tracks).
6. The response data is then processed and displayed in the app, such as populating a list of music tracks in RecyclerView.
[Image]

d. Data Classes
Songs Data
[Image]
Album Data
[Image]

Artist Data
[Image]


3. RecyclerView
- A ViewGroup to contain the views corresponding to Songs data
[Image]

[Image]
- SongAdapter: Binds songs data to each item view in RecyclerView
[Image]

- LayoutManager: Positions songs item linear vertically  
- ViewHolder: Gets references to the views inside the song-item-layout file, which will load data into the views every time the layout is recycled to show new data.
[Image]

- LikedSongFragment implements RecyclerView as well. The same song item view is used for both RecyclerViews.
[Image]

4. SearchView
- A UI widget for users to enter search query
- setOnQueryTextListener: Filter song list based on song's title and artist's name
- SongAdapter updates the list based on the filtered song data
[Image]
[Image]
[Image]


5. MediaPlayer
- A class that enables audio playback, using methods like start(), pause(), seekTo()
[Image]

a. Play/Pause
[Image]

b. Skip to Previous/Next
[Image]
[Image]

c. SeekBar
- To allow users to monitor and alter the progress of the song.
[Image]

- Schedule the Runnable interface to run after a delay of 1000ms, which means updating the seek bar progress every 1 second.
[Image]

6. SharedPreferences: Keep a list of liked-songs
- Using SharedPreferences key-value storage mechanism to store primitive data (in this case, isLiked: Boolean).
[Image]
[Image]
[Image]
[Image]

a. Write to shared preference: Save like state
- Check the like button state when it is clicked.
  - If the button is not clicked (the song is not liked), save the like state (using putString() and putBoolean()) and add to liked songs list.
  - If the button is already clicked (the song is liked), remove the like state and the song from the list.
[Image]

b. Read from shared preference: Restore like state
- Retrieve like state from shared preference file using getString() when navigating to LikedSongFragment.
[Image]


7. Background Playback with Service
- Service enables offscreen playback when user navigates away from the playback activity (for example, when user turns off the screen, or navigates to other applications).
- Service also makes lockscreen playback (MediaSession) and audio focus (AudioFocusManager) possible. 

[Image]

[Image]

8. NotificationCompat & BroadcastReceiver: Notification playback
- NotificationCompat allows the display of playback activity as notification, to display metadata of currently-playing songs (e.g. Song's title, artist, album cover image)
  - NotificationCompat.Builder: Settings of content's title, artist's name, small icon, background cover, etc.
[Image]
- BroadcastReceiver is registered to listen to specific events related to playback control, such as play, pause, skip, stop.
[Image]

9. MediaSession: Media controls (Lockscreen playback & controls)
- Provides an interface between the media playback application and external media controllers (such as in the lockscreen)

[Image]
- MediaMetadataCompat: Provides metadata to external media controllers and represents them consistently across different media sources.
[Image]

- PlaybackStateCompat: Represents state of the media. Conveys playback state and updates UI components accordingly (e.g. Play, pause, skip next, skip previous)
[Image]

[Image]

[Image]

10. AudioFocusChangeListener
- A component that listens to changes in the audio focus of the device.
- In this task, when another source is playing media, or when the device receives a phone call, the app loses focus and pauses the playing-song.
- When the app regains focus, the listener resumes the playback where it was paused.
[Image]

[Image]
[Image]


11. Bugs yet to be Fixed
- NowPlayingFragment blocks the last item of recycler view. A layout is yet to be added below the last item of the view (multi view types adapter)
- Seekbar and play/pause button are not initialized when a song is played the first time the application starts.
- Liked song list does not preserve FIFO manner. Liked songs are stored in random order.
- When the application starts, the placeholder playback activity is shown for a slight delay before the corresponding song details are displayed.
- Clicking on the notification playback returns the user back to the corresponding playback activity. But clicking the back button redirects the user back to the same page.
- Song is removed from liked song list after pressing the liked button, but still present in UI state.
- Handle configuration change (e.g. Screen rotation, switching between light/dark mode)

12. Future Improvements
- Random, loop playback.
- Enables creation of custom playlists, adding to and removing from custom playlists.
- Profile fragment's and settings' UI
