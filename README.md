## What is SoundLocker?

A password manager that is based off of songs.

## Is SoundLocker Secure and Risk-Free?

Short answer: Moderately.

Long answer: There are a number of attack vectors and risks that need to be considered.

An attacker could try brute forcing the generated password. If the app in question is a web application, the attacker will be throttled by the rate limit of the web application's server. If the app in question is a native application and there is no enforcable throttling by the app, then the password's strength starts to matter. SoundLocker uses the first 'x' characters of a SHA-256 hash of the byte data of a song. Hence, assuming the SHA-256 hash distributes the possible output characters randomly (currently "0123456789ABCDEF"), the password will have an entropy of log(16^x,2) bits where 'x' is the length of the password. For example, a password of length 10 would have 123 bits of entropy, which is more than enough for all practical purposes.

More likely, the attacker will probably try to guess the song you are generating the password from. There are approximately 30 million songs on Spotify. If a user chooses one song truly randomly out of those 30 million songs, then the password would have around 24.7 bits of entropy. Without any rate limits from the application in question that you are inserting the password into, it would take around 4 hours to crack a password at 1000 guesses/second.

However, around 20% of songs (as of 2013) on Spotify have never been played: in fact, there are services like Forgotify, which specifically find and play songs that have never been played. Moreover, the songs that any user will know is quite a small percentage of the 30 million. Hence, it is important that users choose songs that are relatively unknown.

If an attacker is monitoring a user's network traffic, they can get a better idea of the song that the user might be generating the password from. Spotify uses secure TLS/SSL connections and thus an attacker cannot see the exact songs the user has downloaded. However, they can see the size of the packets that are going and coming from Spotify and use that to deduce or at least reduce the size of "the possible songs list".

Even more likely, an attacker would probably try to intercept the password as it goes from SoundLocker to the app the user wants to place their password into. If a user uses the "insert through webview" option, the user is relatively safe. If users use the clipboard without clearing it, any attacker with access to their device would be able to access their password.