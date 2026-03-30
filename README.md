# PredictiveWritingTool_MovieScripts

Hi, thanks for checking out my project! I developed a predictive writing tool similar to the feature on smartphones where you might've noticed that it suggests words that you could add to whatever you're typing based on the context of your message. I as tasked to model a scenario like this for a class assignment, but I wanted to add my own spin on it by using movie scripts to suggest words.

The way this project works is that I've attached 3 movie scripts from my favorite movies to the project itself so that you can get word or character suggestions to build your own sentence based on the words that exist in the scripts themselves. To implement the word and character predictions, I used a Trie to break down the words in each script by characters, and there's 2 primary methods involved in the prediction process: mostLikelyNextWord() and mostLikelyNextChar().

mostLikelyNextChar(): The method begins by breaking down the prefix (or how much of a word the user finished) into characters as long as the prefix is valid, or else a _ is returned. Then, we focus on the last character in the prefix and traverse through its children characters, or characters that are most likely to come next within the Trie. Each character is stored with a passCount and endCount value in the Trie, but we want to find the greatest passCoutn value and match it wits the corresponding character to return it.

mostLikelyNextWord(): We begin by breaking down an entered prefix into characters and get its corresponding children similar to the initial steps of mostLikelyNextChar(). However, to generate potential words we need to use a StringBuilder to build predicted words as we traverse through child characters, a Map that will store each potential predicted word as the StringBuilder finished building them along with its endCount, and a recursive implementation to traverse through each potential child character. In the end, we want to return the word from the map that has the greatest endCount or based on alphabetical order.

Okay, I believe I've explained a lot already! But I'll leave the rest for exploration and let you experience it. Thanks again for checking out my project and feel free to reach out to my email: connectash08@gmail.com, with any questions or comments.



Credits / Citations:
- I want to give credit to my Data Structures instructor for providing the necessary code to setup the UI framework and initial Trie structure along with instructional material in class to help me brainstorm and build my project.
- Movie background pictures for the Dark Knight Rises, Toy Story, and Iron Man were taken from the internet.
- Movie transcripts for TDK rises, Toy Story, and Iron Man were taken from online sources.
